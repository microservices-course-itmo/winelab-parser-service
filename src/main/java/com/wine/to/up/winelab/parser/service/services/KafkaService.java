package com.wine.to.up.winelab.parser.service.services;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.winelab.parser.service.dto.Wine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaService {

    private KafkaMessageSender<Wine> kafkaSendMessageService;

    @Autowired
    public KafkaService(KafkaMessageSender<Wine> kafkaSendMessageService) {
        this.kafkaSendMessageService = kafkaSendMessageService;
    }

    void sendWine(Wine wine) {
        kafkaSendMessageService.sendMessage(wine);
    }

    void sendWineList(List<Wine> wineList) {
        wineList.forEach(wine -> kafkaSendMessageService.sendMessage(wine));
    }

}