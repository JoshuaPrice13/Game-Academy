package com.gameacademy.service;

import com.gameacademy.controller.WebSocketController;
import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.Leaderboard;
import com.gameacademy.model.Student;
import com.gameacademy.repository.ClassRepository;
import com.gameacademy.repository.LeaderboardRepository;
import com.gameacademy.repository.StudentRepository;
import com.gameacademy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final WebSocketController webSocketController;

    @Transactional
    public Leaderboard calculateClassLeaderboard(String classId, Leaderboard.Period period) {
        log.info("Calculating leaderboard for class {} with period {}", classId, period);

        // Verify class exists
        classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        // Get all students in the class
        List<Student> students = studentRepository.findByClassId(classId);

        if (students.isEmpty()) {
            log.warn("No students found for class {}", classId);
            return createEmptyLeaderboard(classId, period);
        }

        // Sort students by total points (descending), then by earliest achievement time
        List<Student> sortedStudents = students.stream()
                .sorted(Comparator.comparing(Student::getTotalPoints).reversed()
                        .thenComparing(s -> s.getProgressData().getTotalTimeSpent()))
                .collect(Collectors.toList());

        // Create ranking entries
        List<Leaderboard.RankingEntry> rankings = new ArrayList<>();
        AtomicInteger rank = new AtomicInteger(1);
        Integer previousPoints = null;
        int actualRank = 1;

        for (Student student : sortedStudents) {
            // Handle ties - students with same points get same rank
            if (previousPoints != null && !previousPoints.equals(student.getTotalPoints())) {
                actualRank = rank.get();
            }

            String studentName = userRepository.findById(student.getUserId())
                    .map(user -> user.getUsername())
                    .orElse("Unknown");

            Leaderboard.RankingEntry entry = Leaderboard.RankingEntry.builder()
                    .studentId(student.getId())
                    .studentName(studentName)
                    .points(student.getTotalPoints())
                    .rank(actualRank)
                    .build();

            rankings.add(entry);
            previousPoints = student.getTotalPoints();
            rank.incrementAndGet();
        }

        // Find or create leaderboard
        Optional<Leaderboard> existingLeaderboard = leaderboardRepository.findByClassIdAndPeriod(classId, period);

        Leaderboard leaderboard;
        if (existingLeaderboard.isPresent()) {
            leaderboard = existingLeaderboard.get();
            leaderboard.setRankings(rankings);
            leaderboard.setLastUpdated(LocalDateTime.now());
        } else {
            leaderboard = Leaderboard.builder()
                    .classId(classId)
                    .period(period)
                    .rankings(rankings)
                    .lastUpdated(LocalDateTime.now())
                    .build();
        }

        Leaderboard savedLeaderboard = leaderboardRepository.save(leaderboard);

        // Broadcast update via WebSocket
        try {
            webSocketController.sendLeaderboardUpdate(classId, savedLeaderboard);
        } catch (Exception e) {
            log.warn("Failed to broadcast leaderboard update via WebSocket: {}", e.getMessage());
        }

        return savedLeaderboard;
    }

    @Transactional
    public void updateLeaderboard(String classId) {
        log.info("Updating all leaderboards for class {}", classId);
        calculateClassLeaderboard(classId, Leaderboard.Period.DAILY);
        calculateClassLeaderboard(classId, Leaderboard.Period.WEEKLY);
        calculateClassLeaderboard(classId, Leaderboard.Period.ALL_TIME);
    }

    public Leaderboard getLeaderboardByClass(String classId, Leaderboard.Period period) {
        return leaderboardRepository.findByClassIdAndPeriod(classId, period)
                .orElseGet(() -> calculateClassLeaderboard(classId, period));
    }

    public Optional<Leaderboard.RankingEntry> getStudentRank(String studentId, String classId, Leaderboard.Period period) {
        Leaderboard leaderboard = getLeaderboardByClass(classId, period);

        return leaderboard.getRankings().stream()
                .filter(entry -> entry.getStudentId().equals(studentId))
                .findFirst();
    }

    public int getStudentRankPosition(String studentId, String classId, Leaderboard.Period period) {
        Optional<Leaderboard.RankingEntry> entry = getStudentRank(studentId, classId, period);
        return entry.map(Leaderboard.RankingEntry::getRank).orElse(-1);
    }

    @Async
    public void scheduleLeaderboardRefresh() {
        log.info("Scheduled leaderboard refresh started");
        List<String> classIds = classRepository.findAll().stream()
                .map(classEntity -> classEntity.getId())
                .collect(Collectors.toList());

        for (String classId : classIds) {
            try {
                updateLeaderboard(classId);
            } catch (Exception e) {
                log.error("Error updating leaderboard for class {}: {}", classId, e.getMessage());
            }
        }
        log.info("Scheduled leaderboard refresh completed");
    }

    private Leaderboard createEmptyLeaderboard(String classId, Leaderboard.Period period) {
        return Leaderboard.builder()
                .classId(classId)
                .period(period)
                .rankings(new ArrayList<>())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    public List<Leaderboard.RankingEntry> getLeaderboardContext(String studentId, String classId,
                                                                 Leaderboard.Period period, int contextSize) {
        Leaderboard leaderboard = getLeaderboardByClass(classId, period);
        List<Leaderboard.RankingEntry> rankings = leaderboard.getRankings();

        // Find student's position
        int studentIndex = -1;
        for (int i = 0; i < rankings.size(); i++) {
            if (rankings.get(i).getStudentId().equals(studentId)) {
                studentIndex = i;
                break;
            }
        }

        if (studentIndex == -1) {
            return new ArrayList<>();
        }

        // Get context around student (±contextSize positions)
        int start = Math.max(0, studentIndex - contextSize);
        int end = Math.min(rankings.size(), studentIndex + contextSize + 1);

        return rankings.subList(start, end);
    }
}
