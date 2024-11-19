package org.example.my_project;// Main class for launching the JavaFX application

import javafx.application.Application;
import javafx.geometry.Point2D;
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
        controller.init(primaryStage); // Initialize the main controller
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
    void display(); // Display the diagram (in GUI)
}

// org.example.my_project.ClassDiagram.java - Specialization of
// org.example.my_project.Diagram
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

// org.example.my_project.PackageDiagram.java - Specialization of
// org.example.my_project.Diagram
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

// org.example.my_project.ProjectController.java - Controller for managing the
// project and interactions

abstract class Shape {
    protected double x, y;
    protected String name;

    public Shape(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public abstract void draw(GraphicsContext gc);

    public boolean contains(double px, double py) {
        return px >= x && px <= x + 150 && py >= y && py <= y + 100; // Example bounding box
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class ClassShape extends Shape {
    private List<String> attributes = new ArrayList<>();
    private List<String> methods = new ArrayList<>();
    private double totalHeight;
    private double width;

    public ClassShape(double x, double y, String name) {
        super(x, y, name);
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return totalHeight;
    }

    public void addMethod(String method) {
        methods.add(method);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        // Set the dimensions for the class shape
        width = 150; // Fixed width
        double headerHeight;
        if(getName().equals("Interface")){
             headerHeight = 50;
        }
        else {
             headerHeight = 30; // Height for class name
        }
        double attributesHeight = Math.max(30, attributes.size() * 20); // Minimum height for attributes section
        double methodsHeight = Math.max(30, methods.size() * 20); // Minimum height for methods section
        totalHeight = headerHeight + attributesHeight + methodsHeight;

        // Draw the outer rectangle
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, totalHeight);

        // Draw the class name section
        gc.strokeLine(x, y + headerHeight, x + width, y + headerHeight);
        if(getName().equals("Interface")){
            gc.fillText("<<Interface>>", x + 10, y + 20); // Default interface label
            gc.fillText(name, x + 10, y + 40); // Default interface name
        }
        else {
            gc.fillText(name, x + 10, y + 20);
        }
        // Draw the attributes section
        double attrStartY = y + headerHeight;
        gc.strokeLine(x, attrStartY + attributesHeight, x + width, attrStartY + attributesHeight);

            double attrY = attrStartY + 20;
            for (String attribute : attributes) {
                gc.fillText("+ " + attribute, x + 10, attrY);
                attrY += 20;
            }

        // Draw the methods section
        double methodsStartY = attrStartY + attributesHeight;
            double methodY = methodsStartY + 20;
            for (String method : methods) {
                gc.fillText("+ " + method, x + 10, methodY);
                methodY += 20;
            }
    }

}

class InterfaceShape extends Shape {
    private List<String> attributes = new ArrayList<>();
    private List<String> methods = new ArrayList<>();

    public InterfaceShape(double x, double y, String name) {
        super(x, y, name);
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public void addMethod(String method) {
        methods.add(method);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        // Set the dimensions for the class shape
        double width = 150; // Fixed width
        double headerHeight = 50; // Height for class name
        double attributesHeight = Math.max(30, attributes.size() * 20); // Minimum height for attributes section
        double methodsHeight = Math.max(30, methods.size() * 20); // Minimum height for methods section
        double totalHeight = headerHeight + attributesHeight + methodsHeight;

        // Draw the outer rectangle
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, totalHeight);



        // Draw the class name section
        gc.strokeLine(x, y + headerHeight, x + width, y + headerHeight);
        gc.fillText("<<Interface>>", x + 10, y + 20); // Default interface label
        gc.fillText(name, x + 10, y + 40); // Default interface name

        // Draw the attributes section
        double attrStartY = y + headerHeight;
        gc.strokeLine(x, attrStartY + attributesHeight, x + width, attrStartY + attributesHeight);
        if (attributes.isEmpty()) {
            gc.fillText("<No attributes>", x + 10, attrStartY + 20);
        } else {
            double attrY = attrStartY + 20;
            for (String attribute : attributes) {
                gc.fillText("+ " + attribute, x + 10, attrY);
                attrY += 20;
            }
        }

        // Draw the methods section
        double methodsStartY = attrStartY + attributesHeight;
        if (methods.isEmpty()) {
            gc.fillText("<No methods>", x + 10, methodsStartY + 20);
        } else {
            double methodY = methodsStartY + 20;
            for (String method : methods) {
                gc.fillText("+ " + method, x + 10, methodY);
                methodY += 20;
            }
        }
    }

}

class AssociationShape extends Shape {
    public ClassShape startClass; // Starting class shape
    public ClassShape endClass;   // Ending class shape
    private double endX, endY;     // Coordinates for the end of the line

    public AssociationShape(ClassShape startClass, ClassShape endClass) {
        super(0, 0, "Association");
        this.startClass = startClass;
        this.endClass = endClass;
        updateEndpoints();
    }

    private void updateEndpoints() {
        if (startClass != null) {
            javafx.geometry.Point2D startPoint = getBorderIntersection(
                    startClass.getX(),
                    startClass.getY(),
                    startClass.getWidth(),
                    startClass.getHeight(),
                    endClass == null ? endX : endClass.getX() + endClass.getWidth() / 2,
                    endClass == null ? endY : endClass.getY() + endClass.getHeight() / 2
            );
            this.x = startPoint.getX();
            this.y = startPoint.getY();
        }
        if (endClass != null) {
            Point2D endPoint = getBorderIntersection(
                    endClass.getX(),
                    endClass.getY(),
                    endClass.getWidth(),
                    endClass.getHeight(),
                    startClass == null ? x : startClass.getX() + startClass.getWidth() / 2,
                    startClass == null ? y : startClass.getY() + startClass.getHeight() / 2
            );
            this.endX = endPoint.getX();
            this.endY = endPoint.getY();
        }
    }

    private javafx.geometry.Point2D getBorderIntersection(
            double rectX, double rectY, double rectWidth, double rectHeight,
            double targetX, double targetY) {

        double centerX = rectX + rectWidth / 2;
        double centerY = rectY + rectHeight / 2;

        // Direction vector from rectangle center to target point
        double dx = targetX - centerX;
        double dy = targetY - centerY;

        // Scale factors for intersection with rectangle borders
        double scaleX = rectWidth / 2 / Math.abs(dx);
        double scaleY = rectHeight / 2 / Math.abs(dy);
        double scale = Math.min(scaleX, scaleY);

        // Calculate border intersection point
        double borderX = centerX + dx * scale;
        double borderY = centerY + dy * scale;

        // Return the concrete Point2D
        return new javafx.geometry.Point2D(borderX, borderY);
    }


    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints(); // Ensure endpoints are updated dynamically
        gc.strokeLine(x, y, endX, endY); // Draw the line
    }

    @Override
    public void move(double deltaX, double deltaY) {
        if (startClass == null && endClass == null) {
            x += deltaX;
            y += deltaY;
            endX += deltaX;
            endY += deltaY;
        }
    }

    public void setEndPoint(double endX, double endY) {
        this.endX = endX;
        this.endY = endY;
    }

    public void setStartClass(ClassShape startClass) {
        this.startClass = startClass;
        updateEndpoints();
    }

    public void setEndClass(ClassShape endClass) {
        this.endClass = endClass;
        updateEndpoints();
    }
}



class AggregationShape extends Shape {

