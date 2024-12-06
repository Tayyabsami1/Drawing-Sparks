package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * Represents a direct association relationship between two class shapes.
 * This shape is rendered as a line with an open arrowhead pointing to the end class.
 *
 * <p>Useful in class diagrams to depict simple associations.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class DirectAssociationLineShape extends Shape implements Serializable {
    /**
     * The starting class of the direct association.
     */
    public ClassShape startClass;

    /**
     * The ending class of the direct association.
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
     * Creates a new {@code DirectAssociationLineShape} connecting the specified starting and ending class shapes.
     *
     * @param startClass The starting class of the association.
     * @param endClass   The ending class of the association.
     */
    public DirectAssociationLineShape(ClassShape startClass, ClassShape endClass) {
        super(0, 0, "DirectAssociation");
        this.startClass = startClass;
        this.endClass = endClass;
        updateEndpoints();
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
     * Draws the direct association line with an open arrowhead on the given {@code GraphicsContext}.
     *
     * @param gc The {@code GraphicsContext} used for drawing the shape.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints(); // Ensure endpoints are current
        gc.strokeLine(x, y, endX, endY); // Draw the association line

        // Draw the open arrowhead
        gc.strokePolygon(
                new double[]{endX, endX - 10, endX - 10},
                new double[]{endY, endY - 5, endY + 5},
                3
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
     * Sets the starting class for the association and updates the endpoints.
     *
     * @param startClass The starting class of the association.
     */
    public void setStartClass(ClassShape startClass) {
        this.startClass = startClass;
        updateEndpoints();
    }

    /**
     * Sets the ending class for the association and updates the endpoints.
     *
     * @param endClass The ending class of the association.
     */
    public void setEndClass(ClassShape endClass) {
        this.endClass = endClass;
        updateEndpoints();
    }
}
