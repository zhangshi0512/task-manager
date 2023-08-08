package taskmanager.team;

public enum Priority {
    HIGH,
    MEDIUM,
    LOW;

    public static Priority fromString(String priority) {
        if (priority == null || priority.isEmpty()) {
            return LOW;
        }
        switch (priority.toUpperCase()) {
            case "1":
            case "LOW":
                return LOW;
            case "2":
            case "MEDIUM":
                return MEDIUM;
            case "3":
            case "HIGH":
                return HIGH;
            default:
                return LOW;
        }
    }
    
}
