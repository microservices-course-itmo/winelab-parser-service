package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.dto.WineLocalInfo;
import com.wine.to.up.winelab.parser.service.logging.WineLabParserNotableEvents;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class containing methods for parsing product pages and catalog pages
 *
 * @author : Somov Artyom
 */
@Service
@Slf4j
@Configuration
@PropertySource(value = "classpath:parser.properties", encoding = "Windows-1251")
public class ParserService {
    @InjectEventLogger
    private EventLogger eventLogger;

    @Value("${parser.address}")
    private String SITE_URL;
    @Value("${parser.protocol}")
    private String PROTOCOL;
    @Value("${parser.cookie.key}")
    private String COOKIE_KEY;
    @Value("${parser.cookie.default.value}")
    private String COOKIE_DEFAULT_VALUE;
    @Value("#{${parser.catalogs}}")
    private Map<String, String> CATALOGS;
    @Value("${parser.retries}")
    private int MAX_RETRIES;
    @Value("${parser.catalog.wines.per.page}")
    private int WINES_PER_PAGE;

    @Value("${parser.selector.catalog.id}")
    private String ID_SELECTOR;
    @Value("${parser.selector.catalog.card}")
    private String CARD_SELECTOR;
    @Value("${parser.selector.catalog.nextpage}")
    private String NEXT_PAGE_SELECTOR;
    @Value("${parser.selector.catalog.name}")
    private String CATALOG_NAME_SELECTOR;
    @Value("${parser.catalog.address.start}")
    private String CATALOG_START_URL;
    @Value("${parser.catalog.address.next}")
    private String CATALOG_NEXT_URL;
    @Value("${parser.catalog.address.page}")
    private String CATALOG_PAGE_URL;
    @Value("${parser.selector.catalog.count}")
    private String WINE_COUNT_SELECTOR;

    @Value("${parser.selector.catalog.card.in.stock}")
    private String CARD_IN_STOCK_SELECTOR;
    @Value("${parser.selector.catalog.card.price.old}")
    private String CARD_OLD_PRICE_SELECTOR;

    @Value("${parser.selector.product.name}")
    private String PRODUCT_NAME_SELECTOR;
    @Value("${parser.selector.product.details}")
    private String PRODUCT_DETAILS_SELECTOR;
    @Value("${parser.selector.product.brand}")
    private String BRAND_SELECTOR;
    @Value("${parser.selector.product.tag}")
    private String PRODUCT_TAG_SELECTOR;
    @Value("${parser.selector.product.image}")
    private String IMAGE_SELECTOR;
    @Value("${parser.selector.catalog.card.country}")
    private String CARD_COUNTRY_SELECTOR;
    @Value("${parser.selector.product.price.new}")
    private String NEW_PRICE_SELECTOR;
    @Value("${parser.selector.product.price.old}")
    private String OLD_PRICE_SELECTOR;
    @Value("${parser.selector.product.gastronomy}")
    private String GASTRONOMY_SELECTOR;
    @Value("${parser.selector.product.description}")
    private String DESCRIPTION_SELECTOR;
    @Value("${parser.selector.product.region}")
    private String REGION_SELECTOR;
    @Value("${parser.product.sparkling}")
    private String SPARKLING_CATEGORY;
    @Value("${parser.selector.product.in.stock}")
    private String IN_STOCK_SELECTOR;

    @Value("${parser.product.address}")
    private String PRODUCT_PAGE_URL;
    @Value("#{${parser.catalogs}}")
    private Map<String, String> catalogs;

