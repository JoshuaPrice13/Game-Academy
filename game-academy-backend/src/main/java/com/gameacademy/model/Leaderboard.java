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

@Document(collection = "leaderboards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "class_period_idx", def = "{'classId': 1, 'period': 1}")
public class Leaderboard {

    @Id
    private String id;

    @Field("classId")
    private String classId;

    @Field("period")
    private Period period;

    @Field("rankings")
    @Builder.Default
    private List<RankingEntry> rankings = new ArrayList<>();

    @Field("lastUpdated")
    @Indexed
    private LocalDateTime lastUpdated;

    public enum Period {
        DAILY,
        WEEKLY,
        ALL_TIME
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingEntry {
        private String studentId;
        private String studentName;
        private Integer points;
        private Integer rank;
    }
}