    public AggregationShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x, y, x + 100, y); // Straight line with a diamond
        gc.strokePolygon(
                new double[] { x, x - 10, x, x + 10 },
                new double[] { y, y - 5, y - 10, y - 5 },
                4); // Hollow diamond
    }

}

class CompositionShape extends Shape {

    public CompositionShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x, y, x + 100, y); // Straight line with a solid diamond
        gc.setFill(Color.BLACK);
        gc.fillPolygon(
                new double[] { x, x - 10, x, x + 10 },
                new double[] { y, y - 5, y - 10, y - 5 },
                4); // Solid diamond

    }
}

class GeneralizationShape extends Shape {

    public GeneralizationShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x, y, x + 100, y); // Straight line with a hollow triangle
        gc.strokePolygon(
                new double[] { x + 100, x + 90, x + 90 },
                new double[] { y, y - 5, y + 5 },
                3); // Hollow triangle
    }

}

class DirectAssociationLineShape extends Shape {

    public DirectAssociationLineShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x, y, x + 100, y); // Straight line with a plain arrow
        gc.strokePolygon(
                new double[] { x + 100, x + 90, x + 90 },
                new double[] { y, y - 5, y + 5 },
                3); // Plain arrowhead
    }

}
class DependencyLineShape extends Shape {

