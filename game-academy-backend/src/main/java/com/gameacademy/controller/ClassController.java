package com.gameacademy.controller;

import com.gameacademy.dto.ClassGoalDTO;
import com.gameacademy.model.ClassEntity;
import com.gameacademy.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "Class management APIs")
public class ClassController {

    private final ClassService classService;

    @PostMapping
    @Operation(summary = "Create a new class")
    public ResponseEntity<ClassEntity> createClass(@RequestBody Map<String, String> request) {
        String teacherId = request.get("teacherId");
        String className = request.get("className");
        ClassEntity classEntity = classService.createClass(teacherId, className);
        return new ResponseEntity<>(classEntity, HttpStatus.CREATED);
    }

    @GetMapping("/{classId}")
    @Operation(summary = "Get class details")
    public ResponseEntity<ClassEntity> getClassDetails(@PathVariable String classId) {
        ClassEntity classEntity = classService.getClassDetails(classId);
        return ResponseEntity.ok(classEntity);
    }

    @PostMapping("/{classId}/students/{studentId}")
    @Operation(summary = "Add student to class")
    public ResponseEntity<ClassEntity> addStudent(
            @PathVariable String classId,
            @PathVariable String studentId) {
        ClassEntity classEntity = classService.addStudentToClass(classId, studentId);
        return ResponseEntity.ok(classEntity);
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    @Operation(summary = "Remove student from class")
    public ResponseEntity<ClassEntity> removeStudent(
            @PathVariable String classId,
            @PathVariable String studentId) {
        ClassEntity classEntity = classService.removeStudentFromClass(classId, studentId);
        return ResponseEntity.ok(classEntity);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get all classes for a teacher")
    public ResponseEntity<List<ClassEntity>> getTeacherClasses(@PathVariable String teacherId) {
        List<ClassEntity> classes = classService.getTeacherClasses(teacherId);
        return ResponseEntity.ok(classes);
    }

    @PostMapping("/{classId}/goals")
    @Operation(summary = "Set class goal")
    public ResponseEntity<ClassEntity> setClassGoal(
            @PathVariable String classId,
            @RequestBody ClassGoalDTO goalDTO) {
        ClassEntity classEntity = classService.setClassGoal(classId, goalDTO);
        return ResponseEntity.ok(classEntity);
    }
}
