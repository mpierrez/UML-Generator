package model.pieces;

import model.Coordinate;
import model.GameColor;

public class Pawn extends Piece
{

    private boolean hasMovedTwo = false;

    public Pawn(GameColor color)
    {
        super(color);
        this.imagePath = (color == GameColor.WHITE) ? "/img/whitePawn.png" : "/img/blackPawn.png";
    }

    @Override
    public Coordinate[] move() {
        Coordinate[] res = new Coordinate[4];
        if(this.color == GameColor.BLACK)
        {
            if(moved)
            {
                res[0] = new Coordinate(0,1);
                res[1] = new Coordinate(-1,1);
                res[2] = new Coordinate(1,1);
            } else {
                res[0] = new Coordinate(0,1);
                res[1] = new Coordinate(0,2);
                res[2] = new Coordinate(-1,1);
                res[3] = new Coordinate(1,1);
            }
        } else {
            if(moved)
            {
                res[0] = new Coordinate(0,-1);
                res[1] = new Coordinate(-1,-1);
                res[2] = new Coordinate(1,-1);
            } else {
                res[0] = new Coordinate(0,-1);
                res[1] = new Coordinate(0,-2);
                res[2] = new Coordinate(-1,-1);
                res[3] = new Coordinate(1,-1);
            }
        }
        return res;
    }

    public boolean hasMovedTwo()
    {
        return this.hasMovedTwo;
    }

    public void setMovedTwo(boolean hasMovedTwo)
    {
        this.hasMovedTwo = hasMovedTwo;
    }
}
