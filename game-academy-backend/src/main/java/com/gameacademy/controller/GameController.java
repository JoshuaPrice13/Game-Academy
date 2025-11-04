package com.gameacademy.controller;

import com.gameacademy.model.Game;
import com.gameacademy.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Games", description = "Game management APIs")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "Get all games")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "Get game details")
    public ResponseEntity<Game> getGameDetails(@PathVariable String gameId) {
        Game game = gameService.getGameDetails(gameId);
        return ResponseEntity.ok(game);
    }

    @GetMapping("/subject/{subject}")
    @Operation(summary = "Get games by subject")
    public ResponseEntity<List<Game>> getGamesBySubject(@PathVariable String subject) {
        List<Game> games = gameService.getGamesBySubject(subject);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/grade/{gradeLevel}")
    @Operation(summary = "Get games by grade level")
    public ResponseEntity<List<Game>> getGamesByGradeLevel(@PathVariable String gradeLevel) {
        List<Game> games = gameService.getGamesByGradeLevel(gradeLevel);
        return ResponseEntity.ok(games);
    }

    @PostMapping
    @Operation(summary = "Create a new game (admin only)")
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game createdGame = gameService.createGame(game);
        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
    }

    @PutMapping("/{gameId}")
    @Operation(summary = "Update game details (admin only)")
    public ResponseEntity<Game> updateGame(
            @PathVariable String gameId,
            @RequestBody Game game) {
        Game updatedGame = gameService.updateGame(gameId, game);
        return ResponseEntity.ok(updatedGame);
    }
}
