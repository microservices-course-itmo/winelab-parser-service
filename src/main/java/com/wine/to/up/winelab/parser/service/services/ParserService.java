package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Class containing methods for parsing product pages and catalog pages
 *
 * @author : Somov Artyom
 */
@Service
@Slf4j
@Configuration
@PropertySource("classpath:parser.properties")
public class ParserService {
    @Value("${parser.siteURL}")
    private String siteURL;
    @Value("${parser.protocolPrefix}")
    private String protocol;
    @Value("#{${parser.cookies}}")
    private Map<String, String> cookies;
    @Value("#{${parser.catalogs}}")
    private Map<String, String> catalogs;

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
    static final String DATA_ID_SELECTOR = "data-id";
    static final String CATALOG_URL = "/catalog/";


    public ParserService() {
    }

    /**
     * Parsing wine from winelab web site by the id given
     *
     * @param productID a product id on winelab.ru of wine to be parsed
     * @return parsed wine object
     * @throws IOException in case method couldn't reach web page
     */
    public Wine parseProduct(int productID) throws IOException {
        Set<String> countrySet = new HashSet<>();
        Set<String> grapeSet = new HashSet<>();
        Set<String> manufacturerSet = new HashSet<>();
        for (String catalog : catalogs.values()) {
            String url = protocol + siteURL + CATALOG_URL + catalog;
            Document document = Jsoup.connect(url).cookies(cookies).get();

            countrySet.addAll(loadAttributes(document, countrySelector));
            grapeSet.addAll(loadAttributes(document, grapeSelector));
            manufacturerSet.addAll(loadAttributes(document, manufacturerSelector));
        }
        return parseProduct(productID, countrySet, grapeSet, manufacturerSet);
    }

