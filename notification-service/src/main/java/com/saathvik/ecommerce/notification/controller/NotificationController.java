package com.saathvik.ecommerce.notification.controller;

import com.saathvik.ecommerce.notification.document.NotificationLog;
import com.saathvik.ecommerce.notification.dto.EmailRequest;
import com.saathvik.ecommerce.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<NotificationLog> sendEmail(@Valid @RequestBody EmailRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.sendEmail(request));
    }
}
