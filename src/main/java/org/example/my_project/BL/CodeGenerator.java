package org.example.my_project.BL;

import org.example.my_project.Models.ClassShape;

import java.util.List;
import java.util.stream.Collectors;

public class CodeGenerator {
    public String generateCode() {
//        CodeGenerator generator = new CodeGenerator();
//        generator.generate(project);
        List<ClassShape> classShapes = Diagrams.getShapes().stream()
                .filter(shape -> shape instanceof ClassShape)
                .map(shape -> (ClassShape) shape)
                .collect(Collectors.toList());
        StringBuilder codeBuilder = new StringBuilder();

        for (ClassShape classShape : classShapes) {
            // Generate class declaration
            if(classShape.isInterface())
            {
                codeBuilder.append("public interface ")
                        .append(classShape.getName())
                        .append(" {\n");
            }
            else
            {
                codeBuilder.append("public class ")
                        .append(classShape.getName());
                if(classShape.getExtendingClass()!=null)
                {
                    codeBuilder.append(" extends " )
                            .append(classShape.getExtendingClass());
                }
                int i=0;
                for(String implementing : classShape.getImplementingInterface()) {
                    if (i == 0) {
                        codeBuilder.append(" implements ");
                    } else {
                        codeBuilder.append(", ");
                    }

                    codeBuilder.append(implementing);
                    i++;
                }
                codeBuilder.append(" {\n");
            }

            // Generate attributes
            for (String attribute : classShape.getAttributes()) {
                String[] parts = attribute.split(":"); //DATA TYPE
                String attributeName = parts[0].trim(); //ATTRIBUTE
                String attributeType = (parts.length > 1) ? parts[1].trim() : "String"; //IF NO TYPE IS PROVIDE THEN STRING BY DEFAULT
                codeBuilder.append("    public ")
                        .append(attributeType)
                        .append(" ")
                        .append(attributeName)
                        .append(";\n");
            }
            for (String association : classShape.getAssociations()) {
                //System.out.println("kya hwa???");
                String[] parts = association.split(":"); //DATA TYPE
                String attributeName = parts[0].trim(); //ATTRIBUTE
                String attributeType = (parts.length > 1) ? parts[1].trim() : "String"; //IF NO TYPE IS PROVIDE THEN STRING BY DEFAULT
                codeBuilder.append("    public ")
                        .append(attributeType)
                        .append(" ")
                        .append(attributeName)
                        .append(";\n");
            }


            codeBuilder.append("\n");


            if(classShape.isInterface())
            {
                for (String method : classShape.getMethods()) {
                    codeBuilder.append("    public void ")
                            .append(method)
                            .append(";\n");
                }
            }
            else
            {
                // Generate methods
                for (String method : classShape.getMethods()) {
                    codeBuilder.append("    public void ")
                            .append(method)
                            .append(" {\n")
                            .append("        // TODO: Implement method logic\n")
                            .append("    }\n");
                }
                for (String method : classShape.getOverridenMethods()) {
                    codeBuilder.append("    @Override\n")
                            .append("    public void ")
                            .append(method)
                            .append(" {\n")
                            .append("        // TODO: Implement method logic\n")
                            .append("    }\n");
                }
            }


            codeBuilder.append("}\n\n");
        }
        return codeBuilder.toString();

    }
}