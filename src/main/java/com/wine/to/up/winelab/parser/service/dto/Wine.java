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
        builder.setSparkling(this.sparkling);
        if (getBrand() != null)
            builder.setBrand(getBrand());
        if (getVolume() != null)
            builder.setCapacity(getVolume().floatValue());
        if (getName() != null)
            builder.setName(getName());
        if (getCountry() != null)
            builder.setCountry(getCountry());
        if (getColor() != null)
            builder.setColor(getColor());
        if (getSugar() != null)
            builder.setSugar(getSugar());
        if (getImage() != null)
            builder.setImage(getImage());
        if (getManufacturer() != null)
            builder.setManufacturer(getManufacturer());
        if (getNewPrice() != null)
            builder.setNewPrice(getNewPrice().floatValue());
        if (getOldPrice() != null)
            builder.setOldPrice(getOldPrice().floatValue());
        if (getGastronomy() != null)
            builder.setGastronomy(getGastronomy());
        if (getDescription() != null)
            builder.setDescription(getDescription());
        if (getAlcoholContent() != null)
            builder.setStrength(getAlcoholContent().floatValue());
        if (getLink() != null)
            builder.setLink(getLink());
        if (getRegion() != null)
            builder.addRegion(getRegion());
        if (getGrapeSort() != null)
            builder.addGrapeSort(getGrapeSort());
        return builder.build();
    }
}
