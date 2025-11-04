package com.gameacademy.service;

import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.GameProgress;
import com.gameacademy.model.Student;
import com.gameacademy.repository.GameProgressRepository;
import com.gameacademy.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final StudentRepository studentRepository;
    private final GameProgressRepository gameProgressRepository;
    private final NotificationService notificationService;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementDefinition {
        private String achievementType;
        private String name;
        private String description;
        private int bonusPoints;
    }

    public static List<AchievementDefinition> defineAchievements() {
        List<AchievementDefinition> achievements = new ArrayList<>();

        achievements.add(AchievementDefinition.builder()
                .achievementType("FIRST_STEPS")
                .name("First Steps")
                .description("Complete your first game")
                .bonusPoints(10)
                .build());

        achievements.add(AchievementDefinition.builder()
                .achievementType("PERFECT_SCORE")
                .name("Perfect Score")
                .description("Achieve 100% on any game")
                .bonusPoints(50)
                .build());

        achievements.add(AchievementDefinition.builder()
                .achievementType("CONSISTENT_LEARNER")
                .name("Consistent Learner")
                .description("Play games 5 days in a row")
                .bonusPoints(75)
                .build());

        achievements.add(AchievementDefinition.builder()
                .achievementType("TOP_SCHOLAR")
                .name("Top Scholar")
                .description("Reach #1 on the leaderboard")
                .bonusPoints(100)
                .build());

        achievements.add(AchievementDefinition.builder()
                .achievementType("TEAM_PLAYER")
                .name("Team Player")
                .description("Contribute to completing a class goal")
                .bonusPoints(50)
                .build());

        achievements.add(AchievementDefinition.builder()
                .achievementType("SUBJECT_MASTER")
                .name("Subject Master")
                .description("Complete all games in a subject")
                .bonusPoints(150)
                .build());

        return achievements;
    }

    @Transactional
    public List<Student.Achievement> checkAchievements(String studentId) {
        log.info("Checking achievements for student {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        List<Student.Achievement> newAchievements = new ArrayList<>();

        // Get existing achievement types
        List<String> existingTypes = student.getAchievements().stream()
                .map(Student.Achievement::getAchievementName)
                .collect(Collectors.toList());

        // Check First Steps
        if (!existingTypes.contains("FIRST_STEPS")) {
            if (student.getProgressData().getGamesCompleted() >= 1) {
                Student.Achievement achievement = awardAchievement(student, "FIRST_STEPS");
                newAchievements.add(achievement);
            }
        }

        // Check Perfect Score
        if (!existingTypes.contains("PERFECT_SCORE")) {
            List<GameProgress> perfectScores = gameProgressRepository.findByStudentId(studentId).stream()
                    .filter(gp -> gp.getScore() >= 100)
                    .toList();

            if (!perfectScores.isEmpty()) {
                Student.Achievement achievement = awardAchievement(student, "PERFECT_SCORE");
                newAchievements.add(achievement);
            }
        }

        // Note: Consistent Learner, Top Scholar, Team Player, and Subject Master
        // would require additional tracking data not currently in the model
        // These can be implemented when historical data tracking is added

        if (!newAchievements.isEmpty()) {
            studentRepository.save(student);
        }

        return newAchievements;
    }

    @Transactional
    public Student.Achievement awardAchievement(Student student, String achievementType) {
        log.info("Awarding achievement {} to student {}", achievementType, student.getId());

        AchievementDefinition definition = defineAchievements().stream()
                .filter(a -> a.getAchievementType().equals(achievementType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown achievement type: " + achievementType));

        Student.Achievement achievement = Student.Achievement.builder()
                .achievementName(achievementType)
                .earnedAt(LocalDateTime.now())
                .points(definition.getBonusPoints())
                .build();

        student.getAchievements().add(achievement);
        student.setTotalPoints(student.getTotalPoints() + definition.getBonusPoints());

        // Send notification
        notificationService.sendAchievementNotification(student.getUserId(), definition.getName());

        return achievement;
    }

    public List<Student.Achievement> getStudentAchievements(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        return student.getAchievements();
    }

    public List<AchievementDefinition> getAvailableAchievements() {
        return defineAchievements();
    }

    public List<AchievementDefinition> getEarnableAchievements(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        List<String> earnedTypes = student.getAchievements().stream()
                .map(Student.Achievement::getAchievementName)
                .collect(Collectors.toList());

        return defineAchievements().stream()
                .filter(a -> !earnedTypes.contains(a.getAchievementType()))
                .collect(Collectors.toList());
    }
}