    public DependencyLineShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5); // Dashed line
        gc.strokeLine(x, y, x + 100, y); // Dashed line with an arrow
        gc.strokePolygon(
                new double[] { x + 100, x + 90, x + 90 },
                new double[] { y, y - 5, y + 5 },
                3); // Arrowhead
        gc.setLineDashes(0); // Reset line style
    }

}
class InterfaceRealizationLineShape extends Shape {

    public InterfaceRealizationLineShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(5); // Dashed line
        gc.strokeLine(x, y, x + 100, y); // Dashed line with a hollow triangle
        gc.strokePolygon(
                new double[] { x + 100, x + 90, x + 90 },
                new double[] { y, y - 5, y + 5 },
                3); // Hollow triangle
        gc.setLineDashes(0); // Reset line style
    }

}
class PackageShape extends Shape {

    public PackageShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);

        // Outer rectangle
        gc.strokeRect(x, y, 150, 100);

        // Tab for package header
        gc.strokeRect(x, y, 50, 20);

        gc.fillText("Package", x + 5, y + 15); // Default package name
    }

}
class SubSystemShape extends Shape {

    public SubSystemShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);

        // Outer rectangle
        gc.strokeRect(x, y, 150, 100);

        // Subsystem label
        gc.fillText("<<Subsystem>>", x + 10, y + 20);
        gc.fillText("SubsystemName", x + 10, y + 50);
    }

}
class ContainmentLineShape extends Shape {

    public ContainmentLineShape(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x, y, x + 100, y); // Straight line with a filled diamond
        gc.setFill(Color.BLACK);
        gc.fillPolygon(
                new double[] { x, x - 10, x, x + 10 },
                new double[] { y, y - 5, y - 10, y - 5 },
                4); // Solid diamond
    }

}

class ProjectController extends Application {
    private Project project;
    private Model model;
    private Canvas canvas;
    private GraphicsContext gc;
    private TreeView<String> modelExplorer;
    private Shape selectedShape; // Keeps track of the selected shape
    private double dragStartX, dragStartY; // Tracks initial mouse drag positions
    private List<Shape> shapes = new ArrayList<>(); // List of all shapes on the canvas

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
        TreeView<String> toolTree = new TreeView<>();
        TreeItem<String> rootItem = new TreeItem<>("Diagrams");
        rootItem.setExpanded(true);

        // Class Diagram tools
        TreeItem<String> classDiagramItem = new TreeItem<>("Class Diagram");
        classDiagramItem.getChildren().addAll(
                new TreeItem<>("Class"),
                new TreeItem<>("Interface"),
                new TreeItem<>("Association"),
                new TreeItem<>("Direct Association"),
                new TreeItem<>("Aggregation"),
                new TreeItem<>("Composition"),
                new TreeItem<>("Dependency"),
                new TreeItem<>("Generalization"),
                new TreeItem<>("Interface Realization"));

        // Package Diagram tools
        TreeItem<String> packageDiagramItem = new TreeItem<>("Package Diagram");
        packageDiagramItem.getChildren().addAll(
                new TreeItem<>("Package"),
                new TreeItem<>("Subsystem"),
                new TreeItem<>("Containment"),
                new TreeItem<>("Dependency"));

        rootItem.getChildren().addAll(classDiagramItem, packageDiagramItem);
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

        // Center canvas area
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.setOnMouseClicked(event -> {
            handleCanvasClick(event.getX(), event.getY(), event.getButton());
        });

        canvas.setOnMousePressed(event -> {
            selectedShape = findShapeAt(event.getX(), event.getY());
            if (selectedShape != null) {
                dragStartX = event.getX();
                dragStartY = event.getY();
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (selectedShape != null) {
                double deltaX = event.getX() - dragStartX;
                double deltaY = event.getY() - dragStartY;

                // Handle movement based on shape type
                if (selectedShape instanceof AssociationShape) {
                    AssociationShape association = (AssociationShape) selectedShape;
                    if (association.startClass == null || association.endClass == null) {
                        // Allow dragging if not connected to classes
                        association.move(deltaX, deltaY);
                    } else {
                        // Dynamically update the end of the line
                        association.setEndPoint(event.getX(), event.getY());
                    }
                } else {
                    selectedShape.move(deltaX, deltaY);
                }
                dragStartX = event.getX();
                dragStartY = event.getY();
                redrawCanvas();
            }
        });

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
        return new MenuItem[] { loadItem1 };
    }

