package com.chitalebandhu.chitalebandhu.services;

import com.chitalebandhu.chitalebandhu.entity.Activity;
import com.chitalebandhu.chitalebandhu.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository){
        this.activityRepository = activityRepository;
    }

    public void addActivity(Activity newActivity){
        activityRepository.save(newActivity);
    }

    public List<Activity> getAllActivities(){
        return activityRepository.findAllByOrderByTimeDesc();
    }

    public List<Activity> getAdminActivities(String visibility){
        return activityRepository.findByVisibilityOrderByTimeDesc(visibility);
    }

    public void removeActivity(String id){
        activityRepository.deleteById(id);
    }

    public Activity updateActivity(String id, Activity newActivity){
        Optional <Activity> existingActivity = activityRepository.findById(id);
        if(existingActivity.isEmpty()){
            return null;
        }
        if(newActivity.getUserName() != null && !newActivity.getUserName().trim().isEmpty()){
            existingActivity.get().setUserName(newActivity.getUserName());
        }
        if(newActivity.getProjectName() != null && !newActivity.getProjectName().trim().isEmpty()){
            existingActivity.get().setProjectName(newActivity.getProjectName());
        }
        if(newActivity.getVerb() != null && !newActivity.getVerb().trim().isEmpty()){
            existingActivity.get().setVerb(newActivity.getVerb());
        }
        if(newActivity.getTime() != null){
            existingActivity.get().setTime();
        }

        return activityRepository.save(existingActivity.get());
    }

    public List<Activity> getActivities(List<String> TaskId, String visibility){
        return activityRepository.findByProjectIdInAndVisibilityOrderByTimeDesc(TaskId, visibility);
    }
}
