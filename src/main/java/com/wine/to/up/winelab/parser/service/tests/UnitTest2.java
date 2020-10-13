package com.wine.to.up.winelab.parser.service.tests;

import org.junit.Test;
import org.junit.Assert;

public class UnitTest2 {
    @Test
    public static void testParser(String[] args) {

        Wine testWine = ParserService.parseProduct("1009581");

        Assert.assertEquals(testWine.name) = "Вино Berton Foundstone Shiraz красное сухое 0,75 л";             //test the fields are being parsed correctly
        Assert.assertEquals(testWine.site) = null;
        Assert.assertEquals(testWine.oldPrice) = 750;
        Assert.assertEquals(testWine.link) = "https://www.winelab.ru/product/1009581";
        Assert.assertEquals(testWine.newPrice) = 675;
        Assert.assertEquals(testWine.image) = "https://www.winelab.ru/medias/1009583.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0MDQwN3xpbWFnZS9wbmd8aW1hZ2VzL2g0Ny9oZmUvODgzMjYxODEzNTU4Mi5wbmd8NzJlOWYzYjJkMjNiNjc2ZTMzMDNiMjMyNTUxZWJkYWJlODBkYWZlMDI1MjAzMDE2NTYyMTAyYTU3ODVlNThkOQ";
        Assert.assertEquals(testWine.manufacturer) = "Berton Vineyards";
        Assert.assertEquals(testWine.brand) = "Berton Vinyard Reserve";
        Assert.assertEquals(testWine.country) = "Австралия";
        Assert.assertEquals(testWine.region) = null;
        Assert.assertEquals(testWine.volume) = 0.75;
        Assert.assertEquals(testWine.alcoholPercentage) = 13;
        Assert.assertEquals(testWine.description) = "Отличается непревзойдённым сортовым ароматом с гармонично переплетающимися тонами миндаля, ванили и спелой вишни. Вкус бархатистый, с мягкой танинностью., gastronomy=Отлично подходит к мясу, приготовленному на открытом огне, а также копчёностям и колбасам. Температура подачи 16–18°С)";


    }
}