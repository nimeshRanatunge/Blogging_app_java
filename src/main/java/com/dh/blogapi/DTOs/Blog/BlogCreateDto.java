package com.dh.blogapi.DTOs.Blog;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BlogCreateDto {
    @NotNull
    private String title;
    @NotNull
    private String body;

}
