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

public class Diagrams implements Serializable {

    public static List<Shape> shapes = new ArrayList<>(); // List of all shapes on the canvas
    private static final long serialVersionUID = 1L; // Added for compatibility
    public static List<Shape> getShapes(){
        return shapes;
    }
//    public Shape findShapeAt(double x, double y) {
//        for (Shape shape : shapes) {
//            if (shape.contains(x, y)) {
//                return shape;
//            }
//        }
//        return null; // No shape found at the position
//    }
public Shape findShapeAt(double x, double y) {
    for (Shape shape : shapes) {
        System.out.println("Checking shape: " + shape.getName() +
                " Bounds: [x=" + shape.getX() + ", y=" + shape.getY() +
                ", width=" + shape.getWidth() + ", height=" + shape.getHeight() + "]");

        if (shape.contains(x, y)) {
            System.out.println("Shape found at (" + x + ", " + y + "): " + shape.getName());
            return shape;
        }
    }
    System.out.println("No shape found at (" + x + ", " + y + ")");
    return null; // No shape found at the position
}

    public void addAttributeToShape(ClassShape shape, String attribute) {

                shape.addAttribute(attribute);
    }

    public void addMethodToShape(ClassShape shape,String method) {

                // Add method to the shape
                shape.addMethod(method);
                if(shape.isInterface())
                {
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

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(shapes);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Deserialize non-static fields
        shapes = (List<Shape>) ois.readObject(); // Deserialize static field
    }
}
