package com.gameacademy.service;

import com.gameacademy.dto.ClassGoalDTO;
import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.ClassEntity;
import com.gameacademy.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;

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
}
