package org.example.my_project.Models;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

public class AssociationShape extends Shape implements Serializable {
    public Shape startShape;
    public Shape endShape;
    private double endX, endY;

    public AssociationShape(Shape startShape, Shape endShape) {
        super(0, 0, "Association");
        this.startShape = startShape;
        this.endShape = endShape;
        updateEndpoints();
    }

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

    @Override
    public void draw(javafx.scene.canvas.GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints();
        gc.strokeLine(x, y, endX, endY);
    }
}