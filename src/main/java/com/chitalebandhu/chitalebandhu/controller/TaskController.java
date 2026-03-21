package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.DTOs.PagedResponse;
import com.chitalebandhu.chitalebandhu.entity.Tasks;
import com.chitalebandhu.chitalebandhu.services.TaskService;
import com.chitalebandhu.chitalebandhu.services.OverdueSchedulerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("member/{ownerId}")
    public ResponseEntity<List<Tasks>> getTaskByOwner(@PathVariable String ownerId){
        try{
            List<Tasks> tasks = taskService.getTaskByOwner(ownerId);
            return new ResponseEntity<>(tasks , HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("id/{id}/updateProgress")
    public void updateProgress(@PathVariable String id, @RequestBody short progress){
        taskService.updateProgress(id, progress);
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Tasks> getTaskById(@PathVariable String id){
        try{
            Tasks task = taskService.getTaskById(id);
            return new ResponseEntity<>(task , HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND );
        }
    }

    @PutMapping("update/{Id}")
    public ResponseEntity<Tasks> updateTask(@PathVariable String Id, @RequestBody Tasks newTask){
        try {
            Tasks task = taskService.updateTaskById(Id, newTask);
            if(task != null) return new ResponseEntity<>(task , HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("delete/{Id}")
    public void deleteTask(@PathVariable String Id){
        taskService.deleteTaskById(Id);
    }

    @PutMapping("{id}/status/update/{status}")
    public ResponseEntity<Void> updateStatus(@PathVariable String id, @PathVariable String status){
        try {
            taskService.updateStatusById(id, status);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}/status/transition/{status}")
    public ResponseEntity<Tasks> transitionStatus(
            @PathVariable String id,
            @PathVariable String status,
            @RequestParam String actorId,
            @RequestParam(defaultValue = "USER") String actorRole
    ) {
        try {
            Tasks updated = taskService.transitionTaskStatusWithReview(id, status, actorId, actorRole);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("TaskCount/{type}")
    public long getAllTaskCount(@PathVariable String type){
        return taskService.getAllTaskCountByType(type);
    }

    @GetMapping("TodoCount/{parentTaskId}/{status}")
    public long getCountTodo(@PathVariable String parentTaskId, @PathVariable String status){
        return taskService.getTaskCountByParentTaskIdAndStatus(parentTaskId, status);
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
