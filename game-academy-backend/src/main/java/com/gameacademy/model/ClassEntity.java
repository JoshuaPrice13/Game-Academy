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
import java.util.ArrayList;
import java.util.List;

@Document(collection = "classes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassEntity {

    @Id
    private String id;

    @Field("className")
    private String className;

    @Field("teacherId")
    @Indexed
    private String teacherId;

    @Field("studentIds")
    @Builder.Default
    private List<String> studentIds = new ArrayList<>();

    @Field("goals")
    @Builder.Default
    private List<ClassGoal> goals = new ArrayList<>();

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassGoal {
        private String goalDescription;
        private Integer targetPoints;
        private LocalDateTime deadline;
    }
}
