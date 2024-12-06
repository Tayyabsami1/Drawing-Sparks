package org.example.my_project.BL;

import org.example.my_project.Models.ClassShape;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code CodeGenerator} class is responsible for generating Java code
 * for UML class diagrams represented by {@link ClassShape}.
 * It processes shapes from a diagram, identifies class shapes, and generates
 * appropriate Java code for classes, interfaces, attributes, associations, and methods.
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class CodeGenerator {

    /**
     * Generates Java code from UML class diagrams.
     *
     * <p>This method retrieves shapes from the diagram, filters out {@link ClassShape} instances,
     * and generates Java class or interface definitions, including their attributes, associations,
     * methods, and inheritance or implementation details.</p>
     *
     * @return A {@link String} containing the generated Java code for the UML diagram.
     */
    public String generateCode() {

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
                String member;
                if(attributeName.startsWith("-")){
                    member="    private ";
                }
                else if(attributeName.startsWith("#")){
                    member="    protected ";
                }
                else{
                    member="    public ";
                }
                codeBuilder.append(member)
                        .append(attributeType)
                        .append(" ")
                        .append(attributeName.substring(1))
                        .append(";\n");
            }
            for (String association : classShape.getAssociations()) {

                String[] parts = association.split(":"); //DATA TYPE
                String attributeName = parts[0].trim(); //ATTRIBUTE
                String attributeType = (parts.length > 1) ? parts[1].trim() : "String"; //IF NO TYPE IS PROVIDE THEN STRING BY DEFAULT
                String member;
                if(attributeName.startsWith("-")){
                    member="    private ";
                }
                else if(attributeName.startsWith("#")){
                    member="    protected ";
                }
                else{
                    member="    public ";
                }
                codeBuilder.append(member)
                        .append(attributeType)
                        .append(" ")
                        .append(attributeName.substring(1))
                        .append(";\n");
            }


            codeBuilder.append("\n");


            if(classShape.isInterface())
            {
                for (String method : classShape.getMethods()) {
                    String[] parts = method.split(":");
                    String methodName = parts[0].trim(); // Extract method name
                    String returnType = parts.length > 1 ? parts[1].trim() : "void"; // Default to "void" if no type is provided
                    String member;
                    if(methodName.startsWith("-")){
                        member="    private ";
                    }
                    else if(methodName.startsWith("#")){
                        member="    protected ";
                    }
                    else{
                        member="    public ";
                    }
                    codeBuilder.append(member)
                            .append(returnType)
                            .append(" ")
                            .append(method.substring(1))
                            .append(";\n");
                }
            }
            else
            {
                // Generate methods
                for (String method : classShape.getMethods()) {
                    String[] parts = method.split(":");
                    String methodName = parts[0].trim(); // Extract method name
                    String returnType = parts.length > 1 ? parts[1].trim() : "void"; // Default to "void" if no type is provided
                    String member;
                    if(methodName.startsWith("-")){
                        member="    private ";
                    }
                    else if(methodName.startsWith("#")){
                        member="    protected ";
                    }
                    else{
                        member="    public ";
                    }
                    codeBuilder.append(member)
                            .append(returnType)
                            .append(" ")
                            .append(methodName.substring(1))
                            .append(" {\n")
                            .append("        // TODO: Implement method logic\n");

                    if (!returnType.equals("void")) {
                        codeBuilder.append("        return null; // Replace with appropriate return value\n");
                    }

                    codeBuilder.append("    }\n");
                }
                for (String method : classShape.getOverridenMethods()) {
                    String[] parts = method.split(":");
                    String methodName = parts[0].trim(); // Extract method name
                    String returnType = parts.length > 1 ? parts[1].trim() : "void"; // Default to "void" if no type is provided
                    String member;
                    if(methodName.startsWith("-")){
                        member="    private ";
                    }
                    else if(methodName.startsWith("#")){
                        member="    protected ";
                    }
                    else{
                        member="    public ";
                    }
                    codeBuilder.append("    @Override\n")
                            .append(member)
                            .append(returnType)
                            .append(" ")
                            .append(methodName.substring(1))
                            .append(" {\n")
                            .append("        // TODO: Implement method logic\n");

                    if (!returnType.equals("void")) {
                        codeBuilder.append("        return null; // Replace with appropriate return value\n");
                    }

                    codeBuilder.append("    }\n");
                }

            }


            codeBuilder.append("}\n\n");
        }
        return codeBuilder.toString();

    }
}