package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.entity.Notification;
import com.chitalebandhu.chitalebandhu.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("{id}")
    public List<Notification> getNotificationByUserId(@PathVariable String id){
        return notificationService.getNotificationByUserId(id);
    }

    @DeleteMapping ("softDelete/{id}")
    public void softDelete(@PathVariable String id){
        notificationService.softDelete(id);
    }

    @PostMapping("add")
    public void addNotification(@RequestBody Notification newNotification){
        notificationService.addNotification(newNotification);
    }

    @PutMapping("update/{id}")
    public void updateNotificationById(@PathVariable String id, @RequestBody Notification newNotification){
        notificationService.updateNotificationById(id, newNotification);
    }
}