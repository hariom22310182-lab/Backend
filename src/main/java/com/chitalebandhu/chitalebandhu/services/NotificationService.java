package com.chitalebandhu.chitalebandhu.services;
import com.chitalebandhu.chitalebandhu.entity.Notification;
import com.chitalebandhu.chitalebandhu.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getNotificationByUserId(String id){
        Optional<List<Notification>> exisitingNotifications =  notificationRepository.findByUserIdAndIsDeleted(id, false);
        if(exisitingNotifications.isPresent()){
            return exisitingNotifications.get();
        }
        else{
            throw new RuntimeException("NotificationService > getNotificationByUserId > No notifications found");
        }
    }

    public void addNotification(Notification newNotification){
        notificationRepository.save(newNotification);
    }

    public void deleteNotificationByHelperId(String id){
        notificationRepository.deleteByHelperId(id);
    }

    public void softDelete(String id){
        Optional<Notification> notification = notificationRepository.findById(id);
        if(notification.isPresent()){
            notification.get().setDeleted(true);
            notificationRepository.save(notification.get());
        }
        else{
            throw new RuntimeException("NotificationService > softDelete > Notification not found!");
        }
    }

    public void updateNotificationById(String id, Notification newNotification){
        Optional<Notification> exisitingNotification = notificationRepository.findById(id);
        if(exisitingNotification.isPresent()){
            if(newNotification.getIsRead() != null){
                exisitingNotification.get().setIsRead(newNotification.getIsRead());
            }
            if(newNotification.getUserId() != null && !newNotification.getUserId().isEmpty()){
                exisitingNotification.get().setUserId(newNotification.getUserId());
            }
            if(newNotification.getEventType() != null && !newNotification.getEventType().isEmpty()){
                exisitingNotification.get().setEventType(newNotification.getEventType());
            }
            if(newNotification.getTime() != null){
                exisitingNotification.get().setTime(newNotification.getTime());
            }
            if(newNotification.getMessage() != null && !newNotification.getMessage().isEmpty()){
                exisitingNotification.get().setMessage(newNotification.getMessage());
            }
            if(newNotification.getHelperId() != null && !newNotification.getHelperId().isEmpty()){
                exisitingNotification.get().setHelperId(newNotification.getHelperId());
            }
            notificationRepository.save(exisitingNotification.get());
        }
        else{
            throw new RuntimeException("NotificationService > updateNotificationById > ExisitingNotification is not present");
        }
    }

    public boolean isNotificationAlreadySent(String taskId, String eventType){
        return notificationRepository.existsByHelperIdAndEventType(taskId, eventType);
    }
}