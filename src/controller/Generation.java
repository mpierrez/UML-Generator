package controller;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Generation{

    private String uml="";
    private FileWriter ecriture = null;
    private Classe classe;

    public void generate(File file, String savePath) throws ClassNotFoundException, IOException {
        if(file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    File dir = new File(savePath + "/" + f.getName());
                    if(dir.mkdir())
                    {
                        generate(f, dir.getAbsolutePath());
                    }
                } else {
                    write(f, savePath);
                }
            }
        } else {
            write(file, savePath);
        }
    }

    public void write(File file, String savePath) throws ClassNotFoundException, IOException {
        Class<?> c = Class.forName(file.getClass().getName());
        Classe classe = new Classe(c);
        if (Modifier.isAbstract(c.getModifiers())) {
            if (c.isInterface()) {
                classe = new Interface(c);
            } else {
                classe = new ClasseAbstraite(c);
            }
        } else if (c.isEnum()) {
            classe = new Enumeration(c);
        }
        this.classe = classe;

        //Générer le fichier puml
        String nomFichier = file.getName().split("\\.")[0];
        File fichier = new File(savePath + "\\" +nomFichier+ ".puml");
        this.ecriture = new FileWriter(savePath + "\\" + nomFichier+ ".puml");

        if (fichier.exists()) {
            if (fichier.delete())
                System.out.println("Le fichier " + nomFichier + " existe déjà, nous l'avons donc supprimé pour en générer un nouveau!");
        }
        fichier.createNewFile();

        //Lancement de la sélection de l'utilisateur
        ecrireEnTete();
        ecrireClasse();
        ecrireGlobale();
        ecrirePiedPage();
        ecrire();
        System.out.println("Le fichier " + nomFichier + " a été généré avec succès!");
        if (ecriture != null) ecriture.close();
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
    public void reset(){
        uml.replace(uml, "");
    }
}