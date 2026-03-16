package com.chitalebandhu.chitalebandhu.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "tasks")
public class Tasks {
    @Id
    private String id;
    private String title;
    private String description;
    private String priority;
    private String type; // Here type we assign as PROJECT / TASK
    private String status; // NOT_STARTED / IN_PROGRESS / DONE / OVERDUE
    private String ownerId;
    private String parentTaskId;
    private short progress; // out of 100 (it'll be represented as percentage)
    @JsonProperty("contributionPercent")
    @JsonAlias({"contribution", "contribution_percentage"})
    private int contributionPercent;
    private String remark;
    private LocalDate deadLine;
    private LocalDate startDate;
    private int remainingTask;
    private int completedTask;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadLine;
    }

    public void setDeadline(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getCompletedTask() {
        return completedTask;
    }

    public void setCompletedTask(int completedTask) {
        this.completedTask = completedTask;
    }

    public int getRemainingTask() {
        return remainingTask;
    }

    public void setRemainingTask(int remainingTask) {
        this.remainingTask = remainingTask;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public short getProgress() {
        return progress;
    }

    public void setProgress(short progress) {
        this.progress = progress;
    }

    public int getContributionPercent() {
        return contributionPercent;
    }

    public void setContributionPercent(int contributionPercent) {
        this.contributionPercent = contributionPercent;
    }
}
