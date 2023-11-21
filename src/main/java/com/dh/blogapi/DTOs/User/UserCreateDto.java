package com.dh.blogapi.DTOs.User;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserCreateDto {
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
    private String imageUrl;
}

