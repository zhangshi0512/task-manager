module taskmanager.team {
    requires javafx.controls;
    requires javafx.fxml;

    opens taskmanager.team to javafx.fxml, javafx.base, javafx.graphics;
    exports taskmanager.team;
}
