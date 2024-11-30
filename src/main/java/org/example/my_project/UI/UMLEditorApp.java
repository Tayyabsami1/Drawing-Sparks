package org.example.my_project.UI;// Main class for launching the JavaFX application

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.stage.Stage;

import java.util.*;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.StageStyle;
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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class UMLEditorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Entry point for the JavaFX application
        ProjectController controller = new ProjectController();
        primaryStage.setTitle("UML Editor");
        controller.init(primaryStage); // Initialize the main controller
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class ProjectController extends Application {
    private Project project;
    private Canvas canvas;
    private GraphicsContext gc;

    private Shape selectedShape; // Keeps track of the selected shape
    private double dragStartX, dragStartY; // Tracks initial mouse drag positions
    private Diagrams Object = new Diagrams();
    private ModelExplorer Model =new ModelExplorer();

    private CodeGenerator generator =new CodeGenerator();

    public ProjectController() {
        project = new Project("Untitled", "./");

    }

    @Override
    public void start(Stage primaryStage) {
        init(primaryStage);
    }

    public void init(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Top toolbar
        ToolBar toolBar = new ToolBar();
        Button saveButton = new Button("Save");
        Button exportButton = new Button("Export to PNG");
        Button generateCodeButton = new Button("Generate Code");
        MenuButton loadButton = new MenuButton("Load");

        exportButton.setOnAction(e -> exportToPNG());
        generateCodeButton.setOnAction(e -> generateCode());
        // Save Button Logic
        saveButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("projectName");
            dialog.setTitle("Save Project");
            dialog.setHeaderText("Enter Project Name");
            dialog.setContentText("Project Name:");

            dialog.showAndWait().ifPresent(projectName -> {
                try {
                    ProjectManager.saveProject(projectName, Object);
                    updateLoadButton(loadButton); // Refresh load button items
                } catch (IOException ex) {
                    showErrorDialog("Error Saving Project: " , ex.getMessage());
                }
            });
        });

        updateLoadButton(loadButton);
        toolBar.getItems().addAll(saveButton, exportButton, generateCodeButton, loadButton);
        root.setTop(toolBar);

        // Left toolbox
        VBox toolbox = new VBox();
        Label toolboxLabel = new Label("TOOLBOX");
        TreeView<String> toolTree = new TreeView<>();
        TreeItem<String> rootItem = new TreeItem<>("Diagrams");
        rootItem.setExpanded(true);

        // Class Diagram tools
        TreeItem<String> classDiagramItem = new TreeItem<>("Class Diagram");
        classDiagramItem.getChildren().addAll(
                new TreeItem<>("Class"),
                new TreeItem<>("Interface"));

        // Package Diagram tools
        TreeItem<String> useCaseDiagramItem = new TreeItem<>("Use Case Diagram");
        useCaseDiagramItem.getChildren().addAll(
                new TreeItem<>("Actor"),
                new TreeItem<>("Use Case"));

        rootItem.getChildren().addAll(classDiagramItem, useCaseDiagramItem);
        toolTree.setRoot(rootItem);

        // Add event listener to draw shapes
        toolTree.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = toolTree.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.isLeaf()) {
                addShapeToCanvas(selectedItem.getValue());
            }
        });

        toolbox.getChildren().addAll(toolboxLabel, toolTree);
        root.setLeft(toolbox);

        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

