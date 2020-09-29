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
}
