package controller;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.Type;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class Generation{

    private GenerationStrategy strategy;
    protected StringBuilder plantUMLBuilder = new StringBuilder();
    protected FileWriter ecriture;
    protected Classe classe;

    public void setStrategy(GenerationStrategy strategy)
    {
        this.strategy = strategy;
    }

    public void startGeneration(File file, String savePath) throws IOException, ClassNotFoundException {
        if(strategy == null)
        {
            this.strategy = new MultipleGeneration();
        }
        System.out.println("GEN: " + strategy);
        strategy.generate(file, savePath);
    }

    protected String transformJavaToPlantUML(File file) {
        try {
            plantUMLBuilder = new StringBuilder();
            // Lire le contenu du fichier .java
            String javaCode = new String(Files.readAllBytes(file.toPath()));
            System.out.println(javaCode);

            // Parser le contenu du fichier Java
            ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaCode);

            // Vérifier s'il n'y a pas d'erreur de parsing
                CompilationUnit cu = parseResult.getResult().get();
                List<TypeDeclaration<?>> typeDeclarations = cu.getTypes();

                // Parcourir les déclarations de type
                for (TypeDeclaration<?> typeDeclaration : typeDeclarations) {
                    // Ajouter le nom de la classe
                    plantUMLBuilder.append("class ").append(typeDeclaration.getName().asString()).append(" {\n");

                    // Ajouter les attributs de la classe
                    for (FieldDeclaration fieldDeclaration : typeDeclaration.getFields()) {
                        for (VariableDeclarator variable : fieldDeclaration.getVariables()) {
                            if(!fieldDeclaration.getElementType().isPrimitiveType())
                            {
                                plantUMLBuilder.append("//L'attribut " + variable.getName() + " renvoie une liste : à gérer dans les relations\n");
                            } else {
                                ecrireAttributs(fieldDeclaration, variable);
                            }
                        }
                    }
                    for(ConstructorDeclaration constructorDeclaration : typeDeclaration.getConstructors())
                    {
                        ecrireConstructeurs(constructorDeclaration);
                    }

                    // Ajouter les méthodes de la classe
                    for (MethodDeclaration methodDeclaration : typeDeclaration.getMethods())
                    {
                        ecrireMethodes(methodDeclaration);
                    }
                }
                plantUMLBuilder.append("}\n\n");
                // Enregistrer le fichier .puml
                String plantUMLCode = plantUMLBuilder.toString();
                System.out.println(plantUMLCode);
                //Files.write(Paths.get(file.getAbsolutePath().replace(".java", ".puml")), plantUMLCode.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return plantUMLBuilder.toString();
    }

        public String ecrireEnTete() {
        return """
                @startuml

                skinparam style strictuml
                skinparam classAttributeIconSize 0
                skinparam classFontStyle Bold
                hide enum methods
                hide empty members

                """;
    }

    public String getReturnType(Type origin)
    {
        String res = switch (origin.toString()) {
            case "int" -> ": Integer";
            case "boolean" -> ": Boolean";
            case "char" -> ": Char";
            case "byte" -> ": Byte";
            case "short" -> ": Short";
            case "long" -> ": Long";
            case "float" -> ": Float";
            case "double" -> ": Double";
            case "void" -> "";
            default -> ": " + origin;
        };
        if(origin.isArrayType()) {
            return res.substring(0, res.length()-2) + "[*]";
        } else {
            return res;
        }
    }

    public String getVisibility(String origin)
    {
        return switch (origin) {
            case "public " -> "+";
            case "private " -> "-";
            case "protected " -> "#";
            default -> "~";
        };
    }

    public String getFields(String origin) {
        return switch (origin) {
            case "static" -> "{static}";
            case "final" -> "{readOnly}";
            default -> "";
        };
    }

    public <T extends CallableDeclaration<?>> StringBuilder getParameters(CallableDeclaration<T> method)
    {
        StringBuilder res = new StringBuilder();
        List<Parameter> parameters = method.getParameters();
        for(int i = 0; i<parameters.size(); i++)
        {
            res.append(parameters.get(i).getName()).append(" ").append(getReturnType(parameters.get(i).getType()));
            if(i!=parameters.size()-1) res.append(", ");
        }
        return res;
    }

    public void ecrireAttributs(FieldDeclaration fieldDeclaration, VariableDeclarator variable){
        List<Modifier> modifiers = fieldDeclaration.getModifiers(); // public / static / final / etc...
        StringBuilder fields = new StringBuilder();

        // On passe tous les modificateurs
        for(int i = 1; i<modifiers.size(); i++) {
            fields.append(getFields(modifiers.get(i).toString().toLowerCase()));
        }

        plantUMLBuilder.append(getVisibility(modifiers.get(0).toString().toLowerCase())).append(" ").append(fields).append(" ").append(variable.getName()).append(" ").append(getReturnType(fieldDeclaration.getElementType())).append("\n");
    }

    public void ecrireMethodes(MethodDeclaration method) {
        List<Modifier> modifiers = method.getModifiers(); // public / static / final / etc...
        StringBuilder fields = new StringBuilder();

        // On passe tous les modificateurs
        for(int i = 1; i<modifiers.size(); i++) {
            fields.append(getFields(modifiers.get(i).toString().toLowerCase()));
        }

        try{
            plantUMLBuilder.append(getVisibility(modifiers.get(0).toString().toLowerCase())).append(" ").append(fields).append(" ").append(method.getName()).append("(").append(getParameters(method)).append(") ").append(getReturnType(method.getType())).append("\n");
        } catch(IndexOutOfBoundsException e)
        {
            System.out.println("Interface à faire");
        }
    }

    public void ecrireConstructeurs(ConstructorDeclaration constructor) {
        List<Modifier> modifiers = constructor.getModifiers(); // public / static / final / etc...
        StringBuilder fields = new StringBuilder();

        // On passe tous les modificateurs
        for(int i = 1; i<modifiers.size(); i++) {
            fields.append(getFields(modifiers.get(i).toString().toLowerCase()));
        }

        plantUMLBuilder.append(getVisibility(modifiers.get(0).toString().toLowerCase())).append(" <<Create>>").append(fields).append(" ").append(constructor.getName()).append("(").append(getParameters(constructor)).append(")\n");

    }

    /*public void ecrireRelations() {
        // Ecriture super classe
        Relation relation = new Relation(classe.transformInClass(), classe.transformInClass().getSuperclass());
        try {
            if (relation.estValide()) {
                uml += relation.toString();
            }

            // Ecriture interface(s)
            for (Class c : classe.transformInClass().getInterfaces()) {
                relation = new Relation(classe.transformInClass(), c);
                if (relation.estValide()) {
                    uml += relation.toString();
                }
            }
            // Ecriture relation(s) d'association
            for(Attribut a : classe.getAttributs())
            {
                String association = a.getAssociation();
                if(association != null)uml+= classe.getNom() + " o---> \"" + association.split(":")[0] + association.split(":")[1] + " *\"" + association.split(":")[2] + "\n";
            }
        // Si la classe est une interface / enum
        } catch(NullPointerException e)
        {
            // Aucune relation
        }
    }*/
}