    @Value("${parser.selector.filter}")
    private String FILTER_SELECTOR;
    @Value("${parser.selector.filter.color}")
    private String COLOR_SELECTOR;
    @Value("${parser.selector.filter.sugar}")
    private String SUGAR_SELECTOR;
    @Value("${parser.selector.filter.country}")
    private String COUNTRY_SELECTOR;
    @Value("${parser.selector.filter.grape}")
    private String GRAPE_SELECTOR;
    @Value("${parser.selector.filter.manufacturer}")
    private String MANUFACTURER_SELECTOR;
    @Value("${parser.selector.filter.category}")
    private String CATEGORY_SELECTOR;
    @Value("${parser.selector.filter.alcohol}")
    private String ALCOHOL_SELECTOR;
    @Value("${parser.selector.filter.volume}")
    private String VOLUME_SELECTOR;

    @Value("${parser.list.wines}")
    private String[] WINES;
    @Value("${parser.list.sparkling}")
    private String[] SPARKLINGS;
    @Value("${parser.list.regions}")
    private String[] REGIONS;

    @Value("#{${parser.map.colors}}")
    private Map<String, ParserApi.Wine.Color> COLORS;
    @Value("#{${parser.map.sugars}}")
    private Map<String, ParserApi.Wine.Sugar> SUGARS;

    @Value("${parser.cities.default}")
    private String DEFAULT_CITY;
    @Value("#{${parser.cities}}")
    private Map<String, String> CITIES;


    private Map<String, Function<Wine, Object>> tagGetters;

    static final String IS_PARSING_PRODUCT = "product";
    static final String IS_PARSING_CATALOGS = "catalogs";
    static final String IS_PARSING_CATALOG = "catalog";
    static final String IS_PARSING_CATALOG_PAGE = "catalogPage";

    private static final String PARSING_IN_PROGRESS_GAUGE = "parsing_in_progress";
    private static final String PARSING_PROCESS_DURATION_SUMMARY = "parsing_process_duration";
    private static final String TIME_SINCE_LAST_SUCCEEDED_PARSING_GAUGE = "time_since_last_succeeded_parsing";

    private final AtomicLong lastSucceededParsingTime = new AtomicLong(0);

    private final WineLabParserMetricsCollector metricsCollector;
    private final WineRepository repository;

    public ParserService(WineLabParserMetricsCollector metricsCollector, WineRepository repository) {
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
        this.repository = repository;

        Metrics.gauge(
                TIME_SINCE_LAST_SUCCEEDED_PARSING_GAUGE,
                lastSucceededParsingTime,
                val -> val.get() == 0 ? Double.NaN : (System.currentTimeMillis() - val.get()) / 1000.0
        );
    }

    protected Document getDocument(String url, String cookieValue) throws IOException {

        Map<String, String> cookies = Map.of(COOKIE_KEY, cookieValue);
        for (int count = 0; count < MAX_RETRIES; count++) {
            try {
                Document document = Jsoup.connect(url).cookies(cookies).get();

                long fetchEnd = System.nanoTime();

                return document;
            } catch (IOException ex) {
                log.warn("Couldn't get page {} on try {} : {}", url, count + 1, ex);
            }
        }
        throw new IOException(String.format("Couldn't get page %s", url));
    }

    protected Document getDocument(String url) throws IOException {
        return getDocument(url, COOKIE_DEFAULT_VALUE);
    }

    private String getCatalogLinkByNumber(int number, int wineCount, int sparklingCount) throws IndexOutOfBoundsException {
        String catalogName;
        if (number <= wineCount) {
            catalogName = CATALOGS.get("wine");
        }
        else if (number <= wineCount + sparklingCount) {
            catalogName = CATALOGS.get("sparkling");
            number -= wineCount;
        }
        else {
            throw new IndexOutOfBoundsException("Catalog page number exceeds total catalog page count");
        }
        return String.format(CATALOG_PAGE_URL, catalogName, number);
    }

    public Wine parseProduct(int productID) {
        return parseProduct(productID, DEFAULT_CITY);
    }

