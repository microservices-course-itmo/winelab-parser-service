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

    /* TODO
        parse region(?), maybe parse bigger version of image instead
     */
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
        final String cardSelector = "div.container div.row.filtered_items_row.js-infinite-scroll a.product_card.js-product-click";
        final String cardCountrySelector = "div.country_wrapper h3";
        final String gastronomySelector = "div.product_description_card:contains(Рекомендуемое употребление) p";
        final String descriptionSelector = "div.product_description_card:contains(Электронный сомелье) p";

        final String filterSelectorStart = "div.filter_block__container.js-facet.js-facet-values div[data-code=";
        final String filterSelectorEnd = "] span.text";
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

        wine.setLink(productURL);

        Element img = document.selectFirst(imageSelector);
        String image = protocol + siteURL + img.attr("src");
        wine.setImage(image);

        if (isSparkling(name)) {
            wine.setSparkling(true);
        }

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

        String gastronomy = document.selectFirst(gastronomySelector).html();
        wine.setGastronomy(gastronomy);

        String description = document.selectFirst(descriptionSelector).html();
        wine.setDescription(description);

        document = Jsoup.connect(searchURL).get();

        Elements cards = document.select(cardSelector);
        if (cards.size() == 1) {
            Element colorSpan = document.selectFirst(filterSelectorStart + colorSelector + filterSelectorEnd);
            if (colorSpan != null) {
                String colorText = colorSpan.html();
                Color color = Color.fromString(colorText);
                if(color != null) {
                    wine.setColor(color);
                }
            }

            Element sugarSpan = document.selectFirst(filterSelectorStart + sugarSelector + filterSelectorEnd);
            if (sugarSpan != null) {
                String sugarText = sugarSpan.html();
                Sugar sugar = Sugar.fromString(sugarText);
                if(sugar != null) {
                    wine.setSugar(sugar);
                }
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
            for (Element category : categories) {
                if (category.html().equals(sparklingCategory)) {
                    wine.setSparkling(true);
                    break;
                }
            }

            if (wine.getCountry() == null) {
                Element countryWrapper = document.selectFirst(cardCountrySelector);
                if (countryWrapper != null) {
                    wine.setCountry(countryWrapper.html());
                }
            }
        }

        return wine;
    }

    public List<Integer> parseHome() throws IOException {
        final String cardSelector = "a.product_card.js-product-click";
        final String idSelector = "data-id";
        final String url = protocol + siteURL;

        Document document = Jsoup.connect(url).get();

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
            for (Element card : productCards) {
                String name = card.select(nameSelector).last().html();
                if (isWine(name)) {
                    ids.add(Integer.parseInt(card.attr(idSelector)));
                }
            }

            Element nextPage = document.select(nextPageSelector).first();
            if (nextPage == null) {
                isLastPage = true;
            } else {
                url = protocol + siteURL + nextPage.attr("href");
            }
        }
        return ids;
    }

    private boolean isWine(String name) {
        final String isWinePattern = ".*(вино|винный|шампанское|портвейн|глинтвейн|вермут|кагор).*";
        return name.toLowerCase().matches(isWinePattern);
    }

    private boolean isSparkling(String name) {
        final String isSparklingPattern = ".*(игрист(ый|ое)|шампанское).*";
        return name.toLowerCase().matches(isSparklingPattern);
    }
}
