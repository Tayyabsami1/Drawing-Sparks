package org.example.my_project.BL;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.example.my_project.DAO.DAO;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Project class to handle project-related operations like exporting to PNG.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class Project {
    private String name;
    private String path;

    private DAO object = new DAO();

    /**
     * Constructor to initialize the project with a name and path.
     *
     * @param name the name of the project
     * @param path the path of the project
     */
    public Project(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * Returns the name of the project.
     *
     * @return the name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path of the project.
     *
     * @return the path of the project
     */
    public String getPath() {
        return path;
    }

    /**
     * Exports the content of the given canvas to a PNG file.
     *
     * @param canvas the canvas to export
     */
    public void exportToPNG(Canvas canvas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as PNG");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
