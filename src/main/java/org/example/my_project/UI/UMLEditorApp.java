package org.example.my_project.UI;// Main class for launching the JavaFX application

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.*;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.controlsfx.control.PropertySheet;
import org.example.my_project.BL.*;
import org.example.my_project.Models.ClassShape;
import org.example.my_project.Models.Shape;
import org.example.my_project.Models.UserShape;
import org.example.my_project.Models.UseCaseShape;
import org.example.my_project.Models.AggregationShape;
import org.example.my_project.Models.AssociationShape;
import org.example.my_project.Models.CompositionShape;
import org.example.my_project.Models.DependencyLineShape;
import org.example.my_project.Models.DirectAssociationLineShape;
import org.example.my_project.Models.GeneralizationShape;

import java.io.IOException;
/**
 * The main application class for the UML Editor.
 * This class serves as the entry point for the JavaFX application, initializing
 * the main window and setting up the primary stage.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class UMLEditorApp extends Application {

    /**
     * The entry point for the JavaFX application.
     * Initializes the primary stage and delegates the setup to the {@code ProjectController}.
     *
     * @param primaryStage The primary stage for this JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Entry point for the JavaFX application
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main-page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("main2.css").toExternalForm());

            primaryStage.setTitle("Drawing Sparks");
//            primaryStage.initStyle(StageStyle.UNDECORATED);
//            CodeViewController controller = fxmlLoader.getController();
//            controller.setCode(code);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e) {
            System.out.println("Error loading FXML: " + e.getMessage());
        }
//        ProjectController controller = new ProjectController();
//        primaryStage.setTitle("UML Editor");
//        controller.init(primaryStage,"ClassDiagram"); // Initialize the main controller
//        primaryStage.show();
    }
    /**
     * The main method that serves as the application entry point.
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
