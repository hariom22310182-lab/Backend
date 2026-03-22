package com.chitalebandhu.chitalebandhu.repository;

import com.chitalebandhu.chitalebandhu.entity.Tasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Tasks, String> {
    Optional<List<Tasks>> findByOwnerId(String ownerId);
    List<Tasks> findByParentTaskId(String parentTaskId);

    long countByParentTaskIdAndStatus(String parentTaskId, String Status);
    long countByParentTaskId(String parentTaskId);
    long countByParentTaskIdAndStatusIn(String parentTaskId, List<String> statuses);

    long countByType(String type);

    long countByPriority(String priority);

    Optional <List<Tasks>> findByType(String Type);

    long countByOwnerIdAndType(String ownerId, String type);

    long countByOwnerIdAndStatus(String ownerId, String status);

    // Pagination methods
    Page<Tasks> findByType(String type, Pageable pageable);
    Page<Tasks> findByRootType(String type, Pageable pageable);
    Page<Tasks> findByOwnerId(String ownerId, Pageable pageable);
    Page<Tasks> findByParentTaskId(String parentTaskId, Pageable pageable);

    // Find tasks/projects that are overdue (deadline passed and not completed/already overdue)
    List<Tasks> findByDeadLineBeforeAndStatusNotIn(LocalDate date, List<String> excludedStatuses);
}
