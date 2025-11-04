package com.gameacademy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "gameProgress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "student_game_idx", def = "{'studentId': 1, 'gameId': 1}")
public class GameProgress {

    @Id
    private String id;

    @Field("studentId")
    @Indexed
    private String studentId;

    @Field("gameId")
    private String gameId;

    @Field("score")
    private Integer score;

    @Field("completionStatus")
    private CompletionStatus completionStatus;

    @Field("attemptsCount")
    @Builder.Default
    private Integer attemptsCount = 0;

    @Field("lastPlayed")
    @Indexed
    private LocalDateTime lastPlayed;

    @Field("timeSpent")
    private Integer timeSpent; // in seconds

    @Field("bestScore")
    @Builder.Default
    private Integer bestScore = 0;

    public enum CompletionStatus {
        IN_PROGRESS,
        COMPLETED
    }
}
