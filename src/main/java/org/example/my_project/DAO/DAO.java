package org.example.my_project.DAO;

import org.example.my_project.BL.Diagrams;

import java.io.*;

public class DAO {
    private static final String PROJECTS_DIRECTORY = "projects/";

    static {
        // Ensure the directory exists
        new File(PROJECTS_DIRECTORY).mkdirs();
    }

    // Save the project to a file
    public  void saveProject(String projectName, Diagrams diagrams) throws IOException {
        File file = new File(PROJECTS_DIRECTORY + projectName + ".dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(diagrams);
        }
    }

    // Load the project from a file
    public  Diagrams loadProject(String projectName) throws IOException, ClassNotFoundException {
        File file = new File(PROJECTS_DIRECTORY + projectName + ".dat");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Diagrams) ois.readObject();
        }
    }

}
