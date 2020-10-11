package com.wine.to.up.winelab.parser.service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
    private boolean imageTransparent;
    private String manufacturer;
    private String brand;
    private String country;
    private String region;
    // volume in liters
    private BigDecimal volume;
    private BigDecimal alcoholPercentage;
    // is wine sparkling
    private boolean sparkling;
    private enum Color{
        RED, ROSE, WHITE
    }
    private Color color;
    public Color setColor(String value) {
        switch (value) {
            case "Красное":
                this.color = Color.RED;
                break;
            case "Розовое":
                this.color = Color.ROSE;
                break;
            case "Белое":
            case "Светлое":
                this.color = Color.WHITE;
                break;
            default:
                return null;
        }
        return this.color;
    }
    private enum Sugar{
        DRY, MEDIUM_DRY, MEDIUM, SWEET
    }
    Sugar sugar;
    public Sugar setSugar(String value) {
        switch (value) {
            case "Брют":
            case "Сухое":
                this.sugar = Sugar.DRY;
                break;
            case "Полусухое":
                this.sugar = Sugar.MEDIUM_DRY;
                break;
            case "Полусладкое":
                this.sugar = Sugar.MEDIUM;
                break;
            case "Сладкое":
                this.sugar = Sugar.SWEET;
                break;
            default:
                return null;
        }
        return this.sugar;
    }
    private String grapeSort;
    private String description;
    private String gastronomy;
}
