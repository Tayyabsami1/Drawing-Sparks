package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * Represents an aggregation relationship between two classes in a class diagram.
 * The aggregation relationship is depicted as a line with a hollow diamond at the start.
 *
 * <p>This class manages the drawing of the aggregation line and updates the line's endpoints dynamically
 * as the associated classes move or change position.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class AggregationShape extends Shape implements Serializable {
    /**
     * The starting class in the aggregation relationship.
     */
    public ClassShape startClass;

    /**
     * The ending class in the aggregation relationship.
     */
    public ClassShape endClass;

    /**
     * The x-coordinate of the endpoint of the aggregation line.
     */
    private double endX;

    /**
     * The y-coordinate of the endpoint of the aggregation line.
     */
    private double endY;

    /**
     * Creates a new {@code AggregationShape} representing an aggregation relationship between two classes.
     * The relationship is initialized with the specified starting and ending classes.
     *
     * @param startClass The starting class of the aggregation.
     * @param endClass   The ending class of the aggregation.
     */
    public AggregationShape(ClassShape startClass, ClassShape endClass) {
        super(0, 0, "Aggregation");
        this.startClass = startClass;
        this.endClass = endClass;
        updateEndpoints();
        if (!endClass.isInterface() && !startClass.isInterface()) {
            System.out.println("Adding aggregation relationship...");
            startClass.addAssociation(endClass.generateAttribute());
        }
    }

    /**
     * Updates the endpoints of the aggregation line to ensure it connects correctly to the borders of the classes.
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
     * Returns the width of the aggregation shape.
     *
     * @return The width of the aggregation shape.
     */
    @Override
    public double getWidth() {
        return 0;
    }

    /**
     * Returns the height of the aggregation shape.
     *
     * @return The height of the aggregation shape.
     */
    @Override
    public double getHeight() {
        return 0;
    }

    /**
     * Draws the aggregation line between the start and end classes, including a hollow diamond
     * at the start to represent the aggregation relationship.
     *
     * @param gc The {@code GraphicsContext} used for drawing the aggregation shape.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints(); // Ensure dynamic updates
        gc.strokeLine(x, y, endX, endY); // Draw the aggregation line

        // Draw the hollow diamond at the start of the line to represent the aggregation
        gc.strokePolygon(
                new double[] { x, x - 10, x, x + 10 },
                new double[] { y, y - 5, y - 10, y - 5 },
                4
        );
    }

    /**
     * Moves the aggregation shape by the specified delta values in the x and y directions.
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
     * Sets the starting class of the aggregation and updates the endpoints.
     *
     * @param startClass The new starting class of the aggregation.
     */
    public void setStartClass(ClassShape startClass) {
        this.startClass = startClass;
        updateEndpoints();
    }

    /**
     * Sets the ending class of the aggregation and updates the endpoints.
     *
     * @param endClass The new ending class of the aggregation.
     */
    public void setEndClass(ClassShape endClass) {
        this.endClass = endClass;
        updateEndpoints();
    }
}
