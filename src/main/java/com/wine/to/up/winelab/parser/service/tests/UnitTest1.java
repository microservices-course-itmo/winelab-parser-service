package com.wine.to.up.winelab.parser.service.tests;

import org.junit.Test;
import org.junit.Assert;

public class UnitTest1 {
    @Test
    public static void testParser(String[] args) {

        Wine testWine = ParserService.parseProduct("1009581");

        assertNotNull(testWine.name);           //test the fields are not null/null (depends on the field)
        assertNull(testWine.site);
        assertNotNull(testWine.link);
        assertNotNull(testWine.image);
        assertNotNull(testWine.manufacturer);
        assertNotNull(testWine.brand);
        assertNotNull(testWine.country);
        assertNull(testWine.region);
        assertNotNull(testWine.description);

    }
}