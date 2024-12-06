package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

/**
 * The {@code Shape} class serves as an abstract base class for all shapes in the application.
 * It defines common properties such as position and name, as well as methods for drawing,
 * movement, and boundary checking.
 *
 * <p>Subclasses must implement methods for getting the width and height, and for drawing
 * themselves on a {@code GraphicsContext}.</p>
 *
 * <p>This class implements {@code Serializable} to allow shape objects to be serialized.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
abstract public class Shape implements Serializable {
    private static final long serialVersionUID = 1L; // For compatibility across versions

    /**
     * The x-coordinate of the shape's position.
     */
    protected double x;

    /**
     * The y-coordinate of the shape's position.
     */
    protected double y;

    /**
     * The name of the shape.
     */
    protected String name;

    /**
     * Constructs a new {@code Shape} with the specified position and name.
     *
     * @param x    The x-coordinate of the shape's position.
     * @param y    The y-coordinate of the shape's position.
     * @param name The name of the shape.
     */
    public Shape(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /**
     * Returns the x-coordinate of the shape's position.
     *
     * @return The x-coordinate of the shape.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the shape's position.
     *
     * @return The y-coordinate of the shape.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width of the shape.
     * <p>This method must be implemented by subclasses.</p>
     *
     * @return The width of the shape.
     */
    public abstract double getWidth();

    /**
     * Returns the height of the shape.
     * <p>This method must be implemented by subclasses.</p>
     *
     * @return The height of the shape.
     */
    public abstract double getHeight();

    /**
     * Draws the shape on the given {@code GraphicsContext}.
     *
     * @param gc The {@code GraphicsContext} where the shape will be drawn.
     */
    public abstract void draw(GraphicsContext gc);

    /**
     * Checks if a given point is contained within the shape's bounds.
     *
     * @param px The x-coordinate of the point.
     * @param py The y-coordinate of the point.
     * @return {@code true} if the point is within the shape's bounds, {@code false} otherwise.
     */
    public boolean contains(double px, double py) {

        boolean withinBounds = px >= x && px <= x + 150 && py >= y && py <= y + 90; // Example bounding box

        return withinBounds;

    }


    /**
     * Moves the shape by the specified amounts in the x and y directions.
     *
     * @param dx The amount to move in the x direction.
     * @param dy The amount to move in the y direction.
     */
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Returns the name of the shape.
     *
     * @return The name of the shape.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the shape.
     *
     * @param name The new name for the shape.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Calculates the intersection point of a line from a rectangle's center to a target point.
     *
     * @param rectX      The x-coordinate of the rectangle's top-left corner.
     * @param rectY      The y-coordinate of the rectangle's top-left corner.
     * @param rectWidth  The width of the rectangle.
     * @param rectHeight The height of the rectangle.
     * @param targetX    The x-coordinate of the target point.
     * @param targetY    The y-coordinate of the target point.
     * @return A {@code Point2D} representing the intersection point on the rectangle's border.
     */
    public javafx.geometry.Point2D getBorderIntersection(
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
