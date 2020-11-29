package com.wine.to.up.winelab.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */
@Slf4j
@Component
public class WineLabParserMetricsCollector extends CommonMetricsCollector {
    private static HashMap<String, Boolean> isParsingMap = new HashMap<>();

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
            .help("Time spent parsing the entire directory, in milliseconds")
            .register();

    private static final Summary avgParsingTimeSingleSummary = Summary.build()
            .name(AVG_PARSING_TIME_SINGLE)
            .help("Average parsing time of one wine for one complete parsing of the catalog, in milliseconds")
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
//isParsing
   public void isParsing(String key, Boolean v) {
       isParsingMap.put(key, v);
       if(isParsingMap.containsValue(true)) {
           Metrics.gauge(IS_PARSING,1);
           isParsingGauge.set(1);
       } else {
           Metrics.gauge(IS_PARSING,0);
           isParsingGauge.set(0);
       }
    }
    public void parsingTimeFull(double time) {
        log.debug("parsingTimeFull");
        Metrics.timer(PARSING_TIME_FULL).record((long)time, TimeUnit.MILLISECONDS);
        parsingTimeFullSummary.observe(time);
    }

    public void winesParcedSuccessfully(int count){
        Metrics.counter(WINES_PARSED_SUCCESSFULLY).increment(count);
        winesParcedSuccessfullyCounter.inc(count);
    }

    public void winesParcedUnsuccessfully(int count){
        Metrics.counter(WINES_PARSED_UNSUCCESSFULLY).increment(count);
        winesParcedUnsuccessfullyCounter.inc(count);
    }
    public void avgParsingTimeSingle(double time) {
        Metrics.timer(AVG_PARSING_TIME_SINGLE).record((long)time, TimeUnit.MILLISECONDS);
        avgParsingTimeSingleSummary.observe(time);
    }
    public void attributeLackPrcntg(double count){
        Metrics.counter(ATTRIBUTE_LACK_PRCNTG).increment(count);
        attributeLackPrcntgCounter.inc(count);
    }
    public void successfullyPrcntg(double count){
        Metrics.counter(SUCCESSFUL_PRCNTG).increment(count);
        successfullyPrcntgCounter.inc(count);
    }
}
