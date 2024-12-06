package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.List;

/**
 * The {@code GeneralizationShape} class represents a generalization (inheritance or implementation) relationship
 * between two class shapes. It is visually rendered as a line with a hollow triangle pointing to the superclass or interface.
 *
 * <p>This class extends the {@code Shape} abstract class and is used in class diagrams
 * to depict inheritance or implementation relationships.</p>
 *
 * <p>It updates relationships between the starting and ending class shapes, reflecting whether the end class is an interface or not.</p>
 *
 * <p>This class implements {@code Serializable} to support object serialization.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class GeneralizationShape extends Shape implements Serializable {
    /**
     * The starting class of the generalization relationship.
     */
    public ClassShape startClass;

    /**
     * The ending class of the generalization relationship.
     */
    public ClassShape endClass;

    /**
     * The x-coordinate of the endpoint of the line.
     */
    private double endX;

    /**
     * The y-coordinate of the endpoint of the line.
     */
    private double endY;

    /**
     * Constructs a new {@code GeneralizationShape} between the specified starting and ending class shapes.
     * Updates the relationship details for inheritance or implementation based on the type of the ending class.
     *
     * @param startClass The starting class of the generalization relationship.
     * @param endClass   The ending class of the generalization relationship.
     */
    public GeneralizationShape(ClassShape startClass, ClassShape endClass) {
        super(0, 0, "Generalization");
        this.startClass = startClass;
        this.endClass = endClass;
        updateEndpoints();
        if (endClass.isInterface()) {
            startClass.addImplementingInterface(endClass.getName());
            List<String> methods = endClass.getMethods();
            for (String method : methods) {
                startClass.addOverRidenMethods(method);
            }
        } else if (!endClass.isInterface()) {
            startClass.setExtendingClass(endClass.getName());
        }
    }

    /**
     * Updates the endpoints of the line based on the positions and dimensions of the start and end classes.
     */
    private void updateEndpoints() {
        if (startClass != null) {
            javafx.geometry.Point2D startPoint = getBorderIntersection(
                    startClass.getX(), startClass.getY(), startClass.getWidth(), startClass.getHeight(),
                    endClass == null ? endX : endClass.getX() + endClass.getWidth() / 2,
                    endClass == null ? endY : endClass.getY() + endClass.getHeight() / 2
            );
            this.x = startPoint.getX();
            this.y = startPoint.getY();
        }
        if (endClass != null) {
            javafx.geometry.Point2D endPoint = getBorderIntersection(
                    endClass.getX(), endClass.getY(), endClass.getWidth(), endClass.getHeight(),
                    startClass == null ? x : startClass.getX() + startClass.getWidth() / 2,
                    startClass == null ? y : startClass.getY() + startClass.getHeight() / 2
            );
            this.endX = endPoint.getX();
            this.endY = endPoint.getY();
        }
    }

    /**
     * Returns the width of the {@code GeneralizationShape}.
     *
     * @return The width of the shape.
     */
    @Override
    public double getWidth() {
        return 0;
    }

    /**
     * Returns the height of the {@code GeneralizationShape}.
     *
     * @return The height of the shape.
     */
    @Override
    public double getHeight() {
        return 0;
    }

    /**
     * Draws the generalization relationship on the specified {@code GraphicsContext}.
     *
     * <p>Draws a line between the starting and ending class shapes with a hollow triangle
     * at the endpoint to represent inheritance or implementation.</p>
     *
     * @param gc The {@code GraphicsContext} used to draw the shape on a canvas.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints();
        gc.strokeLine(x, y, endX, endY); // Draw the line

        // Draw the hollow triangle at the end
        gc.strokePolygon(
                new double[]{endX, endX - 10, endX - 10},
                new double[]{endY, endY - 5, endY + 5},
                3
        );
    }

    /**
     * Moves the shape by the specified deltas if both {@code startClass} and {@code endClass} are null.
     * Otherwise, the shape's position remains fixed based on the classes.
     *
     * @param deltaX The change in the x-coordinate.
     * @param deltaY The change in the y-coordinate.
     */
    @Override
    public void move(double deltaX, double deltaY) {
        if (startClass == null && endClass == null) {
            x += deltaX;
            y += deltaY;
            endX += deltaX;
            endY += deltaY;
        }
    }

    /**
     * Sets the starting class of the generalization relationship and updates the endpoints.
     *
     * @param startClass The starting class of the relationship.
     */
    public void setStartClass(ClassShape startClass) {
        this.startClass = startClass;
        updateEndpoints();
    }

    /**
     * Sets the ending class of the generalization relationship and updates the endpoints.
     *
     * @param endClass The ending class of the relationship.
     */
    public void setEndClass(ClassShape endClass) {
        this.endClass = endClass;
        updateEndpoints();
    }
}
