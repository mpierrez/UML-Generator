package model.pieces;

import model.Coordinate;
import model.GameColor;

public class Queen extends Piece {

    public Queen(GameColor color)
    {
        super(color);
        this.imagePath = (color == GameColor.WHITE) ? "/img/whiteQueen.png" : "/img/blackQueen.png";
    }

    @Override
    public Coordinate[] move() {
        Coordinate[] res = new Coordinate[48];
        int i = 1;

        // Mouvements de la tour (de haut en bas et de gauche à droite)
        for(int j = 0; j<28; j+=4)
        {
            res[j] = new Coordinate(0, i);
            res[j+1] = new Coordinate(0, -i);
            res[j+2] = new Coordinate(i, 0);
            res[j+3] = new Coordinate(-i, 0);
            i++;
        }

        // Mouvements du fou (diagonales)
        i = 1;
        for(int j = 28; j<res.length; j+=4)
        {
            res[j] = new Coordinate(-i, i);
            res[j+1] = new Coordinate(-i, -i);
            res[j+2] = new Coordinate(i, i);
            res[j+3] = new Coordinate(i, -i);
            i++;
        }
        return res;
    }
}
