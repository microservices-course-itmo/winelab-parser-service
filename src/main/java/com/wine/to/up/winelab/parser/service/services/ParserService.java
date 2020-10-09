package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ParserService {
    @Value("${parser.siteURL}")
    private String siteURL;
    @Value("${parser.protocolPrefix}")
    private String protocol;

    public ParserService() {
    }

    public Wine parseProduct(int productID) throws IOException {
        final String productURL = protocol + siteURL + "/product/" + productID;
        final String searchURL = protocol + siteURL + "/search/?text=" + productID;

        final String patternVolume = "\\d*([.,]\\d+)? ?[лЛ]";
        final String patternAlcoholPercentage = "\\d{0,2}([.,]\\d+)? ?%";
        final String sparklingCategory = "Шампанские и игристые вина";

        final String nameSelector = "div.product_description div.description";
        final String tagSelector = "div.product_description div.filters > span";
        final String priceSelector = "div.product_description div.prices_main";
        final String discountPriceSelector = "div.prices_cart_price";
        final String imageSelector = "div.image-zoom.js-zoom-product img";

        final String filterSelectorStart = "div.filter_block__container.js-facet.js-facet-values div[data-code=";
        final String filterSelectorEnd = "] span";
        final String colorSelector = "Color";
        final String sugarSelector = "SugarAmount";
        final String countrySelector = "countryfiltr";
        final String grapeSelector = "Sort";
        final String brandSelector = "brands";
        final String manufacturerSelector = "manufacture";
        final String categorySelector = "category";

        Document document = Jsoup.connect(productURL).get();

        Wine wine = new Wine();

        String name = document.select(nameSelector).first().ownText();
        wine.setName(name);

        wine.setSite(siteURL);
        wine.setLink(productURL);

        Element img = document.selectFirst(imageSelector);
        String image = protocol + siteURL + img.attr("src");
        wine.setImage(image);

        for (Element filter : document.select(tagSelector)) {
            String tag = filter.ownText();

            if (tag.matches(patternVolume)) {
                tag = tag.replaceAll("[ лЛ]", "").replaceAll(",", ".");
                BigDecimal volume = new BigDecimal(tag);
                wine.setVolume(volume);
            } else if (tag.matches(patternAlcoholPercentage)) {
                tag = tag.replaceAll("[ %]", "").replaceAll(",", ".");
                BigDecimal alcoholPercentage = new BigDecimal(tag);
                wine.setAlcoholPercentage(alcoholPercentage);
            }
        }

        BigDecimal oldPrice = new BigDecimal(document.selectFirst(priceSelector).ownText().replaceAll(" ", ""));
        wine.setOldPrice(oldPrice);

        for (Element element : document.select(discountPriceSelector)) {
            int quantity = Integer.parseInt(element.select("span").get(0).html().replaceAll("[x шт]", ""));
            if (quantity == 1) {
                BigDecimal price = new BigDecimal(element.select("span").get(1).ownText().replaceAll(" ", ""));
                wine.setNewPrice(price);
            }
        }

        document = Jsoup.connect(searchURL).get();

        // TODO create enum instead
        Element colorSpan = document.selectFirst(filterSelectorStart + colorSelector + filterSelectorEnd);
        if (colorSpan != null) {
            String color = colorSpan.html();
            wine.setColor(color);
        }

        // TODO create enum instead
        Element sugarSpan = document.selectFirst(filterSelectorStart + sugarSelector + filterSelectorEnd);
        if (sugarSpan != null) {
            String sugar = sugarSpan.html();
            wine.setSugar(sugar);
        }

        Element countrySpan = document.selectFirst(filterSelectorStart + countrySelector + filterSelectorEnd);
        if (countrySpan != null) {
            String country = countrySpan.html();
            wine.setCountry(country);
        }

        Element grapeSpan = document.selectFirst(filterSelectorStart + grapeSelector + filterSelectorEnd);
        if (grapeSpan != null) {
            String grapeSort = grapeSpan.html();
            wine.setGrapeSort(grapeSort);
        }

        Element brandSpan = document.selectFirst(filterSelectorStart + brandSelector + filterSelectorEnd);
        if (brandSpan != null) {
            String brand = brandSpan.html();
            wine.setBrand(brand);
        }

        Element manufacturerSpan = document.selectFirst(filterSelectorStart + manufacturerSelector + filterSelectorEnd);
        if (manufacturerSpan != null) {
            String manufacturer = manufacturerSpan.html();
            wine.setManufacturer(manufacturer);
        }

        Elements categories = document.select(filterSelectorStart + categorySelector + filterSelectorEnd);
        if (categories.contains(sparklingCategory)) {
            wine.setSparkling(true);
        } else {
            wine.setSparkling(false);
        }

        /* TODO
            parse description, parse gastronomy, parse region(?), maybe parse bigger version of image instead
            maybe try parsing country from both filters and product name (in case one of these is missing)
            maybe try parseing sparklingness from the product name as well (sometimes sparkling filter just isn't there)
         */

        return wine;
    }

    public List<Integer> parseHome() throws IOException {
        final String cardSelector = "a.product_card.js-product-click";
        final String idSelector = "data-id";

        Document document = Jsoup.connect(siteURL).get();

        Elements elementsWithId = document.select(cardSelector);
        List<Integer> ids = new ArrayList<>();
        for (Element item : elementsWithId) {
            String idStr = item.attr(idSelector);
            int id = Integer.parseInt(idStr);
            if (ids.contains(id)) {
                continue;
            }
            ids.add(id);
        }

        return ids;
    }

    public List<Integer> parseCatalogs() throws IOException {
        final List<String> catalogs = new ArrayList<>(Arrays.asList("vino", "shampanskie-i-igristye-vina"));

        List<Integer> ids = new ArrayList<>();
        for (String catalog : catalogs) {
            ids.addAll(parseCatalog(catalog));
        }

        return ids;
    }

    public List<Integer> parseCatalog(String category) throws IOException {
        final String cardSelector = "div.container a.product_card";
        final String idSelector = "data-id";
        final String nextPageSelector = "ul.pagination li.page-item a[rel=next]";
        final String nameSelector = "div.product_card--header div"; // last in the list
        final String startPage = "/catalog/" + category;

        String url = protocol + siteURL + startPage;
        boolean isLastPage = false;
        List<Integer> ids = new ArrayList<>();

        while (!isLastPage) {
            Document document = Jsoup.connect(url).get();

            Elements productCards = document.select(cardSelector);
            for (Element element : productCards) {
                String name = productCards.select(nameSelector).last().html();
                if (isWine(name)) {
                    ids.add(Integer.parseInt(element.attr(idSelector)));
                }
            }

            Element nextPage = document.select(nextPageSelector).first();
            if (nextPage == null) {
                isLastPage = true;
            } else {
                url = siteURL + nextPage.attr("href").substring(1);
            }
        }
        return ids;
    }

    private boolean isWine(String name) {
        final String isWineRegex = "^(Вино|Винный|Шампанское).*";
        return name.matches(isWineRegex);
    }
}
