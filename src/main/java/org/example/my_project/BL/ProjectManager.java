package org.example.my_project.BL;

import org.example.my_project.DAO.DAO;

import java.io.*;
import java.util.*;

/**
 * ProjectManager class responsible for managing projects, including saving, loading, and retrieving project names.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class ProjectManager {
    private static final String PROJECTS_DIRECTORY = "projects/";
    private static DAO myLayer = new DAO();

    static {
        // Ensure the directory exists
        new File(PROJECTS_DIRECTORY).mkdirs();
    }

    /**
     * Saves a project to a file.
     *
     * @param projectName the name of the project
     * @param diagrams the diagrams associated with the project
     * @throws IOException if an I/O error occurs during saving
     */
    public static void saveProject(String projectName, Diagrams diagrams) throws IOException {
        myLayer.saveProject(projectName, diagrams);
    }

    /**
     * Loads a project from a file.
     *
     * @param projectName the name of the project to load
     * @return the Diagrams object associated with the project
     * @throws IOException if an I/O error occurs during loading
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    public static Diagrams loadProject(String projectName) throws IOException, ClassNotFoundException {
        return myLayer.loadProject(projectName);
    }

    /**
     * Retrieves all saved project names from the project directory.
     *
     * @return a list of project names
     */
    public static List<String> getAllProjectNames() {
        File folder = new File(PROJECTS_DIRECTORY);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) return new ArrayList<>();

        List<String> projectNames = new ArrayList<>();
        for (File file : files) {
            projectNames.add(file.getName().replace(".dat", ""));
        }
        return projectNames;
    }
}
