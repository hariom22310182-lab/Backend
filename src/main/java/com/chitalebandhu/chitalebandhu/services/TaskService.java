package com.chitalebandhu.chitalebandhu.services;

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
import org.springframework.data.mongodb.core.messaging.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayDeque;
import java.util.Deque;

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

    public void addTask(Tasks task){
      //  validateTaskForCreateOrUpdate(task);
        taskRepository.save(task);

        if (task.getParentTaskId() != null && !task.getParentTaskId().trim().isEmpty()) {
            recalculateProjectStats(task.getParentTaskId());
        }
    }

    public Tasks getTaskById(String id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public long getCountByPriority(String priority){
        return taskRepository.countByPriority(priority);
    }

    public List<Tasks> getTaskByOwner(String ownerId){
        Optional<List<Tasks>> tasks = taskRepository.findByOwnerId(ownerId);
        return tasks.orElse(List.of());
    }

    public void deleteTaskById(String id){
        Tasks existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        final String parentTaskId = existing.getParentTaskId();

        deleteDescendantsByParentId(existing.getId());

        taskRepository.delete(existing);

        if (parentTaskId != null && !parentTaskId.trim().isEmpty()) {
            recalculateProjectStats(parentTaskId);
        }
    }

    public Tasks updateProgress(String id, short progress){
        Optional <Tasks> task = taskRepository.findById(id);
        if(task.isPresent()){
            task.get().setProgress(progress);
            taskRepository.save(task.get());
        }
        else{
            throw new ResourceNotFoundException("Task not found with id : " + id);
        }
        return null;
    }

    public void toggleType(String id){
        Optional<Tasks> existingTask = taskRepository.findById(id);
        existingTask.get().setIsProject(true);
        taskRepository.save(existingTask.get());
    }

    public Tasks updateTaskById(String id, Tasks newTask) {
        Optional<Tasks> existingTask = taskRepository.findById(id);

        if(existingTask.isEmpty()){
            return null;
        }


        final Tasks task = existingTask.get();
        final String oldParentTaskId = task.getParentTaskId();

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

        if (newTask.getRemark() != null && !newTask.getRemark().trim().isEmpty()) {
            task.setRemark(newTask.getRemark());
        }

        if (newTask.getPriority() != null && !newTask.getPriority().trim().isEmpty()) {
            task.setPriority(newTask.getPriority());
        }

        if (newTask.getType() != null && !newTask.getType().trim().isEmpty()) {
            task.setType(newTask.getType());
        }

        if (newTask.getParentTaskId() != null) {
            task.setParentTaskId(newTask.getParentTaskId());
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

        final String newParentTaskId = saved.getParentTaskId();
        if (oldParentTaskId != null && !oldParentTaskId.trim().isEmpty()) {
            recalculateProjectStats(oldParentTaskId);
        }
        if (newParentTaskId != null && !newParentTaskId.trim().isEmpty()
                && !newParentTaskId.equals(oldParentTaskId)) {
            recalculateProjectStats(newParentTaskId);
        }

      return saved;
    }

    public long getTaskCountByParentTaskIdAndStatus(String parentTaskId, String status){
        return taskRepository.countByParentTaskIdAndStatus(parentTaskId, status);
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
        if (saved.getParentTaskId() != null && !saved.getParentTaskId().trim().isEmpty()) {
            recalculateProjectStats(saved.getParentTaskId());
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
        if (saved.getParentTaskId() != null && !saved.getParentTaskId().trim().isEmpty()) {
            recalculateProjectStats(saved.getParentTaskId());
        }

        return saved;
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

//    public Page<Tasks> getAllTasksByRootTypePaginated(String rootType, int page, int size){
//        Pageable pageable = PageRequest.of(page, size, Sort.by("_id").descending());
//        return taskRepository.findByRootType(rootType, pageable);
//    }

    public Page<Tasks> getTaskByOwnerPaginated(String ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("_id").descending());
        return taskRepository.findByOwnerId(ownerId, pageable);
    }

//    private void validateTaskForCreateOrUpdate(Tasks task) {
//        final String type = normalize(task.getType());
//        if (type.isEmpty()) {
//            throw new IllegalStateException("Task type is required (PROJECT or TASK)");
//        }
//
//        task.setType(type);
//
//        if ("TASK".equals(type)) {
//            if (task.getParentTaskId() == null || task.getParentTaskId().trim().isEmpty()) {
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

    private void recalculateProjectStats(String projectId) {
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

        long total = taskRepository.countByParentTaskId(projectId);
        long completed = taskRepository.countByParentTaskIdAndStatusIn(projectId, DONE_STATUSES);
        long remaining = Math.max(0, total - completed);
        List<Tasks> children = taskRepository.findByParentTaskId(projectId);
        int completedContribution = children.stream()
                .filter(child -> DONE_STATUSES.contains(normalize(child.getStatus())))
                .mapToInt(Tasks::getContributionPercent)
                .sum();

        project.setCompletedTask((int) completed);
        project.setRemainingTask((int) remaining);
        project.setProgress((short) Math.min(100, Math.max(0, completedContribution)));

        taskRepository.save(project);
    }

    private void deleteDescendantsByParentId(String rootParentId) {
        if (rootParentId == null || rootParentId.trim().isEmpty()) {
            return;
        }

        Deque<String> queue = new ArrayDeque<>();
        List<Tasks> toDelete = new ArrayList<>();
        queue.add(rootParentId);

        while (!queue.isEmpty()) {
            String parentId = queue.removeFirst();
            List<Tasks> children = taskRepository.findByParentTaskId(parentId);
            if (children.isEmpty()) {
                continue;
            }

            toDelete.addAll(children);
            for (Tasks child : children) {
                if (child.getId() != null && !child.getId().trim().isEmpty()) {
                    queue.add(child.getId());
                }
            }
        }

        if (!toDelete.isEmpty()) {
            taskRepository.deleteAll(toDelete);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private void createTransitionActivity(Tasks task, String actorId, String verb) {
        try {
            Activity activity = new Activity();

            String projectId = task.getParentTaskId();
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

    private String resolveActorName(String actorId) {
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
}
