package com.gameacademy.controller;

import com.gameacademy.model.Leaderboard;
import com.gameacademy.model.Student;
import com.gameacademy.service.LeaderboardAnalyticsService;
import com.gameacademy.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Leaderboard and ranking APIs")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final LeaderboardAnalyticsService analyticsService;

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get class leaderboard by period")
    public ResponseEntity<Leaderboard> getClassLeaderboard(
            @PathVariable String classId,
            @RequestParam(defaultValue = "ALL_TIME") Leaderboard.Period period) {
        Leaderboard leaderboard = leaderboardService.getLeaderboardByClass(classId, period);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/class/{classId}/student/{studentId}")
    @Operation(summary = "Get student rank with surrounding context")
    public ResponseEntity<Map<String, Object>> getStudentRankWithContext(
            @PathVariable String classId,
            @PathVariable String studentId,
            @RequestParam(defaultValue = "ALL_TIME") Leaderboard.Period period,
            @RequestParam(defaultValue = "5") int contextSize) {

        Optional<Leaderboard.RankingEntry> studentRank =
                leaderboardService.getStudentRank(studentId, classId, period);

        List<Leaderboard.RankingEntry> context =
                leaderboardService.getLeaderboardContext(studentId, classId, period, contextSize);

        Map<String, Object> response = new HashMap<>();
        response.put("studentRank", studentRank.orElse(null));
        response.put("surroundingRanks", context);
        response.put("period", period);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}/top/{limit}")
    @Operation(summary = "Get top performers in class")
    public ResponseEntity<List<Student>> getTopPerformers(
            @PathVariable String classId,
            @PathVariable int limit) {
        List<Student> topPerformers = analyticsService.getTopPerformers(classId, limit);
        return ResponseEntity.ok(topPerformers);
    }

    @GetMapping("/class/{classId}/analytics")
    @Operation(summary = "Get class analytics and statistics")
    public ResponseEntity<Map<String, Object>> getClassAnalytics(@PathVariable String classId) {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("averageScore", analyticsService.getClassAverageScore(classId));
        analytics.put("pointsDistribution", analyticsService.getPointsDistribution(classId));
        analytics.put("engagementMetrics", analyticsService.getClassEngagementMetrics(classId));

        return ResponseEntity.ok(analytics);
    }

    @PostMapping("/refresh/{classId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Manually trigger leaderboard refresh (teacher only)")
    public ResponseEntity<Map<String, String>> refreshLeaderboard(@PathVariable String classId) {
        leaderboardService.updateLeaderboard(classId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leaderboard refreshed successfully");
        response.put("classId", classId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}/struggling-students")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Identify struggling students (teacher only)")
    public ResponseEntity<List<Student>> getStrugglingStudents(
            @PathVariable String classId,
            @RequestParam(defaultValue = "70") double threshold) {
        List<Student> strugglingStudents =
                analyticsService.identifyStrugglingStudents(classId, threshold);
        return ResponseEntity.ok(strugglingStudents);
    }

    @GetMapping("/student/{studentId}/trend")
    @Operation(summary = "Get student improvement trend")
    public ResponseEntity<Map<String, Object>> getStudentTrend(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> trend = analyticsService.getImprovementTrend(studentId, days);
        return ResponseEntity.ok(trend);
    }
}
