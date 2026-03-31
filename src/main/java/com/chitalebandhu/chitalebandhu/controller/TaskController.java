package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.DTOs.PagedResponse;
import com.chitalebandhu.chitalebandhu.entity.Notification;
import com.chitalebandhu.chitalebandhu.entity.Remark;
import com.chitalebandhu.chitalebandhu.entity.Tasks;
import com.chitalebandhu.chitalebandhu.services.TaskService;
import com.chitalebandhu.chitalebandhu.services.OverdueSchedulerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("tasks")
public class TaskController {

    private final TaskService taskService;
    private final OverdueSchedulerService overdueSchedulerService;

    public TaskController(TaskService taskService, OverdueSchedulerService overdueSchedulerService) {
        this.taskService = taskService;
        this.overdueSchedulerService = overdueSchedulerService;
    }

    @GetMapping("count/{priority}")
    public long getCountByPriority(@PathVariable String priority){
        return taskService.getCountByPriority(priority);
    }

    @GetMapping("allTasks/{type}")
    public List<Tasks> getAllProject(@PathVariable String type){return taskService.getAllTasksByType(type);}

    @PostMapping("add")
    public void addTask(@RequestBody Tasks task){
        taskService.addTask(task);
    }

    @GetMapping("projects")
    public List<Tasks> getAllProjects(){
        return taskService.getAllProjects();
    }

    @GetMapping("member/{ownerId}")
    public List<Tasks> getTaskByOwnerId(@PathVariable String ownerId){
        return taskService.getTaskByOwnerId(ownerId);
    }

    @PutMapping("id/{id}/updateProgress")
    public void updateProgress(@PathVariable String id, @RequestBody short progress){
        taskService.updateProgress(id, progress);
    }

    @GetMapping("id/{id}")
    public Tasks getTasksById(@PathVariable String id){
        return taskService.getTaskById(id);
    }

    @GetMapping("parentId/{id}")
    public ResponseEntity<List<Tasks>> getTasksByParentId(@PathVariable String id){
        try{
            List<Tasks> task = taskService.getTasksByParentId(id);
            return new ResponseEntity<>(task , HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND );
        }
    }

    @GetMapping("/getAllTasksByCollaboration/{id}")
    public ResponseEntity<Map<String, List<Tasks>>> getAllTasksByCollaboration(@PathVariable String id) {
        try {
            Tasks task = taskService.getTaskById(id);

            if (task == null) {
                System.out.println("Task not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<String> projectIds = task.getCollaboratedProjects();
            Map<String, List<Tasks>> result = new HashMap<>();

            for (String projectId : projectIds) {
                try {
                    List<Tasks> tasks = taskService.getTasksByParentId(projectId);
                    Tasks parentTask = taskService.getTaskById(projectId);

                    if (parentTask != null) {
                        result.put(parentTask.getTitle(), tasks);
                    }

                } catch (Exception e) {
                    System.out.println("Skipping projectId: " + projectId);
                    System.out.println("Reason: " + e.getMessage());
                }
            }

            System.out.println("FINAL RESULT: " + result);

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 ADD THIS
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("update/{id}")
    public void updateTask(@PathVariable String id, @RequestBody Tasks newTask){
        taskService.updateTaskById(id, newTask);
    }

    @PutMapping("updateType/{Id}")
    public void toggleType(@PathVariable String id){
        taskService.toggleType(id);
    }



    @DeleteMapping("delete/{id}")
    public void deleteTask(@PathVariable("id") String id){
        taskService.deleteTaskById(id);
    }

    @PutMapping("{id}/status/update/{status}")
    public void updateStatus(@PathVariable String id, @PathVariable String status){
        taskService.updateStatusById(id, status);
    }

    @PutMapping("{id}/status/transition/{status}")
    public ResponseEntity<Tasks> transitionStatus(
            @PathVariable String id,
            @PathVariable String status,
            @RequestParam String actorId,
            @RequestParam(defaultValue = "USER") String actorRole
    )
    {
        try {
            Tasks updated = taskService.transitionTaskStatusWithReview(id, status, actorId, actorRole);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dependency/add/{id}")
    public void addDependency(@PathVariable String id, @RequestBody String projectId){

        taskService.addDependency(id, projectId);
    }

    @DeleteMapping("dependency/remove/{id}")
    public void removeDependency(@PathVariable String id, @RequestBody String projectId){
        taskService.removeDependency(id, projectId);
    }

    @GetMapping("getCollaboratedProject/{id}")
    public List<Tasks> getCollaboratedProjects(@PathVariable String id){
        return taskService.getCollaboratedProjects(id);
    }

    @PostMapping("collaboratedProject/add/{id}")
    public void addCollaboratedProject(@PathVariable String id, @RequestBody String projectId){
        taskService.addCollaboratedProject(id, projectId);
    }
    @PostMapping("remark/add/{id}")
    public void addRemark(@PathVariable String id , @RequestBody Remark remark){
        remark.setTime(LocalDateTime.now());
        System.out.println(remark.getSenderId());
        System.out.println(id);
        taskService.addRemark(id ,remark);
    }



    @GetMapping("getAllRemarks/{id}")
    public List<Remark> getAllRemarks(@PathVariable String id){
        List<Remark> remarks = taskService.getAllremarks(id);
        return remarks;
    }



    @PostMapping("collaboratedProject/remove/{id}")
    public void removeCollaboratedProject(@PathVariable String id, @RequestBody String projectId){
        taskService.removeCollaboratedProject(id, projectId);
    }

    @GetMapping("TaskCount/{type}")
    public long getAllTaskCount(@PathVariable String type){
        return taskService.getAllTaskCountByType(type);
    }

    @GetMapping("TodoCount/{parentId}/{status}")
    public long getCountTodo(@PathVariable String parentId, @PathVariable String status){
        return taskService.getTaskCountByParentIdAndStatus(parentId, status);
    }

    // Paginated endpoints
    @GetMapping("paginated/{type}")
    public ResponseEntity<PagedResponse<Tasks>> getTasksPaginated(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Tasks> tasksPage = taskService.getAllTasksByTypePaginated(type, page, size);
            return new ResponseEntity<>(new PagedResponse<>(tasksPage), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("paginated/owner/{ownerId}")
    public ResponseEntity<PagedResponse<Tasks>> getTasksByOwnerPaginated(
            @PathVariable String ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Tasks> tasksPage = taskService.getTaskByOwnerPaginated(ownerId, page, size);
            return new ResponseEntity<>(new PagedResponse<>(tasksPage), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("check-overdue")
    public ResponseEntity<String> checkOverdueTasks() {
        try {
            overdueSchedulerService.markOverdueTasks();
            return new ResponseEntity<>("Overdue check completed", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to check overdue tasks", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
