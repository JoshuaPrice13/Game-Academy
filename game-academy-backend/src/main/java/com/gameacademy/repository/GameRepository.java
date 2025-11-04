package com.gameacademy.repository;

import com.gameacademy.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {

    List<Game> findBySubject(String subject);

    List<Game> findByGradeLevel(String gradeLevel);

    List<Game> findBySubjectAndGradeLevel(String subject, String gradeLevel);
}
