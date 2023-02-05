import controller.Generation;
import view.MainMenuWindow;

public class GeneratorApp {

    public static void main(String[] args) {
        Generation generation = new Generation();
        new MainMenuWindow(generation);
        /*if (args.length == 1) {

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
        }*/
    }
}