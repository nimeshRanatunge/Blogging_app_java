package com.dh.blogapi.DTOs.User;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private String username;
    private String email;
    private String permissionLevel;
    private Date joinedDate;
    private String imageUrl;
}
