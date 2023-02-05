package controller;

import model.Classe;
import model.ClasseAbstraite;
import model.Enumeration;
import model.Interface;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class MultipleGeneration extends Generation implements GenerationStrategy
{

    @Override
    public void generate(File file, String savePath) throws IOException {
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

    @Override
    public void write(File file, String savePath) throws IOException {
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
        ecriture.write(ecrireEnTete());
        ecriture.write(transformJavaToPlantUML(file));
        ecriture.write("\n\n@enduml\n");

        System.out.println("Le fichier " + nomFichier + " a été généré avec succès!");
        if (ecriture != null) ecriture.close();
    }
}