// Handle mouse interactions
        canvas.setOnMouseClicked(event -> {
            handleCanvasClick(event.getX(), event.getY(), event.getButton());
        });

        canvas.setOnMousePressed(event -> {
            selectedShape = Object.findShapeAt(event.getX(), event.getY());
            if (selectedShape != null) {
                dragStartX = event.getX();
                dragStartY = event.getY();
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (selectedShape != null) {
                double deltaX = event.getX() - dragStartX;
                double deltaY = event.getY() - dragStartY;

                selectedShape.move(deltaX, deltaY);
                dragStartX = event.getX();
                dragStartY = event.getY();
                redrawCanvas();
            }
        });
        root.setCenter(canvas);

        Model.modelExplorer.setRoot(new TreeItem<>("Model Explorer"));
        root.setRight(Model.modelExplorer);

        // Set up and show scene
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("UML Editor");
        primaryStage.show();
    }


    private void addShapeToCanvas(String shape) {
        double x = 100, y = 100; // Default position for now
        switch (shape) {
            case "Class":
                drawClassShape(x, y,"Class");
                break;
            case "Interface":
                drawClassShape(x, y,"Interface");
                break;
            case "Actor":
                drawUserShape(x, y);
                break;
            case "Use Case":
                drawUseCaseShape(x, y);
                break;
            default:
                System.out.println("Unhandled shape: " + shape);
        }

    }

    // Draw Class Shape
    private void drawClassShape(double x, double y,String n) {
        String name;
        if(n.equals("Class"))
        {
            name=n+ Integer.toString(ClassShape.count+1);
        }
        else {
            name=n+ Integer.toString(ClassShape.count1+1);
        }
        ClassShape classShape = new ClassShape(x, y, name);
        Object.shapes.add(classShape);
        classShape.draw(gc);
        Model.updateModelExplorer(classShape.getName());
    }
    private void drawUserShape(double x, double y) {
        UserShape user = new UserShape(x, y, "User "+ Integer.toString(UserShape.count+1));
        Object.shapes.add(user);
        user.draw(gc);
        Model.updateModelExplorer(user.getName());
    }

    private void drawUseCaseShape(double x, double y) {
        String name = "UseCase" + Integer.toString(UseCaseShape.count+1);
        UseCaseShape useCase = new UseCaseShape(x, y, name);
        Object.shapes.add(useCase);
        useCase.draw(gc);
        Model.updateModelExplorer(useCase.getName());
    }


    private void handleCanvasClick(double x, double y, MouseButton button) {
        if (button == MouseButton.SECONDARY) {
            Shape shape = Object.findShapeAt(x, y);
            ContextMenu contextMenu = new ContextMenu();

            if (shape instanceof ClassShape) {
                ClassShape classShape = (ClassShape) shape;

                MenuItem addAttribute = new MenuItem("Add Attribute");
                MenuItem addMethod = new MenuItem("Add Method");
                MenuItem rename = new MenuItem("Rename");
                MenuItem delete = new MenuItem("Delete");
                MenuItem connectAssociation = new MenuItem("Connect Association");
                MenuItem connectAggregation = new MenuItem("Connect Aggregation");
                MenuItem connectComposition = new MenuItem("Connect Composition");
                MenuItem connectGeneralization = new MenuItem("Connect Generalization");
                MenuItem connectDirectAssociation = new MenuItem("Connect Direct Association");
                MenuItem connectDependency = new MenuItem("Connect Dependency");

                addAttribute.setOnAction(e -> addAttributeToShape(x, y));
                addMethod.setOnAction(e -> addMethodToShape(x, y));
                rename.setOnAction(e -> renameShape(x, y));
                delete.setOnAction(e -> deleteShape(x, y));
                connectAssociation.setOnAction(e -> startAssociationConnection(shape));
                connectAggregation.setOnAction(e -> startAggregationConnection(classShape));
                connectComposition.setOnAction(e -> startCompositionConnection(classShape));
                connectGeneralization.setOnAction(e -> startGeneralizationConnection(classShape));
                connectDirectAssociation.setOnAction(e -> startDirectAssociationConnection(classShape));
                connectDependency.setOnAction(e -> startDependencyConnection(shape,""));

                contextMenu.getItems().addAll(addAttribute, addMethod, rename, delete, connectAssociation, connectAggregation, connectComposition, connectGeneralization, connectDirectAssociation, connectDependency);
            }
            else if (shape instanceof UserShape) {
                MenuItem rename = new MenuItem("Rename");
                MenuItem delete = new MenuItem("Delete");
                MenuItem addAssociation = new MenuItem("Add Association");

                rename.setOnAction(e -> renameShape(x, y));
                delete.setOnAction(e -> deleteShape(x, y));
                addAssociation.setOnAction(e -> startAssociationConnection(shape));

                contextMenu.getItems().addAll(rename, delete, addAssociation);
            }
            else if (shape instanceof UseCaseShape) {
                MenuItem rename = new MenuItem("Rename");
                MenuItem delete = new MenuItem("Delete");
                MenuItem addAssociation = new MenuItem("Add Association");
                MenuItem addDependency = new MenuItem("Add Include Dependency");
                MenuItem addDependency1 = new MenuItem("Add Extend Dependency");

                rename.setOnAction(e -> renameShape(x, y));
                delete.setOnAction(e -> deleteShape(x, y));
                addAssociation.setOnAction(e -> startAssociationConnection(shape));
                addDependency.setOnAction(e -> startDependencyConnection(shape,"Include"));
                addDependency1.setOnAction(e -> startDependencyConnection(shape,"Extend"));

                contextMenu.getItems().addAll(rename, delete, addAssociation, addDependency,addDependency1);
            }


            contextMenu.show(canvas, x, y);
        }
    }

    private void startAssociationConnection(Shape startShape) {
        canvas.setOnMouseClicked(event -> {
            Shape endShape = Object.findShapeAt(event.getX(), event.getY());
            if (endShape != null && endShape != startShape) {
                // Create and store the new association shape
                AssociationShape association = new AssociationShape(startShape, endShape);
                Object.shapes.add(association); // Add the new association to the shapes list
                redrawCanvas(); // Redraw canvas to reflect changes

                // Update Model Explorer
                String associationName = "- Association (" + startShape.getName() + " to " + endShape.getName() + ")";
                Model.addConnectionToModelExplorer(startShape.getName(), associationName);
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }

    private void startAggregationConnection(ClassShape startClass) {
        canvas.setOnMouseClicked(event -> {
            ClassShape endClass = (ClassShape) Object.findShapeAt(event.getX(), event.getY());
            if (endClass != null && endClass != startClass) {
                AggregationShape aggregation = new AggregationShape(startClass, endClass);
                Object.shapes.add(aggregation); // Add the new aggregation to the shapes list
                redrawCanvas();
                // Update Model Explorer
                String connectionName = "- Aggregation (" + startClass.getName() + " to " + endClass.getName() + ")";
                Model.addConnectionToModelExplorer(startClass.getName(), connectionName);
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }
    private void startCompositionConnection(ClassShape startClass) {
        canvas.setOnMouseClicked(event -> {
            ClassShape endClass = (ClassShape) Object.findShapeAt(event.getX(), event.getY());
            if (endClass != null && endClass != startClass) {
                CompositionShape composition = new CompositionShape(startClass, endClass);
                Object.shapes.add(composition); // Add the new composition to the shapes list
                redrawCanvas();
                String connectionName = "- Composition (" + startClass.getName() + " to " + endClass.getName() + ")";
                Model.addConnectionToModelExplorer(startClass.getName(), connectionName);
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }
    private void startGeneralizationConnection(ClassShape startClass) {
        canvas.setOnMouseClicked(event -> {
            ClassShape endClass = (ClassShape) Object.findShapeAt(event.getX(), event.getY());
            if (endClass != null && endClass != startClass) {
                GeneralizationShape generalization = new GeneralizationShape(startClass, endClass);
                Object.shapes.add(generalization); // Add the new generalization to the shapes list
                redrawCanvas();

                String connectionName = "- Generalization (" + startClass.getName() + " to " + endClass.getName() + ")";
                Model.addConnectionToModelExplorer(startClass.getName(), connectionName);
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }
    private void startDirectAssociationConnection(ClassShape startClass) {
        canvas.setOnMouseClicked(event -> {
            ClassShape endClass = (ClassShape) Object.findShapeAt(event.getX(), event.getY());
            if (endClass != null && endClass != startClass) {
                DirectAssociationLineShape directAssociation = new DirectAssociationLineShape(startClass, endClass);
                Object.shapes.add(directAssociation); // Add the new direct association to the shapes list
                redrawCanvas();

                String connectionName = "- DirectAssociation (" + startClass.getName() + " to " + endClass.getName() + ")";
                Model.addConnectionToModelExplorer(startClass.getName(), connectionName);
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }
    private void startDependencyConnection(Shape startShape, String dependencyType) {
        canvas.setOnMouseClicked(event -> {
            Shape endShape = Object.findShapeAt(event.getX(), event.getY());
            if (endShape != null && endShape != startShape) {
                // Pass the dependency type ("Include" or "Extend") when creating the DependencyLineShape
                if ((startShape instanceof ClassShape && endShape instanceof ClassShape) ||
                        (startShape instanceof UseCaseShape && endShape instanceof UseCaseShape)) {
                    DependencyLineShape dependency = new DependencyLineShape(startShape, endShape, dependencyType);
                    Object.shapes.add(dependency); // Add the new dependency to the shapes list
                    redrawCanvas();

                    String connectionName = "- Dependency (" + startShape.getName() + " to " + endShape.getName() + ")";
                    Model.addConnectionToModelExplorer(startShape.getName(), connectionName);
                }
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }



    private void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear canvas
        for (Shape shape : Object.shapes) {
            System.out.println(shape.getName());
            gc.setLineDashes(null); // Reset line dash to solid for regular shapes
            shape.draw(gc);         // Draw each shape
        }
    }

    private void addAttributeToShape(double x, double y) {
        ClassShape shape = (ClassShape) Object.findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Attribute");
            dialog.setHeaderText("Enter Attribute Name:");
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String attribute = result.get();
                // Add attribute to the shape
                Object.addAttributeToShape(shape,attribute);
                // Update the diagram
                redrawCanvas();

                // Add to the Model Explorer
                TreeItem<String> classItem = Model.getModelExplorerItemAt(x, y);
                if (classItem != null) {
                    classItem.getChildren().add(new TreeItem<>("+ " + attribute));
                }
            }
        }
    }

    private void addMethodToShape(double x, double y) {
        ClassShape shape = (ClassShape) Object.findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Method");
            dialog.setHeaderText("Enter Method Name (e.g., methodName()):");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String method = result.get();
                if (!method.endsWith("()")) {
                    showErrorDialog("Invalid Method Format", "Method name must end with '()'.");
                    return;
                }
                Object.addMethodToShape(shape,method);
                // Update the diagram
                redrawCanvas();

                // Add to the Model Explorer
                TreeItem<String> classItem = Model.getModelExplorerItemAt(x, y);
                if (classItem != null) {
                    classItem.getChildren().add(new TreeItem<>("+ " + method));
                }
            }
        }
    }

    private void renameShape(double x, double y) {
        Shape shape = Object.findShapeAt(x, y);
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog(shape.getName());
            dialog.setTitle("Rename Shape");
            dialog.setHeaderText("Enter New Name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String newName = result.get();
                Model.updateModelExplorerItem(shape, newName);  // Update the model explorer
                Object.renameShape(shape, newName);
                redrawCanvas();
            }
        }
    }


    private void deleteShape(double x, double y) {
        Model.removeModelExplorerItem(Object.deleteShape(x,y));
            // Redraw the canvas
            redrawCanvas();
        }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Update the load button with current project list
    private void updateLoadButton(MenuButton loadButton) {
        loadButton.getItems().clear(); // Clear existing items

        for (String projectName : ProjectManager.getAllProjectNames()) {
            MenuItem item = new MenuItem(projectName);
            item.setOnAction(e -> {
                try {
                    Object = ProjectManager.loadProject(projectName);
                    redrawCanvas();
                } catch (IOException | ClassNotFoundException ex) {
                    showErrorDialog("Error Loading Project: " , ex.getMessage());
                }
            });
            loadButton.getItems().add(item);
        }
    }


    public void generateCode() {
        showGeneratedCodeDialog(generator.generateCode());

    }
    private void showGeneratedCodeDialog(String code) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("code-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Generated Code");
            stage.initStyle(StageStyle.UNDECORATED);
            CodeViewController controller = fxmlLoader.getController();
            controller.setCode(code);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading FXML: " + e.getMessage());
        }
    }

    private void exportToPNG() {
      project.exportToPNG(canvas);
    }
}
