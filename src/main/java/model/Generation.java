package model;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.body.CallableDeclaration;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import controller.GenerationStrategy;
import controller.MainMenuObserver;
import controller.MultipleGeneration;

public class Generation{

    private GenerationStrategy strategy;
    private List<MainMenuObserver> observers = new ArrayList<>();
    protected StringBuilder classOptions;
    protected StringBuilder plantUMLBuilder;
    protected StringBuilder plantUMLRelations;
    protected File file;
    protected String savePath;

    public void setStrategy(GenerationStrategy strategy)
    {
        this.strategy = strategy;
    }

    public Generation(File file, String savePath)
    {
        this.file = file;
        this.savePath = savePath;
    }

    public void addObserver(MainMenuObserver mainMenuObserver)
    {
        observers.add(mainMenuObserver);
    }

    public void notifyObserversWithMessageBox(String message)
    {
        for(MainMenuObserver observer : observers)
        {
            observer.showMessageBox(message);
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile()
    {
        return this.file;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSavePath()
    {
        return this.savePath;
    }

    public void startGeneration() throws IOException, ClassNotFoundException {
        if(this.file.getName().equals("Aucun fichier sélectionné"))
        {
            notifyObserversWithMessageBox("Vous n'avez sélectionné aucun fichier à convertir.");
        } else {
            strategy.generate(this.file, this.savePath);
            if(strategy instanceof MultipleGeneration)
            {
                notifyObserversWithMessageBox("Les fichiers ont été générés avec succès!");
            } else {
                notifyObserversWithMessageBox("Le fichier " + file.getName().split("\\.")[0] + ".puml a été généré avec succès!");
            }
        }
    }

    protected String transformJavaToPlantUML(File file) {
        try {
            plantUMLBuilder = new StringBuilder();
            plantUMLRelations = new StringBuilder();
            classOptions = new StringBuilder();
            // Lire le contenu du fichier .java
            String javaCode = new String(Files.readAllBytes(file.toPath()));

            // Parser le contenu du fichier Java
            ParseResult<CompilationUnit> parseResult = new JavaParser().parse(javaCode);

            // Vérifier s'il n'y a pas d'erreur de parsing
                CompilationUnit cu = parseResult.getResult().get();
                List<TypeDeclaration<?>> typeDeclarations = cu.getTypes();

                // Parcourir les déclarations de type
                for (TypeDeclaration<?> typeDeclaration : typeDeclarations) {
                    // Ajouter le nom de la classe
                    if(typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                        ClassOrInterfaceDeclaration clazz = typeDeclaration.asClassOrInterfaceDeclaration();
                        // Est-ce que la classe possède des extends ?
                        if (clazz.getExtendedTypes().isNonEmpty()) {
                            NodeList<ClassOrInterfaceType> extendedTypes = clazz.getExtendedTypes();
                            String substring = extendedTypes.toString().substring(1, extendedTypes.toString().length() - 1);
                            classOptions.append(" extends ").append(substring);
                        }
                        // Est-ce que la classe possède des implements ?
                        if(clazz.getImplementedTypes().isNonEmpty())
                        {
                            NodeList<ClassOrInterfaceType> implementedTypes = clazz.getImplementedTypes();
                            String substring = implementedTypes.toString().substring(1, implementedTypes.toString().length() - 1);
                            classOptions.append(" implements ").append(substring);
                        }

                        if(clazz.isAbstract())
                        {
                            plantUMLBuilder.append("abstract class ").append(typeDeclaration.getName()).append(classOptions).append(" <<abstract>> {\n");
                        } else if(clazz.isInterface()){
                            plantUMLBuilder.append("interface ").append(typeDeclaration.getName()).append(classOptions).append(" <<interface>> {\n");
                        } else {
                            plantUMLBuilder.append("class ").append(typeDeclaration.getName()).append(classOptions).append("{\n");
                        }

                    } else if(typeDeclaration instanceof EnumDeclaration)
                    {
                        plantUMLBuilder.append("enum ").append(typeDeclaration.getName()).append(" <<enumeration>> {\n");
                    }

                    // Ajouter les attributs de la classe
                    for (FieldDeclaration fieldDeclaration : typeDeclaration.getFields()) {
                        for (VariableDeclarator variable : fieldDeclaration.getVariables()) {
                            if(!fieldDeclaration.getElementType().isPrimitiveType() && fieldDeclaration.getElementType().toString().compareTo("String") != 0)
                            {
                                ecrireRelations(typeDeclaration, fieldDeclaration, variable);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return plantUMLBuilder.toString();
    }

    protected String ecrireEnTete() {
    return """
            @startuml

            skinparam style strictuml
            skinparam classAttributeIconSize 0
            skinparam classFontStyle Bold
            hide enum methods
            hide empty members

            """;
    }

    protected String ecrirePiedDePage() {
        return plantUMLRelations + "\n\n@enduml\n";
    }

    private String getReturnType(Type origin)
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
        if(origin instanceof ClassOrInterfaceType) {
            ClassOrInterfaceType classOrInterfaceType = origin.asClassOrInterfaceType();
            Optional<NodeList<Type>> typeArguments = classOrInterfaceType.getTypeArguments();

            //list of arguments might have different length
            if(typeArguments.isPresent())
            {
                return ": " + typeArguments.get().get(0);
            }
        }
        if(origin.isArrayType()) {
            return res.substring(0, res.length()-2) + "[*]";
        } else {
            return res;
        }
    }

    private String getVisibility(String origin)
    {
        return switch (origin) {
            case "public " -> "+";
            case "private " -> "-";
            case "protected " -> "#";
            default -> "~";
        };
    }

    private String getFields(String origin) {
        return switch (origin) {
            case "static" -> "{static}";
            case "final" -> "{readOnly}";
            default -> "";
        };
    }

    private  <T extends CallableDeclaration<?>> StringBuilder getParameters(CallableDeclaration<T> method)
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

    private void ecrireAttributs(FieldDeclaration fieldDeclaration, VariableDeclarator variable){
        List<Modifier> modifiers = fieldDeclaration.getModifiers(); // public / static / final / etc...
        StringBuilder fields = new StringBuilder();

        // On passe tous les modificateurs
        for(int i = 1; i<modifiers.size(); i++) {
            fields.append(getFields(modifiers.get(i).toString().toLowerCase()));
        }

        plantUMLBuilder.append(getVisibility(modifiers.get(0).toString().toLowerCase())).append(" ").append(fields).append(" ").append(variable.getName()).append(" ").append(getReturnType(fieldDeclaration.getElementType())).append("\n");
    }

    private void ecrireMethodes(MethodDeclaration method) {
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
            plantUMLBuilder.append(" ").append(fields).append(" ").append(method.getName()).append("(").append(getParameters(method)).append(") ").append(getReturnType(method.getType())).append("\n");
        }
    }

    private void ecrireConstructeurs(ConstructorDeclaration constructor) {
        List<Modifier> modifiers = constructor.getModifiers(); // public / static / final / etc...
        StringBuilder fields = new StringBuilder();

        // On passe tous les modificateurs
        for(int i = 1; i<modifiers.size(); i++) {
            fields.append(getFields(modifiers.get(i).toString().toLowerCase()));
        }
        plantUMLBuilder.append(getVisibility(modifiers.get(0).toString().toLowerCase())).append(" <<Create>>").append(fields).append(" ").append(constructor.getName()).append("(").append(getParameters(constructor)).append(")\n");

    }

    private void ecrireRelations(TypeDeclaration<?> typeDeclaration, FieldDeclaration fieldDeclaration, VariableDeclarator variable)
    {
        plantUMLRelations.append(typeDeclaration.getName().asString()).append(" --> ").append(getReturnType(fieldDeclaration.getElementType()).substring(2)).append(" : \"1\\n").append(getVisibility(fieldDeclaration.getModifiers().get(0).toString().toLowerCase())).append(variable.getName()).append("\"\n");
    }
}