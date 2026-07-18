package com.saathvik.ecommerce.notification.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    private String id;

    private String recipient;
    private String subject;
    private String body;
    private NotificationType type;
    private NotificationStatus status;
    private Instant sentAt;

    public enum NotificationType {
        EMAIL, SMS
    }

    public enum NotificationStatus {
        SENT, FAILED
    }
}
