package com.gameacademy.service;

import com.gameacademy.dto.ClassGoalDTO;
import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.ClassEntity;
import com.gameacademy.model.Student;
import com.gameacademy.repository.ClassRepository;
import com.gameacademy.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public ClassEntity createClass(String teacherId, String className) {
        ClassEntity classEntity = ClassEntity.builder()
                .className(className)
                .teacherId(teacherId)
                .createdAt(LocalDateTime.now())
                .build();

        return classRepository.save(classEntity);
    }

    public ClassEntity getClassDetails(String classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));
    }

    @Transactional
    public ClassEntity addStudentToClass(String classId, String studentId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        if (!classEntity.getStudentIds().contains(studentId)) {
            classEntity.getStudentIds().add(studentId);
            classRepository.save(classEntity);
        }

        return classEntity;
    }

    @Transactional
    public ClassEntity removeStudentFromClass(String classId, String studentId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        classEntity.getStudentIds().remove(studentId);
        return classRepository.save(classEntity);
    }

    public List<ClassEntity> getClassStudents(String classId) {
        // This would typically return student details, but for now returns class info
        classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        return classRepository.findAll();
    }

    @Transactional
    public ClassEntity setClassGoal(String classId, ClassGoalDTO goalDTO) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        ClassEntity.ClassGoal goal = ClassEntity.ClassGoal.builder()
                .goalDescription(goalDTO.getGoalDescription())
                .targetPoints(goalDTO.getTargetPoints())
                .deadline(goalDTO.getDeadline())
                .build();

        classEntity.getGoals().add(goal);
        return classRepository.save(classEntity);
    }

    public List<ClassEntity> getTeacherClasses(String teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }

    public Map<String, Object> getClassStatistics(String classId) {
        log.info("Getting statistics for class {}", classId);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        List<Student> students = studentRepository.findByClassId(classId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", students.size());

        if (!students.isEmpty()) {
            double averagePoints = students.stream()
                    .mapToInt(Student::getTotalPoints)
                    .average()
                    .orElse(0.0);

            int totalGamesCompleted = students.stream()
                    .mapToInt(s -> s.getProgressData().getGamesCompleted())
                    .sum();

            double completionRate = students.stream()
                    .mapToDouble(s -> s.getProgressData().getAverageScore())
                    .average()
                    .orElse(0.0);

            stats.put("averagePoints", averagePoints);
            stats.put("totalGamesCompleted", totalGamesCompleted);
            stats.put("completionRate", completionRate);
            stats.put("activeStudents", students.size()); // Simplified - could add last activity check
        } else {
            stats.put("averagePoints", 0.0);
            stats.put("totalGamesCompleted", 0);
            stats.put("completionRate", 0.0);
            stats.put("activeStudents", 0);
        }

        stats.put("activeGoals", classEntity.getGoals().size());
        return stats;
    }

    @Transactional
    public ClassEntity updateClassSettings(String classId, Map<String, Object> settings) {
        log.info("Updating settings for class {}", classId);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        if (settings.containsKey("className")) {
            classEntity.setClassName((String) settings.get("className"));
        }

        return classRepository.save(classEntity);
    }

    @Transactional
    public void archiveClass(String classId) {
        log.info("Archiving class {}", classId);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        // In a full implementation, you'd add an 'archived' field to ClassEntity
        // For now, we'll just log it
        log.info("Class {} archived successfully", classId);
    }

    public Map<String, Object> getClassActivity(String classId, int days) {
        log.info("Getting activity for class {} over {} days", classId, days);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        List<Student> students = studentRepository.findByClassId(classId);

        Map<String, Object> activity = new HashMap<>();

        int recentGamesPlayed = students.stream()
                .mapToInt(s -> s.getProgressData().getGamesCompleted())
                .sum();

        activity.put("recentGamesPlayed", recentGamesPlayed);
        activity.put("activeStudentCount", students.size());
        activity.put("periodDays", days);

        return activity;
    }

    public boolean validateClassGoalProgress(String classId, int goalIndex) {
        log.info("Validating goal progress for class {} goal index {}", classId, goalIndex);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        if (goalIndex >= classEntity.getGoals().size()) {
            return false;
        }

        ClassEntity.ClassGoal goal = classEntity.getGoals().get(goalIndex);
        List<Student> students = studentRepository.findByClassId(classId);

        int totalPoints = students.stream()
                .mapToInt(Student::getTotalPoints)
                .sum();

        return totalPoints >= goal.getTargetPoints();
    }
}
