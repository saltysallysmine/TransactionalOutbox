package com.mipt.worker.service;

import com.google.gson.Gson;

import com.mipt.worker.configuration.BrokerConfiguration;
import com.rabbitmq.client.Channel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class TasksReceiver {

    Set<Long> sentEmailsId = new HashSet<>();

    @Data
    private static class MessageDTO {
        private Long id;
        private String login;
    }

    private MessageDTO getMessageDTOFromMessageText(String text) {
        Gson gson = new Gson();
        return gson.fromJson(text, MessageDTO.class);
    }

    @RabbitListener(queues = BrokerConfiguration.QUERY_NAME, ackMode = "MANUAL")
    public void receiveFromEmailQuery(String text, MessageHeaders headers, Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long tag)
            throws IOException, InterruptedException {
        MessageDTO message = getMessageDTOFromMessageText(text);
        log.info("Get message from Email query. User(login=%s) from Plan#%d"
                .formatted(message.getLogin(), message.getId()));
        if (sentEmailsId.contains(message.getId())) {
            log.info("Already sent message to this user");
        } else {
            sentEmailsId.add(message.getId());
            log.info("Send email to User(login=%s) by Plan#%d".formatted(message.getLogin(), message.getId()));
        }
        Thread.sleep(1000);
        channel.basicAck(tag, false);
    }

}
