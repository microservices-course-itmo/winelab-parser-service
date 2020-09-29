package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.domain.entity.Message;
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
    public void parseProductPage(@RequestParam Integer productID) {

        String url = "https://www.winelab.ru/product/" + productID;

        try {
            Document document = Jsoup.connect(url).get();

            final String patternVolumeInMilliliters = "(?i)\\d*([.,]\\d+)? ?([мМ][лЛ])";
            final String patternVolumeInLiters = "(?i)\\d*([.,]\\d+)? ?[лЛ]";
            final String patternAlcoholPercentage = "\\d{0,2}([.,]\\d+)? ?%";
            final int millilitersInLiter = 1000;
            final int centsInRubles = 100;

            String description = document.select("div.product_description div.description").first().ownText();

            System.out.println("Description: " + description);

            for (Element filter: document.select("div.product_description div.filters > span")){
                String tag = filter.ownText();
                Integer volume;
                BigDecimal alcoholPerecentage;

                if (tag.matches(patternVolumeInMilliliters)){
                    tag = tag.replaceAll("[ мМлЛ]", "").replaceAll(",", ".");
                    volume = (int) (Double.parseDouble(tag));
                    log.info("Volume: " + volume + " ml");
                }
                if (tag.matches(patternVolumeInLiters)){
                    tag = tag.replaceAll("[ лЛ]", "").replaceAll(",", ".");
                    volume = (int) (Double.parseDouble(tag) * millilitersInLiter);
                    log.info("Volume: " + volume + " ml");
                }
                else if (tag.matches(patternAlcoholPercentage)){
                    tag = tag.replaceAll("[ %]", "").replaceAll(",", ".");
                    alcoholPerecentage = new BigDecimal(Double.parseDouble(tag));
                    log.info("Alcohol percentage: " + alcoholPerecentage + "%");
                }
                else{
                    log.info("Tag: " + tag);
                }
            }

            String oldPrice = document.select("div.product_description div.prices_main").first().ownText();

            log.info("Old price: " + oldPrice);

            Map<Integer, Integer> prices = new HashMap<>();
            for(Element element: document.select("div.prices_cart_price")){
                Integer quantity = Integer.parseInt(element.select("span").get(0).html().replaceAll("[x шт]", ""));
                Integer price = (int) (Double.parseDouble(element.select("span").get(1).ownText().replaceAll(" ", "")) * 100);
                prices.put(quantity, price);
                log.info("Price for " + quantity + " item(s): " + price + " cents");
            }

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @GetMapping("/home")
    public void parseHomePage() {

        String url = "https://www.winelab.ru/";

        try {
            Document document = Jsoup.connect(url).get();

            Elements elementsWithId = document.select("a.product_card.js-product-click");
            List<Integer> ids = new ArrayList<Integer>();
            for(Element item: elementsWithId) {
                String idStr = item.attr("data-id");
                Integer id = Integer.parseInt(idStr);
                if(ids.contains(id)) continue;
                ids.add(id);
            }

            for(Integer id: ids) {
                this.parseProductPage(id);
            }

        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

}