package org.example.my_project.UI;// Main class for launching the JavaFX application

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
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

 class UMLEditorApp extends Application {

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

public class ProjectController extends Application {
    private Project project;
    public Canvas canvas;
    private GraphicsContext gc;

    private Shape selectedShape; // Keeps track of the selected shape
    private double dragStartX, dragStartY; // Tracks initial mouse drag positions
    public Diagrams Object = new Diagrams();
    public ModelExplorer Model =new ModelExplorer();

    public CodeGenerator generator =new CodeGenerator();

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
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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
        rootItem.setExpanded(false);

// Class Diagram tools
        TreeItem<String> classDiagramItem = new TreeItem<>("Class Diagram");
        TreeItem<String> classToolItem = new TreeItem<>("Class");
        TreeItem<String> interfaceToolItem = new TreeItem<>("Interface");

// Use Case Diagram tools
        TreeItem<String> useCaseDiagramItem = new TreeItem<>("Use Case Diagram");
        TreeItem<String> actorToolItem = new TreeItem<>("Actor");
        TreeItem<String> useCaseToolItem = new TreeItem<>("Use Case");

// Add main diagram types to root
        rootItem.getChildren().addAll(classDiagramItem, useCaseDiagramItem);
        toolTree.setRoot(rootItem);

// Event listener for toolbox interaction
        toolTree.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = toolTree.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                if (selectedItem == classDiagramItem) {
                    // Display only Class Diagram tools
                    rootItem.getChildren().setAll(classDiagramItem);
                    classDiagramItem.getChildren().setAll(classToolItem, interfaceToolItem);
                } else if (selectedItem == useCaseDiagramItem) {
                    // Display only Use Case Diagram tools
                    rootItem.getChildren().setAll(useCaseDiagramItem);
                    useCaseDiagramItem.getChildren().setAll(actorToolItem, useCaseToolItem);
                } else if (selectedItem.isLeaf()) {
                    // Call the addShapeToCanvas function for leaf items
                    addShapeToCanvas(selectedItem.getValue());
                }
            }
        });

