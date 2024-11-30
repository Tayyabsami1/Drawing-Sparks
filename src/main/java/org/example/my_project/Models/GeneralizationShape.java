package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class GeneralizationShape extends Shape {
    public ClassShape startClass, endClass;
    private double endX, endY;

    public GeneralizationShape(ClassShape startClass, ClassShape endClass) {
        super(0, 0, "Generalization");
        this.startClass = startClass;
        this.endClass = endClass;
        updateEndpoints();
        if(endClass.isInterface())
        {
            startClass.addImplementingInterface(endClass.getName());
            List<String> methods=endClass.getMethods();
            for(int i=0;i<methods.size();i++)
            {
                startClass.addOverRidenMethods(methods.get(i));
            }
        } else if (!endClass.isInterface())
        {
            startClass.setExtendingClass(endClass.getName());
        }
    }

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

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        updateEndpoints();
        gc.strokeLine(x, y, endX, endY); // Draw the line

        // Draw the hollow triangle at the end
        gc.strokePolygon(
                new double[] { endX, endX - 10, endX - 10 },
                new double[] { endY, endY - 5, endY + 5 },
                3
        );
    }

    @Override
    public void move(double deltaX, double deltaY) {
        if (startClass == null && endClass == null) {
            x += deltaX;
            y += deltaY;
            endX += deltaX;
            endY += deltaY;
        }
    }

    public void setStartClass(ClassShape startClass) {
        this.startClass = startClass;
        updateEndpoints();
    }

    public void setEndClass(ClassShape endClass) {
        this.endClass = endClass;
        updateEndpoints();
    }
}