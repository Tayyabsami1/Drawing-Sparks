package org.example.my_project.BL;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.example.my_project.Models.Shape;

public class ModelExplorer {
    public TreeView<String> modelExplorer =new TreeView<>();
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
    public void updateModelExplorer(String shapeName) {
        TreeItem<String> shapeItem = new TreeItem<>(shapeName);
        modelExplorer.getRoot().getChildren().add(shapeItem);
    }

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

    public boolean modelExplorerContains(String target) {
        return findItemByValue(modelExplorer.getRoot(), target) != null;
    }

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

    public void printAllTreeItems() {
        TreeItem<String> root = modelExplorer.getRoot();
        if (root != null) {
            printTreeItem(root, 0);  // Start printing from the root node with an initial indentation level of 0
        } else {
            System.out.println("The tree is empty.");
        }
    }

    private void printTreeItem(TreeItem<String> item, int level) {
        // Indent each level for better readability
        String indentation = "  ".repeat(level);
        System.out.println(indentation + item.getValue());  // Print the current node

        // Recursively print children with increased indentation
        for (TreeItem<String> child : item.getChildren()) {
            printTreeItem(child, level + 1);
        }
    }

}
