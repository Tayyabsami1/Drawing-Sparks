package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;


abstract public class Shape {
    protected double x, y;
    protected String name;

    public Shape(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getWidth() {
        return 0.0;
    }
    public double getHeight() {
        return 0.0;
    }

    public abstract void draw(GraphicsContext gc);

    public boolean contains(double px, double py) {
        return px >= x && px <= x + 150 && py >= y && py <= y + 100; // Example bounding box
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public  javafx.geometry.Point2D getBorderIntersection(
            double rectX, double rectY, double rectWidth, double rectHeight,
            double targetX, double targetY) {

        double centerX = rectX + rectWidth / 2;
        double centerY = rectY + rectHeight / 2;

        // Direction vector from rectangle center to target point
        double dx = targetX - centerX;
        double dy = targetY - centerY;

        // Scale factors for intersection with rectangle borders
        double scaleX = rectWidth / 2 / Math.abs(dx);
        double scaleY = rectHeight / 2 / Math.abs(dy);
        double scale = Math.min(scaleX, scaleY);

        // Calculate border intersection point
        double borderX = centerX + dx * scale;
        double borderY = centerY + dy * scale;

        // Return the concrete Point2D
        return new javafx.geometry.Point2D(borderX, borderY);
    }
}
