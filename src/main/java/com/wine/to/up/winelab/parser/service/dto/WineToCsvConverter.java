package com.wine.to.up.winelab.parser.service.dto;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WineToCsvConverter {
    private static Map<String, Function<Wine, Object>> fields;

    public WineToCsvConverter() {
        fields = new LinkedHashMap<>();
        fields.put("NAME", Wine::getName);
        fields.put("LINK", Wine::getLink);
        fields.put("OLD_PRICE", Wine::getOldPrice);
        fields.put("NEW_PRICE", Wine::getNewPrice);
        fields.put("IMAGE", Wine::getImage);
        fields.put("MANUFACTURER", Wine::getManufacturer);
        fields.put("BRAND", Wine::getBrand);
        fields.put("COUNTRY", Wine::getCountry);
        fields.put("REGION", Wine::getRegion);
        fields.put("VOLUME", Wine::getVolume);
        fields.put("ALCOHOL_CONTENT", Wine::getAlcoholContent);
        fields.put("SPARKLING", Wine::isSparkling);
        fields.put("COLOR", Wine::getColor);
        fields.put("SUGAR", Wine::getSugar);
        fields.put("GRAPE_SORT", Wine::getGrapeSort);
        fields.put("DESCRIPTION", Wine::getDescription);
        fields.put("GASTRONOMY", Wine::getGastronomy);
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
        return toCsvString(new ArrayList<>(fields.keySet()));
    }

    private String getWine(Wine wine) {
        List<Object> attributes = new ArrayList<>();
        for (Function<Wine, Object> func : fields.values()) {
            attributes.add(func.apply(wine));
        }
        return toCsvString(attributes);
    }

    private String toCsvString(List<Object> list) {
        return new StringBuilder(
                list.stream()
                        .map(s -> s == null ? "\"\"" : "\"" + s + "\"")
                        .collect(Collectors.joining(",")))
                .append("\n")
                .toString();
    }
}
