package model.pieces;

import model.ChessBoard;
import model.Coordinate;
import model.GameColor;

public class Bishop extends Piece {

    public Bishop(GameColor color)
    {
        super(color);
        this.imagePath = (color == GameColor.WHITE) ? "/img/whiteBishop.png" : "/img/blackBishop.png";
    }

    @Override
    public Coordinate[] move() {
        Coordinate[] res = new Coordinate[20];
        int i = 1;
        for(int j = 0; j<res.length; j+=4)
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
