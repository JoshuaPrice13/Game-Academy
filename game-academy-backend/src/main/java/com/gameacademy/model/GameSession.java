package com.gameacademy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "game_sessions")
@CompoundIndex(name = "student_game_idx", def = "{'studentId': 1, 'gameId': 1, 'startTime': -1}")
public class GameSession {

    @Id
    private String id;

    @Indexed
    private String studentId;

    @Indexed
    private String gameId;

    private SessionStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime lastPausedTime;

    private int currentScore;

    private int questionsAnswered;

    private int correctAnswers;

    private int incorrectAnswers;

    private long timeSpentSeconds; // Total active time (excluding pauses)

    private long pausedDurationSeconds; // Total time spent paused

    private Map<String, Object> gameState; // Game-specific state data

    private Map<String, Object> performanceMetrics; // Accuracy, speed, etc.

    private boolean bonusPointsAwarded;

    private int bonusPointsEarned;

    public enum SessionStatus {
        ACTIVE,      // Game is currently being played
        PAUSED,      // Game is paused
        COMPLETED,   // Game finished successfully
        ABANDONED    // Game was left incomplete
    }

    public GameSession() {
        this.status = SessionStatus.ACTIVE;
        this.startTime = LocalDateTime.now();
        this.currentScore = 0;
        this.questionsAnswered = 0;
        this.correctAnswers = 0;
        this.incorrectAnswers = 0;
        this.timeSpentSeconds = 0;
        this.pausedDurationSeconds = 0;
        this.gameState = new HashMap<>();
        this.performanceMetrics = new HashMap<>();
        this.bonusPointsAwarded = false;
        this.bonusPointsEarned = 0;
    }

    // Calculate accuracy percentage
    public double getAccuracy() {
        if (questionsAnswered == 0) return 0.0;
        return (double) correctAnswers / questionsAnswered * 100.0;
    }

    // Calculate average time per question
    public double getAverageTimePerQuestion() {
        if (questionsAnswered == 0) return 0.0;
        return (double) timeSpentSeconds / questionsAnswered;
    }

    // Check if session qualifies for bonus points
    public boolean qualifiesForBonus() {
        return getAccuracy() >= 90.0 && questionsAnswered >= 5;
    }

    // Calculate bonus points based on performance
    public int calculateBonusPoints() {
        if (!qualifiesForBonus()) return 0;

        int bonus = 0;

        // Perfect score bonus
        if (getAccuracy() == 100.0) {
            bonus += 50;
        } else if (getAccuracy() >= 95.0) {
            bonus += 30;
        } else if (getAccuracy() >= 90.0) {
            bonus += 20;
        }

        // Speed bonus (less than 10 seconds per question)
        if (getAverageTimePerQuestion() < 10.0 && questionsAnswered >= 5) {
            bonus += 25;
        }

        // Completion bonus (answered at least 10 questions)
        if (questionsAnswered >= 10) {
            bonus += 15;
        }

        return bonus;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getLastPausedTime() {
        return lastPausedTime;
    }

    public void setLastPausedTime(LocalDateTime lastPausedTime) {
        this.lastPausedTime = lastPausedTime;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(int incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public long getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(long timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public long getPausedDurationSeconds() {
        return pausedDurationSeconds;
    }

    public void setPausedDurationSeconds(long pausedDurationSeconds) {
        this.pausedDurationSeconds = pausedDurationSeconds;
    }

    public Map<String, Object> getGameState() {
        return gameState;
    }

    public void setGameState(Map<String, Object> gameState) {
        this.gameState = gameState;
    }

    public Map<String, Object> getPerformanceMetrics() {
        return performanceMetrics;
    }

    public void setPerformanceMetrics(Map<String, Object> performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }

    public boolean isBonusPointsAwarded() {
        return bonusPointsAwarded;
    }

    public void setBonusPointsAwarded(boolean bonusPointsAwarded) {
        this.bonusPointsAwarded = bonusPointsAwarded;
    }

    public int getBonusPointsEarned() {
        return bonusPointsEarned;
    }

    public void setBonusPointsEarned(int bonusPointsEarned) {
        this.bonusPointsEarned = bonusPointsEarned;
    }
}
