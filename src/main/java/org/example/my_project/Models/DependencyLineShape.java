package org.example.my_project.Models;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class DependencyLineShape extends Shape implements Serializable {
    public final Shape startShape;
    public final Shape endShape;
    private final String type;
    private double endX, endY;

    public DependencyLineShape(Shape startShape, Shape endShape, String type) {
        super(0, 0, type); // "Include" or "Extend" or empty string for Class Diagram
        this.startShape = startShape;
        this.endShape = endShape;
        this.type = type;
        updateEndpoints();
    }

    // Updates the endpoints to ensure lines connect at the border of shapes
    private void updateEndpoints() {
        if (startShape != null && endShape != null) {
            // Calculate the intersection points for both shapes from center to center
            Point2D startPoint = getBorderIntersection(
                    startShape.getX(), startShape.getY(), startShape.getWidth(), startShape.getHeight(),
                    endShape.getX() + endShape.getWidth()/ 2, endShape.getY() + endShape.getHeight() / 2
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


    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    // Draws the dependency line between shapes
    @Override
    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        gc.setLineDashes(5); // Dashed line for dependency

        updateEndpoints();
        gc.setStroke(Color.BLACK);

        // Draw the dashed line
        gc.strokeLine(x, y, endX, endY);

        // Draw arrowhead
        drawArrowHead(gc, x, y, endX, endY);

        // Add label only if type is "Include" or "Extend"
        if (!type.isEmpty()) {
            String label = type.equalsIgnoreCase("Include") ? "<<include>>" : "<<extend>>";
            double midX = (x + endX) / 2;
            double midY = (y + endY) / 2;
            gc.fillText(label, midX, midY - 10);
        }

        // Reset to solid line for other shapes
        gc.setLineDashes(null);
    }

    // Helper method to draw arrowhead at the end of the line
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