    /**
     * Parsing wine from winelab web site by the id given
     *
     * @param productID a product id on winelab.ru of wine to be parsed
     * @param city      cookie value on website
     * @return parsed wine object
     */
    public Wine parseProduct(int productID, String city) {
        long parseStart = System.nanoTime();

        final String productURL = String.format(PRODUCT_PAGE_URL, productID);

        Document document;
        try {
            document = getDocument(productURL, city);
        } catch (IOException e) {
            log.error("Could not get product page during parsing of wine {}", productID);
            metricsCollector.winesParsedUnsuccessfully(1);
            return null;
        }
        long fetchEnd = System.nanoTime();
        metricsCollector.timeWineDetailsFetchingDuration(fetchEnd - parseStart);

        Wine wine = parseBasicProductInfo(productID, document);

        WineLocalInfo localInfo = getLocalInfo(document);
        wine.setOldPrice(localInfo.getOldPrice());
        wine.setNewPrice(localInfo.getNewPrice());
        if (wine.getNewPrice() == null) {
            eventLogger.warn(WineLabParserNotableEvents.W_WINE_DETAILS_PARSING_FAILED);
            log.warn("Wine {} will not be parsed because could not get price", productID);
            metricsCollector.winesParsedUnsuccessfully(1);
            return null;
        }

        metricsCollector.winesParsedSuccessfully(1);
        long parseEnd = System.nanoTime();
        metricsCollector.timeWineDetailsParsingDuration(parseEnd - parseStart);
        eventLogger.info(WineLabParserNotableEvents.I_WINE_DETAILS_PARSED);
        List<String> lackAttributes = wine.lackAttributes();
        if (!lackAttributes.isEmpty()) {
            lackAttributes.forEach(attribute -> eventLogger.info(WineLabParserNotableEvents.W_WINE_ATTRIBUTE_ABSENT, attribute, productURL));
        }
        return wine;
    }

    private Wine parseBasicProductInfo(int productID, Document document) {
        Wine wine = new Wine();

        wine.setId(productID);

        String name;
        try {
            name = document.selectFirst(PRODUCT_NAME_SELECTOR).ownText();
            wine.setName(name);
        } catch (NullPointerException ex) {
            eventLogger.warn(WineLabParserNotableEvents.W_WINE_DETAILS_PARSING_FAILED);
            log.warn("Wine {} will not be parsed because could not get name", productID);
            return null;
        }

        final String productURL = String.format(PRODUCT_PAGE_URL, productID);
        wine.setLink(productURL);

        Element details = document.selectFirst(PRODUCT_DETAILS_SELECTOR);

        String brand = details.attr(BRAND_SELECTOR);
        if (!brand.isEmpty()) {
            wine.setBrand(brand);
        }

        String region = details.attr(REGION_SELECTOR);
        if (isRegion(region)) {
            wine.setRegion(region);
        }

        Element img = document.selectFirst(IMAGE_SELECTOR);
        if (img != null) {
            String image = img.attr("src");
            wine.setImage(image);
        }

        if (isSparkling(name)) {
            wine.setSparkling(true);
        }

        Elements tags = document.select(PRODUCT_TAG_SELECTOR);
        fillTags(wine, tags);

        String gastronomy = document.selectFirst(GASTRONOMY_SELECTOR).html();
        wine.setGastronomy(gastronomy);

        String description = document.selectFirst(DESCRIPTION_SELECTOR).html();
        wine.setDescription(description);

        return wine;
    }

    /**
     * Parsing all wine-related catalogs from winelab web site
     *
     * @return map of parsed wines in format (product id, parsed wine object)
     */
    public Map<Integer, Wine> parseCatalogs() {
        metricsCollector.parsingStarted();
        try {
            Map<Integer, Wine> wines = new HashMap<>();
            for (String catalog : CATALOGS.values()) {
                parseCatalog(catalog, wines);
            }
            if (wines.size() > 0) {
                log.info("Parsing done! Total {} wines parsed", wines.size());
            } else {
                log.warn("Parsing completed with 0 wines being returned");
            }
            metricsCollector.parsingCompleteSuccessful();
            lastSucceededParsingTime.set(System.currentTimeMillis());
            return wines;
        } catch (IOException ex) {
            metricsCollector.parsingCompleteFailed();
            log.error("Error while parsing catalogs : ", ex);
            return new HashMap<>();
        }
    }

