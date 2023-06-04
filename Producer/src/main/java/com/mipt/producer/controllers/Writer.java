package com.mipt.producer.controllers;

import com.mipt.producer.model.OutboxRepository;
import com.mipt.producer.model.Plan;

import com.mipt.producer.model.UsersRepository;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class Writer {

    private final OutboxRepository outboxRepository;
    private final UsersRepository usersRepository;
    private final RabbitTemplate rabbitTemplate;

    public Writer(OutboxRepository outboxRepository, UsersRepository usersRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.usersRepository = usersRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public Long writePlanToOutbox(@NotNull RequestDTO user) {
        Plan plan = new Plan();
        plan.setLogin(user.getLogin());
        plan.setPassword(user.getPassword());
        Long planId = outboxRepository.save(plan).getId();
        log.info("Save plan with id=" + planId + " to outbox");
        return planId;
    }

    @Transactional
    private void ImplementSinglePlan(Plan plan) {
        log.info("Start processing Plan(isWrittenToDB=%s, isWrittenToBroker=%s)"
                .formatted(plan.getIsWrittenToDB(), plan.getIsWrittenToBroker()));
    }

    @Scheduled(fixedDelay = 5000)
    public void ImplementPlans() {
        log.info("Scheduler starts");
        List<Plan> plansNotWritten = outboxRepository.findByProgress();
        log.info("Found " + plansNotWritten.size() + " not written tasks");
        plansNotWritten.forEach(plan -> {
            ImplementSinglePlan(plan);
        });
    }

}
