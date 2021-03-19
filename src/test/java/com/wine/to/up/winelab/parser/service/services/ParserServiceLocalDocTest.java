package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ParserServiceLocalDocTest {
    ParserService parserService;
    ParserService mockedParserService;
    WineLabParserMetricsCollector metricsCollector;
    WineRepository repository;
    private static final String wineToStrReference = "Wine(name=Вино Berton Foundstone Shiraz красное сухое 0,75 л, link=https://winelab.ru/product/1009581, oldPrice=750, newPrice=479.0, image=https://winelab.ru/medias/1009581.png-300Wx300H?context=bWFzdGVyfGltYWdlc3w0NTc2NXxpbWFnZS9wbmd8aW1hZ2VzL2hjOC9oMDcvODgzMjYxNzQ4MDIyMi5wbmd8NGUxN2NiMzk2YjUxOTVmOTBhOTcwMTAwY2I1YjljZWZhMTViY2ViODIzZTczYzgxYWE3YzlmYzEzZmVkMmM5ZQ, manufacturer=Berton Vineyards, brand=Berton Vinyard Foundstone, country=Австралия, region=null, volume=0.75, alcoholContent=13, sparkling=false, color=RED, sugar=DRY, grapeSort=Шираз, description=Регион: Юго-Восточная Австралия. Сорт винограда: 100% Шираз. Выдержка: чаны из нержавеющей стали. Цвет: насыщенный пурпурный с фиолетовым оттенком. Аромат: насыщенный выразительный с яркими нотами специй, спелой ежевики, сливы и легкими сладковатыми оттенками дуба, кофе, ванили и карамели. Вкус: полнотелый насыщенный с умеренно терпкими приятными шелковистыми танинами и оттенками ежевики, черешни, сливы и длительным послевкусием., gastronomy=Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С)";
    private static final String gastronomyReference = "Гастрономическое сочетание: стейк из говядины прожарки medium, свинина на косточке, твердые сыры, хамон, колбасы. Температура подачи: 14-16° С";

    @BeforeEach
    void init() {
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        parserService = new ParserServiceStub(metricsCollector);
        mockedParserService = Mockito.mock(ParserService.class);
        repository = Mockito.mock(WineRepository.class);
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
        ReflectionTestUtils.setField(parserService, "PRODUCT_TAG_SELECTOR", "div.product_description div.filters > a");
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
        ReflectionTestUtils.setField(parserService, "ALCOHOL_SELECTOR", "AlcoholContent");
        ReflectionTestUtils.setField(parserService, "VOLUME_SELECTOR", "Capacity");
        ReflectionTestUtils.setField(parserService, "CATEGORY_SELECTOR", "category");
        ReflectionTestUtils.setField(parserService, "IN_STOCK_SELECTOR", "div.product__page_prices_status.red");
        ReflectionTestUtils.setField(parserService, "WINES", new String[] {"вино","винный","шампанское","портвейн","глинтвейн","вермут","кагор","сангрия"});
        ReflectionTestUtils.setField(parserService, "SPARKLINGS", new String[] {"игрист","шампанское"});
        ReflectionTestUtils.setField(parserService, "REGIONS", new String[] {"бордо","венето","тоскана","риоха","кастилья ла манча","бургундия","долина луары",
                "кампо де борха","риберо дель дуэро","пьемонт","долина роны","сицилия","другие регионы"});
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
        ReflectionTestUtils.setField(parserService, "MAX_RETRIES", 3);
        ReflectionTestUtils.setField(parserService, "eventLogger", eventLoggerMock);
    }

    @Test
    void testParsedValuesEqualExpectedLocal() {
        Wine wine = parserService.parseProduct(1014769);
        ParserApi.Wine apiWine = wine.toParserWine();

        Assertions.assertEquals("Вино Saga Domaine Barons de Rothschild Bordeaux красное сухое 0,75 л", apiWine.getName());             //test the fields are being mapped correctly
        Assertions.assertEquals(1243.0f, apiWine.getOldPrice());
        Assertions.assertEquals("https://winelab.ru/product/1014769", apiWine.getLink());
        Assertions.assertEquals(599.0f, apiWine.getNewPrice());
        Assertions.assertEquals("https://jmrkpxyvei.a.trbcdn.net/medias/1014769.png-300Wx300H?context=bWFzdGVyfGltYWdlc3wzMzQwMXxpbWFnZS9wbmd8aW1hZ2VzL2g4NC9oZWMvODgzMjY0NjkzODY1NC5wbmd8OTk3MDg5NjdlMTk4NzlhNWM2MWQ0YzBiZGNhZmFmNGM3ZDViYmU1NWJmMzgyNDUwNWY0ZmRiYjczODdmOTJhOA", apiWine.getImage());
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

    private class ParserServiceStub extends ParserService {
        public ParserServiceStub(WineLabParserMetricsCollector metricsCollector) {
            super(metricsCollector, repository);
        }
        @Override
        protected Document getDocument(String url) throws IOException {
            StringBuffer localURLBuffer = new StringBuffer(url
                    .replaceFirst("https://winelab.ru/", "src/test/resources/pages/")
                    .replaceAll("\\?*", "")
                    .replaceAll(";", ""))
                    .append(".html");
            String localURL = localURLBuffer.toString();
            File localPage = new File(localURL);
            System.out.println();
            return Jsoup.parse(localPage, "UTF-8");
        }
    }
}
