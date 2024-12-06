package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * The {@code UseCaseShape} class represents a use-case oval shape that can be drawn on a canvas.
 * It extends the {@code Shape} abstract class and provides specific implementations for width,
 * height, and drawing methods.
 *
 * <p>Each instance of {@code UseCaseShape} represents a use-case diagram element positioned
 * at a specific (x, y) coordinate with a name label inside.</p>
 *
 * <p>This class implements {@code Serializable} to allow instances to be serialized.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class UseCaseShape extends Shape implements Serializable {
    /**
     * A static counter to keep track of the number of {@code UseCaseShape} instances created.
     */
    public static int count = 0;

    /**
     * Constructs a new {@code UseCaseShape} with the specified position and name.
     * Increments the static {@code count} variable to track the number of instances.
     *
     * @param x    The x-coordinate of the shape's position.
     * @param y    The y-coordinate of the shape's position.
     * @param name The name of the use case.
     */
    public UseCaseShape(double x, double y, String name) {
        super(x, y, name);
        count++;
    }

    /**
     * Returns the width of the {@code UseCaseShape}.
     *
     * @return The width of the use-case shape, which is always {@code 150}.
     */
    public double getWidth() {
        return 150;
    }

    /**
     * Returns the height of the {@code UseCaseShape}.
     *
     * @return The height of the use-case shape, which is always {@code 75}.
     */
    public double getHeight() {
        return 75;
    }

    /**
     * Draws the use-case oval shape representing the {@code UseCaseShape} on the given {@code GraphicsContext}.
     *
     * <p>The shape is an oval with a label at its center.</p>
     *
     * @param gc The {@code GraphicsContext} used to draw the shape on a canvas.
     */
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
