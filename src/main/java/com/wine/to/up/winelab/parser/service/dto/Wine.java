package com.wine.to.up.winelab.parser.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wine {
    private String brand;
    private String country;
    //Needs further discussion. Originally suggested float might cause artifacts
    private BigDecimal volume;
    private BigDecimal alcoholPercentage;
    //TODO: Enum of colors
    private String color;
    //TODO: Enum of possible sugar concentration values
    private String sugarConcentration;
    private Integer grapeSortId;
    //Price in cents
    private Integer price;

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setAlcoholPercentage(BigDecimal alcoholPercentage) {
        this.alcoholPercentage = alcoholPercentage;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSugarConcentration(String sugarConcentration) {
        this.sugarConcentration = sugarConcentration;
    }

    public void setGrapeSortId(Integer grapeSortId) {
        this.grapeSortId = grapeSortId;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Wine{" +
                "brand='" + brand + '\'' +
                ", country='" + country + '\'' +
                ", volume=" + volume +
                ", alcoholPercentage=" + alcoholPercentage +
                ", color='" + color + '\'' +
                ", sugarConcentration='" + sugarConcentration + '\'' +
                ", grapeSortId=" + grapeSortId +
                ", price=" + price +
                '}';
    }
}
