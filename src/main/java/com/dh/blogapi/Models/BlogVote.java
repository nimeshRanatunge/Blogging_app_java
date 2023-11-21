package com.dh.blogapi.Models;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "blogs_vote")
@Entity
public class BlogVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "blogId")
    private Integer blogId;
    @Column(name = "username")
    private String username;
    @Column(name = "vote")
    private Integer vote;
}
