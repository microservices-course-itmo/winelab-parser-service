package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.logging.WineLabParserNotableEvents;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Value("#{${parser.cookies}}")
    private Map<String, String> COOKIES;
    @Value("#{${parser.catalogs}}")
    private Map<String, String> CATALOGS;
    @Value("${parser.retries}")
    private int MAX_RETRIES;

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

    @Value("${parser.product.address}")
    private String PRODUCT_PAGE_URL;
    private Map<String, String> cookies;
    @Value("#{${parser.catalogs}}")
    private Map<String, String> catalogs;
    static final String IS_PARSING_PRODUCT = "product";
    static final String IS_PARSING_CATALOGS = "catalogs";
    static final String IS_PARSING_CATALOG = "catalog";
    static final String IS_PARSING_CATALOG_PAGE = "catalogPage";

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

    @Value("${parser.search.query.base}")
    private String SEARCH_QUERY_BASE;
    @Value("${parser.search.query.brand}")
    private String SEARCH_QUERY_BRAND;
    @Value("${parser.search.query.alcohol}")
    private String SEARCH_QUERY_ALCOHOL;
    @Value("${parser.search.query.price}")
    private String SEARCH_QUERY_PRICE;

    @Value("${parser.list.wines}")
    private String[] WINES;
    @Value("${parser.list.sparkling}")
    private String[] SPARKLINGS;
    @Value("${parser.list.regions}")
    private String[] REGIONS;

    @Value("#{${parser.map.countries}}")
    private Map<String, String> COUNTRY_FIX;
    @Value("#{${parser.map.colors}}")
    private Map<String, ParserApi.Wine.Color> COLORS;
    @Value("#{${parser.map.sugars}}")
    private Map<String, ParserApi.Wine.Sugar> SUGARS;

    @Value("${parser.pattern.volume}")
    private String PATTERN_VOLUME;
    @Value("${parser.pattern.alcohol}")
    private String PATTERN_ALCOHOL;

    private Long lastParse = null;

    private final WineLabParserMetricsCollector metricsCollector;

    public ParserService(WineLabParserMetricsCollector metricsCollector) {
        this.metricsCollector = Objects.requireNonNull(metricsCollector, "Can't get metricsCollector");
    }

    /**
     * Parsing wine from winelab web site by the id given
     *
     * @param productID a product id on winelab.ru of wine to be parsed
     * @return parsed wine object
     */
    public Wine parseProduct(int productID) {
        try {
            Set<String> countrySet = new HashSet<>();
            Set<String> grapeSet = new HashSet<>();
            Set<String> manufacturerSet = new HashSet<>();
            for (String catalog : CATALOGS.values()) {
                String url = String.format(CATALOG_START_URL, catalog);
                Document document = getDocument(url);

                countrySet.addAll(loadAttributes(document, COUNTRY_SELECTOR));
                grapeSet.addAll(loadAttributes(document, GRAPE_SELECTOR));
                manufacturerSet.addAll(loadAttributes(document, MANUFACTURER_SELECTOR));
            }
            return parseProduct(productID, countrySet, grapeSet, manufacturerSet);
        } catch (IOException ex) {
            log.error("Error while parsing wine {} : ", productID, ex);
            return null;
        }
    }

    protected Document getDocument(String url) throws IOException {
        for (int count = 0; count < MAX_RETRIES; count++) {
            try {
                Document document = Jsoup.connect(url).cookies(COOKIES).get();
                return document;
            } catch (IOException ex) {
                log.warn("Couldn't get page {} on try {} : {}", url, count + 1, ex);
            }
        }
        throw new IOException(String.format("Couldn't get page %s", url));
    }

    private Wine parseProduct(int productID, Set<String> countrySet, Set<String> grapeSet, Set<String> manufacturerSet) throws IOException {
        long parseStart = System.nanoTime();
        long fetchStart = System.nanoTime();

        final String productURL = String.format(PRODUCT_PAGE_URL, productID);
        Document document = getDocument(productURL);

        long fetchEnd = System.nanoTime();
        metricsCollector.timeWineDetailsFetchingDuration(fetchEnd - fetchStart);

        Wine wine = new Wine();

        String name;
        try {
            name = document.selectFirst(PRODUCT_NAME_SELECTOR).ownText();
            wine.setName(name);
        } catch (NullPointerException ex) {
            eventLogger.warn(WineLabParserNotableEvents.W_WINE_DETAILS_PARSING_FAILED);
            log.warn("Wine {} will not be parsed because could not get name", productID);
            return null;
        }

        wine.setLink(productURL);

        Element details = document.selectFirst(PRODUCT_DETAILS_SELECTOR);

        if(!fillPrices(wine, document, details)) {
            eventLogger.warn(WineLabParserNotableEvents.W_WINE_DETAILS_PARSING_FAILED);
            log.warn("Wine {} will not be parsed because could not get price", productID);
            return null;
        }

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
        fillTags(tags, wine);

        String gastronomy = document.selectFirst(GASTRONOMY_SELECTOR).html();
        wine.setGastronomy(gastronomy);

        String description = document.selectFirst(DESCRIPTION_SELECTOR).html();
        wine.setDescription(description);
        String searchURL = getLink(PROTOCOL, SITE_URL, productID, wine);
        Document searchPage = null;
        boolean searchSuccessfull;
        try {
            searchPage = getDocument(searchURL);
            Elements cards = searchPage.select(CARD_SELECTOR);
            searchSuccessfull = cards.size() == 1 && Integer.parseInt(cards.first().attr(ID_SELECTOR)) == productID;
        } catch (IOException ex) {
            searchSuccessfull = false;
        }
        if (searchSuccessfull) {
            fillValuesBySearchURL(searchPage, wine);
            if (!wine.isSparkling()) {
                Elements categories = searchPage.select(String.format(FILTER_SELECTOR, CATEGORY_SELECTOR));
                for (Element category : categories) {
                    if (category.html().equals(SPARKLING_CATEGORY)) {
                        wine.setSparkling(true);
                        break;
                    }
                }
            }

            if (wine.getCountry() == null) {
                Element countryWrapper = searchPage.selectFirst(CARD_COUNTRY_SELECTOR);
                if (countryWrapper != null) {
                    String country = countryWrapper.html();
                    wine.setCountry(countryFix(country));
                }
            }
        } else {
            fillValuesOnException(tags, wine, grapeSet, manufacturerSet, countrySet);
        }

        long parseEnd = System.nanoTime();
        metricsCollector.timeWineDetailsParsingDuration(parseEnd - parseStart);
        eventLogger.info(WineLabParserNotableEvents.I_WINE_DETAILS_PARSED);
        var lackAttributes = wine.lackAttributes();
        if (!lackAttributes.isEmpty()) {
            lackAttributes.forEach(attribute -> eventLogger.info(WineLabParserNotableEvents.W_WINE_ATTRIBUTE_ABSENT, attribute, productURL));
        }
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
            long currentParse = System.nanoTime();
            if (lastParse != null) {
                //metricsCollector.timeSinceLastSucceededParse(currentParse - lastParse);
            }
            lastParse = currentParse;
            if (wines.size() > 0) {
                log.info("Parsing done! Total {} wines parsed", wines.size());
            } else {
                log.warn("Parsing completed with 0 wines being returned");
            }
            metricsCollector.parsingCompleteSuccessful();
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

        Set<String> countrySet = loadAttributes(document, COUNTRY_SELECTOR);
        Set<String> grapeSet = loadAttributes(document, GRAPE_SELECTOR);
        Set<String> manufacturerSet = loadAttributes(document, MANUFACTURER_SELECTOR);

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
                                    Wine wine = parseProduct(id, countrySet, grapeSet, manufacturerSet);
                                    if(wine != null) {
                                        wines.put(id, wine);
                                    }
                                    metricsCollector.winesParsedSuccessfully(1);
                                }
                            } catch (Exception ex) {
                                unsuccessfullCounter.incrementAndGet();
                                metricsCollector.winesParsedUnsuccessfully(1);
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
                }
                catch (IOException ex) {
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

            Set<String> countrySet = loadAttributes(document, COUNTRY_SELECTOR);
            Set<String> grapeSet = loadAttributes(document, GRAPE_SELECTOR);
            Set<String> manufacturerSet = loadAttributes(document, MANUFACTURER_SELECTOR);

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
                                    wines.put(id, parseProduct(id, countrySet, grapeSet, manufacturerSet));
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

    /* Utility */

    private Set<String> loadAttributes(Document document, String attrSelector) {
        return document.select(String.format(FILTER_SELECTOR, attrSelector))
                .stream()
                .map(Element::html)
                .collect(Collectors.toSet());
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

    private String countryFix(String country) { // fix double names for countries
        return COUNTRY_FIX.getOrDefault(country, country);
    }

    private ParserApi.Wine.Color getColor(String color) {
        return COLORS.getOrDefault(color.toLowerCase(), null);
    }

    private ParserApi.Wine.Sugar getSugar(String sugar) {
        return SUGARS.getOrDefault(sugar.toLowerCase(), null);
    }

    private String getLink(String protocol, String siteURL, int productID, Wine wine) {
        StringBuffer query = new StringBuffer(String.format(Locale.US, SEARCH_QUERY_BASE, productID));
        if (wine.getBrand() != null) {
            query.append(String.format(Locale.US, SEARCH_QUERY_BRAND, wine.getBrand()));
        }
        if (wine.getAlcoholContent() != null) {
            query.append(String.format(Locale.US, SEARCH_QUERY_ALCOHOL, wine.getAlcoholContent(), wine.getAlcoholContent()));
        }
        if (wine.getNewPrice() != null) {
            query.append(String.format(Locale.US, SEARCH_QUERY_PRICE, wine.getNewPrice(), wine.getNewPrice()));
        }
        return query.toString();
    }

    private void fillTags(Elements tags, Wine wine) {
        for (Element tagEl : tags) {
            String tag = tagEl.ownText();
            if (tag.matches(PATTERN_VOLUME)) {
                tag = tag.replaceAll("[ Л]", "");
                BigDecimal volume = new BigDecimal(tag);
                wine.setVolume(volume);
            } else if (tag.matches(PATTERN_ALCOHOL)) {
                tag = tag.replaceAll("[ %]", "");
                BigDecimal alcoholContent = new BigDecimal(tag);
                wine.setAlcoholContent(alcoholContent);
            }
        }
    }

    private void fillValuesOnException(Elements tags, Wine wine, Set<String> grapeSet, Set<String> manufacturerSet, Set<String> countrySet) {
        for (Element tagEl : tags) {
            String tag = tagEl.ownText();
            ParserApi.Wine.Color color = getColor(tag);
            ParserApi.Wine.Sugar sugar = getSugar(tag);
            if (color != null) {
                wine.setColor(color);
            } else if (sugar != null) {
                wine.setSugar(sugar);
            } else if (countrySet.contains(tag)) {
                wine.setCountry(countryFix(tag));
            } else if (grapeSet.contains(tag)) {
                wine.setGrapeSort(tag);
            } else if (manufacturerSet.contains(tag)) {
                wine.setManufacturer(tag);
            }
        }
    }

    private void fillValuesBySearchURL(Document searchPage, Wine wine) {
        Element colorSpan = searchPage.selectFirst(String.format(FILTER_SELECTOR, COLOR_SELECTOR));
        if (colorSpan != null) {
            String colorText = colorSpan.html();
            ParserApi.Wine.Color color = getColor(colorText);
            if (color != null) {
                wine.setColor(color);
            }
        }

        Element sugarSpan = searchPage.selectFirst(String.format(FILTER_SELECTOR, SUGAR_SELECTOR));
        if (sugarSpan != null) {
            String sugarText = sugarSpan.html();
            ParserApi.Wine.Sugar sugar = getSugar(sugarText);
            if (sugar != null) {
                wine.setSugar(sugar);
            }
        }

        Element countrySpan = searchPage.selectFirst(String.format(FILTER_SELECTOR, COUNTRY_SELECTOR));
        if (countrySpan != null) {
            String country = countrySpan.html();
            wine.setCountry(countryFix(country));
        }

        Element grapeSpan = searchPage.selectFirst(String.format(FILTER_SELECTOR, GRAPE_SELECTOR));
        if (grapeSpan != null) {
            String grapeSort = grapeSpan.html();
            wine.setGrapeSort(grapeSort);
        }

        Element manufacturerSpan = searchPage.selectFirst(String.format(FILTER_SELECTOR, MANUFACTURER_SELECTOR));
        if (manufacturerSpan != null) {
            String manufacturer = manufacturerSpan.html();
            wine.setManufacturer(manufacturer);
        }
    }

    private boolean fillPrices(Wine wine, Document document, Element details) {
        String newPriceString = details.attr(NEW_PRICE_SELECTOR);
        if (!newPriceString.isEmpty()) {
            BigDecimal newPrice = new BigDecimal(newPriceString);
            wine.setNewPrice(newPrice);
        }
        else {
            return false;
        }

        Element oldPriceSpan = document.selectFirst(OLD_PRICE_SELECTOR);
        if (oldPriceSpan != null) {
            BigDecimal oldPrice = new BigDecimal(oldPriceSpan.ownText().replace(" ", ""));
            wine.setOldPrice(oldPrice);
        }
        return true;
    }
}
