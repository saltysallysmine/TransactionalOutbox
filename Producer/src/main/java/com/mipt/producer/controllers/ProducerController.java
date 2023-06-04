package com.mipt.producer.controllers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.jetbrains.annotations.NotNull;

@Slf4j
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String QUERY_NAME = "QUERY";

    @Data
    private static class RequestDTO {

        @NotNull
        String login;

        @NotNull
        String password;

    }

    @PostMapping("/add-user")
    @ResponseBody
    public ResponseEntity<String> addUser(@RequestBody @NotNull RequestDTO request) {
        log.info("Get new request: " + request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
