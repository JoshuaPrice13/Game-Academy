package com.gameacademy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    private String id;

    @Field("gameTitle")
    private String gameTitle;

    @Field("subject")
    @Indexed
    private String subject;

    @Field("gradeLevel")
    @Indexed
    private String gradeLevel;

    @Field("description")
    private String description;

    @Field("maxPoints")
    private Integer maxPoints;

    @Field("difficultyLevel")
    private DifficultyLevel difficultyLevel;

    public enum DifficultyLevel {
        EASY,
        MEDIUM,
        HARD
    }
}
