package controller;

import model.Generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class SingleGeneration extends Generation implements GenerationStrategy
{
    private StringBuilder uml = new StringBuilder();
    private StringBuilder relations = new StringBuilder();
    protected FileWriter ecriture;

    public SingleGeneration(File file, String savePath) {
        super(file, savePath);
    }

    @Override
    public void generate(File file, String savePath) throws IOException
    {
        if(file.isDirectory())
        {
            uml.setLength(0);
            relations.setLength(0);
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    generate(f, savePath + "/" + f.getName());
                } else {
                    uml.append(transformJavaToPlantUML(f));
                    relations.append(plantUMLRelations);
                }
            }
            write(file, savePath);
        } else {
            uml.append(transformJavaToPlantUML(file));
            relations.append(plantUMLRelations);
            write(file, savePath);
        }
    }

    @Override
    public void write(File file, String savePath) throws IOException {
        //Générer le fichier puml
        String nomFichier = file.getName().split("\\.")[0];
        File fichier = new File(savePath + "//" + nomFichier+ ".puml");
        this.ecriture = new FileWriter(savePath + "//" + nomFichier+ ".puml");

        fichier.createNewFile();

        //Lancement de l'écriture dans le fichier
        ecriture.write(ecrireEnTete());
        ecriture.write(uml.toString());
        ecriture.write(relations.toString());
        ecriture.write(ecrirePiedDePage());

        System.out.println("Le fichier " + nomFichier + " a été généré avec succès!");
        if (ecriture != null) ecriture.close();
    }
}
