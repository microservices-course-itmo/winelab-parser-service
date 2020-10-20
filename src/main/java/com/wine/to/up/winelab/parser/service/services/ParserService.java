package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.utils.enums.Color;
import com.wine.to.up.winelab.parser.service.utils.enums.Sugar;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ParserService {
    @Value("${parser.siteURL}")
    private String siteURL;
    @Value("${parser.protocolPrefix}")
    private String protocol;
    @Value("#{${parser.cookies}}")
    private Map<String, String> cookies;
    @Value("${parser.catalogs}")
    String[] catalogs;

    @Value("${parser.selector.filter}")
    private String filterSelector;
    @Value("${parser.selector.filter.color}")
    private String colorSelector;
    @Value("${parser.selector.filter.sugar}")
    private String sugarSelector;
    @Value("${parser.selector.filter.country}")
    private String countrySelector;
    @Value("${parser.selector.filter.grape}")
    private String grapeSelector;
    @Value("${parser.selector.filter.manufacturer}")
    private String manufacturerSelector;
    @Value("${parser.selector.filter.category}")
    private String categorySelector;

    public ParserService() {
    }

    public Wine parseProduct(int productID) throws IOException {
        Set<String> countrySet = new HashSet<>();
        Set<String> grapeSet = new HashSet<>();
        Set<String> manufacturerSet = new HashSet<>();
        for (String catalog : catalogs) {
            String url = protocol + siteURL + "/catalog/" + catalog;
            Document document = Jsoup.connect(url).cookies(cookies).get();

            countrySet.addAll(loadAttributes(document, countrySelector));
            grapeSet.addAll(loadAttributes(document, grapeSelector));
            manufacturerSet.addAll(loadAttributes(document, manufacturerSelector));
        }
        return parseProduct(productID, countrySet, grapeSet, manufacturerSet);
    }

    private Wine parseProduct(int productID, Set<String> countrySet, Set<String> grapeSet, Set<String> manufacturerSet) throws IOException {
        final String productURL = protocol + siteURL + "/product/" + productID;

        final String patternVolume = "\\d+([,.]\\d+)? [Лл]";
        final String patternAlcoholContent = "\\d{0,2}(.\\d+)? %";
        final String sparklingCategory = "Шампанские и игристые вина";

        final String nameSelector = "div.product_description div.description";
        final String detailsSelector = "div.container div.row.product-detail-page.product_card_row.js-add-recent-list";
        final String brandSelector = "data-brand";
        final String tagSelector = "div.product_description div.filters > span";
        final String oldPriceSelector = "div.product_description div.prices_main";
        final String newPriceSelector = "data-price";
        final String imageSelector = "div.image-zoom.js-zoom-product img";
        final String cardSelector = "div.container div.row.filtered_items_row.js-infinite-scroll a.product_card.js-product-click";
        final String cardCountrySelector = "div.container div.country_wrapper h3";
        final String gastronomySelector = "div.product_description_card:contains(Рекомендуемое употребление) p";
        final String descriptionSelector = "div.product_description_card:contains(Электронный сомелье) p";
        final String regionSelector = "data-category";
        final String idSelector = "data-id";

        Document document = Jsoup.connect(productURL).cookies(cookies).get();

        Wine wine = new Wine();

        String name = document.selectFirst(nameSelector).ownText();
        wine.setName(name);

        wine.setLink(productURL);

        Element details = document.selectFirst(detailsSelector);
        String brand = details.attr(brandSelector);
        if (!brand.isEmpty()) {
            wine.setBrand(brand);
        }
        String newPriceString = details.attr(newPriceSelector);
        if (!newPriceString.isEmpty()) {
            BigDecimal newPrice = new BigDecimal(newPriceString);
            wine.setNewPrice(newPrice);
        }
        String region = details.attr(regionSelector);
        if (isRegion(region)) {
            wine.setRegion(region);
        }

        Element img = document.selectFirst(imageSelector);
        if (img != null) {
            String image = protocol + siteURL + img.attr("src");
            wine.setImage(image);
        }

        if (isSparkling(name)) {
            wine.setSparkling(true);
        }

        Elements tags = document.select(tagSelector);
        for (Element tagEl : tags) {
            String tag = tagEl.ownText();
            if (tag.matches(patternVolume)) {
                tag = tag.replaceAll("[ Л]", "");
                BigDecimal volume = new BigDecimal(tag);
                wine.setVolume(volume);
            } else if (tag.matches(patternAlcoholContent)) {
                tag = tag.replaceAll("[ %]", "");
                BigDecimal alcoholContent = new BigDecimal(tag);
                wine.setAlcoholContent(alcoholContent);
            }
        }

        Element oldPriceSpan = document.selectFirst(oldPriceSelector);
        if (oldPriceSpan != null) {
            BigDecimal oldPrice = new BigDecimal(oldPriceSpan.ownText().replaceAll(" ", ""));
            wine.setOldPrice(oldPrice);
        }

        String gastronomy = document.selectFirst(gastronomySelector).html();
        wine.setGastronomy(gastronomy);

        String description = document.selectFirst(descriptionSelector).html();
        wine.setDescription(description);

        final String searchURL = protocol + siteURL + "/search?q=" +
                productID + "%3Arelevance" +
                (wine.getAlcoholContent() != null ? "%3AAlcoholContent%3A%255B" + wine.getAlcoholContent() + "%2BTO%2B" + wine.getAlcoholContent() + "%255D" : "") +
                (wine.getNewPrice() != null ? "%3Aprice%3A%5B" + wine.getNewPrice() + "%20TO%20" + wine.getNewPrice() + "%5D" : "");
        Document searchPage;
        Elements cards;
        try {
            searchPage = Jsoup.connect(searchURL).cookies(cookies).get();
            cards = searchPage.select(cardSelector);
            assert cards.size() == 1 && Integer.parseInt(cards.first().attr(idSelector)) == productID;

            Element colorSpan = searchPage.selectFirst(String.format(filterSelector, colorSelector));
            if (colorSpan != null) {
                String colorText = colorSpan.html();
                Color color = Color.fromString(colorText);
                if (color != null) {
                    wine.setColor(color);
                }
            }

            Element sugarSpan = searchPage.selectFirst(String.format(filterSelector, sugarSelector));
            if (sugarSpan != null) {
                String sugarText = sugarSpan.html();
                Sugar sugar = Sugar.fromString(sugarText);
                if (sugar != null) {
                    wine.setSugar(sugar);
                }
            }

            Element countrySpan = searchPage.selectFirst(String.format(filterSelector, countrySelector));
            if (countrySpan != null) {
                String country = countrySpan.html();
                wine.setCountry(countryFix(country));
            }

            Element grapeSpan = searchPage.selectFirst(String.format(filterSelector, grapeSelector));
            if (grapeSpan != null) {
                String grapeSort = grapeSpan.html();
                wine.setGrapeSort(grapeSort);
            }

            Element manufacturerSpan = searchPage.selectFirst(String.format(filterSelector, manufacturerSelector));
            if (manufacturerSpan != null) {
                String manufacturer = manufacturerSpan.html();
                wine.setManufacturer(manufacturer);
            }

            if (!wine.isSparkling()) {
                Elements categories = searchPage.select(String.format(filterSelector, categorySelector));
                for (Element category : categories) {
                    if (category.html().equals(sparklingCategory)) {
                        wine.setSparkling(true);
                        break;
                    }
                }
            }

            if (wine.getCountry() == null) {
                Element countryWrapper = searchPage.selectFirst(cardCountrySelector);
                if (countryWrapper != null) {
                    String country = countryWrapper.html();
                    wine.setCountry(countryFix(country));
                }
            }
        } catch (Exception ex) {
            for (Element tagEl : tags) {
                String tag = tagEl.ownText();
                Color color = Color.fromString(tag);
                Sugar sugar = Sugar.fromString(tag);
                if (color != null) {
                    wine.setColor(color);
                } else if (sugar != null) {
                    wine.setSugar(sugar);
                } else if (countrySet.contains(tag)) {
                    wine.setCountry(countryFix(tag));
                } else if (grapeSet.contains(tag)) {
                    wine.setGrapeSort(tag);
                } else if (manufacturerSet.contains(tag)) {
                    wine.setManufacturer(tag);
                }
            }
        }
        return wine;
    }

    public Map<Integer, Wine> parseCatalogs() throws IOException {
        Map<Integer, Wine> wines = new HashMap<>();
        for (String catalog : catalogs) {
            parseCatalog(catalog, wines);
        }
        return wines;
    }

    private void parseCatalog(String category, Map<Integer, Wine> wines) throws IOException {
        final String cardSelector = "div.container a.product_card";
        final String idSelector = "data-id";
        final String nextPageSelector = "ul.pagination li.page-item a[rel=next]";
        final String nameSelector = "div.product_card--header div"; // last in the list
        final String startPage = "/catalog/" + category;
        long begin = System.currentTimeMillis();
        Map<Integer, Wine> ids = new HashMap<>();
        String url = protocol + siteURL + startPage;
        Document document = Jsoup.connect(url).cookies(cookies).get();
        boolean isLastPage = false;

        Set<String> countrySet = loadAttributes(document, countrySelector);
        Set<String> grapeSet = loadAttributes(document, grapeSelector);
        Set<String> manufacturerSet = loadAttributes(document, manufacturerSelector);

        while (!isLastPage) {
            document.select(cardSelector)
                    .parallelStream()
                    .forEach(card -> {
                        String name = card.select(nameSelector).last().html();
                        if (isWine(name)) {
                            int id = Integer.parseInt(card.attr(idSelector));
                            try { // TODO log time spent for parsing one position
                                if (!wines.containsKey(id)) {
                                    long start = System.currentTimeMillis();
                                    wines.put(id, parseProduct(id, countrySet, grapeSet, manufacturerSet));
                                    long finish = System.currentTimeMillis();
                                    long timeElapsed = finish - start;
                                    log.info("Time elapsed parsing wine with id {} = {} ms", id, timeElapsed);
                                }
                            } catch (Exception ex) {
                                log.error("Error while parsing wine with id {} {}", id, ex);
                            }
                        }
                    });

            Element nextPage = document.select(nextPageSelector).first();
            if (nextPage == null) {
                isLastPage = true;
            } else {
                url = protocol + siteURL + nextPage.attr("href");
                document = Jsoup.connect(url).cookies(cookies).get();
            }
        }

        long end = System.currentTimeMillis();
        long timeElapsedTotal = end - begin;
        log.info("Time elapsed total:", timeElapsedTotal);
    }

    /* Utility */

    private Set<String> loadAttributes(Document document, String attrSelector) {
        return document.select(String.format(filterSelector, attrSelector))
                .stream()
                .map(Element::html)
                .collect(Collectors.toSet());
    }

    private boolean isWine(String name) {
        final String[] wineStrings = {"вино", "винный", "шампанское", "портвейн", "глинтвейн", "вермут", "кагор", "сангрия"};
        return Arrays.stream(wineStrings).reduce(false,
                (a, b) -> a || name.toLowerCase().contains(b),
                (a, b) -> a || b);
    }

    private boolean isSparkling(String name) {
        final String[] sparklingStrings = {"игрист", "шампанское"};
        return Arrays.stream(sparklingStrings).reduce(false,
                (a, b) -> a || name.toLowerCase().contains(b),
                (a, b) -> a || b);
    }

    private boolean isRegion(String subcategory) {
        final String[] regions = {"бордо", "венето", "тоскана", "риоха", "кастилья ла манча", "бургундия", "долина луары",
                "кампо де борха", "риберо дель дуэро", "пьемонт", "долина роны", "сицилия", "другие регионы"};
        return Arrays.stream(regions).reduce(false,
                (a, b) -> a || subcategory.toLowerCase().contains(b),
                (a, b) -> a || b);
    }

    private String countryFix(String country) { // fix double names for countries
        final Map<String, String> countries = Map.of(
                "Российская Федерация", "Россия",
                "Южная Африка", "ЮАР",
                "Соединенные Штаты Америки", "США",
                "Соед. Королев.", "Великобритания");
        return countries.getOrDefault(country, country);
    }
}
