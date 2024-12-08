package org.example.my_project.BL;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.example.my_project.Models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ModelExplorer class is responsible for managing and updating the visual representation
 * of the UML model in a TreeView. It allows adding, removing, and updating shapes and their
 * connections in the model explorer.
 */
public class ModelExplorer {
    public TreeView<String> modelExplorer =new TreeView<>();

    /**
     * Checks if the given name represents a parent type (e.g., Class, Interface, Actor, Use Case).
     *
     * @param name The name of the shape.
     * @return true if the name is a parent type, false otherwise.
     */
    private boolean isParentType(String name) {
        return name.contains("Class") || name.contains("Interface") || name.contains("User") || name.contains("UseCase");
    }

    /**
     * Updates the model explorer when the shapes are loaded from the diagram.
     * This method organizes shapes into a tree structure based on their type.
     *
     * @param shapes The list of shapes to load into the model explorer.
     */
    public void modelUpdateOnLoad(List<Shape> shapes) {
        TreeItem<String> root = new TreeItem<>("Model Explorer");
        modelExplorer.setRoot(root);

        for (Shape shape : shapes) {
            String name = shape.getName();
            System.out.println(name);
            if (isParentType(name)) {
                TreeItem<String> parentNode = new TreeItem<>(name);
                if (shape instanceof ClassShape) {
                    List<String> attributes = new ArrayList<>();
                    List<String> methods = new ArrayList<>();
                    attributes = ((ClassShape) shape).getAttributes();
                    methods = ((ClassShape) shape).getMethods();
                    for (String attribute : attributes) {
                        if (parentNode.getChildren().stream().noneMatch(child -> child.getValue().equals(attribute))) {
                            parentNode.getChildren().add(new TreeItem<>(attribute));
                        }
                    }

                    for (String method : methods) {
                        if (parentNode.getChildren().stream().noneMatch(child -> child.getValue().equals(method))) {
                            parentNode.getChildren().add(new TreeItem<>(method));
                        }
                    }


                }

                modelExplorer.getRoot().getChildren().add(parentNode);
            } else {
                String startClassName = null;
                List<String> attributes = new ArrayList<>();
                List<String> methods = new ArrayList<>();

                if (shape instanceof AggregationShape) {
                    startClassName = ((AggregationShape) shape).startClass.getName();
                    attributes = ((AggregationShape) shape).startClass.getAttributes();
                    methods = ((AggregationShape) shape).startClass.getMethods();
                } else if (shape instanceof CompositionShape) {
                    startClassName = ((CompositionShape) shape).startClass.getName();
                    attributes = ((CompositionShape) shape).startClass.getAttributes();
                    methods = ((CompositionShape) shape).startClass.getMethods();
                } else if (shape instanceof DirectAssociationLineShape) {
                    startClassName = ((DirectAssociationLineShape) shape).startClass.getName();
                    attributes = ((DirectAssociationLineShape) shape).startClass.getAttributes();
                    methods = ((DirectAssociationLineShape) shape).startClass.getMethods();
                } else if (shape instanceof GeneralizationShape) {
                    startClassName = ((GeneralizationShape) shape).startClass.getName();
                    attributes = ((GeneralizationShape) shape).startClass.getAttributes();
                    methods = ((GeneralizationShape) shape).startClass.getMethods();
                } else if (shape instanceof AssociationShape) {
                    startClassName = ((AssociationShape) shape).startShape.getName();
                } else if (shape instanceof DependencyLineShape) {
                    startClassName = ((DependencyLineShape) shape).startShape.getName();
                }

                if (startClassName != null) {
                    // Traverse the model explorer to find the parent node matching the start class name
                    TreeItem<String> startClassItem = null;
                    for (TreeItem<String> item : modelExplorer.getRoot().getChildren()) {
                        if (item.getValue().equals(startClassName)) {
                            startClassItem = item;
                            break;
                        }
                    }

                    // Add the current shape as a child of the found parent node
                    if (startClassItem != null) {
                        TreeItem<String> connectionItem = new TreeItem<>(name);
                        startClassItem.getChildren().add(connectionItem);

                        // Add attributes and methods if they don't already exist as children
                        for (String attribute : attributes) {
                            if (startClassItem.getChildren().stream().noneMatch(child -> child.getValue().equals(attribute))) {
                                startClassItem.getChildren().add(new TreeItem<>(attribute));
                            }
                        }

                        for (String method : methods) {
                            if (startClassItem.getChildren().stream().noneMatch(child -> child.getValue().equals(method))) {
                                startClassItem.getChildren().add(new TreeItem<>(method));
                            }
                        }

                        // Refresh the TreeView to display the new child
                        modelExplorer.refresh();
                    }
                }
            }

        }
    }
    /**
     * Adds a connection between two shapes in the model explorer.
     *
     * @param startClassName The name of the starting class.
     * @param connectionName The name of the connection.
     */
    public void addConnectionToModelExplorer(String startClassName, String connectionName) {
        // Find the TreeItem for the start class
        TreeItem<String> startClassItem = null;
        for (TreeItem<String> item : modelExplorer.getRoot().getChildren()) {
            if (item.getValue().equals(startClassName)) {
                startClassItem = item;
                break;
            }
        }

        // If start class is found, add the association as its child
        if (startClassItem != null) {
            TreeItem<String> connectionItem = new TreeItem<>(connectionName);
            startClassItem.getChildren().add(connectionItem);
            // Refresh the TreeView to display the new association
            modelExplorer.refresh();
        }
    }

