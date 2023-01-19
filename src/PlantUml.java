import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;

public class PlantUml {
    private static Generation g;

    public static void generate(String subFile, File file) throws ClassNotFoundException, IOException {
        FileWriter ecriture=null;
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
            {
                if(f.getName().equals("pieces"))
                {
                    generate("model", f);
                } else {
                    Class c = (subFile.equals("model")) ? Class.forName(subFile + "." + file.getName() + "." + f.getName().split("\\.")[0]) : Class.forName(file.getName() + "." + f.getName().split("\\.")[0]);
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
                    //Générer le fichier puml
                    String nomFichier = f.getName().split("\\.")[0];
                    File fichier = new File(nomFichier+ ".puml");
                    ecriture = new FileWriter(nomFichier+ ".puml");

                    if (fichier.exists()) {
                        if (fichier.delete())
                            System.out.println("Le fichier existe déjà, nous l'avons donc supprimé pour en générer un nouveau!");
                    }
                    fichier.createNewFile();

                    //Générer l'objet g de type Génération
                    g = new Generation(ecriture, classe);

                    //Lancement de la sélection de l'utilisateur
                    g.ecrireEnTete();
                    g.ecrireClasse();
                    g.ecrireGlobale();
                    g.ecrirePiedPage();
                    g.ecrire();
                    System.out.println("Votre fichier a été généré avec succès!");
                    if (ecriture != null) ecriture.close();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 1) {

                //Générer l'objet classe de type Classe avec l'argument entré en paramètre
                File dir = new File(System.getProperty("user.dir") + "/src");
                for(File file : Objects.requireNonNull(dir.listFiles()))
                {
                    generate(".", file);
                }
        } else if (args.length > 1) {
            throw new NullPointerException("Vous avez entré beaucoup trop d'arguments!");
        } else {
            throw new NullPointerException("Il manque un argument à votre commande");
        }
    }

    public static void traiterMenu(Classe classe) throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        boolean termine = false;
        g.ecrireEnTete();
        g.ecrireClasse();
        while (!termine) {
            afficherMenu(classe.getNom());
            System.out.print("Votre choix ? ");
            String ligne = sc.nextLine();
            int val = Integer.parseInt(ligne);
            switch (val) {
                case 1 -> {
                    g.ecrireConstructeurs();
                }
                case 2 -> {
                    System.out.print("Attributs ajoutés ");
                    g.ecrireAttributs();
                }
                case 3 -> {
                    System.out.print("Méthodes ajoutées ");
                    g.ecrireMethodes();
                }
                case 4 -> {
                    System.out.print("Toute la sélection a été ajoutée ");
                    g.ecrireGlobale();
                }
                case 5 ->{
                    System.out.print("Toute la sélection a été supprimée ");
                    g.reset();
                }
                case 6 -> termine = true;
            }
        }
        g.ecrirePiedPage();
        g.ecrire();
    }

    public static void afficherMenu(String nom) {
        System.out.println("Générer le diagramme de classe pour la classe " + nom);
        System.out.println("MENU :");
        System.out.println("1. Ajouter les constructeurs");
        System.out.println("2. Ajouter les attributs");
        System.out.println("3. Ajouter les méthodes");
        System.out.println("4. Tout ajouter");
        System.out.println("5. Tout effacer");
        System.out.println("6. Générer le fichier");
    }
}