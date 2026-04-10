package com.chitalebandhu.chitalebandhu.entity;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Notification {
    @Id
    private String id;
    private String message;
    private LocalDateTime time;
    private String userId;
    private Boolean isRead;
    // event type decides which page to navigate
    // evenTypes = NEW_TASK_CREATION, REMARK_SECTION, REVIEW_REQUEST, OVERDUE_WARNING, PROJECT_READY_TO_WORK
    // When eventType = NEW_TASK_CREATION, then navigate to the newly created project
    // when eventType = REMARK_SECTION, navigate to the remark page of the project where the notification came
    // when eventType = OVERDUE_WARNING, navigate to the project / task detail page
    // when eventType = PROEJCT_READY_TO_WORK, navigate to project /task detail page (When dependent tasks are completed, when start date is reached)
    private String eventType;
    // helper id will be the project's id where we'll navigate
    private String helperId;

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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }
}
