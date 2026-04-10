package com.chitalebandhu.chitalebandhu.entity;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Remark {

    @Id
    private String id;
    private String senderName;
    private String senderId;
    private List<String> mentionedUserId = new ArrayList<>();
    private String message;
    private LocalDateTime time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public List<String> getMentionedUserId() {
        return mentionedUserId;
    }

    public void setMentionedUserId(List<String> mentionedUserId) {
        this.mentionedUserId = mentionedUserId;
    }

    public void addMentionedUserId(String userId) {
        mentionedUserId.add(userId);
    }

    public void removeMentionedId(String userId) {
        mentionedUserId.remove(userId);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}