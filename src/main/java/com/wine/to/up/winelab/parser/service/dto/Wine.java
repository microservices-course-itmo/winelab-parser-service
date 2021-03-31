package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Wine implements Serializable {
    @Id
    private int id;
    // wine name as it is on product page
    private String name;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    // product page address
    private String link;
    // product image
    private String image;
    private String manufacturer;
    private String brand;
    private String country;
    private String region;
    // volume in liters
    private BigDecimal capacity; // он же volume, он же объём
    private BigDecimal strength; // он же alcoholContent, он же процентное содержание алкоголя
    // is wine sparkling
    private boolean sparkling;
    //should enums really be private? We can't access any methods that way.
    private ParserApi.Wine.Color color;
    private ParserApi.Wine.Sugar sugar;
    private String grapeSort;
    private String description;
    private String gastronomy;
    private City city;
    private int inStock;
    private LocalDateTime lastSeen;

    public ParserApi.Wine toParserWine() {
        ParserApi.Wine.Builder builder = ParserApi.Wine.newBuilder();
        updateValue(builder::setName, this.name);
        updateValue(builder::setLink, this.link);
        updateValue(builder::setOldPrice, this.oldPrice, BigDecimal::floatValue);
        updateValue(builder::setNewPrice, this.newPrice, BigDecimal::floatValue);
        updateValue(builder::setImage, this.image);
        updateValue(builder::setBrand, this.brand);
        updateValue(builder::setManufacturer, this.manufacturer);
        updateValue(builder::setCountry, this.country);
        updateValue(builder::addRegion, this.region);
        updateValue(builder::setCapacity, this.capacity, BigDecimal::floatValue);
        updateValue(builder::setStrength, this.strength, BigDecimal::floatValue);
        updateValue(builder::setColor, this.color);
        updateValue(builder::setSugar, this.sugar);
        updateValue(builder::addGrapeSort, this.grapeSort);
        updateValue(builder::setDescription, this.description);
        updateValue(builder::setGastronomy, this.gastronomy);
        updateValue(builder::setCity, this.city, City::toString);
        updateValue(builder::setInStock, Integer.toString(this.inStock));
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

    public double lackPercentage() {
        double res = 0;
        Field[] fields = getClass().getDeclaredFields();
        double fieldValue = 1.0 / fields.length;
        try {
            for (Field f : fields)
                if (f.get(this) == null)
                    res+=fieldValue;
            return res;
        } catch(Exception exception) {
            return res;
        }

    }
    public List<String> lackAttributes() {
        List<String> result = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        try {
            for (Field f : fields)
                if (f.get(this) == null)
                    result.add(f.getName());
            return result;
        } catch(Exception exception) {
            return result;
        }
    }
}