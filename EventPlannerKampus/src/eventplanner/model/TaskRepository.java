package eventplanner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic repository for managing Tasks in memory using collections.
 * Uses a HashMap for fast lookups.
 * 
 * @param <T> a type that extends Task
 */
public class TaskRepository<T extends Task> {
    private final Map<String, T> taskMap;

    public TaskRepository() {
        this.taskMap = new HashMap<>();
    }

    /**
     * Create/Add a new task to the repository.
     * Uses the task's ID as the key.
     * 
     * @param task the task to add
     */
    public void addTask(T task) {
        if (task != null && task.getTaskId() != null) {
            taskMap.put(task.getTaskId(), task);
        }
    }

    /**
     * Read/Get a task by its unique ID.
     * 
     * @param taskId the ID of the task
     * @return the Task object, or null if not found
     */
    public T getTaskById(String taskId) {
        return taskMap.get(taskId);
    }

    /**
     * Read/Get all tasks currently stored in the repository.
     * 
     * @return a List of all tasks
     */
    public List<T> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    /**
     * Update an existing task in the repository.
     * 
     * @param task the updated task
     * @return true if updated successfully, false if task was not found
     */
    public boolean updateTask(T task) {
        if (task != null && task.getTaskId() != null && taskMap.containsKey(task.getTaskId())) {
            taskMap.put(task.getTaskId(), task);
            return true;
        }
        return false;
    }

    /**
     * Delete a task from the repository by its ID.
     * 
     * @param taskId the ID of the task to delete
     * @return the removed task, or null if not found
     */
    public T deleteTask(String taskId) {
        return taskMap.remove(taskId);
    }
}
