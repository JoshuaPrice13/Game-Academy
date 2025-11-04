package com.gameacademy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressDTO {

    private String studentId;
    private String username;
    private Integer totalPoints;
    private Integer level;
    private List<RecentGameDTO> recentGames;
    private Integer gamesCompleted;
    private Integer totalTimeSpent;
    private Double averageScore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentGameDTO {
        private String gameId;
        private String gameTitle;
        private Integer score;
        private String completionStatus;
        private String lastPlayed;
    }
}
