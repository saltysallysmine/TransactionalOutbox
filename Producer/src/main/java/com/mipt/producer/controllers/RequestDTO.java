package com.mipt.producer.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class RequestDTO {

    @NotNull
    private String login;

    @NotNull
    private String password;

}
