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

public class CodeViewController {

    @FXML
    private TextArea codeArea;

    @FXML
    private Button copyButton;


    // Method to set the code in the TextArea
    public void setCode(String code) {
        codeArea.setText(code);
        codeArea.setEditable(false);
    }

    // Handle "Copy" button action
    @FXML
    private void handleCopy() {
        String code = codeArea.getText();
        StringSelection selection = new StringSelection(code);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        // Change button text to "Copied"
        String originalText = copyButton.getText();
        copyButton.setText("Copied!");

        // Revert text back after 2 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> copyButton.setText(originalText)));
        timeline.setCycleCount(1);
        timeline.play();
    }

    // Handle "Save" button action

    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Code as .java File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));

        // Suggest a default filename
        fileChooser.setInitialFileName("MyClass.java");

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(codeArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
