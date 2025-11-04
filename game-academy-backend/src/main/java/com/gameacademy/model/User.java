package com.gameacademy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Field("username")
    @Indexed(unique = true)
    private String username;

    @Field("email")
    @Indexed(unique = true)
    private String email;

    @Field("passwordHash")
    private String passwordHash;

    @Field("role")
    private UserRole role; // STUDENT or TEACHER

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("lastLogin")
    private LocalDateTime lastLogin;

    public enum UserRole {
        STUDENT,
        TEACHER
    }
}
