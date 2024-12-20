package org.example.my_project.UI;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.example.my_project.BL.*;
import org.example.my_project.Models.*;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Optional; /**
 * The {@code ProjectController} class is the main controller for managing the project UI and interactions.
 * It extends {@link Application} to serve as the entry point for the JavaFX application.
 * <p>
 * This controller handles the user interface components, including the toolbar, toolbox, canvas, and other UI elements.
 * It also manages user interactions like saving projects, exporting diagrams, generating code, and manipulating shapes on the canvas.
 * </p>
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class ProjectController extends Application {
    /**
     * The current project being managed.
     */
    private Project project;

    /**
     * The canvas where diagrams and shapes are drawn.
     */
    public Canvas canvas;

    /**
     * The {@link GraphicsContext} used for drawing on the canvas.
     */
    private GraphicsContext gc;

    /**
     * The currently selected shape on the canvas.
     */
    private Shape selectedShape;

    /**
     * Tracks the x-coordinate of the initial mouse drag position.
     */
    private double dragStartX;

    /**
     * Tracks the y-coordinate of the initial mouse drag position.
     */
    private double dragStartY;

    /**
     * The {@link Diagrams} object for managing diagrams.
     */
    public Diagrams Object = new Diagrams();

    /**
     * The {@link ModelExplorer} object for managing the model explorer view.
     */
    public ModelExplorer Model = new ModelExplorer();

    /**
     * The {@link CodeGenerator} object for generating code from diagrams.
     */
    public CodeGenerator generator = new CodeGenerator();

    /**
     * Constructs a new {@code ProjectController} and initializes a default project.
     */
    public ProjectController() {
        project = new Project("Untitled", "./");
    }

    /**
     * Entry point for the JavaFX application. Sets up the primary stage and initializes UI components.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        init(primaryStage,"ClassDiagram");
    }

    /**
     * Initializes the primary stage and sets up the user interface.
     * This includes creating the toolbar, toolbox, canvas, and other layout elements.
     *
     * @param primaryStage The primary stage for the application.
     */
    public void init(Stage primaryStage,String diagram) {

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
                    Model.modelExplorer.setRoot(new TreeItem<>("Model Explorer"));
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
        if(diagram.equals("ClassDiagram"))
        {
            rootItem.getChildren().setAll(classDiagramItem);
            classDiagramItem.getChildren().setAll(classToolItem, interfaceToolItem);
        }
        else if(diagram.equals("UseCaseDiagram"))
        {
            rootItem.getChildren().setAll(useCaseDiagramItem);
            useCaseDiagramItem.getChildren().setAll(actorToolItem, useCaseToolItem);
        }

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

    /**
     * Adds a shape to the canvas based on the specified shape type.
     *
     * @param shape the type of shape to add (e.g., "Class", "Interface", "Actor", "Use Case")
     */
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
    /**
     * Draws a class or interface shape on the canvas.
     *
     * @param x the x-coordinate of the shape's position
     * @param y the y-coordinate of the shape's position
     * @param n the name/type of the shape ("Class" or "Interface")
     */
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
    /**
     * Draws a user shape (actor) on the canvas.
     *
     * @param x the x-coordinate of the shape's position
     * @param y the y-coordinate of the shape's position
     */
    public void drawUserShape(double x, double y) {
        UserShape user = new UserShape(x, y, "User "+ Integer.toString(UserShape.count+1));
        Object.shapes.add(user);
        user.draw(gc);
        Model.updateModelExplorer(user.getName());
    }
    /**
     * Draws a use case shape on the canvas.
     *
     * @param x the x-coordinate of the shape's position
     * @param y the y-coordinate of the shape's position
     */
    public void drawUseCaseShape(double x, double y) {
        String name = "UseCase " + Integer.toString(UseCaseShape.count+1);
        UseCaseShape useCase = new UseCaseShape(x, y, name);
        Object.shapes.add(useCase);
        useCase.draw(gc);
        Model.updateModelExplorer(useCase.getName());
    }

    /**
     * Handles mouse click events on the canvas and shows context menus
     * based on the clicked shape or area.
     *
     * @param x       the x-coordinate of the mouse click
     * @param y       the y-coordinate of the mouse click
     * @param button  the mouse button used for the click
     */
    private void handleCanvasClick(double x, double y, MouseButton button) {
        if (button == MouseButton.SECONDARY) {
            Shape shape = Object.findShapeAt(x, y);
            ContextMenu contextMenu = new ContextMenu();

            if (shape instanceof ClassShape) {
                ClassShape classShape = (ClassShape) shape;
                double attrStartY ;
                if (classShape.isInterface())
                {
                    attrStartY=classShape.getY() + 50; // 50 FOR INTERFACE

                }
                else {
                    attrStartY=classShape.getY() + 30; // 30 FOR CLASS
                }

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
    /**
     * Edits the attribute of a specified class shape.
     *
     * @param classShape      the class shape containing the attribute to edit
     * @param attributeIndex  the index of the attribute to edit
     */
;
    private void editAttribute(ClassShape classShape, int attributeIndex) {
        String currentAttribute = classShape.getAttributes().get(attributeIndex);
        String currentVisibility = currentAttribute.substring(0, 1);
        String currentName = currentAttribute.substring(2); // Assuming format "+ attributeName"

        // Create dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Attribute");
        dialog.setHeaderText("Edit Attribute Details:");

        // Create input fields
        TextField nameField = new TextField(currentName);
        nameField.setPromptText("Attribute Name");

        ToggleGroup visibilityGroup = new ToggleGroup();
        RadioButton publicButton = new RadioButton("Public (+)");
        publicButton.setToggleGroup(visibilityGroup);

        RadioButton privateButton = new RadioButton("Private (-)");
        privateButton.setToggleGroup(visibilityGroup);

        RadioButton protectedButton = new RadioButton("Protected (#)");
        protectedButton.setToggleGroup(visibilityGroup);

        // Set current visibility
        switch (currentVisibility) {
            case "+":
                publicButton.setSelected(true);
                break;
            case "-":
                privateButton.setSelected(true);
                break;
            case "#":
                protectedButton.setSelected(true);
                break;
            default:
                publicButton.setSelected(true); // Default to public if unrecognized
        }

        // Layout
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Attribute Name:"),
                nameField,
                new Label("Visibility Type:"),
                publicButton,
                privateButton,
                protectedButton
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String visibility = publicButton.isSelected() ? "+" :
                        privateButton.isSelected() ? "-" : "#";
                return new Pair<>(visibility, nameField.getText().trim());
            }
            return null;
        });

        // Show dialog and process result
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent()) {
            String visibility = result.get().getKey();
            String newName = result.get().getValue();
            String newAttribute = visibility + " " + newName;

            Model.updateChild(classShape.getName(), currentAttribute, newAttribute);
            classShape.editAttribute(newAttribute);
            redrawCanvas(); // Redraw the canvas
        }
    }
    /**
     * Deletes the currently selected attribute from the specified class shape.
     *
     * @param classShape the class shape for which the selected attribute to delete
     */
    private void deleteAttribute(ClassShape classShape)
     {
         Model.deleteChild(classShape.getName(),classShape.getAttributes().get(classShape.selectedAttributeIndex));
         classShape.deleteAttribute();
         redrawCanvas();
     }
    /**
     * Edits the method of a specified class shape.
     *
     * @param classShape   the class shape containing the method to edit
     * @param methodIndex  the index of the method to edit
     */
    private void editMethod(ClassShape classShape, int methodIndex) {
        String currentMethod = classShape.getMethods().get(methodIndex);
        String currentVisibility = currentMethod.substring(0, 1);
        String currentName = currentMethod.substring(2); // Assuming format "+ methodName()"

        // Create dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Method");
        dialog.setHeaderText("Edit Method Details:");

        // Create input fields
        TextField nameField = new TextField(currentName);
        nameField.setPromptText("Method Name (e.g., methodName())");

        ToggleGroup visibilityGroup = new ToggleGroup();
        RadioButton publicButton = new RadioButton("Public (+)");
        publicButton.setToggleGroup(visibilityGroup);

        RadioButton privateButton = new RadioButton("Private (-)");
        privateButton.setToggleGroup(visibilityGroup);

        RadioButton protectedButton = new RadioButton("Protected (#)");
        protectedButton.setToggleGroup(visibilityGroup);

        // Set current visibility
        switch (currentVisibility) {
            case "+":
                publicButton.setSelected(true);
                break;
            case "-":
                privateButton.setSelected(true);
                break;
            case "#":
                protectedButton.setSelected(true);
                break;
            default:
                publicButton.setSelected(true); // Default to public if unrecognized
        }

        // Layout
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Method Name:"),
                nameField,
                new Label("Visibility Type:"),
                publicButton,
                privateButton,
                protectedButton
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String visibility = publicButton.isSelected() ? "+" :
                        privateButton.isSelected() ? "-" : "#";
                return new Pair<>(visibility, nameField.getText().trim());
            }
            return null;
        });

        // Show dialog and process result
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if (result.isPresent()) {
            String visibility = result.get().getKey();
            String newName = result.get().getValue();

            // Validate method format
            while (!(newName.contains("(") && newName.contains(")"))) {
                showErrorDialog("Invalid Method Format", "Method name must contain '()'.");
                Optional<Pair<String, String>> retryResult = dialog.showAndWait();
                if (retryResult.isPresent()) {
                    visibility = retryResult.get().getKey();
                    newName = retryResult.get().getValue();
                } else {
                    return; // User canceled
                }
            }

            String newMethod = visibility + " " + newName;
            if(classShape.isInterface())
            {
               String firstclass = Model.getStartClassForEndClass(classShape.getName());
               for(Shape s:Object.getShapes())
               {
                   if(s.getName().equals(firstclass)){
                       ClassShape sh=(ClassShape) s;
                       sh.overridenMethods.remove(currentMethod);
                       sh.overridenMethods.add(newMethod);
                   }
               }
            }

            Model.updateChild(classShape.getName(), currentMethod, newMethod);
            classShape.editMethod(newMethod); // Update the method name in the class shape
            redrawCanvas(); // Redraw the canvas
        }
    }
    /**
     * Deletes the currently selected method from the specified class shape.
     *
     * @param classShape the class shape for which the selected method to delete
     */
    private void deleteMethod(ClassShape classShape)
     {
         Model.deleteChild(classShape.getName(),classShape.getMethods().get(classShape.selectedMethodIndex));
         classShape.deleteMethod();
         redrawCanvas();
         if(classShape.isInterface())
         {
             String firstclass = Model.getStartClassForEndClass(classShape.getName());
             for(Shape s:Object.getShapes())
             {
                 if(s.getName().equals(firstclass)){
                     ClassShape sh=(ClassShape) s;
                     sh.overridenMethods.remove(classShape.selectedMethodIndex);
                 }
             }
         }
     }

    /**
     * Starts creating an association connection from a specified shape.
     *
     * @param startShape the shape to start the association connection from
     */
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

    /**
     * Starts creating an aggregation connection from a specified class shape.
     *
     * @param startClass the class shape to start the aggregation connection from
     */
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
    /**
     * Starts creating a composition connection from a specified class shape.
     *
     * @param startClass the class shape to start the composition connection from
     */
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
    /**
     * Starts creating a generalization connection from a specified class shape.
     *
     * @param startClass the class shape to start the generalization connection from
     */
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
    /**
     * Starts creating a direct association connection from a specified class shape.
     *
     * @param startClass the class shape to start the direct association connection from
     */
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
    /**
     * Initiates a dependency connection between two shapes on the canvas.
     * Allows the user to create a dependency line by clicking on the start shape
     * and then the end shape. Ensures that the dependency is valid based on shape types.
     *
     * @param startShape     The shape where the dependency starts.
     * @param dependencyType The type of dependency ("Include" or "Extend").
     */
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



    /**
     * Redraws all shapes on the canvas by clearing the current display
     * and re-rendering each shape from the shapes list.
     */
         public void redrawCanvas() {
             gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear canvas
        for (Shape shape : Object.shapes) {
            gc.setLineDashes(null); // Reset line dash to solid for regular shapes
            shape.draw(gc);         // Draw each shape
        }
    }
    /**
     * Adds an attribute to a class shape located at the given coordinates.
     * Prompts the user to input the attribute name and updates the model explorer.
     *
     * @param x The x-coordinate of the shape.
     * @param y The y-coordinate of the shape.
     */
    public void addAttributeToShape(double x, double y) {
        ClassShape shape = (ClassShape) Object.findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            // Create dialog
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add Attribute");
            dialog.setHeaderText("Enter Attribute Details:");

            // Create input fields
            TextField attributeField = new TextField();
            attributeField.setPromptText("Attribute Name");

            ToggleGroup visibilityGroup = new ToggleGroup();

            RadioButton publicButton = new RadioButton("Public (+)");
            publicButton.setToggleGroup(visibilityGroup);
            publicButton.setSelected(true); // Default selection

            RadioButton privateButton = new RadioButton("Private (-)");
            privateButton.setToggleGroup(visibilityGroup);

            RadioButton protectedButton = new RadioButton("Protected (#)");
            protectedButton.setToggleGroup(visibilityGroup);

            // Layout
            VBox content = new VBox(10);
            content.getChildren().addAll(
                    new Label("Attribute Name:"),
                    attributeField,
                    new Label("Visibility Type:"),
                    publicButton,
                    privateButton,
                    protectedButton
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Convert result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    String attributeName = attributeField.getText().trim();
                    String visibility = "+";
                    if (privateButton.isSelected()) {
                        visibility = "-";
                    } else if (protectedButton.isSelected()) {
                        visibility = "#";
                    }
                    return new Pair<>(visibility, attributeName);
                }
                return null;
            });

            // Show dialog and get result
            Optional<Pair<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                String visibilitySymbol = result.get().getKey();
                String attributeName = result.get().getValue();

                if (!attributeName.isEmpty()) {
                    String formattedAttribute = visibilitySymbol + " " + attributeName;

                    // Add attribute to the shape
                    Object.addAttributeToShape(shape, formattedAttribute);

                    // Update the diagram
                    redrawCanvas();

                    // Add to the Model Explorer
                    TreeItem<String> classItem = Model.getModelExplorerItemAt(x, y);
                    if (classItem != null) {
                        classItem.getChildren().add(new TreeItem<>(formattedAttribute));
                    }
                } else {
                    // Show error if attribute name is empty
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Attribute name cannot be empty!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        }
    }
    /**
     * Adds a method to a class shape located at the given coordinates.
     * Prompts the user to input the method name, validates the format,
     * and updates the model explorer.
     *
     * @param x The x-coordinate of the shape.
     * @param y The y-coordinate of the shape.
     */
    public void addMethodToShape(double x, double y) {
        ClassShape shape = (ClassShape) Object.findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            // Create dialog
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add Method");
            dialog.setHeaderText("Enter Method Details:");

            // Create input fields
            TextField methodField = new TextField();
            methodField.setPromptText("Method Name (e.g., methodName())");

            ToggleGroup visibilityGroup = new ToggleGroup();

            RadioButton publicButton = new RadioButton("Public (+)");
            publicButton.setToggleGroup(visibilityGroup);
            publicButton.setSelected(true); // Default selection

            RadioButton privateButton = new RadioButton("Private (-)");
            privateButton.setToggleGroup(visibilityGroup);

            RadioButton protectedButton = new RadioButton("Protected (#)");
            protectedButton.setToggleGroup(visibilityGroup);

            // Layout
            VBox content = new VBox(10);
            content.getChildren().addAll(
                    new Label("Method Name:"),
                    methodField,
                    new Label("Visibility Type:"),
                    publicButton,
                    privateButton,
                    protectedButton
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Convert result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    String methodName = methodField.getText().trim();
                    String visibility = "+";
                    if (privateButton.isSelected()) {
                        visibility = "-";
                    } else if (protectedButton.isSelected()) {
                        visibility = "#";
                    }
                    return new Pair<>(visibility, methodName);
                }
                return null;
            });

            // Show dialog and get result
            Optional<Pair<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                String visibilitySymbol = result.get().getKey();
                String methodName = result.get().getValue();

                // Validate method format
                while (!(methodName.contains("(") && methodName.contains(")"))) {
                    showErrorDialog("Invalid Method Format", "Method name must contain '()'.");
                    Optional<Pair<String, String>> newResult = dialog.showAndWait();
                    if (newResult.isPresent()) {
                        visibilitySymbol = newResult.get().getKey();
                        methodName = newResult.get().getValue();
                    } else {
                        return; // User canceled
                    }
                }

                String formattedMethod = visibilitySymbol + " " + methodName;

                // Add method to the shape
                Object.addMethodToShape(shape, formattedMethod);

                // Update the diagram
                redrawCanvas();

                // Add to the Model Explorer
                TreeItem<String> classItem = Model.getModelExplorerItemAt(x, y);
                if (classItem != null) {
                    classItem.getChildren().add(new TreeItem<>(formattedMethod));
                }
            }

        }
    }

    /**
     * Renames a shape located at the given coordinates. Prompts the user
     * for a new name, updates the shape's name, and refreshes the canvas.
     *
     * @param x The x-coordinate of the shape.
     * @param y The y-coordinate of the shape.
     */
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

    /**
     * Deletes a shape located at the given coordinates. Removes the shape
     * from the canvas and the model explorer.
     *
     * @param x The x-coordinate of the shape.
     * @param y The y-coordinate of the shape.
     */
    public void deleteShape(double x, double y) {
        Shape shape=Object.findShapeAt(x,y);
        Model.removeModelExplorerItem(Object.deleteShape(shape));
            // Redraw the canvas
            redrawCanvas();
        }
    /**
     * Displays an error dialog with the specified title and content.
     *
     * @param title   The title of the error dialog.
     * @param content The content message of the error dialog.
     */
    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Updates the load button menu with the list of available projects.
     * Adds menu items for each project and sets up their action handlers.
     *
     * @param loadButton The menu button to update.
     */
    // Update the load button with current project list
    public void updateLoadButton(MenuButton loadButton) {
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
    public void openRecentProject(String project)
    {
        try {
            Object = ProjectManager.loadProject(project);
            Model.modelUpdateOnLoad(Object.getShapes());
            redrawCanvas();
        } catch (IOException | ClassNotFoundException ex) {
            showErrorDialog("Error Loading Project: " , ex.getMessage());
        }
    }
    /**
     * Calls for generate code based on the current diagram and displays it in a new dialog.
     */
    public void generateCode() {
        showGeneratedCodeDialog(generator.generateCode());

    }
    /**
     * Displays the generated code in a new dialog window.
     *
     * @param code The generated code to display.
     */
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
    /**
     * Exports the current project displayed on the canvas to a PNG file.
     */
    private void exportToPNG() {
      project.exportToPNG(canvas);
    }
}
