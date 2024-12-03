package org.example.my_project.BL;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.example.my_project.Models.AssociationShape;
import org.example.my_project.Models.ClassShape;
import org.example.my_project.UI.ProjectController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaFxTestExtension1 implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown); // Initialize JavaFX once
        latch.await(); // Wait for initialization
    }
}

@ExtendWith(JavaFxTestExtension1.class)
public class ProjectControllerTest {

    private ProjectController projectController;
    private ClassShape classShape1;
    private ClassShape classShape2;

    @BeforeEach
    public void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            projectController = new ProjectController();
            Stage primaryStage = new Stage();
            projectController.start(primaryStage);

            classShape1 = new ClassShape(100, 100, "Class1");
            classShape2 = new ClassShape(200, 200, "Class2");


            projectController.Object.shapes.add(classShape1);
            projectController.Object.shapes.add(classShape2);

            projectController.redrawCanvas();
            latch.countDown();
        });
        latch.await(); // Wait for JavaFX thread to complete setup
    }

    @Test
    public void testStartAssociationConnection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println("List size before click: " + projectController.Object.getShapes().size());
        System.out.println("X:  " + classShape2.getX()+ " Y: "+classShape2.getY());
        System.out.println("Width:  " + classShape2.getWidth()+ " Height: "+classShape2.getHeight());
        System.out.println("Canvas Width:  " + projectController.canvas.getWidth()+ " Canvas Height: "+projectController.canvas.getHeight());
        Platform.runLater(() -> {
            projectController.startAssociationConnection(classShape1);

            // Get the exact coordinates in the canvas's local coordinate space
            Point2D canvasCoordinates = projectController.canvas.sceneToLocal(
                    classShape2.getX() + classShape2.getWidth() / 2, // Center X of the shape
                    classShape2.getY() + classShape2.getHeight() / 2 // Center Y of the shape
            );

//            double clickX = canvasCoordinates.getX(); // Correctly translated X
//            double clickY = canvasCoordinates.getY(); // Correctly translated Y

            double clickX=275;
            double clickY=245;
            System.out.println("Expected clickX: 275, Actual clickX: " + clickX);
            System.out.println("Expected clickY: 245, Actual clickY: " + clickY);

            javafx.scene.input.MouseEvent clickEvent = new javafx.scene.input.MouseEvent(
                    javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                    clickX, clickY, clickX, clickY, // Adjusted coordinates
                    MouseButton.PRIMARY,
                    1,
                    false, false, false, false,
                    true, false, false, false, false, false,
                    null
            );
            projectController.canvas.fireEvent(clickEvent); // Trigger the click event
            latch.countDown();
        });




        latch.await(); // Ensure Platform.runLater completes before assertions
        System.out.println("List size after click: " + projectController.Object.getShapes().size());

        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof AssociationShape &&
                        ((AssociationShape) shape).startShape == classShape1 &&
                        ((AssociationShape) shape).endShape == classShape2);

        assertTrue(associationExists, "Association should be created between Class1 and Class2");
    }



}


