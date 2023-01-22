package controller;

import model.Classe;
import model.ClasseAbstraite;
import model.Enumeration;
import model.Interface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class SingleGeneration extends Generation implements GenerationStrategy
{
    private File fichier;

    @Override
    public void generate(File file, String savePath) throws IOException, ClassNotFoundException
    {
        System.out.println("Generation en single");
        //Générer le fichier puml
        if(fichier == null) {
            String nomFichier = file.getName().split("\\.")[0];
            fichier = new File(savePath + "\\" + nomFichier + ".puml");
            if (fichier.exists()) {
                if (fichier.delete())
                    System.out.println("Le fichier " + fichier.getName() + " existe déjà, nous l'avons donc supprimé pour en générer un nouveau!");
            }
            fichier.createNewFile();

            this.ecriture = new FileWriter(savePath + "\\" + nomFichier + ".puml");
            ecrireEnTete();
        }
        if(file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    generate(f, savePath + "/" + f.getName());
                } else {
                    write(f, savePath);
                }
            }
        } else {
            write(file, savePath);
        }
        if(fichier.getName().split("\\.")[0].equals(file.getName())) {
            System.out.println(fichier.getName() + " == " + file.getName());
            ecrirePiedPage();
            ecrire();
            System.out.println("Le fichier " + fichier.getName() + " a été généré avec succès!");
            fichier = null;
            if (ecriture != null) ecriture.close();
        } else {
            System.out.println(fichier.getName() + " != " + file.getName());
        }
    }

    @Override
    public void write(File file, String savePath) throws ClassNotFoundException, IOException
    {
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

        //Lancement de la sélection de l'utilisateur
        ecrireClasse();
        ecrireGlobale();
        ecrire();
    }
}
