package com.chitalebandhu.chitalebandhu.repository;

import com.chitalebandhu.chitalebandhu.entity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findAllByOrderByTimeDesc();
    List<Activity> findByVisibilityOrderByTimeDesc(String visibility);
    void deleteByProjectId(String projectId);
    List<Activity> findByProjectIdInAndVisibilityOrderByTimeDesc(List<String> projectIds, String visibility);
}
