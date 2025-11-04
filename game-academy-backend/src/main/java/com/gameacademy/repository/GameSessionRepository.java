package com.gameacademy.repository;

import com.gameacademy.model.GameSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends MongoRepository<GameSession, String> {

    // Find all sessions for a student
    List<GameSession> findByStudentId(String studentId);

    // Find all sessions for a specific game
    List<GameSession> findByGameId(String gameId);

    // Find sessions by student and game
    List<GameSession> findByStudentIdAndGameId(String studentId, String gameId);

    // Find active session for a student and game (to prevent multiple active sessions)
    Optional<GameSession> findByStudentIdAndGameIdAndStatus(String studentId, String gameId, GameSession.SessionStatus status);

    // Find all active sessions for a student
    List<GameSession> findByStudentIdAndStatus(String studentId, GameSession.SessionStatus status);

    // Find sessions by status
    List<GameSession> findByStatus(GameSession.SessionStatus status);

    // Find sessions started after a certain time
    List<GameSession> findByStartTimeAfter(LocalDateTime startTime);

    // Find sessions by student within a time range
    List<GameSession> findByStudentIdAndStartTimeBetween(String studentId, LocalDateTime start, LocalDateTime end);

    // Find completed sessions for a student
    List<GameSession> findByStudentIdAndStatusOrderByEndTimeDesc(String studentId, GameSession.SessionStatus status);

    // Count sessions by student
    long countByStudentId(String studentId);

    // Count completed sessions by student and game
    long countByStudentIdAndGameIdAndStatus(String studentId, String gameId, GameSession.SessionStatus status);
}
