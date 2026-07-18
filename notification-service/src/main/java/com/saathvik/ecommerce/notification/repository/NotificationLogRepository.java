package com.saathvik.ecommerce.notification.repository;

import com.saathvik.ecommerce.notification.document.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {
}
