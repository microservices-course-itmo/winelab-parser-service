package com.wine.to.up.winelab.parser.service.logging;

import com.wine.to.up.commonlib.logging.NotableEvent;

public enum WineLabParserNotableEvents implements NotableEvent {
    //TODO create-service: replace
    SOME_DEMO_EVENT("Something happened");

    private final String template;

    WineLabParserNotableEvents(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String getName() {
        return name();
    }


}
