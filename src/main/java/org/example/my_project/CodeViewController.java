package org.example.my_project;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;


public class CodeViewController {
        @FXML
        private TextArea codeArea;

        // Method to set the code in the TextArea
        public void setCode(String code) {
            codeArea.setText(code);
            codeArea.setEditable(false);
        }

}
