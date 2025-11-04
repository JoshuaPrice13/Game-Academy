package com.gameacademy.service;

import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.GameProgress;
import com.gameacademy.model.Student;
import com.gameacademy.repository.GameProgressRepository;
import com.gameacademy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameProgressService {

    private final GameProgressRepository gameProgressRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @Transactional
    public GameProgress recordGameSession(String studentId, String gameId, int score, int timeSpent) {
        Optional<GameProgress> existingProgress = gameProgressRepository.findByStudentIdAndGameId(studentId, gameId);

        GameProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setScore(score);
            progress.setAttemptsCount(progress.getAttemptsCount() + 1);
            progress.setLastPlayed(LocalDateTime.now());
            progress.setTimeSpent((progress.getTimeSpent() != null ? progress.getTimeSpent() : 0) + timeSpent);

            // Update best score if current score is higher
            if (score > progress.getBestScore()) {
                progress.setBestScore(score);
            }

            // Mark as completed if score meets threshold
            if (score >= 70) { // Assuming 70% is passing
                progress.setCompletionStatus(GameProgress.CompletionStatus.COMPLETED);

                // Update student's games completed count
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

                if (progress.getAttemptsCount() == 1) { // Only count first completion
                    Student.ProgressData progressData = student.getProgressData();
                    progressData.setGamesCompleted(progressData.getGamesCompleted() + 1);
                    studentRepository.save(student);
                }
            }
        } else {
            progress = GameProgress.builder()
                    .studentId(studentId)
                    .gameId(gameId)
                    .score(score)
                    .completionStatus(score >= 70 ? GameProgress.CompletionStatus.COMPLETED : GameProgress.CompletionStatus.IN_PROGRESS)
                    .attemptsCount(1)
                    .lastPlayed(LocalDateTime.now())
                    .timeSpent(timeSpent)
                    .bestScore(score)
                    .build();

            // Update student's games completed count for new completion
            if (score >= 70) {
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

                Student.ProgressData progressData = student.getProgressData();
                progressData.setGamesCompleted(progressData.getGamesCompleted() + 1);
                studentRepository.save(student);
            }
        }

        GameProgress savedProgress = gameProgressRepository.save(progress);

        // Update student total points and time
        studentService.updateStudentProgress(studentId, score);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        Student.ProgressData progressData = student.getProgressData();
        progressData.setTotalTimeSpent(progressData.getTotalTimeSpent() + timeSpent);
        studentRepository.save(student);

        return savedProgress;
    }

    public GameProgress getStudentGameProgress(String studentId, String gameId) {
        return gameProgressRepository.findByStudentIdAndGameId(studentId, gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game progress not found"));
    }

    public double calculateGameCompletion(String studentId) {
        List<GameProgress> allProgress = gameProgressRepository.findByStudentId(studentId);
        if (allProgress.isEmpty()) {
            return 0.0;
        }

        long completedGames = allProgress.stream()
                .filter(gp -> gp.getCompletionStatus() == GameProgress.CompletionStatus.COMPLETED)
                .count();

        return (completedGames * 100.0) / allProgress.size();
    }

    public List<GameProgress> getRecentGames(String studentId, int limit) {
        List<GameProgress> allGames = gameProgressRepository.findByStudentId(
                studentId,
                Sort.by(Sort.Direction.DESC, "lastPlayed")
        );

        return allGames.stream().limit(limit).toList();
    }

    @Transactional
    public GameProgress updateBestScore(String studentId, String gameId, int newScore) {
        GameProgress progress = gameProgressRepository.findByStudentIdAndGameId(studentId, gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game progress not found"));

        if (newScore > progress.getBestScore()) {
            progress.setBestScore(newScore);
            progress.setScore(newScore);
            return gameProgressRepository.save(progress);
        }

        return progress;
    }

    public List<GameProgress> getAllStudentProgress(String studentId) {
        return gameProgressRepository.findByStudentId(studentId);
    }
}
