package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.entity.Activity;
import com.chitalebandhu.chitalebandhu.entity.Tasks;
import com.chitalebandhu.chitalebandhu.services.ActivityService;
import com.chitalebandhu.chitalebandhu.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("activity")
public class ActivityController {
    private final ActivityService activityService;
    private final TaskService taskService;

    public ActivityController(ActivityService activityService, TaskService taskService){
        this.activityService = activityService;
        this.taskService = taskService;
    }

    @GetMapping("{ownerId}/{visibility}")
    public List<Activity> getActivities(@PathVariable String ownerId, @PathVariable String visibility){
        List <Tasks> tasks = taskService.getTaskByOwner(ownerId);
        List<String> taskId = tasks
                .stream()
                .map(Tasks::getId)
                .toList();

        return activityService.getActivities(taskId, visibility);
    }

    @GetMapping("adminActivities")
    public List<Activity> getAdminActivities(){
        return activityService.getAdminActivities("PROJECT");
    }

    @GetMapping("activities")
    public List<Activity> getAllActivities(){
        return activityService.getAllActivities();
    }

    @PostMapping("add")
    public void addActivity(@RequestBody Activity newActivity){
        activityService.addActivity(newActivity);
    }

    @DeleteMapping("delete")
    public void deleteActivity(@RequestBody String id){
        activityService.removeActivity(id);
    }

    @PutMapping("update/{activityId}")
    public void updateActivity(@PathVariable String activityId, @RequestBody Activity newActivity){
        activityService.updateActivity(activityId, newActivity);
    }
}