    private void addShapeToCanvas(String shape) {
        double x = 100, y = 100; // Default position for now
        switch (shape) {
            case "Class":
                drawClassShape(x, y,"Class");
                break;
            case "Association":
                drawAssociationLine(x, y);
                break;
            case "Aggregation":
                drawAggregationLine(x, y);
                break;
            case "Composition":
                drawCompositionLine(x, y);
                break;
            case "Generalization":
                drawGeneralizationLine(x, y);
                break;
            case "Interface":
                drawClassShape(x, y,"Interface");
                break;
            case "Direct Association":
                drawDirectAssociationLine(x, y);
                break;
            case "Dependency":
                drawDependencyLine(x, y);
                break;
            case "Interface Realization":
                drawInterfaceRealizationLine(x, y);
                break;
            case "Package":
                drawPackageShape(x, y);
                break;
            case "Subsystem":
                drawSubsystemShape(x, y);
                break;
            case "Containment":
                drawContainmentLine(x, y);
                break;
            // Add cases for other shapes
            default:
                System.out.println("Unhandled shape: " + shape);
        }
        updateModelExplorer(shape);
    }

    // Draw Class Shape
    private void drawClassShape(double x, double y,String n) {
        ClassShape classShape = new ClassShape(x, y, n);
        shapes.add(classShape);
        classShape.draw(gc);
    }

    // Draw Interface Shape


    // Draw Association Line
    private void drawAssociationLine(double x, double y) {
//        AssociationShape Shape = new AssociationShape(x, y, "Association");
//        shapes.add(Shape);
//        Shape.draw(gc);
    }