    private Wine parseProduct(int productID, Set<String> countrySet, Set<String> grapeSet, Set<String> manufacturerSet) throws IOException {
        final String productURL = protocol + siteURL + "/product/" + productID;


        final String nameSelector = "div.product_description div.description";
        final String detailsSelector = "div.container div.row.product-detail-page.product_card_row.js-add-recent-list";
        final String brandSelector = "data-brand";
        final String tagSelector = "div.product_description div.filters > span";
        final String imageSelector = "div.image-zoom.js-zoom-product img";
        final String cardCountrySelector = "div.container div.country_wrapper h3";

        final String gastronomySelector = "div.product_description_card:contains(Рекомендуемое употребление) p";
        final String descriptionSelector = "div.product_description_card:contains(Электронный сомелье) p";
        final String regionSelector = "data-category";
        final String sparklingCategory = "Шампанские и игристые вина";

        Document document = Jsoup.connect(productURL).cookies(cookies).get();

        Wine wine = new Wine();

        String name = document.selectFirst(nameSelector).ownText();
        wine.setName(name);

        wine.setLink(productURL);

        Element details = document.selectFirst(detailsSelector);

        fillPrices(wine, document, details);

        String brand = details.attr(brandSelector);
        if (!brand.isEmpty()) {
            wine.setBrand(brand);
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
        fillTags(tags, wine);

        String gastronomy = document.selectFirst(gastronomySelector).html();
        wine.setGastronomy(gastronomy);

        String description = document.selectFirst(descriptionSelector).html();
        wine.setDescription(description);
        String searchURL = getLink(protocol, siteURL, productID, wine);
        try {
            Document searchPage = Jsoup.connect(searchURL).cookies(cookies).get();
            fillValuesBySearchURL(searchPage, productID, wine);
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
            fillValuesOnException(tags, wine, grapeSet, manufacturerSet, countrySet);

        }
        return wine;
    }

    /**
     * Parsing all wine-related catalogs from winelab web site
     *
     * @return map of parsed wines in format (product id, parsed wine object)
     * @throws IOException in case method couldn't reach web page
     */
    public Map<Integer, Wine> parseCatalogs() throws IOException {
        Map<Integer, Wine> wines = new HashMap<>();
        for (String catalog : catalogs.values()) {
            parseCatalog(catalog, wines);
        }
        return wines;
    }

    private void parseCatalog(String category, Map<Integer, Wine> wines) throws IOException {
        final String cardSelector = "div.container a.product_card";
        final String idSelector = "data-id";
        final String nextPageSelector = "ul.pagination li.page-item a[rel=next]";
        final String nameSelector = "div.product_card--header div"; // last in the list
        final String startPage = CATALOG_URL + category;

        String url = protocol + siteURL + startPage;
        Document document = Jsoup.connect(url).cookies(cookies).get();
        boolean isLastPage = false;

        Set<String> countrySet = loadAttributes(document, countrySelector);
        Set<String> grapeSet = loadAttributes(document, grapeSelector);
        Set<String> manufacturerSet = loadAttributes(document, manufacturerSelector);
        AtomicInteger count = new AtomicInteger();
        while (!isLastPage) {
            document.select(cardSelector)
                    .parallelStream()
                    .forEach(card -> {
                        String name = card.select(nameSelector).last().html();
                        if (isWine(name)) {
                            int id = Integer.parseInt(card.attr(DATA_ID_SELECTOR));
                            try {
                                if (!wines.containsKey(id)) {
                                    long start = System.currentTimeMillis();
                                    wines.put(id, parseProduct(id, countrySet, grapeSet, manufacturerSet));
                                    long finish = System.currentTimeMillis();
                                    long timeElapsed = finish - start;
                                    log.info("Time elapsed parsing wine with id {} = {} ms", id, timeElapsed);
                                }
                            } catch (Exception ex) {
                                count.set(count.get() + 1);
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

        log.info("Total failed-to-parse wines: {}", count);
    }

    public Map<Integer, Wine> parseCatalogPage(String catalog, int page) throws IOException {
        final String cardSelector = "div.container a.product_card";
        final String nameSelector = "div.product_card--header div"; // last in the list
        final String url = protocol + siteURL + CATALOG_URL + catalogs.get(catalog) + "?page=" + page + "&sort=relevance";

        Document document = Jsoup.connect(url).cookies(cookies).get();

        Set<String> countrySet = loadAttributes(document, countrySelector);
        Set<String> grapeSet = loadAttributes(document, grapeSelector);
        Set<String> manufacturerSet = loadAttributes(document, manufacturerSelector);

        Map<Integer, Wine> wines = new HashMap<>();

        AtomicInteger count = new AtomicInteger();

        document.select(cardSelector)
                .parallelStream()
                .forEach(card -> {
                    String name = card.select(nameSelector).last().html();
                    if (isWine(name)) {
                        int id = Integer.parseInt(card.attr(DATA_ID_SELECTOR));
                        try {
                            if (!wines.containsKey(id)) {
                                long start = System.currentTimeMillis();
                                wines.put(id, parseProduct(id, countrySet, grapeSet, manufacturerSet));
                                long finish = System.currentTimeMillis();
                                long timeElapsed = finish - start;
                                log.info("Time elapsed parsing wine with id {} = {} ms", id, timeElapsed);
                            }
                        } catch (Exception ex) {
                            count.set(count.get() + 1);
                            log.error("Error while parsing wine with id {} {}", id, ex);
                        }
                    }
                });

        log.info("Total failed-to-parse wines: {}", count);
        return wines;
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

    private ParserApi.Wine.Color getColor(String color) {
        final Map<String, ParserApi.Wine.Color> colors = Map.of(
                "красное", ParserApi.Wine.Color.RED,
                "розовое", ParserApi.Wine.Color.ROSE,
                "белое", ParserApi.Wine.Color.WHITE
        );
        return colors.getOrDefault(color.toLowerCase(), null);
    }

    private ParserApi.Wine.Sugar getSugar(String sugar) {
        final Map<String, ParserApi.Wine.Sugar> sugars = Map.of(
                "брют", ParserApi.Wine.Sugar.DRY,
                "сухое", ParserApi.Wine.Sugar.DRY,
                "полусухое", ParserApi.Wine.Sugar.MEDIUM_DRY,
                "полусладкое", ParserApi.Wine.Sugar.MEDIUM,
                "сладкое", ParserApi.Wine.Sugar.SWEET
        );
        return sugars.getOrDefault(sugar.toLowerCase(), null);
    }

    private String getLink(String protocol, String siteURL, int productID, Wine wine) {
        return protocol + siteURL + "/search?q=" +
                productID + "%3Arelevance" +
                (wine.getBrand() != null ? "%3Abrands%3A" + wine.getBrand() : "") +
                (wine.getAlcoholContent() != null ? "%3AAlcoholContent%3A%255B" + wine.getAlcoholContent() + "%2BTO%2B" + wine.getAlcoholContent() + "%255D" : "") +
                (wine.getNewPrice() != null ? "%3Aprice%3A%5B" + wine.getNewPrice() + "%20TO%20" + wine.getNewPrice() + "%5D" : "");

    }

    private void fillTags(Elements tags, Wine wine) {
        final String patternVolume = "\\d+([,.]\\d+)? [Лл]";
        final String patternAlcoholContent = "\\d{0,2}(.\\d+)? %";
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
    }

    private void fillValuesOnException(Elements tags, Wine wine, Set<String> grapeSet, Set<String> manufacturerSet, Set<String> countrySet) {
        for (Element tagEl : tags) {
            String tag = tagEl.ownText();
            ParserApi.Wine.Color color = getColor(tag);
            ParserApi.Wine.Sugar sugar = getSugar(tag);
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

    private void fillValuesBySearchURL(Document searchPage, int productID, Wine wine) {
        final String cardSelector = "div.container div.row.filtered_items_row.js-infinite-scroll a.product_card.js-product-click";
        final String idSelector = "data-id";

        Elements cards = searchPage.select(cardSelector);
        assert cards.size() == 1 && Integer.parseInt(cards.first().attr(idSelector)) == productID;

        Element colorSpan = searchPage.selectFirst(String.format(filterSelector, colorSelector));
        if (colorSpan != null) {
            String colorText = colorSpan.html();
            ParserApi.Wine.Color color = getColor(colorText);
            if (color != null) {
                wine.setColor(color);
            }
        }

        Element sugarSpan = searchPage.selectFirst(String.format(filterSelector, sugarSelector));
        if (sugarSpan != null) {
            String sugarText = sugarSpan.html();
            ParserApi.Wine.Sugar sugar = getSugar(sugarText);
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
    }
    private void fillPrices(Wine wine, Document document, Element details) {
        final String newPriceSelector = "data-price";
        final String oldPriceSelector = "div.product_description div.prices_main";

        String newPriceString = details.attr(newPriceSelector);
        if (!newPriceString.isEmpty()) {
            BigDecimal newPrice = new BigDecimal(newPriceString);
            wine.setNewPrice(newPrice);
        }
        Element oldPriceSpan = document.selectFirst(oldPriceSelector);
        if (oldPriceSpan != null) {
            BigDecimal oldPrice = new BigDecimal(oldPriceSpan.ownText().replace(" ", ""));
            wine.setOldPrice(oldPrice);
        }
    }
}
