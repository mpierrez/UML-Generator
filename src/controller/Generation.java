package controller;

import model.*;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generation{

    private GenerationStrategy strategy;
    protected String uml="";
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

    public static void copyFile(File file)
    {
        try {
            InputStream is = new FileInputStream(file.getAbsolutePath());
            OutputStream os = new FileOutputStream(file.getName());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            is.close();
            os.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String getPackageName(File file)
    {
        String filePath = file.getAbsolutePath();
        filePath = filePath.replace(File.separator, ".");
        Pattern pattern = Pattern.compile("(.*\\.java\\.)(.*)(\\..*)");
        Matcher matcher = pattern.matcher(filePath);
        if(matcher.find())
        {
            return matcher.group(2);
        }
        return "default";
    }

    public void ecrireEnTete() {
        uml += """
                @startuml

                skinparam style strictuml
                skinparam classAttributeIconSize 0
                skinparam classFontStyle Bold
                hide enum methods
                hide empty members

                """;
    }

    public void ecrirePiedPage() {
        uml += "@enduml";
    }

    public void ecrireClasse() {
        uml += classe.toString();
    }

    public void ecrireAttributs() throws ClassNotFoundException {
        for (Attribut attribut : classe.getAttributs()) {
            if(attribut.type() != null) {
                if (classe instanceof Enumeration) {
                    if (attribut.estUneEnum()) {
                        uml += attribut.toEnum();
                    }
                } else {
                    uml += attribut.toString();
                }
            }
        }
    }

    public void ecrireMethodes() throws ClassNotFoundException {
        for(Methode methode : classe.getMethodes())
        {
            if(classe instanceof Interface){
                uml += methode.toInterface();
            }
            else{
                uml += methode.toString();
            }
        }
        uml+="}\n";

    }

    public void ecrireConstructeurs() {
        for(Constructeur constructeur : classe.getConstructeurs())
        {
            uml += constructeur.toString();
        }
    }

    public void ecrireRelations() {
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
    }

    public void ecrireGlobale() throws ClassNotFoundException {
        ecrireAttributs();
        ecrireConstructeurs();
        ecrireMethodes();
        ecrireRelations();
    }

    public void ecrire() throws IOException {
        ecriture.write(uml);
    }
}