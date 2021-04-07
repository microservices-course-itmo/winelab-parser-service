package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/database")
@Slf4j
@Configuration
public class DatabaseController {

    private final int DAYS_BEFORE_DELETE = 7;
    private final WineRepository repository;

    public DatabaseController(WineRepository repository) {
        this.repository = repository;
    }

    @PutMapping("/clear")
    public void clearDatabase() {
        repository.deleteAll();
        log.info("Database was cleared");
    }

    @PutMapping("/clear/old")
    public void clearOld() {
        List<Wine> winesToDelete = repository.findAll()
                .stream()
                .filter(wine -> wine.getLastSeen()
                        .plusDays(DAYS_BEFORE_DELETE)
                        .isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        repository.deleteAll(winesToDelete);
        log.info("Cleared database from old entries, total {} wines deleted", winesToDelete.size());
    }
}