// Add components to the toolbox
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

        String img=getClass().getResource("logo.png").toString();
        Image logo=new Image(img);
        primaryStage.getIcons().add(logo);
        primaryStage.setTitle("Drawing Sparks");
        primaryStage.setMaximized(true);

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
    public void drawClassShape(double x, double y,String n) {
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
    public void drawUserShape(double x, double y) {
        UserShape user = new UserShape(x, y, "User "+ Integer.toString(UserShape.count+1));
        Object.shapes.add(user);
        user.draw(gc);
        Model.updateModelExplorer(user.getName());
    }

    public void drawUseCaseShape(double x, double y) {
        String name = "UseCase " + Integer.toString(UseCaseShape.count+1);
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
                double attrStartY = classShape.getY() + 30; // 30 is the header height

                for (int i = 0; i < classShape.getAttributes().size(); i++) {
                    if (y > attrStartY + (i * 22) && y < attrStartY + (i * 22) + 20) {
                        // Clicked on attribute i
                        ContextMenu attributeMenu=new ContextMenu();
                        MenuItem updateAttribute = new MenuItem("Update Attribute");
                        MenuItem deleteAttribute = new MenuItem("Delete Attribute");
                        int finalI = i;
                        classShape.selectedAttributeIndex = i;
                        updateAttribute.setOnAction(e->editAttribute(classShape, finalI));
                        deleteAttribute.setOnAction(e->deleteAttribute(classShape));
                        attributeMenu.getItems().addAll(updateAttribute,deleteAttribute);
                        attributeMenu.show(canvas,x,y);
                        return;
                    }
                }

                double methodsStartY = classShape.getY() + classShape.getHeight() - classShape.getMethods().size() * 22; // Calculate the Y position of methods
                for (int i = 0; i < classShape.getMethods().size(); i++) {
                    if (y > methodsStartY + (i * 22) && y < methodsStartY + (i * 22) + 20) {
                        // Clicked on method i
                        ContextMenu methodMenu=new ContextMenu();
                        MenuItem updateMethod = new MenuItem("Update method");
                        MenuItem deleteMethod = new MenuItem("Delete Method");
                        int finalI = i;
                        classShape.selectedMethodIndex = i; // Store the index of the selected method
                        updateMethod.setOnAction(e->editMethod(classShape, finalI));
                        deleteMethod.setOnAction(e->deleteMethod(classShape));
                        methodMenu.getItems().addAll(updateMethod,deleteMethod);
                        methodMenu.show(canvas,x,y);
//                        editMethod(classShape, i); // Call method to edit the selected method
                        return;
                    }
                }

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
                connectAssociation.setOnAction(e -> startAssociationConnection(shape,false));
                connectAggregation.setOnAction(e -> startAggregationConnection(classShape,false));
                connectComposition.setOnAction(e -> startCompositionConnection(classShape,false));
                connectGeneralization.setOnAction(e -> startGeneralizationConnection(classShape,false));
                connectDirectAssociation.setOnAction(e -> startDirectAssociationConnection(classShape,false));
                connectDependency.setOnAction(e -> startDependencyConnection(shape,"",false));

                contextMenu.getItems().addAll(addAttribute, addMethod, rename, delete, connectAssociation, connectAggregation, connectComposition, connectGeneralization, connectDirectAssociation, connectDependency);
            }
            else if (shape instanceof UserShape) {
                MenuItem rename = new MenuItem("Rename");
                MenuItem delete = new MenuItem("Delete");
                MenuItem addAssociation = new MenuItem("Add Association");

                rename.setOnAction(e -> renameShape(x, y));
                delete.setOnAction(e -> deleteShape(x, y));
                addAssociation.setOnAction(e -> startAssociationConnection(shape,false));

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
                addAssociation.setOnAction(e -> startAssociationConnection(shape,false));
                addDependency.setOnAction(e -> startDependencyConnection(shape,"Include",false));
                addDependency1.setOnAction(e -> startDependencyConnection(shape,"Extend",false));

                contextMenu.getItems().addAll(rename, delete, addAssociation, addDependency,addDependency1);
            }
            else{
                System.out.println("No Shape at this point"+ y);
            }


            contextMenu.show(canvas, x, y);
        }
    }
     private void editAttribute(ClassShape classShape, int attributeIndex) {
         TextInputDialog dialog = new TextInputDialog(classShape.getAttributes().get(attributeIndex));
         dialog.setTitle("Edit Attribute");
         dialog.setHeaderText("Edit Attribute Name:");

         Optional<String> result = dialog.showAndWait();
         if (result.isPresent()) {
             String newAttribute = result.get();
             Model.updateChild(classShape.getName(),"+ "+classShape.getAttributes().get(classShape.selectedAttributeIndex),"+ "+newAttribute);
             classShape.editAttribute(newAttribute);
             redrawCanvas(); // Redraw the canvas
         }
     }
     private void deleteAttribute(ClassShape classShape)
     {
         Model.deleteChild(classShape.getName(),"+ "+classShape.getAttributes().get(classShape.selectedAttributeIndex));
         classShape.deleteAttribute();
         redrawCanvas();
     }
     private void editMethod(ClassShape classShape, int methodIndex) {
         TextInputDialog dialog = new TextInputDialog(classShape.getMethods().get(methodIndex));
         dialog.setTitle("Edit Method");
         dialog.setHeaderText("Edit Method Name (e.g., methodName()):");

         Optional<String> result = dialog.showAndWait();
         if (result.isPresent()) {
             String newMethod = result.get();
             while(!(newMethod.contains("(") && newMethod.contains(")"))) {
                 showErrorDialog("Invalid Method Format", "Method name must contains '()'.");
                 dialog = new TextInputDialog();
                 dialog.setTitle("Edit Method");
                 dialog.setHeaderText("Enter Method Name (e.g., methodName()):");
                 dialog.setContentText(classShape.getMethods().get(methodIndex));
                 result = dialog.showAndWait();
                 if (result.isPresent()) {
                     newMethod = result.get();
                 }
             }
             Model.updateChild(classShape.getName(),"+ "+classShape.getMethods().get(classShape.selectedMethodIndex),"+ "+ newMethod);
             classShape.editMethod(newMethod); // Update the method name in the class shape
             redrawCanvas(); // Redraw the canvas
         }
     }
     private void deleteMethod(ClassShape classShape)
     {
         Model.deleteChild(classShape.getName(),"+ "+classShape.getMethods().get(classShape.selectedMethodIndex));
         classShape.deleteMethod();
         redrawCanvas();
     }
    public void startAssociationConnection(Shape startShape,boolean isTest) {

        canvas.setOnMouseClicked(event -> {
            double x,y;
            if(isTest)
            {
                x=event.getSceneX();
                y=event.getSceneY();
            }
            else {
                x=event.getX();
                y=event.getY();
            }
            Shape endShape = Object.findShapeAt(x,y);
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

    public void startAggregationConnection(ClassShape startClass, boolean isTest) {
        canvas.setOnMouseClicked(event -> {
            double x,y;
            if(isTest)
            {
                x=event.getSceneX();
                y=event.getSceneY();
            }
            else {
                x=event.getX();
                y=event.getY();
            }
            ClassShape endClass = (ClassShape)Object.findShapeAt(x,y);
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
    public void startCompositionConnection(ClassShape startClass,boolean isTest) {
        canvas.setOnMouseClicked(event -> {
            double x,y;
            if(isTest)
            {
                x=event.getSceneX();
                y=event.getSceneY();
            }
            else {
                x=event.getX();
                y=event.getY();
            }
            ClassShape endClass = (ClassShape)Object.findShapeAt(x,y);
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
    public void startGeneralizationConnection(ClassShape startClass,boolean isTest) {
        canvas.setOnMouseClicked(event -> {

            double x,y;
            if(isTest)
            {
                x=event.getSceneX();
                y=event.getSceneY();
            }
            else {
                x=event.getX();
                y=event.getY();
            }
            ClassShape endClass = (ClassShape)Object.findShapeAt(x,y);
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
    public void startDirectAssociationConnection(ClassShape startClass,boolean isTest) {
        canvas.setOnMouseClicked(event -> {
            double x,y;
            if(isTest)
            {
                x=event.getSceneX();
                y=event.getSceneY();
            }
            else {
                x=event.getX();
                y=event.getY();
            }
            ClassShape endClass = (ClassShape)Object.findShapeAt(x,y);
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
    public void startDependencyConnection(Shape startShape, String dependencyType,boolean isTest) {
        canvas.setOnMouseClicked(event -> {
            double x,y;
            if(isTest)
            {
                x=event.getSceneX();
                y=event.getSceneY();
            }
            else {
                x=event.getX();
                y=event.getY();
            }
            Shape endShape = Object.findShapeAt(x,y);
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



    public void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear canvas
        for (Shape shape : Object.shapes) {
            gc.setLineDashes(null); // Reset line dash to solid for regular shapes
            shape.draw(gc);         // Draw each shape
        }
    }



    public void addAttributeToShape(double x, double y) {
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

    public void addMethodToShape(double x, double y) {
        ClassShape shape = (ClassShape) Object.findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Method");
            dialog.setHeaderText("Enter Method Name (e.g., methodName()):");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String method = result.get();
                while(!(method.contains("(") && method.contains(")"))) {
                    showErrorDialog("Invalid Method Format", "Method name must contains '()'.");
                    dialog = new TextInputDialog();
                    dialog.setTitle("Add Method");
                    dialog.setHeaderText("Enter Method Name (e.g., methodName()):");
                    result = dialog.showAndWait();
                    if (result.isPresent()) {
                        method = result.get();
                    }
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


     public void renameShape(double x, double y) {
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


    public void deleteShape(double x, double y) {
        Shape shape=Object.findShapeAt(x,y);
        Model.removeModelExplorerItem(Object.deleteShape(shape));
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
                    Model.modelUpdateOnLoad(Object.getShapes());
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
