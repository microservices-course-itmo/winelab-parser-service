package com.wine.to.up.winelab.parser.service.job;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import com.wine.to.up.winelab.parser.service.services.UpdateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

class ParseJobTest {
    ParserService mockedParserService;
    ParserService parserService;
    UpdateService mockedUpdateService;
    ThreadPoolTaskScheduler taskScheduler;
    WineLabParserMetricsCollector metricsCollector;
    WineRepository repository;
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void init() {
        metricsCollector = Mockito.mock(WineLabParserMetricsCollector.class);
        repository = Mockito.mock(WineRepository.class);
        parserService = new ParserService(metricsCollector, repository);
        mockedParserService = Mockito.mock(ParserService.class);
        mockedUpdateService = Mockito.mock(UpdateService.class);
        taskScheduler = Mockito.mock(ThreadPoolTaskScheduler.class);
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
        Logger logger = (Logger) LoggerFactory.getLogger(UpdateService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testParseJobDoesntThrow() {
        Mockito.when(mockedParserService.parseCatalogs()).thenReturn(Map.of());
        ParseJob job = new ParseJob(mockedParserService, mockedUpdateService, taskScheduler);
        Assertions.assertDoesNotThrow(job::setPeriodicCatalogUpdateJob);
        List<ILoggingEvent> logsList = listAppender.list;
        Assertions.assertFalse(logsList.stream().anyMatch(it -> it.getLevel() == Level.ERROR));
    }
}