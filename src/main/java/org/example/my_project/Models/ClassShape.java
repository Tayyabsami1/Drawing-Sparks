package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The {@code ClassShape} class represents a shape used to depict a class or interface in a UML-like diagram.
 * It extends the abstract {@code Shape} class and provides additional functionalities for managing
 * attributes, methods, associations, and inheritance/implementation relationships.
 *
 * <p>This class implements {@code Serializable} to support object serialization.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @version 1.0
 * @since 2024-12-05
 */
public class ClassShape extends Shape implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * A list of attributes associated with the class or interface.
     */
    private List<String> attributes = new ArrayList<>();



    /**
     * A list of methods associated with the class or interface.
     */
    private List<String> methods = new ArrayList<>();

    /**
     * The total height of the class shape, including its name, attributes, and methods sections.
     */
    private double totalHeight;

    /**
     * The width of the class shape.
     */
    private double width;

    /**
     * A list of interfaces implemented by the class.
     */
    private List<String> implementingInterface = new ArrayList<>();

    /**
     * A list of methods overridden by the class.
     */
    public List<String> overridenMethods = new ArrayList<>();

    /**
     * The class extended by this class, if any.
     */
    private String extendingClass = null;

    /**
     * A list of associations (relationships) involving this class.
     */
    private List<String> associations = new ArrayList<>();

    /**
     * The index of the currently selected attribute.
     */
    public int selectedAttributeIndex = -1;

    /**
     * The index of the currently selected method.
     */
    public int selectedMethodIndex = -1;

    /**
     * Indicates whether this shape represents an interface.
     */
    private Boolean isInterface = false;

    /**
     * Counters for tracking the number of classes and interfaces.
     */
    public static int count = 0;
    public static int count1 = 0;

    /**
     * Constructs a new {@code ClassShape} with the specified position and name.
     *
     * @param x    The x-coordinate of the class shape.
     * @param y    The y-coordinate of the class shape.
     * @param name The name of the class or interface.
     */
    public ClassShape(double x, double y, String name) {
        super(x, y, name);
        if(!name.contains("Interface")) {
            count++;
        }
        else{
            count1++;
        }

    }

    /**
     * Adds an attribute to the class shape.
     *
     * @param attribute The attribute to add.
     */
    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }
    /**
     * Returns the x-coordinate of the top-left corner of the class.
     *
     * @return The x-coordinate of the class' position.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the top-left corner of the class.
     *
     * @return The y-coordinate of the class' position.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width of the class.
     *
     * @return The width of the class.
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * Returns the total height of the class, including the header, attributes, and methods sections.
     *
     * @return The total height of the class.
     */
    @Override
    public double getHeight() {
        return totalHeight;
    }


    /**
     * Adds a method to the class shape.
     *
     * @param method The method to add.
     */
    public void addMethod(String method) {
        methods.add(method);
    }

    /**
     * Returns whether the class shape represents an interface.
     *
     * @return {@code true} if it is an interface, {@code false} otherwise.
     */
    public boolean isInterface() {
        return isInterface;
    }

    /**
     * Generates a string representation of an attribute in the format: {@code <name>Obj:<name>}.
     *
     * @return The generated attribute string.
     */
    public String generateAttribute() {
        return "-"+this.name + "Obj:" + this.name;
    }

    /**
     * Adds an implemented interface to the class shape.
     *
     * @param interfaceName The name of the interface.
     */
    public void addImplementingInterface(String interfaceName) {
        this.implementingInterface.add(interfaceName);
    }

    /**
     * Returns the list of implemented interfaces.
     *
     * @return A list of interface names.
     */
    public List<String> getImplementingInterface() {
        return implementingInterface;
    }

    /**
     * Sets the class that this class shape extends.
     *
     * @param className The name of the parent class.
     */
    public void setExtendingClass(String className) {
        this.extendingClass = className;
    }

    /**
     * Returns the name of the class that this class shape extends.
     *
     * @return The name of the parent class.
     */
    public String getExtendingClass() {
        return extendingClass;
    }

    /**
     * Adds an overridden method to the class shape.
     *
     * @param method The method to add.
     */
    public void addOverRidenMethods(String method) {
        this.overridenMethods.add(method);
    }

    /**
     * Returns the list of overridden methods.
     *
     * @return A list of overridden methods.
     */
    public List<String> getOverridenMethods() {
        return overridenMethods;
    }

    /**
     * Draws the class shape on the provided {@code GraphicsContext}.
     *
     * @param gc The {@code GraphicsContext} to draw on.
     */
    @Override
    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        // Set the dimensions for the class shape
        width = 150; // Fixed width
        double headerHeight;
        if(getName().contains("Interface") || isInterface){
            isInterface=true;
            headerHeight = 50;
        }
        else {
            headerHeight = 30; // Height for class name
        }
        double attributesHeight = Math.max(30, attributes.size() * 22); // Minimum height for attributes section
        double methodsHeight = Math.max(30, methods.size() * 22); // Minimum height for methods section
        totalHeight = headerHeight + attributesHeight + methodsHeight;

        // Draw the outer rectangle
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, totalHeight);

        // Draw the class name section
        gc.strokeLine(x, y + headerHeight, x + width, y + headerHeight);
        if(getName().contains("Interface")|| isInterface){
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
            gc.fillText(attribute, x + 10, attrY);
            attrY += 20;
        }

        // Draw the methods section
        double methodsStartY = attrStartY + attributesHeight;
        double methodY = methodsStartY + 20;
        for (int i = 0; i < methods.size(); i++) {
            String method = methods.get(i);
//            if (i == selectedMethodIndex) { // Highlight the selected method
//                gc.setFill(Color.LIGHTBLUE); // Change color for selected method
//                gc.fillRect(x + 5, methodY - 15, width - 10, 20); // Draw rectangle behind text
//                gc.setFill(Color.BLACK); // Reset color for text
//            }
            gc.fillText(method, x + 10, methodY);
            methodY += 20;
        }
    }

    /**
     * Returns the list of attributes in the class shape.
     *
     * @return A list of attributes.
     */
    public List<String> getAttributes() {
        return this.attributes;
    }

    /**
     * Returns the list of methods in the class shape.
     *
     * @return A list of methods.
     */
    public List<String> getMethods() {
        return methods;
    }

    /**
     * Returns the list of associations for the class shape.
     *
     * @return A list of associations.
     */
    public List<String> getAssociations() {
        return associations;
    }

    /**
     * Adds an association to the class shape.
     *
     * @param association The association to add.
     */
    public void addAssociation(String association) {
        if(!associations.contains(association))
        {
            associations.add(association);
        }
    }

    /**
     * Edits the currently selected attribute.
     *
     * @param newAttribute The new value for the attribute.
     */
    public void editAttribute(String newAttribute) {
        attributes.set(selectedAttributeIndex, newAttribute); // Update the method name in the class shape

    }

    /**
     * Deletes the currently selected attribute.
     */
    public void deleteAttribute() {
        attributes.remove(selectedAttributeIndex);
    }

    /**
     * Edits the currently selected method.
     *
     * @param newMethod The new value for the method.
     */
    public void editMethod(String newMethod) {
        methods.set(selectedMethodIndex, newMethod); // Update the method name in the class shape

    }

    /**
     * Deletes the currently selected method.
     */
    public void deleteMethod() {
        methods.remove(selectedMethodIndex);
    }

    /**
     * Checks if a given point is within the bounds of the class shape.
     *
     * @param px The x-coordinate of the point.
     * @param py The y-coordinate of the point.
     * @return {@code true} if the point is within the bounds, {@code false} otherwise.
     */
    @Override
    public boolean contains(double px, double py)
    {
         return px >= x && px <= x + width && py >= y && py <= y + totalHeight;
    }
}