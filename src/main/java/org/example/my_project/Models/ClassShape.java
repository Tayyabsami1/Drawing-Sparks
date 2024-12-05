package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassShape extends Shape implements Serializable {
    private List<String> attributes = new ArrayList<>();
    private List<String> methods = new ArrayList<>();
    private double totalHeight;
    private double width;

    private List<String> implementingInterface=new ArrayList<>();
    private List<String> overridenMethods=new ArrayList<>();
    private String extendingClass=null;
    private List<String> associations=new ArrayList<>();
    public int selectedAttributeIndex = -1;
    public int selectedMethodIndex = -1; // -1 means no method is selected

    private Boolean isInterface=false;

    public static int count=0;
    public static int count1=0;

    public ClassShape(double x, double y, String name) {
        super(x, y, name);
        if(!name.contains("Interface")) {
            count++;
        }
        else{
            count1++;
        }

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
    @Override
    public double getWidth() {
        return width;
    }
    @Override
    public double getHeight() {
        return totalHeight;
    }

    public void addMethod(String method) {
        methods.add(method);
    }
    public boolean isInterface()
    {
        return isInterface;
    }
    public String generateAttribute()
    {

        StringBuilder attribute= new StringBuilder();
        attribute.append(this.name).append("Obj:").append(this.name);
        return attribute.toString();
    }
    public void addImplementingInterface(String interfaceName)
    {
        this.implementingInterface.add(interfaceName);
    }

    public List<String> getImplementingInterface() {
        return implementingInterface;
    }
    public void setExtendingClass(String className)
    {
        this.extendingClass=className;
    }
    public String getExtendingClass() {
        return extendingClass;
    }
    public void addOverRidenMethods(String method)
    {
        this.overridenMethods.add(method);
    }

    public List<String> getOverridenMethods() {
        return overridenMethods;
    }

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

    public List<String> getAttributes() {
        return this.attributes;
    }
    public List<String> getMethods(){
        return this.methods;
    }

    public List<String> getAssociations() {
        return associations;
    }

    public void addAssociation(String association) {
        if(!associations.contains(association))
        {
            associations.add(association);
        }
    }
    public void editAttribute(String newAttribute) {
        attributes.set(selectedAttributeIndex, newAttribute); // Update the method name in the class shape

    }
    public void deleteAttribute()
    {
        attributes.remove(selectedAttributeIndex);
    }
    public void editMethod(String newMethod) {
        methods.set(selectedMethodIndex, newMethod); // Update the method name in the class shape

    }
    public void deleteMethod()
    {
        methods.remove(selectedMethodIndex);
    }
    @Override
    public boolean contains(double px, double py)
    {
         return px >= x && px <= x + width && py >= y && py <= y + totalHeight;
    }
}