package com.gameacademy.service;

import com.gameacademy.model.Student;
import com.gameacademy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardAnalyticsService {

    private final StudentRepository studentRepository;

    public List<Student> getTopPerformers(String classId, int limit) {
        log.info("Getting top {} performers for class {}", limit, classId);
        List<Student> students = studentRepository.findByClassId(classId);

        return students.stream()
                .sorted(Comparator.comparing(Student::getTotalPoints).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double getClassAverageScore(String classId) {
        log.info("Calculating average score for class {}", classId);
        List<Student> students = studentRepository.findByClassId(classId);

        if (students.isEmpty()) {
            return 0.0;
        }

        return students.stream()
                .mapToInt(Student::getTotalPoints)
                .average()
                .orElse(0.0);
    }

    public Map<String, Object> getPointsDistribution(String classId) {
        log.info("Calculating points distribution for class {}", classId);
        List<Student> students = studentRepository.findByClassId(classId);

        if (students.isEmpty()) {
            return createEmptyDistribution();
        }

        List<Integer> points = students.stream()
                .map(Student::getTotalPoints)
                .sorted()
                .collect(Collectors.toList());

        int size = points.size();
        Map<String, Object> distribution = new HashMap<>();

        // Calculate quartiles
        distribution.put("minimum", points.get(0));
        distribution.put("maximum", points.get(size - 1));
        distribution.put("q1", calculateQuartile(points, 0.25));
        distribution.put("median", calculateQuartile(points, 0.5));
        distribution.put("q3", calculateQuartile(points, 0.75));
        distribution.put("mean", getClassAverageScore(classId));
        distribution.put("totalStudents", size);

        return distribution;
    }

    public Map<String, Object> getImprovementTrend(String studentId, int days) {
        log.info("Calculating improvement trend for student {} over {} days", studentId, days);

        Student student = studentRepository.findById(studentId)
                .orElse(null);

        if (student == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> trend = new HashMap<>();

        // Current metrics
        trend.put("currentPoints", student.getTotalPoints());
        trend.put("currentLevel", student.getLevel());
        trend.put("gamesCompleted", student.getProgressData().getGamesCompleted());
        trend.put("totalTimeSpent", student.getProgressData().getTotalTimeSpent());
        trend.put("averageScore", student.getProgressData().getAverageScore());

        // Calculate estimated daily rate (simplified)
        // In a real implementation, you'd track historical data
        int estimatedDailyPoints = student.getTotalPoints() / Math.max(days, 1);
        trend.put("estimatedDailyPoints", estimatedDailyPoints);
        trend.put("projectedPointsIn7Days", student.getTotalPoints() + (estimatedDailyPoints * 7));

        return trend;
    }

    public List<Student> identifyStrugglingStudents(String classId, double threshold) {
        log.info("Identifying struggling students in class {} with threshold {}%", classId, threshold);

        double averageScore = getClassAverageScore(classId);
        double thresholdScore = averageScore * (threshold / 100.0);

        List<Student> students = studentRepository.findByClassId(classId);

        return students.stream()
                .filter(student -> student.getTotalPoints() < thresholdScore)
                .sorted(Comparator.comparing(Student::getTotalPoints))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getClassEngagementMetrics(String classId) {
        log.info("Calculating engagement metrics for class {}", classId);
        List<Student> students = studentRepository.findByClassId(classId);

        if (students.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> metrics = new HashMap<>();

        // Total engagement
        int totalGamesCompleted = students.stream()
                .mapToInt(s -> s.getProgressData().getGamesCompleted())
                .sum();

        int totalTimeSpent = students.stream()
                .mapToInt(s -> s.getProgressData().getTotalTimeSpent())
                .sum();

        // Average per student
        double avgGamesPerStudent = (double) totalGamesCompleted / students.size();
        double avgTimePerStudent = (double) totalTimeSpent / students.size();

        metrics.put("totalStudents", students.size());
        metrics.put("totalGamesCompleted", totalGamesCompleted);
        metrics.put("totalTimeSpent", totalTimeSpent);
        metrics.put("averageGamesPerStudent", avgGamesPerStudent);
        metrics.put("averageTimePerStudent", avgTimePerStudent);
        metrics.put("averageScoreAcrossClass",
            students.stream()
                .mapToDouble(s -> s.getProgressData().getAverageScore())
                .average()
                .orElse(0.0));

        // Activity level classification
        long highlyActive = students.stream()
                .filter(s -> s.getProgressData().getGamesCompleted() >= avgGamesPerStudent * 1.5)
                .count();
        long moderatelyActive = students.stream()
                .filter(s -> {
                    int games = s.getProgressData().getGamesCompleted();
                    return games >= avgGamesPerStudent * 0.5 && games < avgGamesPerStudent * 1.5;
                })
                .count();
        long lowActivity = students.size() - highlyActive - moderatelyActive;

        metrics.put("highlyActiveStudents", highlyActive);
        metrics.put("moderatelyActiveStudents", moderatelyActive);
        metrics.put("lowActivityStudents", lowActivity);

        return metrics;
    }

    private double calculateQuartile(List<Integer> sortedData, double percentile) {
        int size = sortedData.size();
        double position = percentile * (size - 1);
        int lowerIndex = (int) Math.floor(position);
        int upperIndex = (int) Math.ceil(position);

        if (lowerIndex == upperIndex) {
            return sortedData.get(lowerIndex);
        }

        double lowerValue = sortedData.get(lowerIndex);
        double upperValue = sortedData.get(upperIndex);
        double fraction = position - lowerIndex;

        return lowerValue + (upperValue - lowerValue) * fraction;
    }

    private Map<String, Object> createEmptyDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("minimum", 0);
        distribution.put("maximum", 0);
        distribution.put("q1", 0);
        distribution.put("median", 0);
        distribution.put("q3", 0);
        distribution.put("mean", 0.0);
        distribution.put("totalStudents", 0);
        return distribution;
    }
}
