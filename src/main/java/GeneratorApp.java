import model.Generation;
import view.MainMenuWindow;

import java.io.File;

public class GeneratorApp {

    public static void main(String[] args) {
        //C:\Users\mathy\OneDrive\Bureau\Robert_Schuman\2\A32\a31-chessgame\ChessGame\src\model
        Generation generation = new Generation(new File("Aucun fichier sélectionné"), System.getProperty("user.dir"));
        generation.addObserver(new MainMenuWindow(generation));
    }
}