package taskmanager.team;

import java.time.LocalDate;

public class Task {
    // Task properties
    private int id;
    private String text;
    private boolean completed;
    private LocalDate dueDate;
    private Priority priority; // Updated to use Priority enum
    private String category;

    // Constructor
    public Task(int id, String text, boolean completed, LocalDate due, Priority priority, String category) { // Updated to take Priority enum
        this.id = id;
        this.text = text;
        this.completed = completed;
        this.dueDate = due;
        this.priority = priority; // Store the enum directly
        this.category = category;
    }

    // Getters and Setters...
    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getCategory() {
        return category;
    }
}