    private void parseCatalog(String category, Map<Integer, Wine> wines) throws IOException {
        long parseStart = System.nanoTime();
        String url = String.format(CATALOG_START_URL, category);
        Document document = getDocument(url);
        long firstFetchEnd = System.nanoTime();
        metricsCollector.timeWinePageFetchingDuration(firstFetchEnd - parseStart);
        boolean isLastPage = false;

        AtomicInteger unsuccessfullCounter = new AtomicInteger();
        AtomicInteger allCounter = new AtomicInteger();

        int page = 1;

        while (!isLastPage) {
            parseStart = System.nanoTime();
            document.select(CARD_SELECTOR)
                    .parallelStream()
                    .forEach(card -> {
                        String name = card.select(CATALOG_NAME_SELECTOR).last().html();
                        if (isWine(name)) {
                            allCounter.incrementAndGet();
                            int id = Integer.parseInt(card.attr(ID_SELECTOR));
                            try {
                                if (!wines.containsKey(id)) {
                                    Wine wine = parseProduct(id);
                                    if (wine != null) {
                                        wines.put(id, wine);
                                    }
                                }
                            } catch (Exception ex) {
                                unsuccessfullCounter.incrementAndGet();
                                log.error("Error while parsing wine with id {} {}", id, ex);
                            }
                        }
                    });
            eventLogger.info(WineLabParserNotableEvents.I_WINES_PAGE_PARSED, page);
            page++;
            Element nextPage = document.select(NEXT_PAGE_SELECTOR).first();
            long parseEnd = System.nanoTime();
            metricsCollector.timeWinePageParsingDuration(parseEnd - parseStart);
            if (nextPage == null) {
                isLastPage = true;
            } else {
                long fetchStart = System.nanoTime();
                url = String.format(CATALOG_NEXT_URL, nextPage.attr("href"));
                try {
                    document = getDocument(url);
                } catch (IOException ex) {
                    log.error("Error while parsing catalog page {} {}", url, ex);
                    eventLogger.warn(WineLabParserNotableEvents.W_WINE_PAGE_PARSING_FAILED, page);
                    break;
                }
                long fetchEnd = System.nanoTime();
                metricsCollector.timeWinePageFetchingDuration(fetchEnd - fetchStart);
            }
        }
    }

    public Map<Integer, Wine> parseCatalogPage(String catalog, int page) {
        final String url = String.format(CATALOG_PAGE_URL, CATALOGS.get(catalog), page);
        try {
            Document document = getDocument(url);
            Map<Integer, Wine> wines = new HashMap<>();
            AtomicInteger count = new AtomicInteger();

            document.select(CARD_SELECTOR)
                    .parallelStream()
                    .forEach(card -> {
                        String name = card.select(CATALOG_NAME_SELECTOR).last().html();
                        if (isWine(name)) {
                            int id = Integer.parseInt(card.attr(ID_SELECTOR));
                            try {
                                if (!wines.containsKey(id)) {
                                    wines.put(id, parseProduct(id));
                                }
                            } catch (Exception ex) {
                                count.incrementAndGet();
                                log.error("Error while parsing wine with id {} {}", id, ex);
                            }
                        }
                    });
            log.info("Total failed-to-parse wines: {}", count);
            return wines;
        } catch (IOException ex) {
            log.error("Error while parsing {} catalog's page {} : ", catalog, page, ex);
            return new HashMap<>();
        }
    }

    public List<Wine> getFromCatalogPage(int pageNumber, int winePageCount, int sparklingPageCount) {
        return getFromCatalogPage(pageNumber, winePageCount, sparklingPageCount, DEFAULT_CITY);
    }

