package org.example.my_project.BL;
import javafx.scene.control.TreeItem;
import org.example.my_project.Models.*;

import org.example.my_project.UI.ProjectController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
class JavaFxTestExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown()); // Initialize JavaFX once
        latch.await(); // Wait for initialization
    }
}
@ExtendWith(JavaFxTestExtension.class)
public class DiagramTest {

    private Diagrams diagrams;
    private ModelExplorer modelExplorer;
    private CodeGenerator codeGenerator;


    @BeforeEach
    public void setUp() {
        diagrams = new Diagrams();
        modelExplorer = new ModelExplorer();
        codeGenerator = new CodeGenerator();

    }

    @Test
    public void testAddClassShape() {
        ClassShape classShape = new ClassShape(100, 100, "TestClass");
        diagrams.shapes.add(classShape);

        assertEquals(1, Diagrams.getShapes().size());
        assertEquals("TestClass", Diagrams.getShapes().get(0).getName());
        diagrams.shapes.remove(classShape);
    }

    @Test
    public void testAddAttributeToShape() {
        ClassShape classShape = new ClassShape(100, 100, "TestClass");
        diagrams.addAttributeToShape(classShape, "attribute:String");
        assertTrue(classShape.getAttributes().contains("attribute:String"));
        diagrams.shapes.remove(classShape);
    }

    @Test
    public void testAddMethodToShape() {
        ClassShape classShape = new ClassShape(100, 100, "TestClass");
        diagrams.addMethodToShape(classShape, "testMethod");
        assertTrue(classShape.getMethods().contains("testMethod"));
        diagrams.shapes.remove(classShape);
    }

    @Test
    public void testRenameShape() {
        ClassShape classShape = new ClassShape(100, 100, "OldName");
        diagrams.shapes.add(classShape);

        diagrams.renameShape(classShape, "NewName");

        assertEquals("NewName", classShape.getName());
        diagrams.shapes.remove(classShape);
    }

    @Test
    public void testDeleteShape() {
        ClassShape classShape = new ClassShape(100, 100, "TestClass");
        diagrams.shapes.add(classShape);

        diagrams.deleteShape(classShape);

        assertEquals(0, Diagrams.getShapes().size());
    }

    @Test
    public void testGenerateCode() {
        ClassShape classShape = new ClassShape(100, 100, "TestClass");
        classShape.addAttribute("+attr:String");
        classShape.addMethod("+method()");

        Diagrams.getShapes().add(classShape);

        String code = codeGenerator.generateCode();
        assertTrue(code.contains("public class TestClass"));
        assertTrue(code.contains("public String attr"));
        assertTrue(code.contains("public void method"));
        diagrams.shapes.remove(classShape);
    }

    @Test
    public void testModelExplorerUpdate() {

        TreeItem<String> root = new TreeItem<>("Root");
        modelExplorer.modelExplorer.setRoot(root);

        modelExplorer.updateModelExplorer("TestClass");

        // Assert that the root is not null
        assertNotNull(modelExplorer.modelExplorer.getRoot());

        // Assert that the new shape name has been added as a child of the root
        assertTrue(
                modelExplorer.modelExplorer.getRoot().getChildren().stream()
                        .anyMatch(item -> item.getValue().equals("TestClass")));

    }

}
