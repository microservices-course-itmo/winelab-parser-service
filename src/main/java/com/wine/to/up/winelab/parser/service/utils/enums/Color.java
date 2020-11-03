package com.wine.to.up.winelab.parser.service.utils.enums;

import java.util.Map;

public enum Color {
    RED, ROSE, WHITE;
    private static final Map<String, Color> colorMap = Map.of("красное", RED, "розовое",
            ROSE, "белое", WHITE, "светлое", WHITE);
    public static Color fromString(String value) {
        return colorMap.get(value.toLowerCase());
    }
}