    // Draw Direct Association Line
    private void drawDirectAssociationLine(double x, double y) {
        DirectAssociationLineShape Shape = new DirectAssociationLineShape(x, y, "DirectAssociation");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Aggregation Line
    private void drawAggregationLine(double x, double y) {
        AggregationShape Shape = new AggregationShape(x, y, "Aggregation");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Composition Line
    private void drawCompositionLine(double x, double y) {
        CompositionShape Shape = new CompositionShape(x, y, "Composition");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Dependency Line
    private void drawDependencyLine(double x, double y) {
        DependencyLineShape Shape = new DependencyLineShape(x, y, "Dependency");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Generalization Line
    private void drawGeneralizationLine(double x, double y) {
        GeneralizationShape Shape = new GeneralizationShape(x, y, "GeneralizationShape");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Interface Realization Line
    private void drawInterfaceRealizationLine(double x, double y) {
        InterfaceRealizationLineShape Shape = new InterfaceRealizationLineShape(x, y, "InterfaceRealization");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Package
    private void drawPackageShape(double x, double y) {
        PackageShape Shape = new PackageShape(x, y, "Package");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Subsystem Shape
    private void drawSubsystemShape(double x, double y) {
        SubSystemShape Shape = new SubSystemShape(x, y, "SubSystem");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    // Draw Containment Line
    private void drawContainmentLine(double x, double y) {
        ContainmentLineShape Shape = new ContainmentLineShape(x, y, "ContainmentLine");
        shapes.add(Shape);
        Shape.draw(gc);
    }

    private void handleCanvasClick(double x, double y, MouseButton button) {
        if (button == MouseButton.SECONDARY) {
            Shape shape = findShapeAt(x, y);
            ContextMenu contextMenu = new ContextMenu();

            if (shape instanceof ClassShape) {
                ClassShape classShape = (ClassShape) shape;

                MenuItem addAttribute = new MenuItem("Add Attribute");
                MenuItem addMethod = new MenuItem("Add Method");
                MenuItem rename = new MenuItem("Rename");
                MenuItem delete = new MenuItem("Delete");
                MenuItem connectAssociation = new MenuItem("Connect Association");

                addAttribute.setOnAction(e -> addAttributeToShape(x, y));
                addMethod.setOnAction(e -> addMethodToShape(x, y));
                rename.setOnAction(e -> renameShape(x, y));
                delete.setOnAction(e -> deleteShape(x, y));
                connectAssociation.setOnAction(e -> startAssociationConnection(classShape));

                contextMenu.getItems().addAll(addAttribute, addMethod, rename, delete, connectAssociation);
            }

            contextMenu.show(canvas, x, y);
        }
    }
    private void startAssociationConnection(ClassShape startClass) {
        canvas.setOnMouseClicked(event -> {
            ClassShape endClass = (ClassShape) findShapeAt(event.getX(), event.getY());
            if (endClass != null && endClass != startClass) {
                AssociationShape association = new AssociationShape(startClass, endClass);
                shapes.add(association); // Add the new association to the shapes list
                redrawCanvas();
            }
            // Reset mouse click behavior to the default
            canvas.setOnMouseClicked(evt -> handleCanvasClick(evt.getX(), evt.getY(), evt.getButton()));
        });
    }


    private void updateModelExplorer(String shapeName) {
        TreeItem<String> shapeItem = new TreeItem<>(shapeName);
        modelExplorer.getRoot().getChildren().add(shapeItem);
    }

    private void updateModelExplorerItem(Shape shape, String newName) {
        for (TreeItem<String> item : modelExplorer.getRoot().getChildren()) {
            if (item.getValue().equals(shape.getName())) {
                item.setValue(newName);
                break;
            }
        }
    }

    private void removeModelExplorerItem(Shape shape) {
        modelExplorer.getRoot().getChildren().removeIf(item -> item.getValue().equals(shape.getName()));
    }

    private TreeItem<String> getModelExplorerItemAt(double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
                for (TreeItem<String> item : modelExplorer.getRoot().getChildren()) {
                    if (item.getValue().equals(shape.getName())) {
                        return item;
                    }
                }
            }
        }
        return null; // No matching item found
    }

    private Shape findShapeAt(double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null; // No shape found at the position
    }

    private void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear canvas
        for (Shape shape : shapes) {
            shape.draw(gc);
        }
    }

    private void addAttributeToShape(double x, double y) {
        ClassShape shape = (ClassShape) findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Attribute");
            dialog.setHeaderText("Enter Attribute Name:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(attribute -> {
                // Add attribute to the shape
                shape.addAttribute(attribute);

                // Update the diagram
                redrawCanvas();

                // Add to the Model Explorer
                TreeItem<String> classItem = getModelExplorerItemAt(x, y);
                if (classItem != null) {
                    classItem.getChildren().add(new TreeItem<>("+ " + attribute));
                }
            });
        }
    }

    private void addMethodToShape(double x, double y) {
        ClassShape shape = (ClassShape) findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Method");
            dialog.setHeaderText("Enter Method Name (e.g., methodName()):");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(method -> {
                if (!method.endsWith("()")) {
                    showErrorDialog("Invalid Method Format", "Method name must end with '()'.");
                    return;
                }

                // Add method to the shape
                shape.addMethod(method);

                // Update the diagram
                redrawCanvas();

                // Add to the Model Explorer
                TreeItem<String> classItem = getModelExplorerItemAt(x, y);
                if (classItem != null) {
                    classItem.getChildren().add(new TreeItem<>("+ " + method));
                }
            });
        }
    }

    private void renameShape(double x, double y) {
        ClassShape shape = (ClassShape) findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            TextInputDialog dialog = new TextInputDialog(shape.getName());
            dialog.setTitle("Rename Class");
            dialog.setHeaderText("Enter New Name for the Class:");
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(newName -> {
                // Rename the shape
                shape.setName(newName);

                // Update the diagram
                redrawCanvas();

                // Update the Model Explorer
                TreeItem<String> classItem = getModelExplorerItemAt(x, y);
                if (classItem != null) {
                    classItem.setValue(newName);
                }
            });
        }
    }

    private void deleteShape(double x, double y) {
        ClassShape shape = (ClassShape) findShapeAt(x, y); // Find the shape at (x, y)
        if (shape != null) {
            // Remove the shape from the canvas
            shapes.remove(shape);

            // Remove from the Model Explorer
            TreeItem<String> classItem = getModelExplorerItemAt(x, y);
            if (classItem != null) {
                modelExplorer.getRoot().getChildren().remove(classItem);
            }

            // Redraw the canvas
            redrawCanvas();
        }
    }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
