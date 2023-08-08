package taskmanager.team;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TaskManager {
    private List<Task> tasks;
    private String filePath;  // Add this instance variable

    public TaskManager(InputStream in, String filePath) {
        this.filePath = filePath;
        tasks = new ArrayList<>();
        loadTasksFromStream(in);
    }

    void loadTasksFromStream(InputStream in) {
        try (Scanner scanner = new Scanner(new InputStreamReader(in))) {
            // Ignore the first line
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            // Read the rest of the lines
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Task task = TaskHelper.parseTask(line);
                tasks.add(task);
            }
        }
    }
    
    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        updateCSV();
    }

    public void completeTask(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setCompleted(true);
            }
        }
        updateCSV();
    }

    public void displayTasks(String filterCategory, boolean filterIncomplete, String sortBy) {
        tasks.stream()
             .filter(task -> filterCategory == null || task.getCategory().equals(filterCategory))
             .filter(task -> !filterIncomplete || !task.isCompleted())
             .sorted(getComparator(sortBy))
             .forEach(task -> System.out.println(TaskHelper.formatTask(task)));
    }

    private Comparator<Task> getComparator(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "complete":
                return Comparator.comparing(Task::isCompleted);
            case "due":
                return Comparator.comparing(Task::getDueDate);
            case "priority":
                return Comparator.comparing(task -> task.getPriority().ordinal());
            default:
                return Comparator.comparing(Task::getId); // default sort by id
        }
    }

    public void updateCSV() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (Task task : tasks) {
                writer.write(TaskHelper.formatTask(task));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Task> filterTasks(String filterSortBy, String completeValue, LocalDate dueDateValue, String priorityValue) {
        Stream<Task> stream = tasks.stream();
    
        // No filtering by filterSortBy if it's an empty string
        if (!filterSortBy.isEmpty()) {
            switch (filterSortBy.toLowerCase()) {
                case "complete":
                    if (completeValue != null) {
                        Boolean boolValue = completeValue.equalsIgnoreCase("Yes") ? Boolean.TRUE : Boolean.FALSE;
                        stream = stream.filter(task -> task.isCompleted() == boolValue);
                    }
                    break;
                case "due date":
                    if (dueDateValue != null) {
                        stream = stream.filter(task -> task.getDueDate().isEqual(dueDateValue));
                    }
                    break;
                case "priority":
                    if (priorityValue != null) {
                        stream = stream.filter(task -> task.getPriority().name().equals(priorityValue));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown filtering criterion: " + filterSortBy);
            }
        }
    
        return stream.collect(Collectors.toList());
    }
    
    
    public List<Task> sortTasks(String filterSortBy, String completeValue, LocalDate dueDateValue, String priorityValue) {
        Comparator<Task> comparator;
    
        // Use default comparator if filterSortBy is an empty string
        if (filterSortBy.isEmpty()) {
            comparator = Comparator.comparing(Task::getId);
        } else {
            switch (filterSortBy.toLowerCase()) {
                case "complete":
                    comparator = Comparator.comparing(Task::isCompleted);
                    break;
                case "due date":
                    comparator = Comparator.comparing(Task::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()));
                    break;
                case "priority":
                    comparator = Comparator.comparing(task -> task.getPriority().ordinal());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sorting criterion: " + filterSortBy);
            }
        }
    
        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    
}
