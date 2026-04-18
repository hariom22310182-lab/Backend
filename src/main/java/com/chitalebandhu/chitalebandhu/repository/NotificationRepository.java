package com.chitalebandhu.chitalebandhu.repository;

import com.chitalebandhu.chitalebandhu.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Optional<List<Notification>> findByUserIdAndIsDeleted(String s, boolean bool);
    boolean existsByHelperIdAndEventType(String HelperId, String EventType);
    void deleteByHelperId(String helperId);
}
