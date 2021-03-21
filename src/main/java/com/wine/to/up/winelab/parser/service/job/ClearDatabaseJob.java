package com.wine.to.up.winelab.parser.service.job;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import com.wine.to.up.winelab.parser.service.repositories.WineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Configuration
public class ClearDatabaseJob {
    private final WineRepository repository;
    private final int DAYS_BEFORE_DELETE = 7;

    public ClearDatabaseJob(WineRepository repository, ParseJob parseJob) {
        this.repository = repository;
    }

    @Scheduled(fixedRateString = "${job.rate.clear.database}")
    public void clearOldWines() {
        repository.deleteAll();
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
