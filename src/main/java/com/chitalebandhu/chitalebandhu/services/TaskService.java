package com.chitalebandhu.chitalebandhu.services;

import com.chitalebandhu.chitalebandhu.entity.Remark;
import com.chitalebandhu.chitalebandhu.entity.Tasks;
import com.chitalebandhu.chitalebandhu.entity.Activity;
import com.chitalebandhu.chitalebandhu.entity.Member;
import com.chitalebandhu.chitalebandhu.exceptions.ResourceNotFoundException;
import com.chitalebandhu.chitalebandhu.repository.TaskRepository;
import com.chitalebandhu.chitalebandhu.repository.ActivityRepository;
import com.chitalebandhu.chitalebandhu.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    private static final List<String> DONE_STATUSES = Arrays.asList("DONE", "COMPLETED");

    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;
    private final MemberRepository memberRepository;

    public TaskService(
            TaskRepository taskRepository,
            ActivityRepository activityRepository,
            MemberRepository memberRepository
    ) {
        this.taskRepository = taskRepository;
        this.activityRepository = activityRepository;
        this.memberRepository = memberRepository;
    }

    public List<Tasks> getCollaboratedProjects(String id){
        Tasks task = getTaskById(id);
        List<String> projectIds =  task.getCollaboratedProjects();

        List<Tasks> projects  = new ArrayList<>();

        for (String projectId : projectIds) {
            Tasks temp = getTaskById(projectId);
            projects.add(temp);
        }
        return projects;
    }
    public List<Tasks> getDependency(String id){
        Tasks task = getTaskById(id);
        List<String> projectIds =  task.getDependencies();

        List<Tasks> projects  = new ArrayList<>();

        for (String projectId : projectIds) {
            Tasks temp = getTaskById(projectId);
            projects.add(temp);
        }

        return projects;
    }

    public void addTask(Tasks task){
        if(task.getParentId() != null){
            toggleType(task.getParentId());
        }
        taskRepository.save(task);

        if (task.getParentId() != null && !task.getParentId().trim().isEmpty()) {
            recalculateProjectStats(task.getParentId());
        }
    }



    public Tasks getTaskById(String id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public long getCountByPriority(String priority){
        return taskRepository.countByPriority(priority);
    }

    public List<Tasks> getTaskByOwnerId(String ownerId){
        Optional<List<Tasks>> tasks = taskRepository.findByOwnerId(ownerId);
        return tasks.orElse(List.of());
    }

    public void deleteTaskById(String id){
        deleteDescendantsByParentId(id);
    }

    public void updateProgress(String id, short progress){
        Optional <Tasks> task = taskRepository.findById(id);
        if(task.isPresent()){
            task.get().setProgress(progress);
            taskRepository.save(task.get());
        }
        else{
            throw new ResourceNotFoundException("Task not found with id : " + id);
        }
    }

    public void toggleType(String id){
        Optional<Tasks> existingTask = taskRepository.findById(id);
        if(existingTask.isPresent()){
            existingTask.get().setIsProject(true);
            taskRepository.save(existingTask.get());
        }
        else{
            throw new RuntimeException("Task not found");
        }
    }

    public Tasks updateTaskById(String id, Tasks newTask) {
        Optional<Tasks> existingTask = taskRepository.findById(id);

        if(existingTask.isEmpty()){
            return null;
        }


        final Tasks task = existingTask.get();
        final String oldParentId = task.getParentId();

        if (newTask.getTitle() != null && !newTask.getTitle().trim().isEmpty()) {
            task.setTitle(newTask.getTitle());
        }

        if (newTask.getDescription() != null && !newTask.getDescription().trim().isEmpty()) {
            task.setDescription(newTask.getDescription());
        }

        if (newTask.getStatus() != null && !newTask.getStatus().trim().isEmpty()) {
            final String requestedStatus = normalize(newTask.getStatus());
            if (DONE_STATUSES.contains(requestedStatus)) {
                throw new IllegalStateException("Direct DONE update is blocked. Use /tasks/{id}/status/transition/DONE");
            }
            task.setStatus(newTask.getStatus());
        }

        if (newTask.getOwnerId() != null && !newTask.getOwnerId().trim().isEmpty()) {
            task.setOwnerId(newTask.getOwnerId());
        }

        task.setCriticalDays(newTask.getCriticalDays());

        if (newTask.getPriority() != null && !newTask.getPriority().trim().isEmpty()) {
            task.setPriority(newTask.getPriority());
        }

        if (newTask.getType() != null && !newTask.getType().trim().isEmpty()) {
            task.setType(newTask.getType());
        }

        if (newTask.getParentId() != null) {
            task.setParentId(newTask.getParentId());
        }

        if (newTask.getDeadline() != null) {
            task.setDeadline(newTask.getDeadline());
        }

        if (newTask.getStartDate() != null) {
            task.setStartDate(newTask.getStartDate());
        }

        if (newTask.getProgress() > 0) {
            task.setProgress(newTask.getProgress());
        }

        if (newTask.getContributionPercent() > 0) {
            task.setContributionPercent(newTask.getContributionPercent());
        }

      //  validateTaskForCreateOrUpdate(task);

        final Tasks saved = taskRepository.save(task);

        final String newParentId = saved.getParentId();
        if (oldParentId != null && !oldParentId.trim().isEmpty()) {
            recalculateProjectStats(oldParentId);
        }
        if (newParentId != null && !newParentId.trim().isEmpty()
                && !newParentId.equals(oldParentId)) {
            recalculateProjectStats(newParentId);
        }

      return saved;
    }

    public long getTaskCountByParentIdAndStatus(String parentId, String status){
        return taskRepository.countByParentIdAndStatus(parentId, status);
    }

    public void updateStatusById(String id, String status){
        final String requestedStatus = normalize(status);
        if (DONE_STATUSES.contains(requestedStatus)) {
            throw new IllegalStateException("Direct DONE update is blocked. Use /tasks/{id}/status/transition/DONE");
        }

        Optional<Tasks> existingTask = taskRepository.findById(id);
        if(existingTask.isPresent()){
            existingTask.get().setStatus(status);
        }
        else{
            return;
        }
        Tasks saved = taskRepository.save(existingTask.get());
        if (saved.getParentId() != null && !saved.getParentId().trim().isEmpty()) {
            recalculateProjectStats(saved.getParentId());
        }
    }

    public Tasks transitionTaskStatusWithReview(String id, String status, String actorId, String actorRole) {
        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        final String nextStatus = normalize(status);
        final String currentStatus = normalize(task.getStatus());
        final String role = normalize(actorRole);

        if (nextStatus.isEmpty()) {
            throw new IllegalStateException("Status is required");
        }

        // Task owner can only move to REVIEW. It cannot self-complete to DONE.
        if ("REVIEW".equals(nextStatus)) {

            task.setStatus("REVIEW");
            createTransitionActivity(task, actorId, "submitted for review in");
        } else if (DONE_STATUSES.contains(nextStatus)) {
            // Completion is allowed only from REVIEW and only by project owner or admin.
            if (!"REVIEW".equals(currentStatus)) {
                throw new IllegalStateException("Task must be in REVIEW before marking DONE");
            }

            task.setStatus("DONE");
            createTransitionActivity(task, actorId, "approved completion for");
        } else {
            throw new IllegalStateException("Unsupported transition status: " + nextStatus + ". Use REVIEW or DONE.");
        }

        Tasks saved = taskRepository.save(task);
        if (saved.getParentId() != null && !saved.getParentId().trim().isEmpty()) {
            recalculateProjectStats(saved.getParentId());
        }

        return saved;
    }
    public List<Tasks> getAllProjects(){
     Optional<List<Tasks>> tasks = taskRepository.findByTypeOrIsProject("PROJECT" , true);

     if(tasks.isPresent()){
         return tasks.get();
     }else {
         throw  new ResourceNotFoundException("No projects found");
     }

    }
    public List<Tasks> getTasksByParentId(String parentId){
        List<Tasks> tasks = taskRepository.findByParentId(parentId);
        if(!tasks.isEmpty()){
            return tasks;
        }else {
            throw  new ResourceNotFoundException("No projects found");
        }

    }

    public long getAllTaskCountByType(String type){
        return taskRepository.countByType(type);
    }

    public List<Tasks> getAllTasksByType(String type){
        Optional <List<Tasks>> allProjects = taskRepository.findByType(type);
        return allProjects.orElse(List.of());
    }
    // Pagination methods
    public Page<Tasks> getAllTasksByTypePaginated(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("_id").descending());
        return taskRepository.findByType(type, pageable);
    }

    public Page<Tasks> getTaskByOwnerPaginated(String ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("_id").descending());
        return taskRepository.findByOwnerId(ownerId, pageable);
    }

