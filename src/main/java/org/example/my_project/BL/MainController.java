package org.example.my_project.BL;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.example.my_project.UI.ProjectController;

import java.io.File;
import java.util.Arrays;

public class MainController {
     private ProjectController projectController =new ProjectController();

    @FXML
    private Button classDiagramBtn;

    @FXML
    private Button useCaseDiagramBtn;
    @FXML
    private MenuButton openRecentMenu;

    @FXML
    private void openClassDiagram() {
        Stage stage = (Stage) classDiagramBtn.getScene().getWindow();
        projectController.init(stage,"ClassDiagram");

    }

    @FXML
    private void openUseCaseDiagram() {
        Stage stage = (Stage) useCaseDiagramBtn.getScene().getWindow();
        projectController.init(stage,"UseCaseDiagram");
    }
    @FXML
    private void initialize() {
        loadRecentProjects();
    }
    // Load recent projects into the "Open Recent" menu
    private void loadRecentProjects() {
        File projectsDir = new File("projects");

        // Check if the directory exists and is a directory
        if (projectsDir.exists() && projectsDir.isDirectory()) {
            // Get all files (or directories) in the projects folder with a .dat extension
            File[] projectFiles = projectsDir.listFiles((dir, name) -> name.endsWith(".dat")); // Filter for .dat files

            if (projectFiles != null) {
                Arrays.sort(projectFiles); // Sort the files (Optional: you can sort them based on time)

                // Add the files as MenuItems to the MenuButton
                for (File projectFile : projectFiles) {
                    MenuItem projectItem = new MenuItem(projectFile.getName());
                    projectItem.setOnAction(e -> openProject(projectFile));
                    openRecentMenu.getItems().add(projectItem);
                }
            }
        } else {
            showAlert("Error", "Projects folder not found.");
        }
    }
    private void openProject(File projectFile) {
        // Logic to open the selected project
        Stage stage = (Stage) openRecentMenu.getScene().getWindow();
        projectController.init(stage, projectFile.getName()); // Assuming the project name is sufficient to identify the project
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

