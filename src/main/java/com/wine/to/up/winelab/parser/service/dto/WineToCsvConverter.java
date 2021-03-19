package com.wine.to.up.winelab.parser.service.dto;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WineToCsvConverter {
    private Map<String, Function<Wine, Object>> fields;

    public WineToCsvConverter() {
        this.fields = new LinkedHashMap<>();
        this.fields.put("NAME", Wine::getName);
        this.fields.put("LINK", Wine::getLink);
        this.fields.put("OLD_PRICE", Wine::getOldPrice);
        this.fields.put("NEW_PRICE", Wine::getNewPrice);
        this.fields.put("IMAGE", Wine::getImage);
        this.fields.put("MANUFACTURER", Wine::getManufacturer);
        this.fields.put("BRAND", Wine::getBrand);
        this.fields.put("COUNTRY", Wine::getCountry);
        this.fields.put("REGION", Wine::getRegion);
        this.fields.put("VOLUME", Wine::getCapacity);
        this.fields.put("ALCOHOL_CONTENT", Wine::getStrength);
        this.fields.put("SPARKLING", Wine::isSparkling);
        this.fields.put("COLOR", Wine::getColor);
        this.fields.put("SUGAR", Wine::getSugar);
        this.fields.put("GRAPE_SORT", Wine::getGrapeSort);
        this.fields.put("DESCRIPTION", Wine::getDescription);
        this.fields.put("GASTRONOMY", Wine::getGastronomy);
    }

    public String convert(Collection<Wine> wines) {
        StringBuilder builder = new StringBuilder();
        builder.append(getHeader());
        for (Wine wine : wines) {
            builder.append(getWine(wine));
        }
        return builder.toString();
    }

    private String getHeader() {
        return toCsvString(new ArrayList<>(this.fields.keySet()));
    }

    private String getWine(Wine wine) {
        List<Object> attributes = new ArrayList<>();
        for (Function<Wine, Object> func : this.fields.values()) {
            attributes.add(func.apply(wine));
        }
        return toCsvString(attributes);
    }

    private String toCsvString(List<Object> list) {
        return list.stream().map(s -> s == null ? "\"\"" : "\"" + s + "\"") .collect(Collectors.joining(",")) + "\n";
    }
}
