package org.example.my_project.BL;
import java.io.*;
import java.util.*;

public class ProjectManager {
    private static final String PROJECTS_DIRECTORY = "projects/";

    static {
        // Ensure the directory exists
        new File(PROJECTS_DIRECTORY).mkdirs();
    }

    // Save a project to a file
    public static void saveProject(String projectName, Diagrams diagrams) throws IOException {
        File file = new File(PROJECTS_DIRECTORY + projectName + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(diagrams);
        }
    }

    // Load a project from a file
    public static Diagrams loadProject(String projectName) throws IOException, ClassNotFoundException {
        File file = new File(PROJECTS_DIRECTORY + projectName + ".dat");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Diagrams) ois.readObject();
        }
    }

    // Get all saved project names
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

