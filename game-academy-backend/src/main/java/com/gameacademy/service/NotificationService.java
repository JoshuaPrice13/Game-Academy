package com.gameacademy.service;

import com.gameacademy.exception.ResourceNotFoundException;
import com.gameacademy.model.Notification;
import com.gameacademy.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification queueNotification(String userId, String message,
                                         Notification.NotificationType type,
                                         String relatedEntityId) {
        log.info("Queuing notification for user {}: {}", userId, message);

        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .relatedEntityId(relatedEntityId)
                .build();

        return notificationRepository.save(notification);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndIsRead(userId, false);
    }

    public List<Notification> getAllNotifications(String userId, int limit) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().limit(limit).toList();
    }

    @Transactional
    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Transactional
    public void sendGoalCompletionNotification(String classId, List<String> studentIds, String goalDescription) {
        log.info("Sending goal completion notifications for class {}", classId);

        String message = String.format("Congratulations! Your class completed the goal: %s", goalDescription);

        for (String studentId : studentIds) {
            queueNotification(studentId, message, Notification.NotificationType.GOAL_COMPLETION, classId);
        }
    }

    @Transactional
    public void sendLeaderboardUpdateNotification(String userId, int oldRank, int newRank, String classId) {
        if (oldRank == newRank) {
            return; // No change
        }

        String message;
        if (newRank < oldRank) {
            message = String.format("Great job! You moved up to rank #%d in your class!", newRank);
        } else {
            message = String.format("Your class rank changed to #%d. Keep learning to improve!", newRank);
        }

        queueNotification(userId, message, Notification.NotificationType.RANK_CHANGE, classId);
    }

    @Transactional
    public void sendAchievementNotification(String userId, String achievementName) {
        String message = String.format("🎉 Achievement unlocked: %s!", achievementName);
        queueNotification(userId, message, Notification.NotificationType.ACHIEVEMENT_EARNED, null);
    }
}
