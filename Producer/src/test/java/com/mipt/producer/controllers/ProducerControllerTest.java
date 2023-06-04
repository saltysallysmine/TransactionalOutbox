package com.mipt.producer.controllers;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jetbrains.annotations.NotNull;

@SpringBootTest
@AutoConfigureMockMvc
class ProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static ProducerController producerController;

    @BeforeAll
    public static void InitializeController() {
        producerController = new ProducerController();
    }

    @Data
    @AllArgsConstructor
    private static class RequestDTO {

        @NotNull
        String login;

        @NotNull
        String password;

    }

    private String getUserDTO() {
        Gson gson = new Gson();
        RequestDTO requestDTO = new RequestDTO("User", "12345");
        return gson.toJson(requestDTO);
    }

    @Test
    public void AddUserTest() throws Exception {
        mockMvc.perform(post("/producer/add-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getUserDTO())
        ).andExpect(status().isCreated());
    }

}