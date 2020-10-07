package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.winelab.parser.service.dto.Wine;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class ParserService {
    @Value("${parser.siteURL}")
    private String siteURL;

    public ParserService() {
    }

    public Wine parseProduct(int productID) {
        final String url = siteURL + "product/" + productID;
        final String patternVolumeInMilliliters = "\\d+ ?([мМ][лЛ])";
        final String patternVolumeInLiters = "\\d*([.,]\\d+)? ?[лЛ]";
        final String patternAlcoholPercentage = "\\d{0,2}([.,]\\d+)? ?%";
        final int millilitersInLiter = 1000;
        final int centsInRubles = 100;
        final String descriptionSelector = "div.product_description div.description";
        final String tagSelector = "div.product_description div.filters > span";
        final String priceSelector = "div.product_description div.prices_main";
        final String discountPriceSelector = "div.prices_cart_price";

        Wine wine = new Wine();
        try {
            Document document = Jsoup.connect(url).get();

            String description = document.select(descriptionSelector).first().ownText();

            // todo parse brand from description
            wine.setBrand(description);

            for (Element filter : document.select(tagSelector)) {
                String tag = filter.ownText();
                int volume;
                BigDecimal alcoholPercentage;

                if (tag.matches(patternVolumeInMilliliters)) {
                    tag = tag.replaceAll("[ мМлЛ]", "");
                    volume = Integer.parseInt(tag);
                    wine.setVolume(BigDecimal.valueOf(volume));
                } else if (tag.matches(patternVolumeInLiters)) {
                    tag = tag.replaceAll("[ лЛ]", "").replaceAll(",", ".");
                    volume = (int) (Double.parseDouble(tag) * millilitersInLiter);
                    wine.setVolume(BigDecimal.valueOf(volume));
                } else if (tag.matches(patternAlcoholPercentage)) {
                    tag = tag.replaceAll("[ %]", "").replaceAll(",", ".");
                    alcoholPercentage = new BigDecimal(Double.parseDouble(tag));
                    wine.setAlcoholPercentage(alcoholPercentage);
                }
            }

            int oldPrice = Integer.parseInt(document.select(priceSelector).first().ownText().replaceAll(" ", "")) * 100;
            wine.setPrice(oldPrice);

            Map<Integer, Integer> prices = new HashMap<>();
            for (Element element : document.select(discountPriceSelector)) {
                int quantity = Integer.parseInt(element.select("span").get(0).html().replaceAll("[x шт]", ""));
                int price = (int) (Double.parseDouble(element.select("span").get(1).ownText().replaceAll(" ", "")) * 100);
                prices.put(quantity, price);
                if (quantity == 1) {
                    wine.setPrice(price);
                }
            }

            return wine;
        } catch (IOException ex) {
            return null;
        }
    }

    public List<Integer> parseHome() {
        final String cardSelector = "a.product_card.js-product-click";
        final String idSelector = "data-id";

        try {
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
        } catch (IOException ex) {
            return null;
        }
    }

    public List<Integer> parseCatalog() {
        final String cardSelector = "div.container a.product_card";
        final String idSelector = "data-id";
        final String nextPageSelector = "ul.pagination li.page-item a[rel=next]";
        final String startPage = "catalog/vino";

        String url = siteURL + startPage;
        boolean isLastPage = false;
        List<Integer> ids = new ArrayList<>();

        while (!isLastPage) {
            try {
                Document document = Jsoup.connect(url).get();

                Elements productCards = document.select(cardSelector);
                for (Element element : productCards) {
                    ids.add(Integer.parseInt(element.attr(idSelector)));
                }

                Element nextPage = document.select(nextPageSelector).first();
                if (nextPage == null) {
                    isLastPage = true;
                } else {
                    url = siteURL + nextPage.attr("href").substring(1);
                }

            } catch (IOException ex) {
                isLastPage = true;
            }
        }
        return ids;
    }
}
