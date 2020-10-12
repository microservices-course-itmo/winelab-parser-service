package com.wine.to.up.winelab.parser.service.tests;

import org.junit.Test;
import org.junit.Assert;

public class UnitTest2 {
    @Test
    public static void testParser(String[] args) {

        Wine testWine = ParserService.parseProduct("1009581");

        assertEquals(testWine.name) = "Вино Berton Foundstone Shiraz красное сухое 0,75 л";             //test the fields are being parsed correctly
        assertEquals(testWine.site) = null;
        assertEquals(testWine.oldPrice) = 750;
        assertEquals(testWine.link) = "https://www.winelab.ru/product/1009581";
        assertEquals(testWine.newPrice) = 675;
        assertEquals(testWine.image) = "https://www.winelab.ru/medias/1009583.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0MDQwN3xpbWFnZS9wbmd8aW1hZ2VzL2g0Ny9oZmUvODgzMjYxODEzNTU4Mi5wbmd8NzJlOWYzYjJkMjNiNjc2ZTMzMDNiMjMyNTUxZWJkYWJlODBkYWZlMDI1MjAzMDE2NTYyMTAyYTU3ODVlNThkOQ";
        assertEquals(testWine.manufacturer) = "Berton Vineyards";
        assertEquals(testWine.brand) = "Berton Vinyard Reserve";
        assertEquals(testWine.country) = "Австралия";
        assertEquals(testWine.region) = null;
        assertEquals(testWine.volume) = 0.75;
        assertEquals(testWine.alcoholPercentage) = 13;
        assertEquals(testWine.description) = "Отличается непревзойдённым сортовым ароматом с гармонично переплетающимися тонами миндаля, ванили и спелой вишни. Вкус бархатистый, с мягкой танинностью., gastronomy=Отлично подходит к мясу, приготовленному на открытом огне, а также копчёностям и колбасам. Температура подачи 16–18°С)";


    }
}