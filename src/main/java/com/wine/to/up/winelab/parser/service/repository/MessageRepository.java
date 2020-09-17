package com.wine.to.up.winelab.parser.service.repository;

import com.wine.to.up.winelab.parser.service.domain.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {
}
