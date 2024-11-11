package org.example.my_project;// Main class for launching the JavaFX application
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class UMLEditorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Entry point for the JavaFX application
        ProjectController controller = new ProjectController();
        primaryStage.setTitle("UML Editor");
        controller.init(primaryStage);  // Initialize the main controller
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// org.example.my_project.Project.java
class Project {
    private String name;
    private String path;

    public Project(String name, String path) {
        this.name = name;
        this.path = path;
    }

    // Save the project to a file
    public void save() {
        // Logic to save project details and diagrams
    }

    // Load the project from a file
    public void load() {
        // Logic to load a project and its associated diagrams
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}

// org.example.my_project.Model.java
 class Model {
    public void addComponent(Component component) {
        // Logic to add a component (Class, Package, etc.) to the model
    }

    public void removeComponent(Component component) {
        // Logic to remove a component from the model
    }
}

// Interface org.example.my_project.Component.java
 interface Component {
    void display();
    void addProperty();
    void removeProperty();
}

// org.example.my_project.ClassComponent.java - Represents a Class in UML
 class ClassComponent implements Component {
    private String name;
    private List<String> attributes;
    private List<String> methods;

    public ClassComponent(String name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public void display() {
        // Logic to display a org.example.my_project.ClassComponent
    }

    public void addProperty() {
        // Logic to add attributes/methods to the class
    }

    public void removeProperty() {
        // Logic to remove attributes/methods
    }

    // Additional class-specific methods
    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public void addMethod(String method) {
        methods.add(method);
    }
}

// org.example.my_project.PackageComponent.java - Represents a Package in UML
 class PackageComponent implements Component {
    private String name;
    private List<ClassComponent> classes;

    public PackageComponent(String name) {
        this.name = name;
        this.classes = new ArrayList<>();
    }

    public void display() {
        // Logic to display a org.example.my_project.PackageComponent
    }

    public void addClass(ClassComponent classComponent) {
        classes.add(classComponent);
    }

    public void removeClass(ClassComponent classComponent) {
        classes.remove(classComponent);
    }

    public void addProperty() {
        // Implement method
    }

    public void removeProperty() {
        // Implement method
    }
}

// org.example.my_project.Diagram.java
 interface Diagram {
    void display();  // Display the diagram (in GUI)
}

// org.example.my_project.ClassDiagram.java - Specialization of org.example.my_project.Diagram
 class ClassDiagram implements Diagram {
    private List<ClassComponent> classes;

    public ClassDiagram() {
        classes = new ArrayList<>();
    }

    public void display() {
        // Logic to display class diagram on the screen
    }

    public void addClass(ClassComponent classComponent) {
        classes.add(classComponent);
    }

    public void removeClass(ClassComponent classComponent) {
        classes.remove(classComponent);
    }
}

// org.example.my_project.PackageDiagram.java - Specialization of org.example.my_project.Diagram
 class PackageDiagram implements Diagram {
    private List<PackageComponent> packages;

    public PackageDiagram() {
        packages = new ArrayList<>();
    }

    public void display() {
        // Logic to display package diagram on the screen
    }

    public void addPackage(PackageComponent packageComponent) {
        packages.add(packageComponent);
    }

    public void removePackage(PackageComponent packageComponent) {
        packages.remove(packageComponent);
    }
}

// org.example.my_project.CodeGenerator.java
 class CodeGenerator {
    public void generate(Project project) {
        // Logic to convert the UML model (project) to Java code
    }
}

// org.example.my_project.ProjectController.java - Controller for managing the project and interactions




class ProjectController extends Application {
    private Project project;
    private Model model;
    private Canvas canvas;
    private GraphicsContext gc;
    private TreeView<String> modelExplorer;

    public ProjectController() {
        project = new Project("Untitled", "./");
        model = new Model();
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

        saveButton.setOnAction(e -> saveProject());
        exportButton.setOnAction(e -> exportToPNG());
        generateCodeButton.setOnAction(e -> generateCode());
        loadButton.getItems().addAll(getLoadMenuItems());

        toolBar.getItems().addAll(saveButton, exportButton, generateCodeButton, loadButton);
        root.setTop(toolBar);

        // Left toolbox
        VBox toolbox = new VBox();
        Label toolboxLabel = new Label("TOOLBOX");
        ListView<String> tools = new ListView<>();
        tools.getItems().addAll("Class", "Interface", "Association", "Composition");
        tools.setOnMouseClicked(e -> addShapeToCanvas(tools.getSelectionModel().getSelectedItem()));
        toolbox.getChildren().addAll(toolboxLabel, tools);
        root.setLeft(toolbox);

        // Center canvas area
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setOnMouseClicked(e -> handleCanvasClick(e.getX(), e.getY(), e.getButton()));

        root.setCenter(canvas);

        // Right model explorer
        modelExplorer = new TreeView<>();
        modelExplorer.setRoot(new TreeItem<>("Model Explorer"));
        root.setRight(modelExplorer);

        // Bottom properties panel
        VBox propertiesPanel = new VBox();
        Label propertiesLabel = new Label("Properties");
        TextField nameField = new TextField();
        Button updateNameButton = new Button("Update Name");
        updateNameButton.setOnAction(e -> updateSelectedModelName(nameField.getText()));
        propertiesPanel.getChildren().addAll(propertiesLabel, new Label("Name:"), nameField, updateNameButton);
        root.setBottom(propertiesPanel);

        // Set up and show scene
        Scene scene = new Scene(root, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("UML Editor");
        primaryStage.show();
    }

    private MenuItem[] getLoadMenuItems() {
        // Placeholder for loading projects
        MenuItem loadItem1 = new MenuItem("Load Project 1");
        loadItem1.setOnAction(e -> loadProject());
        return new MenuItem[]{loadItem1};
    }

    private void addShapeToCanvas(String shape) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.fillRect(100, 100, 100, 50); // Example shape
        gc.strokeRect(100, 100, 100, 50);
        updateModelExplorer(shape);
    }

    private void handleCanvasClick(double x, double y, MouseButton button) {
        if (button == MouseButton.SECONDARY) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem addAttribute = new MenuItem("Add Attribute");
            MenuItem addMethod = new MenuItem("Add Method");
            MenuItem delete = new MenuItem("Delete");

            addAttribute.setOnAction(e -> addAttributeToShape(x, y));
            addMethod.setOnAction(e -> addMethodToShape(x, y));
            delete.setOnAction(e -> deleteShape(x, y));

            contextMenu.getItems().addAll(addAttribute, addMethod, delete);
            contextMenu.show(canvas, x, y);
        }
    }

    private void updateModelExplorer(String shapeName) {
        TreeItem<String> shapeItem = new TreeItem<>(shapeName);
        modelExplorer.getRoot().getChildren().add(shapeItem);
    }

    private void addAttributeToShape(double x, double y) {
        // Logic to add attribute to a shape at (x, y)
    }

    private void addMethodToShape(double x, double y) {
        // Logic to add method to a shape at (x, y)
    }

    private void deleteShape(double x, double y) {
        // Logic to delete a shape at (x, y)
    }

    private void updateSelectedModelName(String newName) {
        // Update the selected model's name in the Model Explorer and on canvas
    }

    public void saveProject() {
        project.save();
    }

    public void loadProject() {
        project.load();
    }

    public void generateCode() {
        CodeGenerator generator = new CodeGenerator();
        generator.generate(project);
    }

    private void exportToPNG() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as PNG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

