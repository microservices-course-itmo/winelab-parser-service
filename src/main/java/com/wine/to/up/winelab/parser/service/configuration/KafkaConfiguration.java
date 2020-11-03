package com.wine.to.up.winelab.parser.service.configuration;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.ParserCommonApiProperties;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.components.WineLabParserMetricsCollector;
import com.wine.to.up.winelab.parser.service.messaging.serialization.EventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {
    /**
     * List of kafka servers
     */
    @Value("${spring.kafka.bootstrap-server}")
    private String brokers;

    /**
     * Application consumer group id
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String applicationConsumerGroupId;

    /**
     * Creating general producer properties. Common for all the producers
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Properties producerProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

    /**
     * Creates sender based on general properties. It helps to send single message to designated topic.
     * <p>
     * Uses custom serializer as the messages within single topic should be the same type. And
     * the messages in different topics can have different types and require different serializers
     *
     * @param producerProperties       is the general producer properties. {@link #producerProperties()}
     * @param wineLabServiceApiProperties class containing the values of the given service's API properties (in this particular case topic name)
     * @param metricsCollector         class encapsulating the logic of the metrics collecting and publishing
     */
    @Bean
    KafkaMessageSender<ParserApi.Wine> wineTopicKafkaMessageSender(Properties producerProperties,
                                                         ParserCommonApiProperties wineLabServiceApiProperties,
                                                         WineLabParserMetricsCollector metricsCollector) {
        // set appropriate serializer for value
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class.getName());

        return new KafkaMessageSender<ParserApi.Wine>(new KafkaProducer<>(producerProperties), wineLabServiceApiProperties.getWineParsedEventsTopicName(), metricsCollector);
    }
}