    /**
     * Updates the model explorer by adding a new shape.
     *
     * @param shapeName The name of the new shape.
     */
    public void updateModelExplorer(String shapeName) {
        TreeItem<String> shapeItem = new TreeItem<>(shapeName);
        modelExplorer.getRoot().getChildren().add(shapeItem);
    }

    /**
     * Updates the name of a shape in the model explorer.
     *
     * @param shape The shape to be renamed.
     * @param newName The new name for the shape.
     */
    public void updateModelExplorerItem(Shape shape, String newName) {
        String oldName = shape.getName();

        // Update the TreeItem value for the shape itself
        for (TreeItem<String> item : modelExplorer.getRoot().getChildren()) {
            if (item.getValue().equals(oldName)) {
                item.setValue(newName); // Update the class name
                break;
            }
        }

        // Update all connections that reference the old shape name
        for (TreeItem<String> classItem : modelExplorer.getRoot().getChildren()) {
            for (TreeItem<String> connectionItem : classItem.getChildren()) {
                if (connectionItem.getValue().contains(oldName)) {
                    String updatedConnection = connectionItem.getValue().replace(oldName, newName);
                    connectionItem.setValue(updatedConnection); // Update the connection text
                }
            }
        }

        // Force refresh of the TreeView to update the GUI
        modelExplorer.refresh();
    }

    /**
     * Checks if a shape is already present in the model explorer.
     *
     * @param target The name of the shape to check.
     * @return true if the shape is found, false otherwise.
     */
    public boolean modelExplorerContains(String target) {
        return findItemByValue(modelExplorer.getRoot(), target) != null;
    }

