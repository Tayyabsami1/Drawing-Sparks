package org.example.my_project.BL;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.example.my_project.Models.*;
import org.example.my_project.UI.ProjectController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            projectController.Model.updateModelExplorer(classShape1.getName());
            projectController.Model.updateModelExplorer(classShape2.getName());

            projectController.Object.shapes.add(classShape1);
            projectController.Object.shapes.add(classShape2);

            projectController.redrawCanvas();
            latch.countDown();
        });
        latch.await(); // Wait for JavaFX thread to complete setup
    }
    @AfterEach
    public void tearDown() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            projectController.Object.shapes.clear(); // Clear shapes list
//            projectController.Model.clearModelExplorer(); // Clear Model Explorer if needed
            projectController.canvas.getGraphicsContext2D().clearRect(0, 0, projectController.canvas.getWidth(), projectController.canvas.getHeight());
            latch.countDown();
        });
        try {
            latch.await(); // Wait for JavaFX thread to complete teardown
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddClassShape() {
        projectController.drawClassShape(600,800,"Class");

        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof ClassShape &&
                        shape.getX()==600.0 && shape.getY()==800.0);


        assertTrue(associationExists);
        projectController.Object.getShapes().remove(2);
    }

    @Test
    public void testAddUserShape() {
        projectController.drawUserShape(600,800);

        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof UserShape &&
                        shape.getName().equals("User 1"));


        assertTrue(associationExists);
        projectController.Object.getShapes().remove(2);
    }
    @Test
    public void testAddUseCaseShape() {
        projectController.drawUseCaseShape(600,800);

        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof UseCaseShape &&
                        shape.getName().equals("UseCase 1"));


        assertTrue(associationExists);
        projectController.Object.getShapes().remove(2);
    }
    @Test
    public void testStartAssociationConnection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            projectController.startAssociationConnection(classShape1, true);
            double clickX = 275;
            double clickY = 245;
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
        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof AssociationShape &&
                        ((AssociationShape) shape).startShape == classShape1 &&
                        ((AssociationShape) shape).endShape == classShape2);

        assertTrue(associationExists);
        assertTrue(projectController.Model.modelExplorerContains("- Association (Class1 to Class2)"));
        projectController.Object.getShapes().remove(2);
    }


    @Test
    public void testStartAggregationConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            projectController.startAggregationConnection(classShape1, true);
            double clickX = 275;
            double clickY = 245;
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
        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof AggregationShape &&
                        ((AggregationShape) shape).startClass == classShape1 &&
                        ((AggregationShape) shape).endClass == classShape2);

        assertTrue(associationExists);
        assertTrue(projectController.Model.modelExplorerContains("- Aggregation (Class1 to Class2)"));
        projectController.Object.getShapes().remove(2);
    }

    @Test
    public void testStartCompositionConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            projectController.startCompositionConnection(classShape1, true);
            double clickX = 275;
            double clickY = 245;
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
        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof CompositionShape &&
                        ((CompositionShape) shape).startClass == classShape1 &&
                        ((CompositionShape) shape).endClass == classShape2);

        assertTrue(associationExists);
        assertTrue(projectController.Model.modelExplorerContains("- Composition (Class1 to Class2)"));
        projectController.Object.getShapes().remove(2);
    }

    @Test
    public void testStartGeneralizationConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            projectController.startGeneralizationConnection(classShape1, true);
            double clickX = 275;
            double clickY = 245;
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
        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof GeneralizationShape &&
                        ((GeneralizationShape) shape).startClass == classShape1 &&
                        ((GeneralizationShape) shape).endClass == classShape2);

        assertTrue(associationExists);
        assertTrue(projectController.Model.modelExplorerContains("- Generalization (Class1 to Class2)"));
        projectController.Object.getShapes().remove(2);
    }

    @Test
    public void testStartDirectAssociationConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            projectController.startDirectAssociationConnection(classShape1, true);
            double clickX = 275;
            double clickY = 245;
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
        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof DirectAssociationLineShape &&
                        ((DirectAssociationLineShape) shape).startClass == classShape1 &&
                        ((DirectAssociationLineShape) shape).endClass == classShape2);

        assertTrue(associationExists);
        assertTrue(projectController.Model.modelExplorerContains("- DirectAssociation (Class1 to Class2)"));
        projectController.Object.getShapes().remove(2);
    }

    @Test
    public void testStartDependencyConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            projectController.startDependencyConnection(classShape1, "",true);
            double clickX = 275;
            double clickY = 245;
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
        // Assert the association was created
        boolean associationExists = projectController.Object.getShapes().stream()
                .anyMatch(shape -> shape instanceof DependencyLineShape &&
                        ((DependencyLineShape) shape).startShape == classShape1 &&
                        ((DependencyLineShape) shape).endShape == classShape2);

        assertTrue(associationExists);
        assertTrue(projectController.Model.modelExplorerContains("- Dependency (Class1 to Class2)"));
        projectController.Object.getShapes().remove(2);
    }
}