//    public void validateTaskForCreateOrUpdate(Tasks task) {
//        final String type = normalize(task.getType());
//        if (type.isEmpty()) {
//            throw new IllegalStateException("Task type is required (PROJECT or TASK)");
//        }
//
//        task.setType(type);
//
//        if ("TASK".equals(type)) {
//            if (task.getParentId() == null || task.getParentTaskId().trim().isEmpty()) {
//                throw new IllegalStateException("TASK must have a valid parent project id");
//            }
//
//            Tasks parent = taskRepository.findById(task.getParentTaskId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Parent project not found with id: " + task.getParentTaskId()));
//
//            if (!"PROJECT".equals(normalize(parent.getType()))) {
//                throw new IllegalStateException("Parent task must be of type PROJECT");
//            }
//
//            int contribution = task.getContributionPercent();
//            if (contribution < 0 || contribution > 100) {
//                throw new IllegalStateException("Task contribution must be between 1 and 100 percent");
//            }
//
//            List<Tasks> siblings = taskRepository.findByParentTaskId(task.getParentTaskId());
//            int assignedContribution = siblings.stream()
//                    .filter(existing -> task.getId() == null || !task.getId().equals(existing.getId()))
//                    .mapToInt(Tasks::getContributionPercent)
//                    .sum();
//
//            if (assignedContribution + contribution > 100) {
//                throw new IllegalStateException(
//                        "Task contribution exceeds the remaining project percentage. Remaining: "
//                                + Math.max(0, 100 - assignedContribution) + "%"
//                );
//            }
//        }
//
//        if ("PROJECT".equals(type)) {
//            // A PROJECT may or may not have parentTaskId (for nested project flows).
//            // Keep parentTaskId as-is and only normalize contribution fields.
//            task.setContributionPercent(0);
//        }
//
//        if (task.getStatus() == null || task.getStatus().trim().isEmpty()) {
//            task.setStatus("NOT_STARTED");
//        } else {
//            task.setStatus(normalize(task.getStatus()));
//        }
//    }

    public void recalculateProjectStats(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            return;
        }

        Optional<Tasks> projectOpt = taskRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return;
        }

        Tasks project = projectOpt.get();
        if (!"PROJECT".equals(normalize(project.getType()))) {
            return;
        }

        long total = taskRepository.countByParentId(projectId);
        long completed = taskRepository.countByParentIdAndStatusIn(projectId, DONE_STATUSES);
        long remaining = Math.max(0, total - completed);
        List<Tasks> children = taskRepository.findByParentId(projectId);
        int completedContribution = children.stream()
                .filter(child -> DONE_STATUSES.contains(normalize(child.getStatus())))
                .mapToInt(Tasks::getContributionPercent)
                .sum();

        project.setCompletedTask((int) completed);
        project.setRemainingTask((int) remaining);
        project.setProgress((short) Math.min(100, Math.max(0, completedContribution)));

        taskRepository.save(project);
    }

    public void deleteDescendantsByParentId(String rootParentId) {
        if (rootParentId == null || rootParentId.trim().isEmpty()) {
            return;
        }

        Deque<String> queue = new ArrayDeque<>();
        List<Tasks> toDelete = new ArrayList<>();
        queue.add(rootParentId);
        activityRepository.deleteByProjectId(rootParentId);

        Optional<Tasks> parent = taskRepository.findById(rootParentId);
        parent.ifPresent(toDelete::add);

        while (!queue.isEmpty()) {
            String parentId = queue.removeFirst();
            List<Tasks> children = taskRepository.findByParentId(parentId);
            if (children.isEmpty()) {
                continue;
            }

            toDelete.addAll(children);
            for (Tasks child : children) {
                if (child.getId() != null && !child.getId().trim().isEmpty()) {
                    queue.add(child.getId());
                    activityRepository.deleteByProjectId(child.getId());
                }
            }
        }

        if (!toDelete.isEmpty()) {
            taskRepository.deleteAll(toDelete);
        }
    }

    public void removeCollaboratedProject(String id, String projectId){
        Optional <Tasks> existingTask = taskRepository.findById(id);
        if(existingTask.isPresent()){
            existingTask.get().removeCollaboratedProjects(projectId);
            taskRepository.save(existingTask.get());
        }
        else{
            throw new RuntimeException("Task / Project doesn't exist");
        }
    }
    public void addCollaboratedProject(String id, String projectId){
        Optional <Tasks> existingTask = taskRepository.findById(id);
        if(existingTask.isPresent()){
            if(!existingTask.get().getCollaboratedProjects().contains(projectId)){
                existingTask.get().addCollaboratedProjects(projectId);
                taskRepository.save(existingTask.get());
            }
            else{
                throw new RuntimeException("You have already collaborated with this project");
            }
        }
        else{
            throw new RuntimeException("Task / Project doesn't exist");
        }
    }

    public void addDependency(String id, String projectId){
        System.out.println("inside add dependency");
            Optional <Tasks> existingTask = taskRepository.findById(id);
            if(existingTask.isPresent()){
                if(!existingTask.get().getDependencies().contains(projectId)){
                    existingTask.get().addDependencies(projectId);
                    taskRepository.save(existingTask.get());
                }
                else{
                    throw new IllegalStateException("Dependency already added");
                }
            }
    }
    public void addRemark(String id, Remark remark){
        Optional <Tasks> existingTask = taskRepository.findById(id);
        remark.setId(UUID.randomUUID().toString());

        if(existingTask.isPresent()){
                existingTask.get().addRemark(remark);
                taskRepository.save(existingTask.get());
        }
        else{
            throw new IllegalStateException("Failed to send message");
        }
    }

    public void removeDependency(String id, String projectId){
        Optional <Tasks> existingTask = taskRepository.findById(id);
        if(existingTask.isPresent()){
            if(existingTask.get().getDependencies().contains(projectId)){
                existingTask.get().removeDependencies(projectId);
                taskRepository.save(existingTask.get());
            }
            else throw new IllegalStateException("Dependency doesn't exist");
        }
        else{
            throw new IllegalStateException("Dependency doesn't exist");
        }
    }

    public String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    public void createTransitionActivity(Tasks task, String actorId, String verb) {
        try {
            Activity activity = new Activity();

            String projectId = task.getParentId();
            String projectName = task.getTitle();

            if (projectId != null && !projectId.trim().isEmpty()) {
                Optional<Tasks> projectOpt = taskRepository.findById(projectId);
                if (projectOpt.isPresent()) {
                    Tasks project = projectOpt.get();
                    projectName = project.getTitle();
                }
            } else if ("PROJECT".equals(normalize(task.getType()))) {
                projectId = task.getId();
            }

            activity.setProjectId(projectId);
            activity.setProjectName(projectName);
            activity.setVerb(verb);
            activity.setUserName(resolveActorName(actorId));
            if("PROJECT".equals(normalize(task.getType()))){
                activity.setVisibility("PROJECT");
            }
            else{
                if("TASK".equals(normalize(task.getType()))){
                    activity.setVisibility("TASK");
                }
            }
            activity.setTime();

            activityRepository.save(activity);
        } catch (Exception ignored) {
            // Activity logging should not block the primary transition flow.
        }
    }

    public String resolveActorName(String actorId) {
        if (actorId == null || actorId.trim().isEmpty()) {
            return "Unknown";
        }

        Optional<Member> memberById = memberRepository.findById(actorId);
        if (memberById.isPresent() && memberById.get().getName() != null
                && !memberById.get().getName().trim().isEmpty()) {
            return memberById.get().getName();
        }

        // Fallback for flows currently sending username/email instead of member id.
        return memberRepository.findAll()
                .stream()
                .filter(m -> m.getEmail() != null && m.getEmail().equalsIgnoreCase(actorId))
                .map(Member::getName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .findFirst()
                .orElse(actorId);
    }

    public List<Remark> getAllremarks(String id) {

        Tasks task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No remarks found"));

        List<Remark> remarks = task.getRemarks();

        if (remarks == null || remarks.isEmpty()) {
            return new ArrayList<>();
        }

        for (Remark remark : remarks) {

            if (remark.getSenderId() == null) continue;

            Optional<Member> member = memberRepository.findById(remark.getSenderId());

            if (member.isPresent()) {
                remark.setSenderName(member.get().getName());
            } else {
                remark.setSenderName("Unknown User"); // optional safety
            }
        }

        return remarks;
    }
}
