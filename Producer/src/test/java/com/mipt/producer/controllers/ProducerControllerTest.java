package com.mipt.producer.controllers;

import com.google.gson.Gson;
import com.mipt.producer.model.OutboxRepository;

import com.mipt.producer.model.Plan;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OutboxRepository outboxRepository;

    private void configureWriter() throws Exception {
        mockMvc.perform(post("/producer/configure")).andExpect(status().isOk());
    }

    private String getRequestContent(RequestDTO userDTO) {
        Gson gson = new Gson();
        return gson.toJson(userDTO);
    }

    private Plan getPlanFromUserDTO(RequestDTO userDTO) {
        Plan result =  new Plan();
        result.setLogin(userDTO.getLogin());
        result.setPassword(userDTO.getPassword());
        return result;
    }

    private void assertPlanEquals(Plan expectedPlan, Plan actualPlan) {
        assertEquals(expectedPlan.getLogin(), actualPlan.getLogin());
        assertEquals(expectedPlan.getPassword(), actualPlan.getPassword());
        assertEquals(expectedPlan.getIsWrittenToDB(), actualPlan.getIsWrittenToDB());
        assertEquals(expectedPlan.getIsWrittenToBroker(), actualPlan.getIsWrittenToBroker());
    }

    private String addUserAndReturnResponse(String content) throws Exception {
        return mockMvc.perform(post("/producer/add-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    @Test
    public void AddUserTest() throws Exception {
        // Configure writer
        configureWriter();
        // Test functions
        RequestDTO userDTO = new RequestDTO("User", "12345");
        String response = addUserAndReturnResponse(getRequestContent(userDTO));
        Long actualPlanId = Long.valueOf(response);
        // expected plan that should be added
        Plan expectedPlan = getPlanFromUserDTO(userDTO);
        // actual added plan
        Optional<Plan> actualPlanRecord = outboxRepository.findById(actualPlanId);
        assertFalse(actualPlanRecord.isEmpty());
        Plan actualPlan = actualPlanRecord.get();
        // assert plans equals
        assertPlanEquals(expectedPlan, actualPlan);
    }

}