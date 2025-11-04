package com.gameacademy.service;

import com.gameacademy.model.GameProgress;
import com.gameacademy.model.Student;
import com.gameacademy.repository.GameProgressRepository;
import com.gameacademy.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameProgressServiceTest {

    @Mock
    private GameProgressRepository gameProgressRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private GameProgressService gameProgressService;

    @Test
    public void testRecordGameSession_NewProgress() {
        String studentId = "student1";
        String gameId = "game1";
        int score = 80;
        int timeSpent = 300;

        Student student = Student.builder()
                .id(studentId)
                .totalPoints(0)
                .progressData(new Student.ProgressData())
                .build();

        when(gameProgressRepository.findByStudentIdAndGameId(studentId, gameId))
                .thenReturn(Optional.empty());
        when(gameProgressRepository.save(any(GameProgress.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArguments()[0]);

        GameProgress result = gameProgressService.recordGameSession(studentId, gameId, score, timeSpent);

        assertNotNull(result);
        assertEquals(score, result.getScore());
        assertEquals(score, result.getBestScore());
        assertEquals(GameProgress.CompletionStatus.COMPLETED, result.getCompletionStatus());
        verify(gameProgressRepository, times(1)).save(any(GameProgress.class));
    }
}
