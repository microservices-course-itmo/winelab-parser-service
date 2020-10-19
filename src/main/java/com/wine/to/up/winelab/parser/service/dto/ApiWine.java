package com.wine.to.up.winelab.parser.service.dto;

import com.wine.to.up.parser.common.api.schema.ParserApi;

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
        // TODO set color and sugar in a more proper way
        if (wine.getColor() != null) {
            builder.setColor(ParserApi.Wine.Color.forNumber(wine.getColor().ordinal()));
        }
        if (wine.getSugar() != null) {
            builder.setSugar(ParserApi.Wine.Sugar.forNumber(wine.getSugar().ordinal()));
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
