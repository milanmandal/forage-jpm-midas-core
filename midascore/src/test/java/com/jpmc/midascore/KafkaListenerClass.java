package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
@KafkaListener(topics = "${general.kafka-topic}", groupId = "${general.kafka-group-id}")
public class KafkaListenerClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaListenerClass.class);
    
    public void listen(Transaction transaction) {
        LOGGER.info(" Received Messasge:" + transaction);
    }
}
