package model.pieces;

import model.Coordinate;
import model.GameColor;

public class Rook extends Piece {

    public Rook(GameColor color)
    {
        super(color);
        this.imagePath = (color == GameColor.WHITE) ? "/img/whiteRook.png" : "/img/blackRook.png";
    }

    @Override
    public Coordinate[] move() {
        Coordinate[] res = new Coordinate[28];
        int i = 1;
        for(int j = 0; j<res.length; j+=4)
        {
            // Mouvements progressifs en haut
            res[j] = new Coordinate(0, i);

            // Mouvements progressifs en bas
            res[j+1] = new Coordinate(0, -i);

            // Mouvements progressifs à droite
            res[j+2] = new Coordinate(i, 0);

            // Mouvements progressifs à gauche
            res[j+3] = new Coordinate(-i, 0);
            i++;
        }
        return res;
    }
}
