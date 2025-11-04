package com.gameacademy.controller;

import com.gameacademy.dto.StudentProgressDTO;
import com.gameacademy.model.GameProgress;
import com.gameacademy.model.Student;
import com.gameacademy.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management APIs")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get student profile")
    public ResponseEntity<StudentProgressDTO> getStudentProfile(@PathVariable String userId) {
        StudentProgressDTO profile = studentService.getStudentProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{studentId}/progress")
    @Operation(summary = "Get student progress details")
    public ResponseEntity<StudentProgressDTO> getStudentProgress(@PathVariable String studentId) {
        // For now, we'll use the same method - in production you'd have different logic
        StudentProgressDTO progress = studentService.getStudentProfile(studentId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/{studentId}/games")
    @Operation(summary = "Get student game history")
    public ResponseEntity<List<GameProgress>> getStudentGames(@PathVariable String studentId) {
        List<GameProgress> games = studentService.getStudentGameHistory(studentId);
        return ResponseEntity.ok(games);
    }

    @PutMapping("/{studentId}/points")
    @Operation(summary = "Update student points")
    public ResponseEntity<Student> updateStudentPoints(
            @PathVariable String studentId,
            @RequestBody Map<String, Integer> request) {
        int points = request.get("points");
        Student student = studentService.updateStudentProgress(studentId, points);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/{studentId}/enroll/{classId}")
    @Operation(summary = "Enroll student in class")
    public ResponseEntity<Map<String, String>> enrollStudent(
            @PathVariable String studentId,
            @PathVariable String classId) {
        studentService.enrollStudentInClass(studentId, classId);
        return ResponseEntity.ok(Map.of("message", "Student enrolled successfully"));
    }
}
