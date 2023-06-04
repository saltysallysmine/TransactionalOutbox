package com.mipt.producer.controllers;

import com.mipt.producer.model.OutboxRepository;
import com.mipt.producer.model.Plan;

import com.mipt.producer.model.User;
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
    private final String QUERY_NAME;

    public Writer(OutboxRepository outboxRepository, UsersRepository usersRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.usersRepository = usersRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.QUERY_NAME = "QUERY";
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
    private void writeUserToDB(Plan plan) {
        if (usersRepository.findByLogin(plan.getLogin()).isEmpty()) {
            User user = new User();
            user.setLogin(plan.getLogin());
            user.setPassword(plan.getLogin());
            usersRepository.save(user);
            log.info("Save User(login=%s, password=%s) to Postgres".formatted(user.getLogin(), user.getPassword()));
        }
        plan.setIsWrittenToDB(true);
        outboxRepository.save(plan);
        log.info("Update outbox for Plan#" + plan.getId() + ". Written to Postgres");
    }

    @Transactional
    private void writeUserToBroker(Plan plan) {
        rabbitTemplate.convertAndSend(QUERY_NAME, "{\"login\": \"%s\"}".formatted(plan.getLogin()));
        plan.setIsWrittenToBroker(true);
        outboxRepository.save(plan);
        log.info("Send message with User(login=%s, password=%s) to Rabbit".formatted(plan.getLogin(),
                plan.getPassword()));
        log.info("Update outbox for Plan#" + plan.getId() + ". Written to Rabbit");
    }

    @Transactional
    private void ImplementSinglePlan(Plan plan) {
        log.info("Start processing Plan(isWrittenToDB=%s, isWrittenToBroker=%s)"
                .formatted(plan.getIsWrittenToDB(), plan.getIsWrittenToBroker()));
        if (!plan.getIsWrittenToDB()) {
            log.info("Trying to save User(login=%s, password=%s) to Postgres".formatted(plan.getLogin(),
                    plan.getPassword()));
            writeUserToDB(plan);
        }
        if (!plan.getIsWrittenToBroker()) {
            log.info("Trying to send message with User(login=%s, password=%s) to Rabbit".formatted(plan.getLogin(),
                    plan.getPassword()));
            writeUserToBroker(plan);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void ImplementPlans() {
        log.info("Scheduler starts");
        List<Plan> plansNotWritten = outboxRepository.findByProgress();
        log.info("Found " + plansNotWritten.size() + " not written tasks");
        plansNotWritten.forEach(this::ImplementSinglePlan);
    }

}
