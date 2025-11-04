package com.gameacademy.service;

import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.Game;
import com.gameacademy.model.GameSession;
import com.gameacademy.model.GameProgress;
import com.gameacademy.model.Student;
import com.gameacademy.repository.GameRepository;
import com.gameacademy.repository.GameSessionRepository;
import com.gameacademy.repository.GameProgressRepository;
import com.gameacademy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameProgressRepository gameProgressRepository;
    private final StudentRepository studentRepository;
    private final AchievementService achievementService;
    private final NotificationService notificationService;

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

    // ==================== Game Session Management ====================

    /**
     * Start a new game session for a student
     */
    @Transactional
    public GameSession startGameSession(String studentId, String gameId) {
        // Verify student and game exist
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));

        // Check if there's already an active session for this student/game
        Optional<GameSession> existingSession = gameSessionRepository
                .findByStudentIdAndGameIdAndStatus(studentId, gameId, GameSession.SessionStatus.ACTIVE);

        if (existingSession.isPresent()) {
            logger.warn("Student {} already has an active session for game {}", studentId, gameId);
            return existingSession.get();
        }

        // Create new session
        GameSession session = new GameSession();
        session.setStudentId(studentId);
        session.setGameId(gameId);
        session.setStatus(GameSession.SessionStatus.ACTIVE);
        session.setStartTime(LocalDateTime.now());

        GameSession savedSession = gameSessionRepository.save(session);
        logger.info("Started game session {} for student {} on game {}", savedSession.getId(), studentId, gameId);

        return savedSession;
    }

    /**
     * Update game session progress (called periodically during gameplay)
     */
    @Transactional
    public GameSession updateGameProgress(String sessionId, Map<String, Object> progressData) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game session not found: " + sessionId));

        if (session.getStatus() != GameSession.SessionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot update inactive session: " + sessionId);
        }

        // Update score and answers
        if (progressData.containsKey("currentScore")) {
            session.setCurrentScore((Integer) progressData.get("currentScore"));
        }
        if (progressData.containsKey("questionsAnswered")) {
            session.setQuestionsAnswered((Integer) progressData.get("questionsAnswered"));
        }
        if (progressData.containsKey("correctAnswers")) {
            session.setCorrectAnswers((Integer) progressData.get("correctAnswers"));
        }
        if (progressData.containsKey("incorrectAnswers")) {
            session.setIncorrectAnswers((Integer) progressData.get("incorrectAnswers"));
        }
        if (progressData.containsKey("timeSpentSeconds")) {
            session.setTimeSpentSeconds((Long) progressData.get("timeSpentSeconds"));
        }

        // Update game-specific state
        if (progressData.containsKey("gameState")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> gameState = (Map<String, Object>) progressData.get("gameState");
            session.setGameState(gameState);
        }

        GameSession updatedSession = gameSessionRepository.save(session);
        logger.debug("Updated game session {}: score={}, questions={}",
                sessionId, session.getCurrentScore(), session.getQuestionsAnswered());

        return updatedSession;
    }

    /**
     * Pause an active game session
     */
    @Transactional
    public GameSession pauseGameSession(String sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game session not found: " + sessionId));

        if (session.getStatus() != GameSession.SessionStatus.ACTIVE) {
            throw new IllegalStateException("Can only pause active sessions");
        }

        session.setStatus(GameSession.SessionStatus.PAUSED);
        session.setLastPausedTime(LocalDateTime.now());

        GameSession updatedSession = gameSessionRepository.save(session);
        logger.info("Paused game session {}", sessionId);

        return updatedSession;
    }

    /**
     * Resume a paused game session
     */
    @Transactional
    public GameSession resumeGameSession(String sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game session not found: " + sessionId));

        if (session.getStatus() != GameSession.SessionStatus.PAUSED) {
            throw new IllegalStateException("Can only resume paused sessions");
        }

        // Calculate paused duration
        if (session.getLastPausedTime() != null) {
            long pausedSeconds = ChronoUnit.SECONDS.between(session.getLastPausedTime(), LocalDateTime.now());
            session.setPausedDurationSeconds(session.getPausedDurationSeconds() + pausedSeconds);
        }

        session.setStatus(GameSession.SessionStatus.ACTIVE);
        session.setLastPausedTime(null);

        GameSession updatedSession = gameSessionRepository.save(session);
        logger.info("Resumed game session {}", sessionId);

        return updatedSession;
    }

    /**
     * Complete a game session and update student progress
     */
    @Transactional
    public Map<String, Object> completeGameSession(String sessionId, Map<String, Object> finalData) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game session not found: " + sessionId));

        if (session.getStatus() == GameSession.SessionStatus.COMPLETED) {
            throw new IllegalStateException("Session already completed: " + sessionId);
        }

        // Update final session data
        session.setStatus(GameSession.SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());

        if (finalData.containsKey("currentScore")) {
            session.setCurrentScore((Integer) finalData.get("currentScore"));
        }
        if (finalData.containsKey("questionsAnswered")) {
            session.setQuestionsAnswered((Integer) finalData.get("questionsAnswered"));
        }
        if (finalData.containsKey("correctAnswers")) {
            session.setCorrectAnswers((Integer) finalData.get("correctAnswers"));
        }
        if (finalData.containsKey("incorrectAnswers")) {
            session.setIncorrectAnswers((Integer) finalData.get("incorrectAnswers"));
        }
        if (finalData.containsKey("timeSpentSeconds")) {
            session.setTimeSpentSeconds((Long) finalData.get("timeSpentSeconds"));
        }

        // Calculate performance metrics
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("accuracy", session.getAccuracy());
        metrics.put("averageTimePerQuestion", session.getAverageTimePerQuestion());
        metrics.put("totalTime", session.getTimeSpentSeconds());
        session.setPerformanceMetrics(metrics);

        // Calculate and award bonus points
        int bonusPoints = session.calculateBonusPoints();
        if (bonusPoints > 0) {
            session.setBonusPointsEarned(bonusPoints);
            session.setBonusPointsAwarded(true);

            // Add bonus points to student's total
            Student student = studentRepository.findById(session.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            student.setTotalPoints(student.getTotalPoints() + bonusPoints);
            studentRepository.save(student);

            logger.info("Awarded {} bonus points to student {} for session {}",
                    bonusPoints, session.getStudentId(), sessionId);
        }

        // Save session
        GameSession completedSession = gameSessionRepository.save(session);

        // Update or create GameProgress entry
        updateGameProgressEntry(completedSession);

        // Check for new achievements
        List<String> newAchievements = achievementService.checkAndAwardAchievements(session.getStudentId());

        // Send notifications for achievements
        if (!newAchievements.isEmpty()) {
            for (String achievement : newAchievements) {
                notificationService.sendAchievementNotification(session.getStudentId(), achievement);
            }
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("session", completedSession);
        response.put("bonusPoints", bonusPoints);
        response.put("newAchievements", newAchievements);
        response.put("performanceMetrics", metrics);

        logger.info("Completed game session {}: score={}, accuracy={}%, bonusPoints={}",
                sessionId, session.getCurrentScore(), session.getAccuracy(), bonusPoints);

        return response;
    }

    /**
     * Abandon a game session (left incomplete)
     */
    @Transactional
    public GameSession abandonGameSession(String sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game session not found: " + sessionId));

        if (session.getStatus() == GameSession.SessionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot abandon completed session");
        }

        session.setStatus(GameSession.SessionStatus.ABANDONED);
        session.setEndTime(LocalDateTime.now());

        GameSession abandonedSession = gameSessionRepository.save(session);
        logger.info("Abandoned game session {}", sessionId);

        return abandonedSession;
    }

    /**
     * Get active session for a student and game
     */
    public Optional<GameSession> getActiveSession(String studentId, String gameId) {
        return gameSessionRepository.findByStudentIdAndGameIdAndStatus(
                studentId, gameId, GameSession.SessionStatus.ACTIVE);
    }

    /**
     * Get all sessions for a student
     */
    public List<GameSession> getStudentSessions(String studentId) {
        return gameSessionRepository.findByStudentId(studentId);
    }

    /**
     * Get session details
     */
    public GameSession getSessionDetails(String sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Game session not found: " + sessionId));
    }

    /**
     * Helper method to update GameProgress after session completion
     */
    private void updateGameProgressEntry(GameSession session) {
        Optional<GameProgress> existingProgress = gameProgressRepository
                .findByStudentIdAndGameId(session.getStudentId(), session.getGameId());

        GameProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setAttemptsCount(progress.getAttemptsCount() + 1);
            progress.setScore(session.getCurrentScore());
            progress.setTimeSpent(progress.getTimeSpent() + session.getTimeSpentSeconds());

            // Update best score if this session was better
            if (session.getCurrentScore() > progress.getBestScore()) {
                progress.setBestScore(session.getCurrentScore());
            }
        } else {
            progress = new GameProgress();
            progress.setStudentId(session.getStudentId());
            progress.setGameId(session.getGameId());
            progress.setScore(session.getCurrentScore());
            progress.setBestScore(session.getCurrentScore());
            progress.setAttemptsCount(1);
            progress.setTimeSpent(session.getTimeSpentSeconds());
        }

        progress.setLastPlayed(session.getEndTime());
        progress.setCompletionStatus(
                session.getQuestionsAnswered() >= 5 ?
                GameProgress.CompletionStatus.COMPLETED :
                GameProgress.CompletionStatus.IN_PROGRESS
        );

        gameProgressRepository.save(progress);
        logger.debug("Updated GameProgress for student {} on game {}",
                session.getStudentId(), session.getGameId());
    }
}
