package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.winelab.parser.service.utils.enums.Color;
import com.wine.to.up.winelab.parser.service.utils.enums.Sugar;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Wine implements Serializable {
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
    private Color color;
    private Sugar sugar;
    private String grapeSort;
    private String description;
    private String gastronomy;
}
