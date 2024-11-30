package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UserShape extends Shape {
    public static int count=0;
    public UserShape(double x, double y, String n) {
        super(x, y, n);
        count++;
    }
    public double getWidth() {
        return 30;
    }
    public double getHeight() {
        return 70;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);

        // Draw the stick figure
        double centerX = x + 15;
        gc.strokeOval(centerX - 10, y, 20, 20); // Head
        gc.strokeLine(centerX, y + 20, centerX, y + 50); // Body
        gc.strokeLine(centerX, y + 50, centerX - 15, y + 70); // Left leg
        gc.strokeLine(centerX, y + 50, centerX + 15, y + 70); // Right leg
        gc.strokeLine(centerX, y + 30, centerX - 15, y + 20); // Left arm
        gc.strokeLine(centerX, y + 30, centerX + 15, y + 20); // Right arm

        // Draw the label
        gc.setFill(Color.BLACK);
        gc.fillText(name, centerX - 20, y + 85);
    }
}