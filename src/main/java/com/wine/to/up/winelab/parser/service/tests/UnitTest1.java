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


            Assert.assertNotNull(parserService.parseProduct(1009581).name);           //test the fields are not null/null (depends on the field)
            Assert.assertNull(parserService.parseProduct(1009581).site);
            Assert.assertNotNull(parserService.parseProduct(1009581).link);
            Assert.assertNotNull(parserService.parseProduct(1009581).image);
            Assert.assertNotNull(parserService.parseProduct(1009581).manufacturer);
            Assert.assertNotNull(parserService.parseProduct(1009581).brand);
            Assert.assertNotNull(parserService.parseProduct(1009581).country);
            Assert.assertNull(parserService.parseProduct(1009581).region);
            Assert.assertNotNull(parserService.parseProduct(1009581).description);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}