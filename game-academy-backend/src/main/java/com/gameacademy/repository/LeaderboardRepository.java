package com.gameacademy.repository;

import com.gameacademy.model.Leaderboard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardRepository extends MongoRepository<Leaderboard, String> {

    Optional<Leaderboard> findByClassIdAndPeriod(String classId, Leaderboard.Period period);
}
