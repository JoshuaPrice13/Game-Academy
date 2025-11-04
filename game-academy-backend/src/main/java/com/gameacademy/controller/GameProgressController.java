package com.gameacademy.controller;

import com.gameacademy.model.GameProgress;
import com.gameacademy.service.GameProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Game Progress", description = "Game progress tracking APIs")
public class GameProgressController {

    private final GameProgressService gameProgressService;

    @PostMapping("/record")
    @Operation(summary = "Record a game session")
    public ResponseEntity<GameProgress> recordGameSession(@RequestBody Map<String, Object> request) {
        String studentId = (String) request.get("studentId");
        String gameId = (String) request.get("gameId");
        int score = (Integer) request.get("score");
        int timeSpent = (Integer) request.get("timeSpent");

        GameProgress progress = gameProgressService.recordGameSession(studentId, gameId, score, timeSpent);
        return new ResponseEntity<>(progress, HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all progress for a student")
    public ResponseEntity<List<GameProgress>> getStudentProgress(@PathVariable String studentId) {
        List<GameProgress> progress = gameProgressService.getAllStudentProgress(studentId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/student/{studentId}/game/{gameId}")
    @Operation(summary = "Get specific game progress for a student")
    public ResponseEntity<GameProgress> getStudentGameProgress(
            @PathVariable String studentId,
            @PathVariable String gameId) {
        GameProgress progress = gameProgressService.getStudentGameProgress(studentId, gameId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/student/{studentId}/recent")
    @Operation(summary = "Get recent games for a student")
    public ResponseEntity<List<GameProgress>> getRecentGames(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "5") int limit) {
        List<GameProgress> recentGames = gameProgressService.getRecentGames(studentId, limit);
        return ResponseEntity.ok(recentGames);
    }

    @GetMapping("/student/{studentId}/completion")
    @Operation(summary = "Get game completion percentage for a student")
    public ResponseEntity<Map<String, Double>> getGameCompletion(@PathVariable String studentId) {
        double completion = gameProgressService.calculateGameCompletion(studentId);
        return ResponseEntity.ok(Map.of("completionPercentage", completion));
    }
}
