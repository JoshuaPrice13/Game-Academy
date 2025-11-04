package com.gameacademy.controller;

import com.gameacademy.model.GameSession;
import com.gameacademy.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/game-sessions")
@RequiredArgsConstructor
@Tag(name = "Game Sessions", description = "Game session management endpoints")
public class GameSessionController {

    private final GameService gameService;

    @PostMapping("/start")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Start a new game session")
    public ResponseEntity<GameSession> startSession(
            @RequestParam String studentId,
            @RequestParam String gameId) {
        GameSession session = gameService.startGameSession(studentId, gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PutMapping("/{sessionId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update game session progress")
    public ResponseEntity<GameSession> updateProgress(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> progressData) {
        GameSession session = gameService.updateGameProgress(sessionId, progressData);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{sessionId}/pause")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Pause an active game session")
    public ResponseEntity<GameSession> pauseSession(@PathVariable String sessionId) {
        GameSession session = gameService.pauseGameSession(sessionId);
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{sessionId}/resume")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Resume a paused game session")
    public ResponseEntity<GameSession> resumeSession(@PathVariable String sessionId) {
        GameSession session = gameService.resumeGameSession(sessionId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{sessionId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Complete a game session and get results")
    public ResponseEntity<Map<String, Object>> completeSession(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> finalData) {
        Map<String, Object> result = gameService.completeGameSession(sessionId, finalData);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{sessionId}/abandon")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Abandon an incomplete game session")
    public ResponseEntity<GameSession> abandonSession(@PathVariable String sessionId) {
        GameSession session = gameService.abandonGameSession(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get game session details")
    public ResponseEntity<GameSession> getSession(@PathVariable String sessionId) {
        GameSession session = gameService.getSessionDetails(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all sessions for a student")
    public ResponseEntity<List<GameSession>> getStudentSessions(@PathVariable String studentId) {
        List<GameSession> sessions = gameService.getStudentSessions(studentId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get active session for a student and game")
    public ResponseEntity<GameSession> getActiveSession(
            @RequestParam String studentId,
            @RequestParam String gameId) {
        Optional<GameSession> session = gameService.getActiveSession(studentId, gameId);
        return session.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
