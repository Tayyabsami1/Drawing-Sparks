package org.example.my_project.DAO;

import org.example.my_project.BL.Diagrams;

import java.io.*;

/**
 * The {@code DAO} class is responsible for handling data access operations.
 * It includes functionalities to save and load projects as serialized objects.
 *
 * <p>Projects are stored in a dedicated directory ("projects/") and managed
 * as serialized files using {@code ObjectOutputStream} and {@code ObjectInputStream}.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class DAO {

    /**
     * The directory where project files are saved.
     */
    private static final String PROJECTS_DIRECTORY = "projects/";

    // Static initializer to ensure the directory exists
    static {
        new File(PROJECTS_DIRECTORY).mkdirs();
    }

    /**
     * Saves a project to a file.
     *
     * @param projectName The name of the project to be saved.
     * @param diagrams    The {@code Diagrams} object representing the project's data.
     * @throws IOException If an I/O error occurs during the saving process.
     */
    public void saveProject(String projectName, Diagrams diagrams) throws IOException {
        File file = new File(PROJECTS_DIRECTORY + projectName + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(diagrams);
        }
    }

    /**
     * Loads a project from a file.
     *
     * @param projectName The name of the project to be loaded.
     * @return The {@code Diagrams} object representing the loaded project's data.
     * @throws IOException            If an I/O error occurs during the loading process.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
    public Diagrams loadProject(String projectName) throws IOException, ClassNotFoundException {
        File file = new File(PROJECTS_DIRECTORY + projectName + ".dat");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Diagrams) ois.readObject();
        }
    }
}
