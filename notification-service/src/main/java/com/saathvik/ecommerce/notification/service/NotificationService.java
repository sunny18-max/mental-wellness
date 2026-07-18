package com.saathvik.ecommerce.notification.service;

import com.saathvik.ecommerce.notification.document.NotificationLog;
import com.saathvik.ecommerce.notification.dto.EmailRequest;
import com.saathvik.ecommerce.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;

    public NotificationLog sendEmail(EmailRequest request) {
        NotificationLog.NotificationStatus status;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.recipient());
            message.setSubject(request.subject());
            message.setText(request.body());
            mailSender.send(message);
            status = NotificationLog.NotificationStatus.SENT;
        } catch (Exception ex) {
            log.warn("Failed to send email to {}: {}", request.recipient(), ex.getMessage());
            status = NotificationLog.NotificationStatus.FAILED;
        }

        NotificationLog logEntry = NotificationLog.builder()
                .recipient(request.recipient())
                .subject(request.subject())
                .body(request.body())
                .type(NotificationLog.NotificationType.EMAIL)
                .status(status)
                .sentAt(Instant.now())
                .build();

        return notificationLogRepository.save(logEntry);
    }
}
