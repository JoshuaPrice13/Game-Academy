package com.gameacademy.repository;

import com.gameacademy.model.Student;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByUserId(String userId);

    List<Student> findByClassId(String classId);

    List<Student> findByClassId(String classId, Sort sort);

    default List<Student> findTopByClassIdOrderByTotalPointsDesc(String classId, int limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "totalPoints");
        return findByClassId(classId, sort);
    }
}
