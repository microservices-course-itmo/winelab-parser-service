//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.wine.to.up.winelab.parser.service.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(
        prefix = "wine.service.api"
)
@Component
public class WineLabServiceApiProperties {
    private String messageSentEventsTopicName = "wine";
    private String host;

    public WineLabServiceApiProperties() {
        //constructor
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