    public List<Wine> getFromCatalogPage(int pageNumber, int winePageCount, int sparklingPageCount, String city) {
        metricsCollector.isParsing();
        List<Wine> wines = new ArrayList<>();
        try {
            long parseStart = System.nanoTime();
            String url = getCatalogLinkByNumber(pageNumber, winePageCount, sparklingPageCount);
            Document document = getDocument(url, city);
            long fetchEnd = System.nanoTime();
            metricsCollector.timeWinePageFetchingDuration(fetchEnd - parseStart);

            AtomicInteger failedCount = new AtomicInteger();
            document.select(CARD_SELECTOR)
                    .stream()
                    .forEach(card -> {
                        try {
                            String name = card.select(CATALOG_NAME_SELECTOR).last().html();
                            if (isWine(name)) {
                                int id = Integer.parseInt(card.attr(ID_SELECTOR));
                                Optional<Wine> oWine = repository.findById(id);
                                Wine wine;
                                if (oWine.isPresent()) {
                                    log.debug("Wine {} was already stored in database", id);
                                    wine = oWine.get();
                                    setLocalInfoFromCard(wine, card);
                                    if (wine.isInStock()) {
                                        wine.setLastInStock(LocalDateTime.now());
                                        repository.save(wine);
                                    }
                                } else {
                                    log.debug("Wine {} was not stored in database previously", id);
                                    wine = parseProduct(id);
                                    wine.setLastInStock(LocalDateTime.now());
                                    repository.save(wine);
                                }
                                wines.add(wine);
                            }
                        }
                        catch (Exception e) {
                            log.error("Exception occured during price parsing for wine {}: {}", card.attr(ID_SELECTOR), e);
                            failedCount.getAndIncrement();
                        }
                    });
            long parseEnd = System.nanoTime();
            metricsCollector.timeWinePageParsingDuration(parseEnd - parseStart);
            log.info("Total failed-to-parse wines: {}", failedCount);
        } catch (IndexOutOfBoundsException e) {
            log.error("Catalog page number exceeds total catalog page count");
        } catch (IOException e) {
            log.error("Exception occurred during catalog page parsing: {}", e);
        }
        metricsCollector.isNotParsing();
        return wines;
    }

    private void setLocalInfoFromCard(Wine wine, Element card) {
        boolean isInStock = (card.selectFirst(CARD_IN_STOCK_SELECTOR) != null);
        wine.setInStock(isInStock);

        String newPriceString = card.attr(NEW_PRICE_SELECTOR);
        BigDecimal newPrice = new BigDecimal(newPriceString);
        wine.setNewPrice(newPrice);

        BigDecimal oldPrice;
        if (isInStock) {
            String oldPriceString = card.selectFirst(CARD_OLD_PRICE_SELECTOR).ownText().replace(" ", "");
            oldPrice = new BigDecimal(oldPriceString);
        }
        else {
            oldPrice = newPrice;
        }
        wine.setOldPrice(oldPrice);
    }

    public List<WineLocalInfo> parseAllLocalInfo(int productID) {
        return CITIES.keySet().stream()
                .map(v -> getLocalInfo(productID, v))
                .filter(Objects::nonNull)
                .filter(v -> v.getNewPrice() != null)
                .collect(Collectors.toList());
    }

    /* Utility */

    public int getCatalogPageCount(String catalog) {
        String url = String.format(CATALOG_PAGE_URL, CATALOGS.get(catalog), 1);
        try {
            Document document = getDocument(url);
            String countString = document.selectFirst(WINE_COUNT_SELECTOR).ownText();
            int count = Integer.parseInt(countString.replaceAll("[^\\d.]", ""));
            return (int) Math.ceil((double) count / WINES_PER_PAGE);
        }
        catch (IOException e) {
            log.error("Unable to get page {}", url);
            return 0;
        }
    }

