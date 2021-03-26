package com.wine.to.up.winelab.parser.service.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WineLocalInfo {
    private String cityName;
    private int inStock;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
}
