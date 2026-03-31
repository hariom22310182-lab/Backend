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
    private boolean isProject;
    private List<Remark> remarks = new ArrayList<>();
    private String type; // PROJECT / TASK
    private String status; // NOT_STARTED / IN_PROGRESS / DONE / OVERDUE
    private String ownerId;
    private String parentId;
    private List<String> collaboratedProjects = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();
    private short progress;
    @JsonProperty("contributionPercent")
    @JsonAlias({"contribution", "contribution_percentage"})
    private int contributionPercent;
    private int criticalDays;
    private LocalDate deadLine;
    private LocalDate startDate;
    private int remainingTask;
    private int completedTask;

    public void addRemark(Remark remark){
        remarks.add(remark);
    }

    public void removeRemark(Remark remark){
        remarks.remove(remark);
    }

    public void updateRemark(String remarkId, Remark newRemark){
        for (Remark exisitingRemark : remarks) {
            if (exisitingRemark.getId().equals(remarkId)) {
                exisitingRemark.setMessage(newRemark.getMessage());
                exisitingRemark.setMentionedUserId(newRemark.getMentionedUserId());
                break;
            }
        }
    }
    public List<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<Remark> remarks) {
        this.remarks = remarks;
    }

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
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
        System.out.println(id);
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
