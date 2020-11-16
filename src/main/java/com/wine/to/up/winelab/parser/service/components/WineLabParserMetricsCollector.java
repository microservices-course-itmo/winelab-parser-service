package com.wine.to.up.winelab.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import io.prometheus.client.Counter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */
@Component
public class WineLabParserMetricsCollector extends CommonMetricsCollector {
    private static final String SERVICE_NAME = "winelab_parce_service";
    private static final String IS_PARSING = "is_parsing";
    private static final String PARSING_TIME_FULL = "parsing_time_full";
    private static final String AVG_PARSING_TIME_SINGLE = "avg_parsing_time_single";
    private static final String SUCCESSFUL_PRCNTG = "successful_prcntg";
    private static final String ATTRIBUTE_LACK_PRCNTG = "attribute_lack_prcntg";
    private static final String WINES_PARSED_UNSUCCESSFULLY = "wines_parsed_unsuccessfully";
    private static final String WINES_PARSED_SUCCESSFULLY = "wines_parsed_successfully";

    public WineLabParserMetricsCollector() {
        super(SERVICE_NAME);
    }
    private static final Gauge isParsingGauge = Gauge.build()
            .name(IS_PARSING)
            .help("Parsing is in progress")
            .register();

    private static final Summary parsingTimeFullSummary = Summary.build()
            .name(PARSING_TIME_FULL)
            .help("Time spent parsing the entire directory")
            .register();

    private static final Summary avgParsingTimeSingleSummary = Summary.build()
            .name(AVG_PARSING_TIME_SINGLE)
            .help("Average parsing time of one wine for one complete parsing of the catalog")
            .register();

    private static final Counter successfullyPrcntgCounter = Counter.build()
            .name(SUCCESSFUL_PRCNTG)
            .help("Percentage of successfully processed wines")
            .register();

    private static final Counter attributeLackPrcntgCounter = Counter.build()
            .name(ATTRIBUTE_LACK_PRCNTG)
            .help("Percentage of unfilled attributes in successful distilled wines")
            .register();
    private static final Counter winesParcedUnsuccessfullyCounter = Counter.build()
            .name(WINES_PARSED_UNSUCCESSFULLY)
            .help("The number of wines that could not be processed")
            .register();
    private static final Counter winesParcedSuccessfullyCounter = Counter.build()
            .name(WINES_PARSED_SUCCESSFULLY)
            .help("Number of successfully processed wines")
            .register();
//isParsing дщдулфть
   public void isParsing(double v) {
        Metrics.gauge(IS_PARSING,v);
        isParsingGauge.set(v);
    }
    public void parsingTimeFull(double time) {
        Metrics.timer(PARSING_TIME_FULL).record((long)time, TimeUnit.MILLISECONDS);
        parsingTimeFullSummary.observe(time);
    }
    public void winesParcedSuccessfully(int count){
        Metrics.counter(WINES_PARSED_SUCCESSFULLY).increment(count);
        winesParcedSuccessfullyCounter.inc(count);
    }
    /*
    public void winesParcedUnsuccessfully(AtomicInteger count){
        Metrics.counter(WINES_PARSED_SUCCESSFULLY).increment(count);
        winesParcedUnsuccessfullyCounter.inc(count);
    }
     */
    public void avgParsingTimeSingle(double time) {
        Metrics.timer(PARSING_TIME_FULL).record((long)time, TimeUnit.MILLISECONDS);
        avgParsingTimeSingleSummary.observe(time);
    }
    public void attributeLackPrcntg(double count){
        Metrics.counter(WINES_PARSED_SUCCESSFULLY).increment(count);
        attributeLackPrcntgCounter.inc(count);
    }
    public void successfullyPrcntg(double count){
        Metrics.counter(WINES_PARSED_SUCCESSFULLY).increment(count);
        successfullyPrcntgCounter.inc(count);
    }
}
