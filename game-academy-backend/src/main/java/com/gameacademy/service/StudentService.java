package com.gameacademy.service;

import com.gameacademy.dto.StudentProgressDTO;
import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.GameProgress;
import com.gameacademy.model.Student;
import com.gameacademy.model.User;
import com.gameacademy.repository.ClassRepository;
import com.gameacademy.repository.GameProgressRepository;
import com.gameacademy.repository.StudentRepository;
import com.gameacademy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final GameProgressRepository gameProgressRepository;
    private final ClassRepository classRepository;

    public StudentProgressDTO getStudentProfile(String userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for user: " + userId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        List<GameProgress> recentGames = gameProgressRepository.findByStudentId(
                student.getId(),
                Sort.by(Sort.Direction.DESC, "lastPlayed")
        ).stream().limit(5).collect(Collectors.toList());

        List<StudentProgressDTO.RecentGameDTO> recentGameDTOs = recentGames.stream()
                .map(gp -> StudentProgressDTO.RecentGameDTO.builder()
                        .gameId(gp.getGameId())
                        .gameTitle("Game " + gp.getGameId()) // You'd fetch actual game title
                        .score(gp.getScore())
                        .completionStatus(gp.getCompletionStatus().name())
                        .lastPlayed(gp.getLastPlayed().toString())
                        .build())
                .collect(Collectors.toList());

        return StudentProgressDTO.builder()
                .studentId(student.getId())
                .username(user.getUsername())
                .totalPoints(student.getTotalPoints())
                .level(student.getLevel())
                .recentGames(recentGameDTOs)
                .gamesCompleted(student.getProgressData().getGamesCompleted())
                .totalTimeSpent(student.getProgressData().getTotalTimeSpent())
                .averageScore(student.getProgressData().getAverageScore())
                .build();
    }

    @Transactional
    public Student updateStudentProgress(String studentId, int pointsEarned) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        int newTotalPoints = student.getTotalPoints() + pointsEarned;
        student.setTotalPoints(newTotalPoints);

        // Calculate new level based on points
        int newLevel = calculateStudentLevel(newTotalPoints);
        student.setLevel(newLevel);

        return studentRepository.save(student);
    }

    public List<GameProgress> getStudentGameHistory(String studentId) {
        return gameProgressRepository.findByStudentId(
                studentId,
                Sort.by(Sort.Direction.DESC, "lastPlayed")
        );
    }

    @Transactional
    public void enrollStudentInClass(String studentId, String classId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        student.setClassId(classId);
        studentRepository.save(student);

        // Also add student to class's studentIds list
        var classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + classId));

        if (!classEntity.getStudentIds().contains(studentId)) {
            classEntity.getStudentIds().add(studentId);
            classRepository.save(classEntity);
        }
    }

    public int calculateStudentLevel(int totalPoints) {
        return (int) Math.floor(Math.sqrt(totalPoints / 100.0));
    }
}
