package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.parser.common.api.schema.ParserApi;
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

    public ParserApi.Wine.Sugar getParserSugar() {
        Sugar instanceSugar = getSugar();
        ParserApi.Wine.Sugar sugar =
                instanceSugar == Sugar.DRY
                        ? ParserApi.Wine.Sugar.DRY
                        : instanceSugar == Sugar.MEDIUM
                        ? ParserApi.Wine.Sugar.MEDIUM
                        : instanceSugar == Sugar.MEDIUM_DRY
                        ? ParserApi.Wine.Sugar.MEDIUM_DRY
                        : instanceSugar == Sugar.SWEET
                        ? ParserApi.Wine.Sugar.SWEET
                        : ParserApi.Wine.Sugar.DRY;// TODO!
        return sugar;
    }

    public ParserApi.Wine.Color getParserColor() {
        Color instanceColor = getColor();
        ParserApi.Wine.Color color =
                instanceColor == Color.RED
                        ? ParserApi.Wine.Color.RED
                        : instanceColor == Color.ROSE
                        ? ParserApi.Wine.Color.ROSE
                        : instanceColor == Color.WHITE
                        ? ParserApi.Wine.Color.WHITE
                        : ParserApi.Wine.Color.UNRECOGNIZED;
        return color;
    }

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
        if (getParserColor() != null)
            builder.setColor(getParserColor());
        if (getParserSugar() != null)
            builder.setSugar(getParserSugar());
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
        if (getAlcoholPercentage() != null)
            builder.setStrength(getAlcoholPercentage().floatValue());
        if (getLink() != null)
            builder.setLink(getLink());
        if (getRegion() != null)
            builder.addRegion(getRegion());
        if (getGrapeSort() != null)
            builder.addGrapeSort(getGrapeSort());
        return builder.build();
    }
}
