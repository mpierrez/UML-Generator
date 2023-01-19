package model.pieces;

import model.Coordinate;
import model.GameColor;

public class Knight extends Piece {

    public Knight(GameColor color)
    {
        super(color);
        this.imagePath = (color == GameColor.WHITE) ? "/img/whiteKnight.png" : "/img/blackKnight.png";
    }


    @Override
    public Coordinate[] move() {
        return new Coordinate[]{
                new Coordinate(2, -1),
                new Coordinate(2, 1),
                new Coordinate(-2, -1),
                new Coordinate(-2, 1),

                new Coordinate(1, 2),
                new Coordinate(1, -2),
                new Coordinate(-1, -2),
                new Coordinate(-1, 2)
        };
    }
}
