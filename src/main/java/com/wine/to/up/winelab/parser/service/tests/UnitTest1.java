package com.wine.to.up.winelab.parser.service.tests;

import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;

public class UnitTest1 {
    @Test
    public static void testParser(String[] args) {
        try {
            ParserService parserService = new ParserService();


            Assert.assertNotNull(parserService.parseProduct(1009581).getName());           //test the fields are not null/null (depends on the field)
            Assert.assertNull(parserService.parseProduct(1009581).getSite());
            Assert.assertNotNull(parserService.parseProduct(1009581).getLink());
            Assert.assertNotNull(parserService.parseProduct(1009581).getImage());
            Assert.assertNotNull(parserService.parseProduct(1009581).getManufacturer());
            Assert.assertNotNull(parserService.parseProduct(1009581).getBrand());
            Assert.assertNotNull(parserService.parseProduct(1009581).getCountry());
            Assert.assertNull(parserService.parseProduct(1009581).getRegion());
            Assert.assertNotNull(parserService.parseProduct(1009581).getDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}