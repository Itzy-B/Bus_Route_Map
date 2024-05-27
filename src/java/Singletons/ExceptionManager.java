package src.java.Singletons;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ExceptionManager {
        public static void showError (String title, String headerText, String content) {
                Platform.runLater(() -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Trip ID");
                alert.setHeaderText("Error:");
                alert.setContentText("No trip-id was found, just walk");
                alert.showAndWait();
            });
        }
    }
