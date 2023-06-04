package com.mipt.producer.controllers;

import com.mipt.producer.model.OutboxRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.jetbrains.annotations.NotNull;

@Slf4j
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private OutboxRepository outboxRepository;

    Writer writer;

    @PostMapping("/configure")
    public void Configure() {
        log.info("Configure writer");
        this.writer = new Writer(outboxRepository);
    }

    /*
     * Write plan to outbox and return its id
     */
    @PostMapping("/add-user")
    @ResponseBody
    public Long AddUser(@RequestBody @NotNull RequestDTO user) {
        log.info("Get new request: " + user);
        return writer.writePlanToOutbox(user);
    }

}
