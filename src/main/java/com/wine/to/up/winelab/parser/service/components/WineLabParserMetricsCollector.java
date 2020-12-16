package com.wine.to.up.winelab.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */
@Slf4j
@Component
public class WineLabParserMetricsCollector extends CommonMetricsCollector {
    private static final String SERVICE_NAME = "winelab_parse_service";

    private static final String PARSING_STARTED = "parsing_started";
    private static final String PARSING_COMPLETE = "parsing_complete";
    private static final String PARSING_DURATION = "parsing_process_duration_seconds";
    private static final String TIME_SINCE_LAST_PARSING = "time_since_last_succeeded_parsing";
    private static final String WINE_DETAILS_FETCHING_DURATION = "wine_details_fetching_duration_seconds";
    private static final String WINE_PAGE_FETCHING_DURATION = "wine_page_fetching_duration_seconds";
    private static final String WINE_DETAILS_PARSING_DURATION = "wine_details_parsing_duration_seconds";
    private static final String WINE_PAGE_PARSING_DURATION = "wine_page_parsing_duration_seconds";
    private static final String IS_PARSING = "parsing_in_progress";
    private static final String WINES_PARSED_UNSUCCESSFULLY = "wines_parsed_unsuccessfully";
    private static final String WINES_PARSED_SUCCESSFULLY = "wines_parsed_successfully";
    private static final String WINES_PUBLISHED_TO_KAFKA = "wines_published_to_kafka_count";

    private static final String PARSING_COMPLETE_STATUS = "status";

    public WineLabParserMetricsCollector() {
        super(SERVICE_NAME);
    }

    private static final Counter parsingStartedCounter = Counter.build()
            .name(PARSING_STARTED)
            .help("Total number of parsing processes ever started")
            .register();
    private static final Counter parsingCompleteCounter = Counter.build()
            .name(PARSING_COMPLETE)
            .help("Total number of parsing processes ever completed")
            .labelNames(PARSING_COMPLETE_STATUS)
            .register();
    private static final Summary parsingDurationSummary = Summary.build()
            .name(PARSING_DURATION)
            .help("The duration of every parsing process completed so far")
            .register();
    private static final Gauge timeSinceLastParsingGauge = Gauge.build()
            .name(TIME_SINCE_LAST_PARSING)
            .help("The amount of time since the last successfully completed parsing process")
            .register();
    private static final Summary wineDetailsFetchingDurationSummary = Summary.build()
            .name(WINE_DETAILS_FETCHING_DURATION)
            .help("The duration of every fetching of a wine details page")
            .register();
    private static final Summary winePageFetchingDurationSummary = Summary.build()
            .name(WINE_PAGE_FETCHING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .register();
    private static final Summary wineDetailsParsingDurationSummary = Summary.build()
            .name(WINE_DETAILS_PARSING_DURATION)
            .help("The duration of every parsing of a wine details page")
            .register();
    private static final Summary winePageParsingDurationSummary = Summary.build()
            .name(WINE_PAGE_PARSING_DURATION)
            .help("The duration of every parsing of a wines page")
            .register();

    private static final Gauge isParsingGauge = Gauge.build()
            .name(IS_PARSING)
            .help("Parsing is in progress")
            .register();
    private static final AtomicInteger micrometerIsParsingGauge = Metrics.gauge(IS_PARSING, new AtomicInteger(0));

    private static final Counter winesParsedUnsuccessfullyCounter = Counter.build()
            .name(WINES_PARSED_UNSUCCESSFULLY)
            .help("The number of wines that could not be processed")
            .register();
    private static final Counter winesParsedSuccessfullyCounter = Counter.build()
            .name(WINES_PARSED_SUCCESSFULLY)
            .help("Number of successfully processed wines")
            .register();

    private static final Counter winesPublishedToKafkaCounter = Counter.build()
            .name(WINES_PUBLISHED_TO_KAFKA)
            .help("Number of wines that have been sent to Kafka")
            .register();

    private static final AtomicLong micrometerTimeSinceLastSucceededParsingGauge = Metrics.gauge(TIME_SINCE_LAST_PARSING, new AtomicLong(0));

    public void parsingStarted() {
        Metrics.counter(PARSING_STARTED).increment();
        parsingStartedCounter.inc();
    }

    public void parsingCompleteSuccessful() {
        Metrics.counter(PARSING_COMPLETE, PARSING_COMPLETE_STATUS, "SUCCESS").increment();
        parsingCompleteCounter.labels("SUCCESS").inc();
    }

    public void parsingCompleteFailed() {
        Metrics.counter(PARSING_COMPLETE,PARSING_COMPLETE_STATUS, "FAILED").increment();
        parsingCompleteCounter.labels("FAILED").inc();
    }
    //private static final AtomicInteger isparsingGauge = Metrics.gauge(IS_PARSING, new AtomicInteger(0));
    /*
    public void incParsingInProgress() {
        parsingInProgressGauge.inc();
        //isparsingGauge.incrementAndGet();
        AtomicInteger gauge = Metrics.gauge(PARSING_IN_PROGRESS, new AtomicInteger(0));
        if (gauge != null) {
            gauge.getAndIncrement();
        }
    }*/

    public static void timeSinceLastSucceededParse(long time) {
        // Metrics.gauge(TIME_SINCE_LAST_SUCCEEDED_PARSING, time);
        micrometerTimeSinceLastSucceededParsingGauge.set(time);
        timeSinceLastParsingGauge.set(time);
    }
    /*
    public void decParsingInProgress() {
        parsingInProgressGauge.dec();
        //isparsingGauge.decrementAndGet();
        AtomicInteger gauge = Metrics.gauge(PARSING_IN_PROGRESS, new AtomicInteger(0));
        if (gauge != null) {
            gauge.getAndDecrement();
        }

        timeSinceLastSucceededParse(System.currentTimeMillis());
    }
    */
    public void timeParsingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        parsingDurationSummary.observe(milliTime);
        Metrics.summary(PARSING_DURATION).record(milliTime);
    }


    public void timeWineDetailsFetchingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        wineDetailsFetchingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_DETAILS_FETCHING_DURATION).record(milliTime);
    }

    public void timeWinePageFetchingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        winePageFetchingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_PAGE_FETCHING_DURATION).record(milliTime);
    }

    public void timeWineDetailsParsingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        wineDetailsParsingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_DETAILS_PARSING_DURATION).record(milliTime);
    }

    public void timeWinePageParsingDuration(long nanoTime) {
        long milliTime = TimeUnit.NANOSECONDS.toSeconds(nanoTime);
        winePageParsingDurationSummary.observe(milliTime);
        Metrics.summary(WINE_PAGE_PARSING_DURATION).record(milliTime);
    }

    public void countWinesPublishedToKafka(double wineNum) {
        Metrics.counter(WINES_PUBLISHED_TO_KAFKA).increment(wineNum);
        winesPublishedToKafkaCounter.inc(wineNum);
    }

    public void isParsing() {
        micrometerIsParsingGauge.getAndIncrement();
        isParsingGauge.inc();
    }

    public void isNotParsing() {
        micrometerIsParsingGauge.getAndDecrement();
        isParsingGauge.dec();
    }

    public void winesParsedSuccessfully(int count) {
        Metrics.counter(WINES_PARSED_SUCCESSFULLY).increment(count);
        winesParsedSuccessfullyCounter.inc(count);
    }

    public void winesParsedUnsuccessfully(int count) {
        Metrics.counter(WINES_PARSED_UNSUCCESSFULLY).increment(count);
        winesParsedUnsuccessfullyCounter.inc(count);
    }
}
