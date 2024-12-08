package org.example.my_project.Models;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * Represents an association relationship between two shapes in a class diagram.
 * This class manages the drawing of an association line between the start and end shapes.
 * The association is depicted as a straight line connecting the two shapes.
 *
 * <p>It ensures that the endpoints of the line are updated dynamically as the shapes move or change position.</p>
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class AssociationShape extends Shape implements Serializable {
    /**
     * The starting shape in the association relationship.
     */
    public Shape startShape;

    /**
     * The ending shape in the association relationship.
     */
    public Shape endShape;

    /**
     * The x-coordinate of the endpoint of the association line.
     */
    private double endX;

    /**
     * The y-coordinate of the endpoint of the association line.
     */
    private double endY;

    /**
     * Creates a new {@code AssociationShape} representing an association relationship between two shapes.
     * The relationship is initialized with the specified starting and ending shapes.
     *
     * @param startShape The starting shape of the association.
     * @param endShape   The ending shape of the association.
     */
    public AssociationShape(Shape startShape, Shape endShape) {
        super(0, 0, "Association");
        this.startShape = startShape;
        this.endShape = endShape;
        updateEndpoints();
    }

    /**
     * Updates the endpoints of the association line to ensure it connects correctly to the borders of the shapes.
     * The method calculates the intersection points between the shapes' borders and the association line.
     */
    private void updateEndpoints() {
        if (startShape != null && endShape != null) {
            Point2D startPoint = getBorderIntersection(
                    startShape.getX(), startShape.getY(), 150, 100,
                    endShape.getX(), endShape.getY()
            );
            Point2D endPoint = getBorderIntersection(
                    endShape.getX(), endShape.getY(), 150, 100,
                    startShape.getX(), startShape.getY()
            );
            this.x = startPoint.getX();
            this.y = startPoint.getY();
            this.endX = endPoint.getX();
            this.endY = endPoint.getY();
        }
    }

    /**
     * Returns the width of the association shape.
     *
     * @return The width of the association shape.
     */
    @Override
    public double getWidth() {
        return 0;
    }

    /**
     * Returns the height of the association shape.
     *
     * @return The height of the association shape.
     */
    @Override
    public double getHeight() {
        return 0;
    }

    /**
     * Draws the association line between the start and end shapes.
     *
     * @param gc The {@code GraphicsContext} used for drawing the association shape.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints(); // Ensure dynamic updates
        gc.strokeLine(x, y, endX, endY); // Draw the association line
    }
}
