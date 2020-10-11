package com.wine.to.up.winelab.parser.service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class Wine {
    // wine name as it is on product page
    private String name;
    // site address
    private String site;
    // price without discount in rubles
    private BigDecimal oldPrice;
    // product page address
    private String link;
    // new price accounting the discount
    private BigDecimal newPrice;
    // product image
    private String image;
    private String manufacturer;
    private String brand;
    private String country;
    private String region;
    // volume in liters
    private BigDecimal volume;
    private BigDecimal alcoholPercentage;
    // is wine sparkling
    private boolean sparkling;
    //should enums really be private? We can't access any methods that way.
    private enum Color{
        RED, ROSE, WHITE
    }
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Color> colorMap = Map.of("красное", Color.RED, "розовое",
            Color.ROSE, "белое", Color.WHITE, "светлое", Color.WHITE);
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Sugar> sugarMap = Map.of("брют", Sugar.DRY, "сухое", Sugar.DRY,
            "полусухое", Sugar.MEDIUM_DRY, "полусладкое", Sugar.MEDIUM, "сладкое", Sugar.SWEET);
    private Color color;
    public Color setColor(String value) {
        this.color = colorMap.get(value.toLowerCase());
        return this.color;
    }
    private enum Sugar{
        DRY, MEDIUM_DRY, MEDIUM, SWEET
    }
    Sugar sugar;
    public Sugar setSugar(String value) {
        this.sugar = sugarMap.get(value.toLowerCase());
        return this.sugar;
    }
    private String grapeSort;
    private String description;
    private String gastronomy;
    public int getColorValue() {
        return this.color.ordinal();
    }
    public int getSugarValue() {
        return this.sugar.ordinal();
    }
}
