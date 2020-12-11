package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

class WineTest {
    ParserService parserService;
    ParserService mockedParserService;
    WineLabParserMetricsCollector metricsCollector;

    @BeforeEach
    public void init() {
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        parserService = new ParserService(metricsCollector);
        mockedParserService = Mockito.mock(ParserService.class);
        EventLogger eventLoggerMock = Mockito.mock(EventLogger.class);
        ReflectionTestUtils.setField(parserService, "SITE_URL", "winelab.ru");
        ReflectionTestUtils.setField(parserService, "PROTOCOL", "https://");
        ReflectionTestUtils.setField(parserService, "COOKIES", Map.of("currentPos", "S734", "currentRegion", "RU-SPE"));
        ReflectionTestUtils.setField(parserService, "CATALOGS", Map.of("wine", "vino", "sparkling", "shampanskie-i-igristye-vina"));
        ReflectionTestUtils.setField(parserService, "ID_SELECTOR", "data-id");
        ReflectionTestUtils.setField(parserService, "CARD_SELECTOR", "div.container a.product_card");
        ReflectionTestUtils.setField(parserService, "NEXT_PAGE_SELECTOR", "ul.pagination li.page-item a[rel=next]");
        ReflectionTestUtils.setField(parserService, "CATALOG_NAME_SELECTOR", "div.product_card--header div");
        ReflectionTestUtils.setField(parserService, "CATALOG_START_URL", "https://winelab.ru/catalog/%s;");
        ReflectionTestUtils.setField(parserService, "CATALOG_NEXT_URL", "https://winelab.ru/%s");
        ReflectionTestUtils.setField(parserService, "CATALOG_PAGE_URL", "https://winelab.ru/catalog/%s?page=%d&sort=relevance");
        ReflectionTestUtils.setField(parserService, "PRODUCT_NAME_SELECTOR", "div.product_description div.description");
        ReflectionTestUtils.setField(parserService, "PRODUCT_DETAILS_SELECTOR", "div.container div.row.product-detail-page.product_card_row.js-add-recent-list");
        ReflectionTestUtils.setField(parserService, "BRAND_SELECTOR", "data-brand");
        ReflectionTestUtils.setField(parserService, "PRODUCT_TAG_SELECTOR", "div.product_description div.filters > span");
        ReflectionTestUtils.setField(parserService, "IMAGE_SELECTOR", "div.image-zoom.js-zoom-product img");
        ReflectionTestUtils.setField(parserService, "CARD_COUNTRY_SELECTOR", "div.product_description div.description");
        ReflectionTestUtils.setField(parserService, "NEW_PRICE_SELECTOR", "data-price");
        ReflectionTestUtils.setField(parserService, "OLD_PRICE_SELECTOR", "div.product_description div.prices_main");
        ReflectionTestUtils.setField(parserService, "GASTRONOMY_SELECTOR", "div.product_description_card:contains(Рекомендуемое употребление) p");
        ReflectionTestUtils.setField(parserService, "DESCRIPTION_SELECTOR", "div.product_description_card:contains(Электронный сомелье) p");
        ReflectionTestUtils.setField(parserService, "REGION_SELECTOR", "data-category");
        ReflectionTestUtils.setField(parserService, "SPARKLING_CATEGORY", "Шампанские и игристые вина");
        ReflectionTestUtils.setField(parserService, "PRODUCT_PAGE_URL", "https://winelab.ru/product/%d");
        ReflectionTestUtils.setField(parserService, "FILTER_SELECTOR", "div.filter_block__container.js-facet.js-facet-values div[data-code=%s] div.filter_button span");
        ReflectionTestUtils.setField(parserService, "COLOR_SELECTOR", "Color");
        ReflectionTestUtils.setField(parserService, "SUGAR_SELECTOR", "SugarAmount");
        ReflectionTestUtils.setField(parserService, "COUNTRY_SELECTOR", "countryfiltr");
        ReflectionTestUtils.setField(parserService, "GRAPE_SELECTOR", "Sort");
        ReflectionTestUtils.setField(parserService, "MANUFACTURER_SELECTOR", "manufacture");
        ReflectionTestUtils.setField(parserService, "CATEGORY_SELECTOR", "category");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_BASE", "https://winelab.ru/search?q=%d%%3Arelevance");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_BRAND", "%%3Abrands%%3A%s");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_ALCOHOL", "%%3AAlcoholContent%%3A%%255B%.1f%%2BTO%%2B%.1f%%255De");
        ReflectionTestUtils.setField(parserService, "SEARCH_QUERY_PRICE", "%%3Aprice%%3A%%5B%.0f%%20TO%%20%.0f%%5D");
        ReflectionTestUtils.setField(parserService, "WINES", new String[] {"вино","винный","шампанское","портвейн","глинтвейн","вермут","кагор","сангрия"});
        ReflectionTestUtils.setField(parserService, "SPARKLINGS", new String[] {"игрист","шампанское"});
        ReflectionTestUtils.setField(parserService, "REGIONS", new String[] {"бордо","венето","тоскана","риоха","кастилья ла манча","бургундия","долина луары",
                "кампо де борха","риберо дель дуэро","пьемонт","долина роны","сицилия","другие регионы"});
        ReflectionTestUtils.setField(parserService, "COUNTRY_FIX", Map.of(
                "Российская Федерация","Россия",
                "Южная Африка","ЮАР",
                "Соединенные Штаты Америки","США",
                "Соед. Королев.","Великобритания"));
        ReflectionTestUtils.setField(parserService, "COLORS", Map.of(
                "красное", ParserApi.Wine.Color.RED,
                "розовое", ParserApi.Wine.Color.ROSE,
                "белое", ParserApi.Wine.Color.WHITE));
        ReflectionTestUtils.setField(parserService, "SUGARS", Map.of(
                "брют", ParserApi.Wine.Sugar.DRY,
                "сухое",ParserApi.Wine.Sugar.DRY,
                "полусухое",ParserApi.Wine.Sugar.MEDIUM_DRY,
                "полусладкое",ParserApi.Wine.Sugar.MEDIUM,
                "сладкое",ParserApi.Wine.Sugar.SWEET));
        ReflectionTestUtils.setField(parserService, "PATTERN_VOLUME", "\\d+([,.]\\d+)? [Лл]");
        ReflectionTestUtils.setField(parserService, "PATTERN_ALCOHOL", "\\d{0,2}(.\\d+)? %");
        ReflectionTestUtils.setField(parserService, "MAX_RETRIES", 3);
        ReflectionTestUtils.setField(parserService, "eventLogger", eventLoggerMock);
    }