    /**
     * Searches for a tree item by its value.
     *
     * @param currentNode The current node in the tree to start searching from.
     * @param targetValue The value of the item to search for.
     * @return The TreeItem if found, null otherwise.
     */
    private TreeItem<String> findItemByValue(TreeItem<String> currentNode, String targetValue) {
        if (currentNode.getValue().equals(targetValue)) {
            return currentNode;
        }
        for (TreeItem<String> child : currentNode.getChildren()) {
            TreeItem<String> result = findItemByValue(child, targetValue);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Removes a shape from the model explorer.
     *
     * @param deletedShape The shape to remove.
     */
    public void removeModelExplorerItem(Shape deletedShape) {
        String deletedShapeName = deletedShape.getName();

        // Remove the class itself from the Model Explorer
        modelExplorer.getRoot().getChildren().removeIf(item -> item.getValue().equals(deletedShapeName));

        // Iterate through the remaining class items and remove any associations or connections
        for (TreeItem<String> classItem : modelExplorer.getRoot().getChildren()) {
            classItem.getChildren().removeIf(connectionItem -> {
                String connection = connectionItem.getValue();

                // Check if the connection references the deleted shape
                return connection.contains(deletedShapeName);
            });
        }

        // Refresh the TreeView to update the GUI
        modelExplorer.refresh();
    }

    /**
     * Finds the TreeItem in the model explorer corresponding to a shape at the given coordinates.
     *
     * @param x The x-coordinate of the shape.
     * @param y The y-coordinate of the shape.
     * @return The TreeItem corresponding to the shape, or null if not found.
     */
    public TreeItem<String> getModelExplorerItemAt(double x, double y) {
        for (Shape shape : Diagrams.getShapes()) {
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

    /**
     * Prints all the tree items in the model explorer starting from the root.
     * This method provides a hierarchical view of the items in the TreeView.
     */
    public void printAllTreeItems() {
        TreeItem<String> root = modelExplorer.getRoot();
        if (root != null) {
            printTreeItem(root, 0);  // Start printing from the root node with an initial indentation level of 0
        } else {
            System.out.println("The tree is empty.");
        }
    }
    /**
     * Recursively prints a TreeItem and its children with indentation for readability.
     *
     * @param item  The TreeItem to print.
     * @param level The current level of indentation.
     */
    private void printTreeItem(TreeItem<String> item, int level) {
        // Indent each level for better readability
        String indentation = "  ".repeat(level);
        System.out.println(indentation + item.getValue());  // Print the current node

        // Recursively print children with increased indentation
        for (TreeItem<String> child : item.getChildren()) {
            printTreeItem(child, level + 1);
        }
    }
    /**
     * This method provides the startClass of generalization corresponding to endClass
     *
     * @param endClass  The endClass
     */
    public String getStartClassForEndClass(String endClass) {
        // Traverse the model explorer tree and look for the matching generalization pattern
        TreeItem<String> root = modelExplorer.getRoot(); // Root of the Model Explorer
        return findStartClassForEndClass(root, endClass);
    }
    /**
     * This method provides the startClass of generalization corresponding to endClass
     *
     *  @param currentNode  The CurrentNode of Tree item.
     * @param endClass  The endClass
     */

    private String findStartClassForEndClass(TreeItem<String> currentNode, String endClass) {
        if (currentNode == null) {
            return null;
        }

        String value = currentNode.getValue();

        // Check if the current node contains a generalization string with the provided endClass
        if (value != null && value.matches("- Generalization \\(.* to " + endClass + "\\)")) {
            // Extract the start class name from the pattern
            String[] parts = value.split("\\(|\\)| to ");
            if (parts.length >= 3) {
                return parts[1].trim(); // Return the start class
            }
        }

        // Recursively check the children of the current node
        for (TreeItem<String> child : currentNode.getChildren()) {
            String result = findStartClassForEndClass(child, endClass);
            if (result != null) {
                return result; // If found in a child, return immediately
            }
        }

        return null; // Not found
    }



    /**
 * Deletes the given child of the given parent node from model explorer .
 *
 * @param parent the parent node whose child to be deleted
 * @param child the child node to delete
 */
    public void deleteChild(String parent, String child) {
        for (TreeItem<String> item : modelExplorer.getRoot().getChildren()) {
            if (item.getValue().equals(parent)) {
                item.getChildren().removeIf(childItem -> childItem.getValue().equals(child));
                break;
            }
        }
        modelExplorer.refresh();
    }

    /**
     * Updates the name of a child node under a given parent in the model explorer.
     *
     * @param parent The name of the parent node.
     * @param oldChild The current name of the child node.
     * @param newChild The new name for the child node.
     */
    public void updateChild(String parent, String oldChild, String newChild) {
        // Find the parent item
        TreeItem<String> parentItem = findItemByValue(modelExplorer.getRoot(), parent);
        if (parentItem != null) {
            // Find the old child item
            for (TreeItem<String> childItem : parentItem.getChildren()) {
                if (childItem.getValue().equals(oldChild)) {
                    // Update the child item value
                    childItem.setValue(newChild);
                    break;
                }
            }
            modelExplorer.refresh();

        }
    }

}
