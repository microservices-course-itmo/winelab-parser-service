package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.parser.common.api.schema.ParserApi;
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
    // product page address
    private String link;
    // price without discount in rubles
    private BigDecimal oldPrice;
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
    private BigDecimal alcoholContent;
    // is wine sparkling
    private boolean sparkling;
    //should enums really be private? We can't access any methods that way.
    private ParserApi.Wine.Color color;
    private ParserApi.Wine.Sugar sugar;
    private String grapeSort;
    private String description;
    private String gastronomy;

    public ParserApi.Wine toParserWine() {
        ParserApi.Wine.Builder builder = ParserApi.Wine.newBuilder();
        builder
                .setSparkling(this.sparkling)
                .setBrand(this.brand)
                .setCapacity(this.volume.floatValue())
                .setName(this.name)
                .setCountry(this.country)
                .setColor(this.color)
                .setSugar(this.sugar)
                .setImage(this.image)
                .setManufacturer(this.manufacturer)
                .setNewPrice(this.newPrice.floatValue())
                .setOldPrice(this.oldPrice.floatValue())
                .setGastronomy(this.gastronomy)
                .setDescription(this.description)
                .setStrength(this.alcoholContent.floatValue())
                .setLink(this.link)
                .addRegion(this.region)
                .addGrapeSort(this.grapeSort);
        return builder.build();
    }
}