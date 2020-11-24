package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Function;

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
    private ParserApi.Wine.Color color;
    private ParserApi.Wine.Sugar sugar;
    private String grapeSort;
    private String description;
    private String gastronomy;

    public ParserApi.Wine toParserWine() {
        ParserApi.Wine.Builder builder = ParserApi.Wine.newBuilder();
        updateValue(builder::setName, this.name);
        updateValue(builder::setLink, this.link);
        updateValue(builder::setNewPrice, this.newPrice, BigDecimal::floatValue);
        updateValue(builder::setOldPrice, this.oldPrice, BigDecimal::floatValue);
        updateValue(builder::setImage, this.image);
        updateValue(builder::setBrand, this.brand);
        updateValue(builder::setManufacturer, this.manufacturer);
        updateValue(builder::setCountry, this.country);
        updateValue(builder::addRegion, this.region);
        updateValue(builder::setCapacity, this.volume, BigDecimal::floatValue);
        updateValue(builder::setStrength, this.alcoholContent, BigDecimal::floatValue);
        updateValue(builder::setColor, this.color);
        updateValue(builder::setSugar, this.sugar);
        updateValue(builder::addGrapeSort, this.grapeSort);
        updateValue(builder::setDescription, this.description);
        updateValue(builder::setGastronomy, this.gastronomy);
        return builder.build();
    }

    private <T> void updateValue(Consumer<T> setterMethod, T value) {
        if (value != null) {
            setterMethod.accept(value);
        }
    }

    private <T, R> void updateValue(Consumer<T> setterMethod, R value, Function<R, T> converter) {
        if (value != null) {
            setterMethod.accept(converter.apply(value));
        }
    }
}