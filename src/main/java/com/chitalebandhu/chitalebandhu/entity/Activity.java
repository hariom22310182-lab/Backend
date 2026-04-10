package com.chitalebandhu.chitalebandhu.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "activity")
public class Activity {
    @Id
    private String id;
    private String projectId;
    private String userName; // Ram, sham
    private String verb; // created, started, completed, deleted, submitted review
    private String projectName; // Improving the marketing strategy
    private Instant time;
    private String visibility; // Set visibility as Task / Project - Tasks with Visibility as Task will only be seen by team leader and visibility with Project will be seen by admin only
    // final activity = Ram created Improving the marketing strategy - 2 hours/days/months ago

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public void setCurrentTimeUtc() {
        this.time = Instant.now();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
