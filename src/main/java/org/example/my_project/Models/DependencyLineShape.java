package org.example.my_project.Models;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * Represents a dependency line between two shapes, used in class diagrams to indicate relationships.
 * This class can handle both "Include" and "Extend" dependency types, drawing a dashed line with an arrowhead.
 *
 * <p>Dependency lines are drawn between shapes with optional labels depending on the type of dependency.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class DependencyLineShape extends Shape implements Serializable {
    /**
     * The starting shape of the dependency line.
     */
    public final Shape startShape;

    /**
     * The ending shape of the dependency line.
     */
    public final Shape endShape;

    /**
     * The type of dependency (e.g., "Include", "Extend", or empty for a generic line).
     */
    private final String type;

    /**
     * The x-coordinate of the endpoint of the dependency line.
     */
    private double endX;

    /**
     * The y-coordinate of the endpoint of the dependency line.
     */
    private double endY;

    /**
     * Creates a new {@code DependencyLineShape} between the specified shapes with the given type.
     * The type could be "Include", "Extend", or an empty string for a generic dependency line.
     *
     * @param startShape The starting shape of the dependency.
     * @param endShape   The ending shape of the dependency.
     * @param type       The type of the dependency ("Include", "Extend", or empty).
     */
    public DependencyLineShape(Shape startShape, Shape endShape, String type) {
        super(0, 0, type); // "Include" or "Extend" or empty string for Class Diagram
        this.startShape = startShape;
        this.endShape = endShape;
        this.type = type;
        updateEndpoints();
    }

    /**
     * Updates the endpoints of the dependency line to ensure the line connects at the borders of the shapes.
     */
    private void updateEndpoints() {
        if (startShape != null && endShape != null) {
            // Calculate the intersection points for both shapes from center to center
            Point2D startPoint = getBorderIntersection(
                    startShape.getX(), startShape.getY(), startShape.getWidth(), startShape.getHeight(),
                    endShape.getX() + endShape.getWidth() / 2, endShape.getY() + endShape.getHeight() / 2
            );

            Point2D endPoint = getBorderIntersection(
                    endShape.getX(), endShape.getY(), endShape.getWidth(), endShape.getHeight(),
                    startShape.getX() + startShape.getWidth() / 2, startShape.getY() + startShape.getHeight() / 2
            );

            this.x = startPoint.getX();
            this.y = startPoint.getY();
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
     * Draws the dependency line between the start and end shapes, along with an optional label
     * and arrowhead, based on the dependency type ("Include" or "Extend").
     *
     * @param gc The {@code GraphicsContext} used for drawing the dependency line.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineDashes(5); // Dashed line for dependency

        updateEndpoints(); // Ensure endpoints are updated
        gc.setStroke(Color.BLACK);

        // Draw the dashed line
        gc.strokeLine(x, y, endX, endY);

        // Draw the arrowhead at the end of the line
        drawArrowHead(gc, x, y, endX, endY);

        // Add label for "Include" or "Extend" types
        if (!type.isEmpty()) {
            String label = type.equalsIgnoreCase("Include") ? "<<include>>" : "<<extend>>";
            double midX = (x + endX) / 2;
            double midY = (y + endY) / 2;
            gc.fillText(label, midX, midY - 10);
        }

        // Reset to solid line for other shapes
        gc.setLineDashes(null);
    }

    /**
     * Helper method to draw an arrowhead at the end of the line, indicating the direction of the dependency.
     *
     * @param gc     The {@code GraphicsContext} used for drawing the arrowhead.
     * @param startX The x-coordinate of the start point of the line.
     * @param startY The y-coordinate of the start point of the line.
     * @param endX   The x-coordinate of the end point of the line.
     * @param endY   The y-coordinate of the end point of the line.
     */
    private void drawArrowHead(GraphicsContext gc, double startX, double startY, double endX, double endY) {
        double angle = Math.atan2(endY - startY, endX - startX);
        double arrowLength = 10;
        double arrowAngle = Math.PI / 6;

        double x1 = endX - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = endY - arrowLength * Math.sin(angle - arrowAngle);
        double x2 = endX - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = endY - arrowLength * Math.sin(angle + arrowAngle);

        gc.strokeLine(endX, endY, x1, y1);
        gc.strokeLine(endX, endY, x2, y2);
    }
}
