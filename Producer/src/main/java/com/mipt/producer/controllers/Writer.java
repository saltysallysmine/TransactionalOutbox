package com.mipt.producer.controllers;

import com.mipt.producer.model.OutboxRepository;
import com.mipt.producer.model.Plan;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Writer {

    private final OutboxRepository outboxRepository;

    public Writer(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public Long writePlanToOutbox(@NotNull RequestDTO user) {
        log.info("Step into writer");
        Plan plan = new Plan();
        plan.setLogin(user.getLogin());
        plan.setPassword(user.getPassword());
        Long planId = outboxRepository.save(plan).getId();
        log.info("Save plan with id=" + planId);
        return planId;
    }

}
