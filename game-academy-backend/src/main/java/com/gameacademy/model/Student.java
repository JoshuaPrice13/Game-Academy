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
import java.util.ArrayList;
import java.util.List;

@Document(collection = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "class_points_idx", def = "{'classId': 1, 'totalPoints': -1}")
public class Student {

    @Id
    private String id;

    @Field("userId")
    @Indexed
    private String userId;

    @Field("classId")
    @Indexed
    private String classId;

    @Field("totalPoints")
    @Builder.Default
    private Integer totalPoints = 0;

    @Field("level")
    @Builder.Default
    private Integer level = 1;

    @Field("achievements")
    @Builder.Default
    private List<Achievement> achievements = new ArrayList<>();

    @Field("progressData")
    @Builder.Default
    private ProgressData progressData = new ProgressData();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Achievement {
        private String achievementName;
        private LocalDateTime earnedAt;
        private Integer points;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressData {
        @Builder.Default
        private Integer gamesCompleted = 0;
        @Builder.Default
        private Integer totalTimeSpent = 0; // in seconds
        @Builder.Default
        private Double averageScore = 0.0;
    }
}
