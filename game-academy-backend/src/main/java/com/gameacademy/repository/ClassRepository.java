package com.gameacademy.repository;

import com.gameacademy.model.ClassEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<ClassEntity, String> {

    List<ClassEntity> findByTeacherId(String teacherId);

    List<ClassEntity> findAllByStudentIdsContaining(String studentId);
}
