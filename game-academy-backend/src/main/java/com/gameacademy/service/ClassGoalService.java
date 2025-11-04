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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassGoalService {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public ClassEntity createClassGoal(String classId, ClassGoalDTO goalDTO) {
        log.info("Creating goal for class {}: {}", classId, goalDTO.getGoalDescription());
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        // Validate goal data
        if (goalDTO.getTargetPoints() == null || goalDTO.getTargetPoints() <= 0) {
            throw new IllegalArgumentException("Target points must be positive");
        }

        if (goalDTO.getDeadline() != null && goalDTO.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }

        ClassEntity.ClassGoal goal = ClassEntity.ClassGoal.builder()
                .goalDescription(goalDTO.getGoalDescription())
                .targetPoints(goalDTO.getTargetPoints())
                .deadline(goalDTO.getDeadline())
                .build();

        classEntity.getGoals().add(goal);
        return classRepository.save(classEntity);
    }

    public Map<String, Object> updateGoalProgress(String classId, int goalIndex) {
        log.info("Updating progress for class {} goal {}", classId, goalIndex);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        if (goalIndex >= classEntity.getGoals().size() || goalIndex < 0) {
            throw new IllegalArgumentException("Invalid goal index");
        }

        ClassEntity.ClassGoal goal = classEntity.getGoals().get(goalIndex);
        List<Student> students = studentRepository.findByClassId(classId);

        int currentProgress = students.stream()
                .mapToInt(Student::getTotalPoints)
                .sum();

        Map<String, Object> progressData = new HashMap<>();
        progressData.put("goalDescription", goal.getGoalDescription());
        progressData.put("targetPoints", goal.getTargetPoints());
        progressData.put("currentProgress", currentProgress);
        progressData.put("progressPercentage", (currentProgress * 100.0) / goal.getTargetPoints());
        progressData.put("isCompleted", currentProgress >= goal.getTargetPoints());
        progressData.put("deadline", goal.getDeadline());
        progressData.put("remainingPoints", Math.max(0, goal.getTargetPoints() - currentProgress));

        return progressData;
    }

    public boolean checkGoalCompletion(String classId, int goalIndex) {
        log.info("Checking completion for class {} goal {}", classId, goalIndex);
        Map<String, Object> progress = updateGoalProgress(classId, goalIndex);
        return (Boolean) progress.get("isCompleted");
    }

    public List<Map<String, Object>> getActiveGoals(String classId) {
        log.info("Getting active goals for class {}", classId);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        List<Map<String, Object>> activeGoals = new ArrayList<>();

        for (int i = 0; i < classEntity.getGoals().size(); i++) {
            ClassEntity.ClassGoal goal = classEntity.getGoals().get(i);

            // Consider goals active if deadline hasn't passed or no deadline set
            boolean isActive = goal.getDeadline() == null ||
                    goal.getDeadline().isAfter(LocalDateTime.now());

            if (isActive) {
                Map<String, Object> goalData = updateGoalProgress(classId, i);
                goalData.put("goalIndex", i);
                activeGoals.add(goalData);
            }
        }

        return activeGoals;
    }

    @Transactional
    public Map<String, Object> completeGoal(String classId, int goalIndex) {
        log.info("Completing goal for class {} goal {}", classId, goalIndex);

        if (!checkGoalCompletion(classId, goalIndex)) {
            throw new IllegalStateException("Goal is not yet completed");
        }

        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        // Award bonus points to all students in the class
        List<Student> students = studentRepository.findByClassId(classId);
        int bonusPoints = 50; // Bonus for completing class goal

        for (Student student : students) {
            student.setTotalPoints(student.getTotalPoints() + bonusPoints);
        }
        studentRepository.saveAll(students);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Goal completed successfully!");
        result.put("bonusPointsPerStudent", bonusPoints);
        result.put("studentsAwarded", students.size());
        result.put("totalBonusPointsDistributed", bonusPoints * students.size());

        return result;
    }

    @Transactional
    public void deleteGoal(String classId, int goalIndex) {
        log.info("Deleting goal from class {} at index {}", classId, goalIndex);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        if (goalIndex >= classEntity.getGoals().size() || goalIndex < 0) {
            throw new IllegalArgumentException("Invalid goal index");
        }

        classEntity.getGoals().remove(goalIndex);
        classRepository.save(classEntity);
    }

    public List<Map<String, Object>> getAllGoalsWithProgress(String classId) {
        log.info("Getting all goals with progress for class {}", classId);
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        List<Map<String, Object>> allGoals = new ArrayList<>();

        for (int i = 0; i < classEntity.getGoals().size(); i++) {
            Map<String, Object> goalData = updateGoalProgress(classId, i);
            goalData.put("goalIndex", i);

            // Add deadline status
            ClassEntity.ClassGoal goal = classEntity.getGoals().get(i);
            if (goal.getDeadline() != null) {
                goalData.put("isExpired", goal.getDeadline().isBefore(LocalDateTime.now()));
            }

            allGoals.add(goalData);
        }

        return allGoals;
    }
}
