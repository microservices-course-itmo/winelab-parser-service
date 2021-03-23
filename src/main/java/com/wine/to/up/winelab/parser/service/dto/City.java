package com.wine.to.up.winelab.parser.service.dto;

public enum City {
    MOSCOW("Москва", "RU-MOW"),
    SAINT_PETERSBURG("Санкт-Петербург", "RU-SPE"),
    AMUR_OBLAST("Амурская область", "RU-AMU"),
    ARCHANGELSK_OBLAST("Архангельская область", "RU-ARK"),
    JEWISH_AUTONOMOUS_OBLAST("Еврейская автономная область", "RU-YEV"),
    MOSCOW_OBLAST("Московская область", "RU-SPE"),
    NIZHNY_NOVGOROD_OBLAST("Нижегородская область", "RU-NIZ"),
    PRIMORSKY_KRAI("Приморский край", "RU-PRI"),
    SAKHALIN_OBLAST("Сахалинская область", "RU-SAK"),
    KHABAROVSKY_KRAI("Хабаровский край", "RU-KHA");

    private String cityName;
    private String cookie;

    City(String cityName, String cookie) {
        this.cityName = cityName;
        this.cookie = cookie;
    }

    public static City defaultCity() {
        return SAINT_PETERSBURG;
    }

    public String getCookie() {
        return cookie;
    }

    @Override
    public String toString() {
        return cityName;
    }
}
