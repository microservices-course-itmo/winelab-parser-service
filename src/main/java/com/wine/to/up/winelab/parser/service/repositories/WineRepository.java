package com.wine.to.up.winelab.parser.service.repositories;

import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WineRepository extends MongoRepository<Wine, Integer> {}
