package com.gameacademy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    @Field("userId")
    @Indexed
    private String userId;

    @Field("message")
    private String message;

    @Field("type")
    private NotificationType type;

    @Field("isRead")
    @Builder.Default
    private Boolean isRead = false;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("relatedEntityId")
    private String relatedEntityId; // e.g., classId, gameId, etc.

    public enum NotificationType {
        RANK_CHANGE,
        GOAL_COMPLETION,
        ACHIEVEMENT_EARNED,
        CLASS_UPDATE,
        GAME_COMPLETION,
        POINTS_AWARDED
    }
}
