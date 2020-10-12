package com.wine.to.up.winelab.parser.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApiWine {
    private String name;
    private String brand;
    private String country;
    private float capacity;
    private float strength;
    private int color;
    private int sugar;
    private float old_price;
    private float new_price;
    private String image;
    private String manufacturer;
    private List<String> region;
    private String link;
    private List<String> grapeSort;
    private int year;
    private String description;
    private String gastronomy;
    private String taste;
    private String flavor;
    private float rating;
    private boolean sparkling;

    public ApiWine(Wine dto) {
        this.name = dto.getName();
        this.brand = dto.getBrand();
        this.capacity = dto.getVolume().floatValue();
        this.strength = dto.getAlcoholPercentage().floatValue();
        this.old_price = dto.getOldPrice().floatValue();
        this.new_price = dto.getNewPrice().floatValue();
        this.image = dto.getImage();
        this.manufacturer = dto.getManufacturer();
        this.region = List.of(dto.getRegion());
        this.link = dto.getLink();
        this.grapeSort = List.of(dto.getGrapeSort());
        this.year = 0;
        this.description = dto.getDescription();
        this.gastronomy = dto.getGastronomy();
        this.rating = 0;
        this.sparkling = dto.isSparkling();
        this.color = dto.getColor().ordinal();
        this.sugar = dto.getSugar().ordinal();
        this.country = dto.getCountry();
        this.description = dto.getDescription();
        //TODO: discuss what should we send as flavor and/or taste
        this.flavor = null;
        this.taste = null;
    }
}
