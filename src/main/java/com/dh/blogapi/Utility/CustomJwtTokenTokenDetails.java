package com.dh.blogapi.Utility;

import lombok.Data;

@Data
public class CustomJwtTokenTokenDetails {
    private String username;
    private String permissionLevel;
}
