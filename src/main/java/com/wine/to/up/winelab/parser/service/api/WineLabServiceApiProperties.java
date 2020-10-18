package com.wine.to.up.winelab.parser.service.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
public class WineLabServiceApiProperties {
    private String messageSentEventsTopicName = "wine";
    private String host;

    public WineLabServiceApiProperties() {
    }

    public void setMessageSentEventsTopicName(String messageSentEventsTopicName) {
        this.messageSentEventsTopicName = messageSentEventsTopicName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMessageSentEventsTopicName() {
        return this.messageSentEventsTopicName;
    }

    public String getHost() {
        return this.host;
    }
}
