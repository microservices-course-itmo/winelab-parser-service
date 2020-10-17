package com.wine.to.up.winelab.parser.service.tests;

import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.Test;
import org.junit.Assert;

public class UnitTest2 {
    @Test
    public void testParser() {
        try {

            ParserService parserService = new ParserService();


            Assert.assertEquals(parserService.parseProduct(1009581).getName(),  "Вино Berton Foundstone Shiraz красное сухое 0,75 л") ;             //test the fields are being parsed correctly
            Assert.assertEquals(parserService.parseProduct(1009581).getSite(), null);
            Assert.assertEquals(parserService.parseProduct(1009581).getOldPrice(), 750);
            Assert.assertEquals(parserService.parseProduct(1009581).getLink(), "https://www.winelab.ru/product/1009581");
            Assert.assertEquals(parserService.parseProduct(1009581).getNewPrice(), 675);
            Assert.assertEquals(parserService.parseProduct(1009581).getImage(),"https://www.winelab.ru/medias/1009583.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0MDQwN3xpbWFnZS9wbmd8aW1hZ2VzL2g0Ny9oZmUvODgzMjYxODEzNTU4Mi5wbmd8NzJlOWYzYjJkMjNiNjc2ZTMzMDNiMjMyNTUxZWJkYWJlODBkYWZlMDI1MjAzMDE2NTYyMTAyYTU3ODVlNThkOQ");
            Assert.assertEquals(parserService.parseProduct(1009581).getManufacturer(),"Berton Vineyards") ;
            Assert.assertEquals(parserService.parseProduct(1009581).getBrand(),"Berton Vinyard Reserve");
            Assert.assertEquals(parserService.parseProduct(1009581).getCountry(),"Австралия") ;
            Assert.assertEquals(parserService.parseProduct(1009581).getRegion(),null);
            Assert.assertEquals(parserService.parseProduct(1009581).getVolume(),0.75);
            Assert.assertEquals(parserService.parseProduct(1009581).getAlcoholPercentage(), 13);
            Assert.assertEquals(parserService.parseProduct(1009581).getDescription(),"Отличается непревзойдённым сортовым ароматом с гармонично переплетающимися тонами миндаля, ванили и спелой вишни. Вкус бархатистый, с мягкой танинностью., gastronomy=Отлично подходит к мясу, приготовленному на открытом огне, а также копчёностям и колбасам. Температура подачи 16–18°С)") ;
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
}