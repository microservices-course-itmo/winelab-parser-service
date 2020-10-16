package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.utils.enums.Color;
import com.wine.to.up.winelab.parser.service.utils.enums.Sugar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParserService {
    @Value("${parser.siteURL}")
    private String siteURL;
    @Value("${parser.protocolPrefix}")
    private String protocol;
    @Value("${parser.catalogs}")
    String[] catalogs;

    @Value("${parser.selector.filter.start}")
    private String filterSelectorStart;
    @Value("${parser.selector.filter.end}")
    private String filterSelectorEnd;
    @Value("${parser.selector.filter.color}")
    private String colorSelector;
    @Value("${parser.selector.filter.sugar}")
    private String sugarSelector;
    @Value("${parser.selector.filter.country}")
    private String countrySelector;
    @Value("${parser.selector.filter.grape}")
    private String grapeSelector;
    @Value("${parser.selector.filter.brand}")
    private String brandSelector;
    @Value("${parser.selector.filter.manufacturer}")
    private String manufacturerSelector;
    @Value("${parser.selector.filter.category}")
    private String categorySelector;

    public ParserService() {
    }

    public Wine parseProduct(int productID, List<String> countryList, List<String> grapeList, List<String> brandList, List<String> manufacturerList) throws IOException {
        final String productURL = protocol + siteURL + "/product/" + productID;
        final String searchURL = protocol + siteURL + "/search/?text=" + productID;

        final String patternVolume = "\\d*([.,]\\d+)? ?[лЛ]";
        final String patternAlcoholPercentage = "\\d{0,2}([.,]\\d+)? ?%";
        final String sparklingCategory = "Шампанские и игристые вина";

        final String nameSelector = "div.product_description div.description";
        final String tagSelector = "div.product_description div.filters > span";
        final String priceSelector = "div.product_description div.prices_main";
        final String discountPriceSelector = "div.prices_cart_price.tooltip";
        final String imageSelector = "div.image-zoom.js-zoom-product img";
        final String cardSelector = "div.container div.row.filtered_items_row.js-infinite-scroll a.product_card.js-product-click";
        final String cardCountrySelector = "div.container div.country_wrapper h3";
        final String gastronomySelector = "div.product_description_card:contains(Рекомендуемое употребление) p";
        final String descriptionSelector = "div.product_description_card:contains(Электронный сомелье) p";
        final String breadcrumbSelector = "ol.breadcrumb li a";
        final String idSelector = "data-id";

        Document document = Jsoup.connect(productURL).get();

        Wine wine = new Wine();

        String name = document.select(nameSelector).first().ownText();
        wine.setName(name);

        wine.setLink(productURL);

        Element img = document.selectFirst(imageSelector);
        String image = protocol + siteURL + img.attr("src");
        wine.setImage(image);

        if (isSparkling(name)) {
            wine.setSparkling(true);
        }

        Elements breadcrumb = document.select(breadcrumbSelector);
        int bcCount = breadcrumb.size();
        String subcategory = breadcrumb.get(bcCount - 2).html();
        if (isRegion(subcategory)) {
            wine.setRegion(subcategory);
        }

        Elements filters = document.select(tagSelector);
        for (Element filter : filters) {
            String tag = filter.ownText();
            if (tag.matches(patternVolume)) {
                tag = tag.replaceAll("[ Л]", "");
                BigDecimal volume = new BigDecimal(tag);
                wine.setVolume(volume);
            } else if (tag.matches(patternAlcoholPercentage)) {
                tag = tag.replaceAll("[ %]", "");
                BigDecimal alcoholPercentage = new BigDecimal(tag);
                wine.setAlcoholPercentage(alcoholPercentage);
            }
        }

        BigDecimal oldPrice = new BigDecimal(document.selectFirst(priceSelector).ownText().replaceAll(" ", ""));
        wine.setOldPrice(oldPrice);

        Element discountPrices = document.selectFirst(discountPriceSelector);
        if (discountPrices != null){
            BigDecimal price = new BigDecimal(discountPrices.select("span").get(1).ownText().replaceAll(" ", ""));
            wine.setNewPrice(price);
        } else { // means that item is out of stock and there's no discount price mentioned
            wine.setNewPrice(oldPrice);
        }

        String gastronomy = document.selectFirst(gastronomySelector).html();
        wine.setGastronomy(gastronomy);

        String description = document.selectFirst(descriptionSelector).html();
        wine.setDescription(description);

        Document searchPage;
        Elements cards;
        try {
            searchPage = Jsoup.connect(searchURL).get();
            cards = searchPage.select(cardSelector);
            // TODO change to asserts(?)
            if (cards.size() != 1 || Integer.parseInt(cards.first().attr(idSelector)) != productID) {
                throw new IOException();
            }
            Element colorSpan = searchPage.selectFirst(filterSelectorStart + colorSelector + filterSelectorEnd);
            if (colorSpan != null) {
                String colorText = colorSpan.html();
                Color color = Color.fromString(colorText);
                if (color != null) {
                    wine.setColor(color);
                }
            }

            Element sugarSpan = searchPage.selectFirst(filterSelectorStart + sugarSelector + filterSelectorEnd);
            if (sugarSpan != null) {
                String sugarText = sugarSpan.html();
                Sugar sugar = Sugar.fromString(sugarText);
                if (sugar != null) {
                    wine.setSugar(sugar);
                }
            }

            Element countrySpan = searchPage.selectFirst(filterSelectorStart + countrySelector + filterSelectorEnd);
            if (countrySpan != null) {
                String country = countrySpan.html();
                wine.setCountry(country);
            }

            Element grapeSpan = searchPage.selectFirst(filterSelectorStart + grapeSelector + filterSelectorEnd);
            if (grapeSpan != null) {
                String grapeSort = grapeSpan.html();
                wine.setGrapeSort(grapeSort);
            }

            Element brandSpan = searchPage.selectFirst(filterSelectorStart + brandSelector + filterSelectorEnd);
            if (brandSpan != null) {
                String brand = brandSpan.html();
                wine.setBrand(brand);
            }

            Element manufacturerSpan = searchPage.selectFirst(filterSelectorStart + manufacturerSelector + filterSelectorEnd);
            if (manufacturerSpan != null) {
                String manufacturer = manufacturerSpan.html();
                wine.setManufacturer(manufacturer);
            }

            Elements categories = searchPage.select(filterSelectorStart + categorySelector + filterSelectorEnd);
            for (Element category : categories) {
                if (category.html().equals(sparklingCategory)) {
                    wine.setSparkling(true);
                    break;
                }
            }

            if (wine.getCountry() == null) {
                Element countryWrapper = searchPage.selectFirst(cardCountrySelector);
                if (countryWrapper != null) {
                    String country = countryWrapper.html();
                    if (!country.isEmpty()) {
                        wine.setCountry(countryWrapper.html());
                    }
                }
            }
        } catch (IOException ex) {
            for (Element filter : filters) {
                String tag = filter.ownText();
                Color color = Color.fromString(tag);
                Sugar sugar = Sugar.fromString(tag);
                if (color != null) {
                    wine.setColor(color);
                } else if (sugar != null) {
                    wine.setSugar(sugar);
                } else if (countryList != null && countryList.contains(tag)) {
                    wine.setCountry(tag);
                } else if (grapeList != null && grapeList.contains(tag)) {
                    wine.setGrapeSort(tag);
                } else if (wine.getBrand() == null && brandList != null && brandList.contains(tag)) { // to prevent overwriting brand with manufacturer in case of the same names
                    wine.setBrand(tag);
                } else if (manufacturerList != null && manufacturerList.contains(tag)) {
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

        String url = protocol + siteURL + startPage;
        Document document = Jsoup.connect(url).get();
        boolean isLastPage = false;

        List<String> countryList = loadAttributes(document, countrySelector);
        List<String> grapeList = loadAttributes(document, grapeSelector);
        List<String> brandList = loadAttributes(document, brandSelector);
        List<String> manufacturerList = loadAttributes(document, manufacturerSelector);

        while (!isLastPage) {
            Elements productCards = document.select(cardSelector);
            for (Element card : productCards) {
                String name = card.select(nameSelector).last().html();
                if (isWine(name)) {
                    int id = Integer.parseInt(card.attr(idSelector));
                    if (!wines.containsKey(id)) {
                        try {
                            wines.put(id, parseProduct(id, countryList, grapeList, brandList, manufacturerList));
                        } catch (IOException ex) { // for some reason wine is in catalog, but product page doesn't exist
                            ; // TODO decide what we do in this case
                        }
                    }
                }
            }

            Element nextPage = document.select(nextPageSelector).first();
            if (nextPage == null) {
                isLastPage = true;
            } else {
                url = protocol + siteURL + nextPage.attr("href");
                document = Jsoup.connect(url).get();
            }
        }
    }

    /* Utility */

    private List<String> loadAttributes(Document document, String attrSelector) {
        List<String> list = new ArrayList<>();

        Elements spans = document.select(filterSelectorStart + attrSelector + filterSelectorEnd);
        for (Element span : spans) {
            list.add(span.html());
        }

        return list;
    }

    private boolean isWine(String name) {
        final String[] wineStrings = {"вино", "винный", "шампанское", "портвейн", "глинтвейн", "вермут", "кагор", "сангрия"};
        name = name.toLowerCase();
        for (String wineString : wineStrings) {
            if (name.contains(wineString)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSparkling(String name) {
        final String[] sparklingStrings = {"игрист", "шампанское"};
        name = name.toLowerCase();
        for (String sparklingString : sparklingStrings) {
            if (name.contains(sparklingString)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRegion(String subcategory) {
        final String[] regions = {"бордо", "венето", "тоскана", "риоха", "кастилья ла манча", "бургундия", "долина луары",
                "кампо де борха", "риберо дель дуэро", "пьемонт", "долина роны", "сицилия", "другие регионы"};
        subcategory = subcategory.toLowerCase();
        for (String region : regions) {
            if (subcategory.contains(region)) {
                return true;
            }
        }
        return false;
    }
}
