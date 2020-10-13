package com.wine.to.up.winelab.parser.service.tests;

import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.Test;
import org.junit.Assert;

public class UnitTest2 {
    @Test
    public static void testParser(String[] args) {

        ParserService parserService = new ParserService();
        parserService.parseProduct(1009581);


        Assert.assertEquals(parserService.parseProduct(1009581).name) = "Вино Berton Foundstone Shiraz красное сухое 0,75 л";             //test the fields are being parsed correctly
        Assert.assertEquals(parserService.parseProduct(1009581).site) = null;
        Assert.assertEquals(parserService.parseProduct(1009581).oldPrice) = 750;
        Assert.assertEquals(parserService.parseProduct(1009581).link) = "https://www.winelab.ru/product/1009581";
        Assert.assertEquals(parserService.parseProduct(1009581).newPrice) = 675;
        Assert.assertEquals(parserService.parseProduct(1009581).image) = "https://www.winelab.ru/medias/1009583.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0MDQwN3xpbWFnZS9wbmd8aW1hZ2VzL2g0Ny9oZmUvODgzMjYxODEzNTU4Mi5wbmd8NzJlOWYzYjJkMjNiNjc2ZTMzMDNiMjMyNTUxZWJkYWJlODBkYWZlMDI1MjAzMDE2NTYyMTAyYTU3ODVlNThkOQ";
        Assert.assertEquals(parserService.parseProduct(1009581).manufacturer) = "Berton Vineyards";
        Assert.assertEquals(parserService.parseProduct(1009581).brand) = "Berton Vinyard Reserve";
        Assert.assertEquals(parserService.parseProduct(1009581).country) = "Австралия";
        Assert.assertEquals(parserService.parseProduct(1009581).region) = null;
        Assert.assertEquals(parserService.parseProduct(1009581).volume) = 0.75;
        Assert.assertEquals(parserService.parseProduct(1009581).alcoholPercentage) = 13;
        Assert.assertEquals(parserService.parseProduct(1009581).description) = "Отличается непревзойдённым сортовым ароматом с гармонично переплетающимися тонами миндаля, ванили и спелой вишни. Вкус бархатистый, с мягкой танинностью., gastronomy=Отлично подходит к мясу, приготовленному на открытом огне, а также копчёностям и колбасам. Температура подачи 16–18°С)";


    }
}