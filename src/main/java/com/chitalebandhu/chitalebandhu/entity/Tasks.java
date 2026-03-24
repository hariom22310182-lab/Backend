package com.chitalebandhu.chitalebandhu.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private boolean isProject;
    private String type; // Here type we assign as PROJECT / TASK
    private String status; // NOT_STARTED / IN_PROGRESS / DONE / OVERDUE
    private String ownerId;
    private String parentId;

    private List<String> collaboratedProjects = new ArrayList<>();
    private List<String> dependencies;

    // We'll be assigning levels to each project and task we create, project (with no parent) will be root (with level = 0)
    // When we create task within a project we'll assign task's level = parent level + 1
    // same within task, when we add task within a task, we'll asign the subtask level = parent task level + 1
    // The add task button will be conditioned by this level, if current task level == 2 then do not show add button
    // if current task level is less than 2 then show add button
    // this controls the branching depth
    private short level;
    private short progress; // out of 100 (it'll be represented as percentage)
    @JsonProperty("contributionPercent")
    @JsonAlias({"contribution", "contribution_percentage"})
    private int contributionPercent;

    // When project's remaining days are less than critical days, then that project is in critical condition
    // Team leader will asign this critical days from project creation page
    private int criticalDays;
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
        return parentId;
    }

    public void setParentTaskId(String parentId) {
        this.parentId = parentId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCriticalDays() {
        return criticalDays;
    }

    public void setCriticalDays(int criticalDays) {
        this.criticalDays = criticalDays;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public boolean getIsProject() {
        return isProject;
    }

    public void setIsProject(boolean project) {
        isProject = project;
    }

    public List<String> getCollaboratedProjects() {
        return collaboratedProjects;
    }

    public void addCollaboratedProjects(String id) {
        collaboratedProjects.add(id);
    }

    public void removeCollaboratedProjects(String id){
        collaboratedProjects.remove(id);
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void addDependencies(String id) {
        dependencies.add(id);
    }

    public void removeDependencies(String id){
        dependencies.remove(id);
    }
}
