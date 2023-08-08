package taskmanager.team;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController {
    @FXML
    private TextField filePathField;
    @FXML
    private Button chooseButton;
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private ComboBox<String> filterSortComboBox;
    @FXML
    private TextField newTaskField;
    @FXML
    private ComboBox<String> completeComboBox;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private ComboBox<String> priorityComboBox;
    @FXML
    private TextField categoryField;
    @FXML
    private Button addButton;
    @FXML
    private ComboBox<String> completeFilterSortComboBox;
    @FXML
    private DatePicker dueDateFilterSortPicker;
    @FXML
    private ComboBox<String> priorityFilterSortComboBox;
    @FXML
    private Button filterSortButton;
    @FXML
    private Button clearDueDateButton;



    private TaskManager taskManager;
    private ObservableList<Task> taskData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        addButton.setOnAction(event -> {
            int id = TaskHelper.generateId();
            String taskText = newTaskField.getText();
            // convert "Yes"/"No" to boolean
            boolean completed = completeComboBox.getValue().equalsIgnoreCase("Yes");
            LocalDate dueDate = dueDatePicker.getValue();
            // convert string to Priority enum
            Priority priority = Priority.valueOf(priorityComboBox.getValue().toUpperCase());
            String category = categoryField.getText();
            Task task = new Task(id, taskText, completed, dueDate, priority, category);
            taskManager.addTask(task);
            taskData.add(task);
            newTaskField.clear();
            completeComboBox.setValue(null);
            dueDatePicker.setValue(null);
            priorityComboBox.setValue(null);
            categoryField.clear();
        });

        chooseButton.setOnAction(event -> chooseFile());

        filterSortButton.setOnAction(event -> filterSortTasks());
    
        filterSortComboBox.setItems(FXCollections.observableArrayList("None", "Filter by Complete", "Filter by Due Date", "Filter by Priority", "Sort by Complete", "Sort by Due Date", "Sort by Priority"));
    
        // Add "None" option to Filter/Sort ComboBoxes
        completeFilterSortComboBox.getItems().add(0, "None");
        priorityFilterSortComboBox.getItems().add(0, "None");
    
        taskTable.setItems(taskData);
        setUpTableColumns();
    
        // Initialize Filter/Sort ComboBoxes and DatePicker
        completeFilterSortComboBox.setItems(FXCollections.observableArrayList("None", "Yes", "No"));
        priorityFilterSortComboBox.setItems(FXCollections.observableArrayList("None", "HIGH", "MEDIUM", "LOW"));
    
        completeComboBox.setItems(FXCollections.observableArrayList("Yes", "No"));
        priorityComboBox.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));

        clearDueDateButton.setOnAction(event -> {
            dueDateFilterSortPicker.setValue(null);
        });
       
    }


    private void setUpTableColumns() {
        List<TableColumn<Task, ?>> columns = taskTable.getColumns();
        String[] propertyNames = {"id", "text", "completed", "dueDate", "priority", "category"};
        for (int i = 0; i < columns.size(); i++) {
            TableColumn<Task, ?> column = columns.get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(propertyNames[i]));
        }
    }

    @FXML
    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
            loadTasks(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    @FXML
    private void loadTasks(String filePath) {
        try {
            InputStream in = new FileInputStream(filePath);
            this.taskManager = new TaskManager(in, filePath);
            List<Task> tasks = taskManager.getTasks();
            taskData.addAll(tasks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterSortTasks() {
        String filterSortBy = filterSortComboBox.getSelectionModel().getSelectedItem();
    
        String[] split = filterSortBy.split(" ", 3);
        String filterOrSortBy = split[0];
        String criterion = split.length == 3 ? split[2] : ""; // Handle cases where there's no criterion
    
        String completeValue = completeFilterSortComboBox.getSelectionModel().getSelectedItem();
        LocalDate dueDateValue = dueDateFilterSortPicker.getValue();
        String priorityValue = priorityFilterSortComboBox.getSelectionModel().getSelectedItem();
    
        List<Task> updatedTasks = new ArrayList<>();
    
        if (filterSortBy.equals("None")) {
            // If "None" is selected, add all tasks to the updatedTasks list
            updatedTasks.addAll(taskManager.getTasks());
        } 
        if (filterSortBy.contains("Filter")) {
            updatedTasks = taskManager.filterTasks(criterion, completeValue, dueDateValue, priorityValue);
        }
        if (filterSortBy.contains("Sort")) {
            System.out.println("Sorting by: " + criterion); // Debug print statement
            updatedTasks = taskManager.sortTasks(criterion, completeValue, dueDateValue, priorityValue);
        }        
    
        taskData.clear();
        taskData.addAll(updatedTasks);
    }
    
}
