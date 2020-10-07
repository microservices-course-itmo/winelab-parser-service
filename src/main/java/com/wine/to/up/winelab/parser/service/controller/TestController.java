package com.wine.to.up.winelab.parser.service.controller;

import com.wine.to.up.winelab.parser.service.domain.entity.Message;
import com.wine.to.up.winelab.parser.service.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public void test(@RequestParam String message) {
        log.info("some info");
        messageRepository.save(new Message(message));
    }
}