    @Test
    void testMappedValuesNotNull() {
        Wine wine = parserService.parseProduct(1014769);
        ParserApi.Wine apiWine = wine.toParserWine();

        Assertions.assertNotNull(apiWine.getName());           //test the fields are not null/null (depends on the field)
        Assertions.assertNotNull(apiWine.getLink());
        Assertions.assertNotNull(apiWine.getImage());
        Assertions.assertNotNull(apiWine.getManufacturer());
        Assertions.assertNotNull(apiWine.getBrand());
        Assertions.assertNotNull(apiWine.getCountry());
        Assertions.assertNotNull(apiWine.getColor());
        Assertions.assertNotNull(apiWine.getSugar());
        Assertions.assertNotNull(apiWine.getRegion(0));
        Assertions.assertNotNull(apiWine.getGrapeSort(0));
        Assertions.assertNotNull(apiWine.getGastronomy());
        Assertions.assertNotNull(apiWine.getDescription());

    }

    @Test
    void testParsedValuesEqualExpected() {
        Wine wine = parserService.parseProduct(1014769);
        ParserApi.Wine apiWine = wine.toParserWine();

        Assertions.assertEquals("Вино Saga Domaine Barons de Rothschild Bordeaux красное сухое 0,75 л", apiWine.getName());             //test the fields are being mapped correctly
        Assertions.assertNotEquals(0.0f, apiWine.getOldPrice());
        Assertions.assertEquals("https://winelab.ru/product/1014769", apiWine.getLink());
        Assertions.assertNotEquals(0.0f, apiWine.getNewPrice());
        Assertions.assertEquals("https://winelab.ru/medias/1014769.png-300Wx300H?context=bWFzdGVyfGltYWdlc3wzMzQwMXxpbWFnZS9wbmd8aW1hZ2VzL2g4NC9oZWMvODgzMjY0NjkzODY1NC5wbmd8OTk3MDg5NjdlMTk4NzlhNWM2MWQ0YzBiZGNhZmFmNGM3ZDViYmU1NWJmMzgyNDUwNWY0ZmRiYjczODdmOTJhOA", apiWine.getImage());
        Assertions.assertEquals("Domaine Barons de Rothschild", apiWine.getManufacturer());
        Assertions.assertEquals("SAGA", apiWine.getBrand());
        Assertions.assertEquals("Франция", apiWine.getCountry());
        Assertions.assertEquals(0.75f, apiWine.getCapacity());
        Assertions.assertEquals(ParserApi.Wine.Color.RED, apiWine.getColor());
        Assertions.assertEquals(ParserApi.Wine.Sugar.DRY, apiWine.getSugar());
        Assertions.assertEquals("Каберне Совиньон", apiWine.getGrapeSort(0));
        Assertions.assertEquals("Сага Бордо Руж – это вино на каждый день. Оно включает в себя традиционные сорта винограда: Каберне Совиньон, Мерло, которые смешиваются в разных пропорциях (в зависимости от урожая). Доминирующий Каберне Совиньон придает вину классическую элегантность, тогда как стиль DBR (Lafite) способствует созданию мягкого, нежного вкуса. Сорт: 60% Каберне Совиньон, 40% Мерло Время выдержки в дубовых бочках : 40% вина, 9 месяцев в нержавеющих емкостях Цвет: Насыщенный пурпурный. Аромат: Нежный и выразительный, с тонами черных ягод (ежевики и черешни) на фоне ванильных и жареных ноток. Вкус: Сочное и мягкое на вкус, с обильной танинной структурой и продолжительным ягодным послевкусием, отмеченным лакричным привкусом.", apiWine.getDescription());
        Assertions.assertEquals("Вино прекрасно сочетается с блюдами из красного мяса. Декантирование Примерно за 1 час Температура подачи: 16-18 °C", apiWine.getGastronomy());
    }


}