    private boolean isWine(String name) {
        return Arrays.stream(WINES).reduce(false,
                (a, b) -> a || name.toLowerCase().contains(b),
                (a, b) -> a || b);
    }

    private boolean isSparkling(String name) {
        return Arrays.stream(SPARKLINGS).reduce(false,
                (a, b) -> a || name.toLowerCase().contains(b),
                (a, b) -> a || b);
    }

    private boolean isRegion(String subcategory) {
        return Arrays.stream(REGIONS).reduce(false,
                (a, b) -> a || subcategory.toLowerCase().contains(b),
                (a, b) -> a || b);
    }

    private ParserApi.Wine.Color getColor(String color) {
        return COLORS.getOrDefault(color.toLowerCase(), null);
    }

    private ParserApi.Wine.Sugar getSugar(String sugar) {
        return SUGARS.getOrDefault(sugar.toLowerCase(), null);
    }

    private void fillTags(Wine wine, Elements tags) {
        for (Element tag : tags) {
            String url = java.net.URLDecoder.decode(tag.attr("href"), StandardCharsets.UTF_8);
            String key = url.split("(:)")[2];
            String value = tag.html();
            if (key.equals(COLOR_SELECTOR)) {
                ParserApi.Wine.Color color = getColor(value);
                wine.setColor(color);
            } else if (key.equals(ALCOHOL_SELECTOR)) {
                BigDecimal alcoholContent = new BigDecimal(value.substring(0, value.length() - 2));
                wine.setAlcoholContent(alcoholContent);
            } else if (key.equals(SUGAR_SELECTOR)) {
                ParserApi.Wine.Sugar sugar = getSugar(value);
                wine.setSugar(sugar);
            } else if (key.equals(VOLUME_SELECTOR)) {
                BigDecimal volume = new BigDecimal(value.substring(0, value.length() - 2));
                wine.setVolume(volume);
            } else if (key.equals(GRAPE_SELECTOR)) {
                wine.setGrapeSort(value);
            } else if (key.equals(COUNTRY_SELECTOR)) {
                wine.setCountry(value);
            } else if (key.equals(BRAND_SELECTOR)) {
                wine.setBrand(value);
            } else if (key.equals(MANUFACTURER_SELECTOR)) {
                wine.setManufacturer(value);
            }
        }
    }

    private WineLocalInfo getLocalInfo(Document document, String cityKey) {
        WineLocalInfo info = new WineLocalInfo();

        info.setCityName(CITIES.get(cityKey));

        Element outOfStock = document.selectFirst(IN_STOCK_SELECTOR);
        if (outOfStock != null) {
            info.setInStock(false);
        } else {
            info.setInStock(true);
        }

        Element details = document.selectFirst(PRODUCT_DETAILS_SELECTOR);

        String newPriceString = details.attr(NEW_PRICE_SELECTOR);
        if (!newPriceString.isEmpty()) {
            BigDecimal newPrice = new BigDecimal(newPriceString);
            info.setNewPrice(newPrice);
        }

        Element oldPriceSpan = document.selectFirst(OLD_PRICE_SELECTOR);
        if (oldPriceSpan != null) {
            BigDecimal oldPrice = new BigDecimal(oldPriceSpan.ownText().replace(" ", ""));
            info.setOldPrice(oldPrice);
        }
        return info;
    }

    private WineLocalInfo getLocalInfo(Document document) {
        return getLocalInfo(document, DEFAULT_CITY);
    }

    private WineLocalInfo getLocalInfo(int productID, String cityKey) {
        final String productURL = String.format(PRODUCT_PAGE_URL, productID);
        Document document;
        try {
            document = getDocument(productURL, cityKey);
            return getLocalInfo(document, cityKey);
        } catch (IOException e) {
            log.error("Could not get product page during parsing wine {}", productID);
            return null;
        }
    }
}
