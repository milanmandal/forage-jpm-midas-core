package com.jpmc.midascore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@EnableKafka
public class KafkaListenerClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaListenerClass.class);
    
    @Autowired
    private UserRepository userRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${general.kafka-group-id}")
    public void listen(Transaction transaction) {
        //task 2
        LOGGER.info(" Received Messasge:" + transaction );

        // LOGGER.info(" SenderBal: " + userRepository.findById(transaction.getSenderId()).getBalance());
        // UserRecord user = userRepository.findById(transaction.getSenderId());
        // user.setBalance(user.getBalance() - transaction.getAmount());
        // userRepository.save(user);
        // LOGGER.info(" SenderNewBal: " + userRepository.findById(transaction.getSenderId()).getBalance());

        //task 3
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord receiver = userRepository.findById(transaction.getRecipientId());
        float senderBalance = sender.getBalance();
        float receiverBalance = receiver.getBalance();

        if (senderBalance >= transaction.getAmount()) {
            senderBalance = senderBalance - transaction.getAmount();
            receiverBalance = receiverBalance + transaction.getAmount();
            sender.setBalance(senderBalance);
            receiver.setBalance(receiverBalance);
            userRepository.save(sender);
            userRepository.save(receiver);
            LOGGER.info("New balances: " + sender.getName() + "-" + sender.getBalance() + "..." + receiver.getName() + "-" + receiver.getBalance());
        } else {
            LOGGER.info("Transaction failed. " + sender.getName() + " does not have enough funds to send " + transaction.getAmount() + " to " + receiver.getName());
        }

        //task 4
        final String uri = "http://localhost:8080/incentive";
        RestTemplate restTemplate = new RestTemplate();
        Transaction reqBody = transaction;
        Transaction result = restTemplate.postForObject(uri, reqBody, Transaction.class);
        receiver.setBalance(receiverBalance + result.getAmount());
        userRepository.save(receiver);
        LOGGER.info("After Incentive: " + sender.getName() + "-" + sender.getBalance() + "..." + receiver.getName() + "-" + receiver.getBalance());
        LOGGER.info("Incentive: " + result.getAmount());

        //task 5
        // API call to get the balance of the sender
    }
}
