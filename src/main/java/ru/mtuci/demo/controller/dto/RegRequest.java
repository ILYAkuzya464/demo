package ru.mtuci.demo.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegRequest {
    private String email;
    private String name;
    private String password;
}

