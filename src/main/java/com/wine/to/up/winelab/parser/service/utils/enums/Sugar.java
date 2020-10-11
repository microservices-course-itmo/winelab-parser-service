package com.wine.to.up.winelab.parser.service.utils.enums;

import java.util.Map;

public enum Sugar{
    DRY, MEDIUM_DRY, MEDIUM, SWEET;
    private static final Map<String, Sugar> sugarMap = Map.of("брют", DRY, "сухое", DRY,
            "полусухое", MEDIUM_DRY, "полусладкое", MEDIUM, "сладкое", SWEET);
    public static Sugar fromString(String value) {
        return sugarMap.get(value.toLowerCase());
    }
}
