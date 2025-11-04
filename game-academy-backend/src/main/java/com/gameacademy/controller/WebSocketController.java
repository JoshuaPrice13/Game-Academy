package com.gameacademy.controller;

import com.gameacademy.model.Leaderboard;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WebSocket", description = "Real-time WebSocket messaging")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/leaderboard/subscribe/{classId}")
    @SendTo("/topic/leaderboard/{classId}")
    public String subscribeToLeaderboard(@DestinationVariable String classId) {
        log.info("Client subscribed to leaderboard for class: {}", classId);
        return "Subscribed to leaderboard updates for class: " + classId;
    }

    @MessageMapping("/notifications/subscribe/{userId}")
    @SendTo("/topic/notifications/{userId}")
    public String subscribeToNotifications(@DestinationVariable String userId) {
        log.info("Client subscribed to notifications for user: {}", userId);
        return "Subscribed to notifications for user: " + userId;
    }

    public void sendLeaderboardUpdate(String classId, Leaderboard leaderboard) {
        log.info("Broadcasting leaderboard update for class: {}", classId);
        messagingTemplate.convertAndSend("/topic/leaderboard/" + classId, leaderboard);
    }

    public void sendNotification(String userId, Map<String, Object> notification) {
        log.info("Sending notification to user: {}", userId);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }

    public void sendGoalUpdate(String classId, Map<String, Object> goalData) {
        log.info("Broadcasting goal update for class: {}", classId);
        messagingTemplate.convertAndSend("/topic/goals/" + classId, goalData);
    }

    public void broadcastAchievement(String userId, Map<String, Object> achievementData) {
        log.info("Broadcasting achievement to user: {}", userId);
        messagingTemplate.convertAndSend("/topic/achievements/" + userId, achievementData);
    }
}
