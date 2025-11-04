package com.gameacademy.service;

import com.gameacademy.model.Student;
import com.gameacademy.repository.ClassRepository;
import com.gameacademy.repository.GameProgressRepository;
import com.gameacademy.repository.StudentRepository;
import com.gameacademy.repository.UserRepository;
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
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameProgressRepository gameProgressRepository;

    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    public void testCalculateStudentLevel() {
        assertEquals(0, studentService.calculateStudentLevel(0));
        assertEquals(1, studentService.calculateStudentLevel(100));
        assertEquals(2, studentService.calculateStudentLevel(400));
        assertEquals(3, studentService.calculateStudentLevel(900));
    }

    @Test
    public void testUpdateStudentProgress() {
        Student student = Student.builder()
                .id("student1")
                .userId("user1")
                .totalPoints(100)
                .level(1)
                .build();

        when(studentRepository.findById("student1")).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArguments()[0]);

        Student updated = studentService.updateStudentProgress("student1", 50);

        assertEquals(150, updated.getTotalPoints());
        verify(studentRepository, times(1)).save(student);
    }
}
