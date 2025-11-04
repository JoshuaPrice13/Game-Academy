package com.gameacademy.service;

import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.Game;
import com.gameacademy.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public List<Game> getGamesBySubject(String subject) {
        return gameRepository.findBySubject(subject);
    }

    public Game getGameDetails(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));
    }

    @Transactional
    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    @Transactional
    public Game updateGame(String gameId, Game gameData) {
        Game existingGame = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));

        existingGame.setGameTitle(gameData.getGameTitle());
        existingGame.setSubject(gameData.getSubject());
        existingGame.setGradeLevel(gameData.getGradeLevel());
        existingGame.setDescription(gameData.getDescription());
        existingGame.setMaxPoints(gameData.getMaxPoints());
        existingGame.setDifficultyLevel(gameData.getDifficultyLevel());

        return gameRepository.save(existingGame);
    }

    public List<Game> getGamesByGradeLevel(String gradeLevel) {
        return gameRepository.findByGradeLevel(gradeLevel);
    }
}
