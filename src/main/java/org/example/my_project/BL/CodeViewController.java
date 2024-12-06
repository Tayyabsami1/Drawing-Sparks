package org.example.my_project.BL;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The {@code CodeViewController} class manages the user interface interactions
 * related to displaying, copying, saving, and closing code files.
 * It includes functionalities for setting the code, copying it to the clipboard,
 * saving the code to a file, and closing the current window.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class CodeViewController {

    // The TextArea where the code is displayed
    @FXML
    private TextArea codeArea;

    // Buttons for copy, save, and close actions
    @FXML
    private Button copyButton;
    @FXML
    private Button closeButton;

    /**
     * Sets the code in the {@code TextArea} for viewing.
     *
     * @param code The Java code to be displayed in the {@code TextArea}.
     */
    public void setCode(String code) {
        codeArea.setText(code);
        codeArea.setEditable(false); // Makes the code view-only
    }

    /**
     * Handles the action of copying the code from the {@code TextArea} to the clipboard.
     * <p>Once copied, the button text is changed to "Copied!" for 2 seconds before reverting back.</p>
     *
     * @FXML
     */
    @FXML
    private void handleCopy() {
        String code = codeArea.getText(); // Get the code from the TextArea
        StringSelection selection = new StringSelection(code); // Prepare the code for copying
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null); // Copy to clipboard

        // Change button text to "Copied"
        String originalText = copyButton.getText();
        copyButton.setText("Copied!");

        // Revert the button text back after 2 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> copyButton.setText(originalText)));
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * Handles the action of saving the code displayed in the {@code TextArea} to a file.
     * <p>A {@code FileChooser} is opened to allow the user to select the file location and name.</p>
     *
     * @FXML
     */
    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Code as .java File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));

        // Suggest a default filename for the user
        fileChooser.setInitialFileName("MyProject.java");

        // Show the save dialog and get the selected file
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(codeArea.getText()); // Write the code to the selected file
            } catch (IOException e) {
                e.printStackTrace(); // Handle potential I/O errors
            }
        }
    }

    /**
     * Handles the action of closing the current window.
     * <p>The current stage (window) is closed.</p>
     *
     * @FXML
     */
    @FXML
    private void handleClose() {
        // Get the current stage and close it
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
