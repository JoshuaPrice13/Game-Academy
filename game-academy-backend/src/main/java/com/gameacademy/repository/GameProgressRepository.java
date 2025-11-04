package com.gameacademy.repository;

import com.gameacademy.model.GameProgress;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameProgressRepository extends MongoRepository<GameProgress, String> {

    List<GameProgress> findByStudentId(String studentId);

    List<GameProgress> findByStudentId(String studentId, Sort sort);

    Optional<GameProgress> findByStudentIdAndGameId(String studentId, String gameId);

    List<GameProgress> findByGameId(String gameId);
}
