package com.dh.blogapi.DTOs.Blog;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class BlogDto {
    private Integer Id;
    @NotNull
    private String title;
    @NotNull
    private String body;
    @NotNull
    private String owner;
    private Date createdDate;
    private String status;
    private Integer views;
}
