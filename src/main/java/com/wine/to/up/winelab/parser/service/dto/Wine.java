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
    public enum Color{
        RED, ROSE, WHITE
    }
    private Color color;
    public enum Sugar{
        DRY, MEDIUM_DRY, MEDIUM, SWEET
    }
    private Sugar sugar;
    private String grapeSort;
    private String description;
    private String gastronomy;
}
