package com.dh.blogapi.Models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "passwordHash")
    private String passwordHash;

    @Column(name = "permissionLevel")
    private String permissionLevel;

    @Column(name = "joinedDate")
    private Date joinedDate;

    @Column(name = "imageUrl")
    private String imageUrl;

}
