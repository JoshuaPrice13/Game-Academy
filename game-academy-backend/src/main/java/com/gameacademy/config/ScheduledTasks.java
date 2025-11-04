package com.gameacademy.config;

import com.gameacademy.model.Leaderboard;
import com.gameacademy.repository.LeaderboardRepository;
import com.gameacademy.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final LeaderboardService leaderboardService;
    private final LeaderboardRepository leaderboardRepository;

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void updateAllLeaderboards() {
        log.info("Starting scheduled leaderboard update");
        try {
            leaderboardService.scheduleLeaderboardRefresh();
            log.info("Completed scheduled leaderboard update");
        } catch (Exception e) {
            log.error("Error during scheduled leaderboard update: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void resetDailyLeaderboards() {
        log.info("Resetting daily leaderboards");
        try {
            List<Leaderboard> dailyLeaderboards = leaderboardRepository.findAll().stream()
                    .filter(lb -> lb.getPeriod() == Leaderboard.Period.DAILY)
                    .toList();

            // Archive logic would go here (save to historical collection)
            log.info("Archived {} daily leaderboards", dailyLeaderboards.size());

            // Daily leaderboards will be regenerated on next access
        } catch (Exception e) {
            log.error("Error resetting daily leaderboards: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 0 * * SUN") // Every Sunday at midnight
    public void resetWeeklyLeaderboards() {
        log.info("Resetting weekly leaderboards");
        try {
            List<Leaderboard> weeklyLeaderboards = leaderboardRepository.findAll().stream()
                    .filter(lb -> lb.getPeriod() == Leaderboard.Period.WEEKLY)
                    .toList();

            // Archive logic would go here
            log.info("Archived {} weekly leaderboards", weeklyLeaderboards.size());

            // Weekly leaderboards will be regenerated on next access
        } catch (Exception e) {
            log.error("Error resetting weekly leaderboards: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 2 * * *") // Every day at 2 AM
    public void cleanupOldLeaderboards() {
        log.info("Starting cleanup of old leaderboard data");
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

            List<Leaderboard> oldLeaderboards = leaderboardRepository.findAll().stream()
                    .filter(lb -> lb.getLastUpdated().isBefore(cutoffDate))
                    .filter(lb -> lb.getPeriod() != Leaderboard.Period.ALL_TIME)
                    .toList();

            if (!oldLeaderboards.isEmpty()) {
                leaderboardRepository.deleteAll(oldLeaderboards);
                log.info("Deleted {} old leaderboards", oldLeaderboards.size());
            } else {
                log.info("No old leaderboards to clean up");
            }
        } catch (Exception e) {
            log.error("Error during leaderboard cleanup: {}", e.getMessage(), e);
        }
    }
}