//    @Test
//    public void testAddClassShape() {
//        ClassShape classShape = new ClassShape(100, 100, "TestClass");
//        diagrams.shapes.add(classShape);
//
//        assertEquals(3, Diagrams.getShapes().size());
//        assertEquals("TestClass", Diagrams.getShapes().get(2).getName());
//        diagrams.shapes.remove(classShape);
//    }
//
//    @Test
//    public void testAddAttributeToShape() {
//        ClassShape classShape = new ClassShape(100, 100, "TestClass");
//        diagrams.addAttributeToShape(classShape, "attribute:String");
//        assertTrue(classShape.getAttributes().contains("attribute:String"));
//    }
//
//    @Test
//    public void testAddMethodToShape() {
//        ClassShape classShape = new ClassShape(100, 100, "TestClass");
//        diagrams.addMethodToShape(classShape, "testMethod");
//        assertTrue(classShape.getMethods().contains("testMethod"));
//    }
//
//    @Test
//    public void testRenameShape() {
//        ClassShape classShape = new ClassShape(100, 100, "OldName");
//        diagrams.shapes.add(classShape);
//
//        diagrams.renameShape(classShape, "NewName");
//        assertEquals("NewName", classShape.getName());
//        diagrams.shapes.remove(classShape);
//    }
//
//    @Test
//    public void testDeleteShape() {
//        ClassShape classShape = new ClassShape(100, 100, "TestClass");
//        diagrams.shapes.add(classShape);
//
//        diagrams.deleteShape(classShape);
//
//        assertEquals(2, Diagrams.getShapes().size());
//    }
//
//    @Test
//    public void testGenerateCode() {
//        ClassShape classShape = new ClassShape(100, 100, "TestClass");
//        classShape.addAttribute("attr:String");
//        classShape.addMethod("method");
//
//        Diagrams.getShapes().add(classShape);
//
//        String code = codeGenerator.generateCode();
//        assertTrue(code.contains("public class TestClass"));
//        assertTrue(code.contains("public String attr"));
//        assertTrue(code.contains("public void method"));
//    }
//
//    @Test
//    public void testModelExplorerUpdate() {
//        // Adding a shape to the diagram and updating the ModelExplorer
//        ClassShape classShape=new ClassShape(300, 300, "NewClass");
//        diagrams.shapes.add(classShape);
//        modelExplorer.updateModelExplorer("NewClass");
//
//        assertTrue(modelExplorer.modelExplorerContains("NewClass"));
//        diagrams.shapes.remove(classShape);
//    }



//    /**
//     * Utility method to wait for JavaFX updates to complete.
//     *
//     * @param milliseconds The time to wait in milliseconds.
//     */
//    private static void waitForFx(int milliseconds) {
//        CountDownLatch latch = new CountDownLatch(1);
//        Platform.runLater(() -> latch.countDown());
//        try {
//            latch.await();  // Wait for JavaFX thread to complete
//            Thread.sleep(milliseconds);  // Pause for observation
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }



//    @Test
//    public void testStartAggregationConnection() {
//        projectController.startAggregationConnection(classShape1);
//
//        AggregationShape aggregation = new AggregationShape(classShape1, classShape2);
//
//        // Check if aggregation shape is added to the diagram
//        assertTrue(projectController.Object.getShapes().contains(aggregation));
//
//        // Check if Model Explorer is updated
//        assertTrue(projectController.Model.modelExplorerContains("- Aggregation (Class1 to Class2)"));
//    }
//
//    @Test
//    public void testStartCompositionConnection() {
//        projectController.startCompositionConnection(classShape1);
//
//        CompositionShape composition = new CompositionShape(classShape1, classShape2);
//
//
//        assertTrue(projectController.Object.getShapes().contains(composition));
//        assertTrue(projectController.Model.modelExplorerContains("- Composition (Class1 to Class2)"));
//    }
//
//    @Test
//    public void testStartGeneralizationConnection() {
//        projectController.startGeneralizationConnection(classShape1);
//
//        GeneralizationShape generalization = new GeneralizationShape(classShape1, classShape2);
//
//
//        assertTrue(projectController.Object.getShapes().contains(generalization));
//        assertTrue(projectController.Model.modelExplorerContains("- Generalization (Class1 to Class2)"));
//    }
//
//    @Test
//    public void testStartDirectAssociationConnection() {
//        projectController.startDirectAssociationConnection(classShape1);
//
//        DirectAssociationLineShape directAssociation = new DirectAssociationLineShape(classShape1, classShape2);
//
//        assertTrue(projectController.Object.getShapes().contains(directAssociation));
//        assertTrue(projectController.Model.modelExplorerContains("- Direct Association (Class1 to Class2)"));
//    }
//
//    @Test
//    public void testStartDependencyConnection() {
//        projectController.startDependencyConnection(classShape1, "DependencyType");
//
//        Shape endShape = classShape2;
//        DependencyLineShape dependency = new DependencyLineShape(classShape1, endShape, "");
//
//
//        assertTrue(projectController.Object.getShapes().contains(dependency));
//        assertTrue(projectController.Model.modelExplorerContains("- Dependency (Class1 to Class2)"));
//    }




