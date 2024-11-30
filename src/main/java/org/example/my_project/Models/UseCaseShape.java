package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UseCaseShape extends Shape {
    public static int count=0;
    public double getWidth() {
        return 150;
    }
    public double getHeight() {
        return 75;
    }
    public UseCaseShape(double x, double y, String name) {
        super(x, y, name);
        count++;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);

        // Draw the oval
        gc.strokeOval(x, y, 150, 75);

        // Draw the label
        gc.setFill(Color.BLACK);
        gc.fillText(name, x + 40, y + 40);
    }
}
