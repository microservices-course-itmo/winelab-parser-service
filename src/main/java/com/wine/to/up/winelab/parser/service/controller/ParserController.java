package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.domain.entity.Message;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repository.MessageRepository;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parser")
@Slf4j
public class ParserController {

    @Autowired
    MessageRepository messageRepository;

    @GetMapping
    public void parseProductPage(@RequestParam int productID) {

        final String url = "https://www.winelab.ru/product/" + productID;

        Wine resultWine = new Wine();
        try {
            Document document = Jsoup.connect(url).get();

            final String patternVolumeInMilliliters = "\\d+ ?([мМ][лЛ])";
            final String patternVolumeInLiters = "\\d*([.,]\\d+)? ?[лЛ]";
            final String patternAlcoholPercentage = "\\d{0,2}([.,]\\d+)? ?%";
            final int millilitersInLiter = 1000;
            final int centsInRubles = 100;

            String description = document.select("div.product_description div.description").first().ownText();

            log.info("Description: " + description);

            // todo parse brand from description
            resultWine.setBrand(description);

            for (Element filter: document.select("div.product_description div.filters > span")){
                String tag = filter.ownText();
                int volume;
                BigDecimal alcoholPercentage;

                if (tag.matches(patternVolumeInMilliliters)){
                    tag = tag.replaceAll("[ мМлЛ]", "");
                    volume = Integer.parseInt(tag);
                    log.info("Volume: " + volume + " ml");
                    resultWine.setVolume(BigDecimal.valueOf(volume));
                }
                else if (tag.matches(patternVolumeInLiters)){
                    tag = tag.replaceAll("[ лЛ]", "").replaceAll(",", ".");
                    volume = (int) (Double.parseDouble(tag) * millilitersInLiter);
                    log.info("Volume: " + volume + " ml");
                    resultWine.setVolume(BigDecimal.valueOf(volume));
                }
                else if (tag.matches(patternAlcoholPercentage)){
                    tag = tag.replaceAll("[ %]", "").replaceAll(",", ".");
                    alcoholPercentage = new BigDecimal(Double.parseDouble(tag));
                    log.info("Alcohol percentage: " + alcoholPercentage + "%");
                    resultWine.setAlcoholPercentage(alcoholPercentage);
                }
                else{
                    log.info("Tag: " + tag);
                }
            }

            int oldPrice = Integer.parseInt(document.select("div.product_description div.prices_main").first().ownText().replaceAll(" ", "")) * 100;
            resultWine.setPrice(oldPrice);

            log.info("Old price: " + oldPrice + " cents");

            Map<Integer, Integer> prices = new HashMap<>();
            for (Element element: document.select("div.prices_cart_price")){
                int quantity = Integer.parseInt(element.select("span").get(0).html().replaceAll("[x шт]", ""));
                int price = (int) (Double.parseDouble(element.select("span").get(1).ownText().replaceAll(" ", "")) * 100);
                prices.put(quantity, price);
                log.info("Price for " + quantity + " item(s): " + price + " cents");
                if(quantity == 1) {
                    resultWine.setPrice(price);
                }
            }

        }
        catch (IOException ex){
            log.info("Error: couldn't load the web page.");
        }

        log.info("result: " + resultWine);
    }

    @GetMapping("/home")
    public void parseHomePage() {

        final String url = "https://www.winelab.ru/";

        try {
            Document document = Jsoup.connect(url).get();

            Elements elementsWithId = document.select("a.product_card.js-product-click");
            List<Integer> ids = new ArrayList<>();
            for(Element item: elementsWithId) {
                String idStr = item.attr("data-id");
                int id = Integer.parseInt(idStr);
                if(ids.contains(id)) continue;
                ids.add(id);
            }

            for(int id: ids) {
                this.parseProductPage(id);
            }

        }
        catch (IOException ex){
            log.info("Error: couldn't load the web page.");
        }
    }

    @GetMapping("/catalog")
    public void parseCatalog() {

        log.info("Started parsing");

        String url = "https://www.winelab.ru/catalog/vino";
        boolean isLastPage = false;
        List<Integer> ids = new ArrayList<>();

        while (!isLastPage) {
            try {
                Document document = Jsoup.connect(url).get();

                Elements productCards = document.select("div.container a.product_card");
                for (Element element: productCards){
                    ids.add(Integer.parseInt(element.attr("data-id")));
                }

                Element nextPage = document.select("ul.pagination li.page-item a[rel=next]").first();
                if (nextPage == null) {
                    isLastPage = true;
                }
                else {
                    url = "https://www.winelab.ru" + nextPage.attr("href");
                }

            } catch (IOException ex) {
                isLastPage = true;
                log.info("Error: couldn't load the web page.");
            }

        }

        log.info(ids.toString());
    }
}