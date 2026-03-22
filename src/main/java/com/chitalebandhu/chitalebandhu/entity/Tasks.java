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
    private String category;
    private String priority;
    // When we add subtasks within a task (not project) we convert that task type into project so the owner of that task will now see that task in his project dashboard
    // This causes on issue, as type == PROJECT now, admin will see it in his dashboard too which we dont want, because this task is project for task owner but still a task within a project
    // To avoid this, we can manage 2 types, type == PROJECT will be used to show this task in the owner's project dashboard
    // And we'll use rootType, we'll declare rootType = PROJECT when we create project, rootType = TASK when we create task, and it'll never change, so admin will fetch only main projects
    private String rootType;
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

    public String getRootType() {
        return rootType;
    }

    public void setRootType(String rootType) {
        this.rootType = rootType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
