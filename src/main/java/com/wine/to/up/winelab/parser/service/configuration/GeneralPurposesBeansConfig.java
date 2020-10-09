package com.wine.to.up.winelab.parser.service.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.to.up.winelab.parser.service.services.ParserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralPurposesBeansConfig {

    /**
     * Model mapper bean
     */
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    /**
     * Object mapper bean
     */
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ParserService getParser() {
        return new ParserService();
    }
}
