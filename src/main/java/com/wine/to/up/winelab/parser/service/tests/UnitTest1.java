package com.wine.to.up.winelab.parser.service.tests;

import org.junit.Test;
import org.junit.Assert;

public class UnitTest1 {
    @Test
    public static void testParser(String[] args) {

        Wine testWine = ParserService.parseProduct("1009581");

        Assert.assertNotNull(testWine.name);           //test the fields are not null/null (depends on the field)
        Assert.assertNull(testWine.site);
        Assert.assertNotNull(testWine.link);
        Assert.assertNotNull(testWine.image);
        Assert.assertNotNull(testWine.manufacturer);
        Assert.assertNotNull(testWine.brand);
        Assert.assertNotNull(testWine.country);
        Assert.assertNull(testWine.region);
        Assert.assertNotNull(testWine.description);

    }
}