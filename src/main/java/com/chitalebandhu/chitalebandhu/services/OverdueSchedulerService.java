package com.chitalebandhu.chitalebandhu.services;

import com.chitalebandhu.chitalebandhu.entity.Tasks;
import com.chitalebandhu.chitalebandhu.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class OverdueSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(OverdueSchedulerService.class);
    private static final List<String> EXCLUDED_STATUSES = Arrays.asList("DONE", "COMPLETED", "OVERDUE");

    private final TaskRepository taskRepository;

    public OverdueSchedulerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Runs every day at midnight to mark overdue tasks/projects.
     * Also runs on application startup.
     */
    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    public void markOverdueTasks() {
        logger.info("Running overdue task scheduler...");

        LocalDate today = LocalDate.now();
        List<Tasks> overdueTasks = taskRepository.findByDeadLineBeforeAndStatusNotIn(today, EXCLUDED_STATUSES);

        if (overdueTasks.isEmpty()) {
            logger.info("No overdue tasks found.");
            return;
        }

        int updatedCount = 0;
        for (Tasks task : overdueTasks) {
            task.setStatus("OVERDUE");
            taskRepository.save(task);
            updatedCount++;
            logger.debug("Marked task/project '{}' (ID: {}) as OVERDUE", task.getTitle(), task.getId());
        }

        logger.info("Marked {} tasks/projects as OVERDUE", updatedCount);
    }

    /**
     * Runs on application startup to catch any overdue items immediately.
     */
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void markOverdueTasksOnStartup() {
        logger.info("Checking for overdue tasks on startup...");
        markOverdueTasks();
    }
}
