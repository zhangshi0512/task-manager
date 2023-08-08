package taskmanager.team;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Helper class that provides static methods for Task objects.
 * It includes methods for parsing a Task object from a CSV line, formatting a Task object into a CSV line,
 * and generating a random task ID.
 */
class TaskHelper {
    private static Set<Integer> existingIds = new HashSet<>();
    private static final int MAX_ID = 1000;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/dd/MM");

    /**
     * Parse a Task object from a CSV line.
     *
     * @param csvLine A string representing a task in CSV format.
     * @return The Task object corresponding to the given CSV line.
     *         If the CSV line is improperly formatted, some fields in the returned Task may have default values.
     */
    public static Task parseTask(String csvLine) {
        String[] parts = csvLine.split(",", -1); // -1 to keep trailing empty strings
        if (parts.length < 6) {
            parts = Arrays.copyOf(parts, 6); // expand the array to the expected size
            for (int i = 0; i < parts.length; i++) {
                if (parts[i] == null) parts[i] = ""; // fill null elements with empty strings
            }
        }
        int id = Integer.parseInt(parts[0]);
        String text = parts[1].replace("�", ",");
        boolean completed = Boolean.parseBoolean(parts[2]);
        LocalDate dueDate = null;
        try {
            dueDate = parts[3].isEmpty() ? null : LocalDate.parse(parts[3], formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format for task id: " + id);
        }
        Priority priority = Priority.LOW; // default priority
        try {
            priority = Priority.fromString(parts[4]);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid priority format for task id: " + id);
        }
        String category = parts[5];
        return new Task(id, text, completed, dueDate, priority, category);
    }

    /**
     * Formats a Task object into a CSV line.
     *
     * @param task The Task object to format.
     * @return A string representing the Task in CSV format.
     */
    public static String formatTask(Task task) {
        String dueDate = task.getDueDate() != null ? task.getDueDate().format(formatter) : "";
        return String.join(",", 
            Integer.toString(task.getId()), 
            task.getText(), 
            Boolean.toString(task.isCompleted()), 
            dueDate, 
            task.getPriority().toString(), 
            task.getCategory());
    }

    /**
     * Generate a unique ID for a Task.
     *
     * @return A unique random integer between 0 and MAX_ID inclusive. If all IDs are exhausted, returns -1.
     */
    public static int generateId() {
        if (existingIds.size() == MAX_ID + 1) {  // If all possible IDs have been generated
            return -1;  // Indicate failure to generate a unique ID
        }

        Random random = new Random();
        int newId = random.nextInt(MAX_ID + 1);

        // While the generated ID is not unique, generate another one
        while (existingIds.contains(newId)) {
            newId = random.nextInt(MAX_ID + 1);
        }

        existingIds.add(newId);  // Remember this ID was generated
        return newId;
        
    }
}