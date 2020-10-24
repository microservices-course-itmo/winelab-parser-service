package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winelab.parser.service.utils.enums.Color;
import com.wine.to.up.winelab.parser.service.utils.enums.Sugar;

import java.util.Arrays;

public class ApiWine {
    public static ParserApi.Wine dtoToApi(Wine wine) {
        ParserApi.Wine.Builder builder = ParserApi.Wine.newBuilder();

        builder.setName(wine.getName());
        if (wine.getBrand() != null) {
            builder.setBrand(wine.getBrand());
        }
        if (wine.getCountry() != null) {
            builder.setCountry(wine.getCountry());
        }
        if (wine.getVolume() != null) {
            builder.setCapacity(wine.getVolume().floatValue());
        }
        if (wine.getAlcoholContent() != null) {
            builder.setStrength(wine.getAlcoholContent().floatValue());
        }
        if (wine.getOldPrice() != null) {
            builder.setOldPrice(wine.getOldPrice().floatValue());
        }
        if (wine.getNewPrice() != null) {
            builder.setNewPrice(wine.getNewPrice().floatValue());
        }
        builder.setLink(wine.getLink());
        Color color = wine.getColor();
        if (color != null) {
            builder.setColor(ParserApi.Wine.Color.valueOf(color.name()));
        }
        Sugar sugar = wine.getSugar();
        if (sugar != null) {
            builder.setSugar(ParserApi.Wine.Sugar.valueOf(sugar.name()));
        }
        builder.setImage(wine.getImage());
        if (wine.getManufacturer() != null) {
            builder.setManufacturer(wine.getManufacturer());
        }
        if (wine.getRegion() != null) {
            builder.addAllRegion(Arrays.asList(wine.getRegion()));
        }
        if (wine.getGrapeSort() != null) {
            builder.addAllGrapeSort(Arrays.asList(wine.getGrapeSort()));
        }
        builder.setDescription(wine.getDescription());
        builder.setGastronomy(wine.getGastronomy());
        builder.setSparkling(wine.isSparkling());

        return builder.build();
    }
}
