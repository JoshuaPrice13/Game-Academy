package com.gameacademy.controller;

import com.gameacademy.dto.ClassGoalDTO;
import com.gameacademy.model.ClassEntity;
import com.gameacademy.service.ClassGoalService;
import com.gameacademy.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "Classes", description = "Class management APIs")
public class ClassController {

    private final ClassService classService;
    private final ClassGoalService classGoalService;

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

    @PutMapping("/{classId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Update class settings (teacher only)")
    public ResponseEntity<ClassEntity> updateClass(
            @PathVariable String classId,
            @RequestBody Map<String, Object> settings) {
        ClassEntity updated = classService.updateClassSettings(classId, settings);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Archive class (teacher only)")
    public ResponseEntity<Map<String, String>> archiveClass(@PathVariable String classId) {
        classService.archiveClass(classId);
        return ResponseEntity.ok(Map.of("message", "Class archived successfully"));
    }

    @GetMapping("/{classId}/statistics")
    @Operation(summary = "Get class statistics")
    public ResponseEntity<Map<String, Object>> getClassStatistics(@PathVariable String classId) {
        Map<String, Object> stats = classService.getClassStatistics(classId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{classId}/activity")
    @Operation(summary = "Get class activity summary")
    public ResponseEntity<Map<String, Object>> getClassActivity(
            @PathVariable String classId,
            @RequestParam(defaultValue = "7") int days) {
        Map<String, Object> activity = classService.getClassActivity(classId, days);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/{classId}/goals")
    @Operation(summary = "Get all active class goals")
    public ResponseEntity<List<Map<String, Object>>> getActiveGoals(@PathVariable String classId) {
        List<Map<String, Object>> goals = classGoalService.getActiveGoals(classId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{classId}/goals/all")
    @Operation(summary = "Get all class goals with progress")
    public ResponseEntity<List<Map<String, Object>>> getAllGoals(@PathVariable String classId) {
        List<Map<String, Object>> goals = classGoalService.getAllGoalsWithProgress(classId);
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{classId}/goals/{goalIndex}")
    @Operation(summary = "Update goal progress")
    public ResponseEntity<Map<String, Object>> updateGoalProgress(
            @PathVariable String classId,
            @PathVariable int goalIndex) {
        Map<String, Object> progress = classGoalService.updateGoalProgress(classId, goalIndex);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/{classId}/goals/{goalIndex}/complete")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Complete goal and award bonus points (teacher only)")
    public ResponseEntity<Map<String, Object>> completeGoal(
            @PathVariable String classId,
            @PathVariable int goalIndex) {
        Map<String, Object> result = classGoalService.completeGoal(classId, goalIndex);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{classId}/goals/{goalIndex}")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Delete class goal (teacher only)")
    public ResponseEntity<Map<String, String>> deleteGoal(
            @PathVariable String classId,
            @PathVariable int goalIndex) {
        classGoalService.deleteGoal(classId, goalIndex);
        return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
    }
}
