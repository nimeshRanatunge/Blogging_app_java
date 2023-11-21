package com.dh.blogapi.Errors;

import lombok.Data;

@Data
public class ApiError {
    private Integer status;
    private String message;
    private long timeStamp;
    private String path;

    public ApiError(Integer status, String message, long timeStamp, String path) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
        this.path = path;
    }
}
