package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * Represents a composition relationship between two class shapes.
 * The shape is rendered as a line with a solid diamond at the starting class.
 *
 * <p>Composition indicates a strong ownership relationship where the lifecycle of the child class
 * is tightly coupled with the parent class.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class CompositionShape extends Shape implements Serializable {
    /**
     * The starting class of the composition relationship.
     */
    public ClassShape startClass;

    /**
     * The ending class of the composition relationship.
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
     * Creates a new {@code CompositionShape} connecting the specified starting and ending class shapes.
     * If both classes are not interfaces, an association is added between the classes.
     *
     * @param startClass The parent class in the composition.
     * @param endClass   The child class in the composition.
     */
    public CompositionShape(ClassShape startClass, ClassShape endClass) {
        super(0, 0, "Composition");
        this.startClass = startClass;
        this.endClass = endClass;
        updateEndpoints();
        if (!endClass.isInterface() && !startClass.isInterface()) {
            System.out.println("Adding composition relationship...");
            startClass.addAssociation(endClass.generateAttribute());
        }
    }

    /**
     * Updates the coordinates of the line's endpoints based on the positions of the associated class shapes.
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
     * Returns the width of the shape.
     *
     * @return The width of the shape.
     */
    @Override
    public double getWidth() {
        return 0;
    }

    /**
     * Returns the height of the shape.
     *
     * @return The height of the shape.
     */
    @Override
    public double getHeight() {
        return 0;
    }

    /**
     * Draws the composition relationship on the given {@code GraphicsContext}.
     * The line is drawn with a solid diamond at the start.
     *
     * @param gc The {@code GraphicsContext} used for drawing the shape.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints(); // Ensure endpoints are updated
        gc.strokeLine(x, y, endX, endY); // Draw the composition line

        // Draw the solid diamond at the start
        gc.setFill(Color.BLACK);
        gc.fillPolygon(
                new double[]{x, x - 10, x, x + 10},
                new double[]{y, y - 5, y - 10, y - 5},
                4
        );
    }

    /**
     * Moves the shape by the specified deltas if both associated class shapes are {@code null}.
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
     * Sets the starting class for the composition and updates the endpoints.
     *
     * @param startClass The parent class in the composition.
     */
    public void setStartClass(ClassShape startClass) {
        this.startClass = startClass;
        updateEndpoints();
    }

    /**
     * Sets the ending class for the composition and updates the endpoints.
     *
     * @param endClass The child class in the composition.
     */
    public void setEndClass(ClassShape endClass) {
        this.endClass = endClass;
        updateEndpoints();
    }
}
