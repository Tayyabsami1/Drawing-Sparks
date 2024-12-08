package org.example.my_project.BL;

import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import org.example.my_project.Models.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a collection of diagram shapes and contains methods to manipulate,
 * add attributes, methods, and delete shapes from the diagram.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class Diagrams implements Serializable {

    // List of all shapes on the canvas
    public static List<Shape> shapes = new ArrayList<>();
    private static final long serialVersionUID = 1L; // Added for compatibility

    /**
     * Gets the list of all shapes in the diagram.
     *
     * @return A list of Shape objects representing all the shapes.
     */
    public static List<Shape> getShapes() {
        return shapes;
    }

    /**
     * Finds a shape at the given coordinates (x, y).
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return The Shape at the given coordinates or null if no shape is found.
     */
    public Shape findShapeAt(double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
                return shape;
            }
        }
        return null; // No shape found at the position
    }

    /**
     * Adds an attribute to a given ClassShape.
     *
     * @param shape The ClassShape to which the attribute will be added.
     * @param attribute The attribute to add to the shape.
     */
    public void addAttributeToShape(ClassShape shape, String attribute) {

                shape.addAttribute(attribute);
    }

    /**
     * Adds a method to a given ClassShape. If the shape is an interface, the method is
     * propagated to its generalizations.
     *
     * @param shape The ClassShape to which the method will be added.
     * @param method The method to add to the shape.
     */
    public void addMethodToShape(ClassShape shape, String method) {
        shape.addMethod(method);
        if (shape.isInterface()) {
            for (Shape connectedShape : shapes) {
                if (connectedShape instanceof GeneralizationShape generalization) {
                    if (generalization.endClass == shape) {
                        if (!generalization.startClass.getOverridenMethods().contains(method)) {
                            generalization.startClass.addOverRidenMethods(method);
                        }
                    }
                }
            }
        }
    }
    /**
     * Renames a shape and updates its connected shapes (associations, generalizations, etc.) accordingly.
     *
     * @param shape The shape to rename.
     * @param newName The new name for the shape.
     */
    public void renameShape(Shape shape,String newName) {

                if (shape instanceof ClassShape renamedClass) {
                    String oldName=shape.getName();
                    String oldAttribute=renamedClass.generateAttribute();
                    shape.setName(newName);
                    for (Shape connectedShape : shapes) {
                        if (connectedShape instanceof GeneralizationShape generalization) {
                            if (generalization.endClass == renamedClass && !renamedClass.isInterface()) {
                                generalization.startClass.setExtendingClass(newName);
                            } else if (generalization.endClass == renamedClass && renamedClass.isInterface()) {
                                List<String> interfaces = generalization.startClass.getImplementingInterface();
                                if (interfaces.contains(oldName)) {
                                    interfaces.remove(oldName);
                                    interfaces.add(newName);
                                }
                            }
                        }
                        else if (connectedShape instanceof CompositionShape compositionShape)
                        {
                            if(compositionShape.endClass==renamedClass)
                            {
                                if(compositionShape.startClass.getAssociations().contains(oldAttribute))
                                {
                                    compositionShape.startClass.getAssociations().remove(oldAttribute);
                                    compositionShape.startClass.addAssociation(compositionShape.endClass.generateAttribute());
                                }
                            }
                        }
                        else if(connectedShape instanceof AggregationShape aggregationShape)
                        {
                            if(aggregationShape.endClass==renamedClass)
                            {
                                if(aggregationShape.startClass.getAssociations().contains(oldAttribute))
                                {
                                    aggregationShape.startClass.getAssociations().remove(oldAttribute);
                                    aggregationShape.startClass.addAssociation(aggregationShape.endClass.generateAttribute());
                                }
                            }
                        }

                    }
                }
                else
                {
                    shape.setName(newName);
                }


    }

    /**
     * Deletes a shape from the diagram, as well as any associated shapes (e.g., associations,
     * aggregations, compositions, generalizations).
     *
     * @param shape The shape to delete.
     * @return The deleted shape.
     */
    public Shape deleteShape(Shape shape) {

        if (shape != null) {
            // Remove the class shape from the shapes list
            shapes.remove(shape);

            // Remove any connected shapes (associations, generalizations, etc.)
            shapes.removeIf(connectedShape -> {
                if (connectedShape instanceof AssociationShape) {
                    AssociationShape association = (AssociationShape) connectedShape;
                    return association.startShape == shape || association.endShape == shape;
                } else if (connectedShape instanceof AggregationShape) {
                    AggregationShape aggregation = (AggregationShape) connectedShape;
                    if(aggregation.endClass==shape)
                    {
                        for (Shape startShape : shapes) {
                            if(startShape instanceof ClassShape startClass && startClass==aggregation.startClass)
                            {
                                startClass.getAssociations().remove(aggregation.endClass.generateAttribute());
                            }
                        }
                    }
                    return aggregation.startClass == shape || aggregation.endClass == shape;
                } else if (connectedShape instanceof CompositionShape) {
                    CompositionShape composition = (CompositionShape) connectedShape;
                    if(composition.endClass==shape)
                    {
                        for (Shape startShape : shapes) {
                            if(startShape instanceof ClassShape startClass && startClass==composition.startClass)
                            {
                                startClass.getAssociations().remove(composition.endClass.generateAttribute());
                            }
                        }
                    }
                    return composition.startClass == shape || composition.endClass == shape;
                } else if (connectedShape instanceof GeneralizationShape) {
                    GeneralizationShape generalization = (GeneralizationShape) connectedShape;
                    if(generalization.endClass==shape)
                    {
                        for (Shape startShape : shapes) {
                            if (generalization.startClass == startShape && startShape instanceof ClassShape) {
                                ClassShape startClass = (ClassShape) startShape;

                                // Remove the endClass name from the list of implementing interfaces
                                String endClassName = generalization.endClass.getName();
                                if(generalization.endClass.isInterface()) {
                                    if (startClass.getImplementingInterface().contains(endClassName))
                                    {
                                        startClass.getImplementingInterface().remove(endClassName);
                                        System.out.println("Removed interface " + endClassName + " from class " + startClass.getName());
                                    }
                                    List<String> overRidenMethodsTobeRemoved=generalization.endClass.getMethods();
                                    for(int i=0;i<overRidenMethodsTobeRemoved.size();i++)
                                    {
                                        if (startClass.getOverridenMethods().contains(overRidenMethodsTobeRemoved.get(i))) {
                                            startClass.getOverridenMethods().remove(overRidenMethodsTobeRemoved.get(i));
                                        }
                                    }
                                }
                                else
                                {
                                    if(startClass.getExtendingClass().equals(endClassName))
                                    {
                                        startClass.setExtendingClass(null);
                                    }
                                }
                            }
                        }

                    }
                    return generalization.startClass == shape || generalization.endClass == shape;
                } else if (connectedShape instanceof DirectAssociationLineShape) {
                    DirectAssociationLineShape directAssociation = (DirectAssociationLineShape) connectedShape;
                    return directAssociation.startClass == shape || directAssociation.endClass == shape;
                } else if (connectedShape instanceof DependencyLineShape) {
                    DependencyLineShape dependency = (DependencyLineShape) connectedShape;
                    return dependency.startShape == shape || dependency.endShape == shape;
                }
                return false;
            });


        }
        return shape;
    }

    /**
     * Custom serialization method to handle the saving of shapes.
     *
     * @param oos The ObjectOutputStream used for saving the object.
     * @throws IOException If an I/O error occurs during serialization.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(shapes);
    }

    /**
     * Custom deserialization method to handle the loading of shapes.
     *
     * @param ois The ObjectInputStream used for loading the object.
     * @throws IOException If an I/O error occurs during deserialization.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-static fields
        shapes = (List<Shape>) ois.readObject(); // Deserialize static field
    }
}
