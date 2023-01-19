package model.pieces;

import model.Coordinate;
import model.GameColor;
import model.Square;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece
{

    private boolean inCheck;
    private boolean inCheckMate;
    private List<Square> attackers;
    private List<Square> protectors;

    public King(GameColor color)
    {
        super(color);
        this.attackers = new ArrayList<>();
        this.imagePath = (color == GameColor.WHITE) ? "/img/whiteKing.png" : "/img/blackKing.png";
    }

    public void setInCheck(boolean value)
    {
        this.inCheck = value;
    }
    public void setInCheckMate(boolean value){ this.inCheckMate = value;}

    public List<Square> getAttackers()
    {
        return this.attackers;
    }

    public void resetAttackers()
    {
        this.attackers = new ArrayList<>();
    }

    public List<Square> getProtectors()
    {
        return this.protectors;
    }

    public void resetProtectors()
    {
        this.protectors = new ArrayList<>();
    }


    @Override
    public Coordinate[] move() {
        if(moved){
            return new Coordinate[]{new Coordinate(0, 1),
                    new Coordinate(1, 1),
                    new Coordinate(1, 0),
                    new Coordinate(1, -1),
                    new Coordinate(0, -1),
                    new Coordinate(-1, -1),
                    new Coordinate(-1, 0),
                    new Coordinate(-1, 1)
            };
        }
        else{
            return new Coordinate[]{new Coordinate(0, 1),
                    new Coordinate(1, 1),
                    new Coordinate(1, 0),
                    new Coordinate(1, -1),
                    new Coordinate(0, -1),
                    new Coordinate(-1, -1),
                    new Coordinate(-1, 0),
                    new Coordinate(-1, 1),
                    new Coordinate(2, 0),
                    new Coordinate(-2, 0)
            };
        }
    }

    public boolean IsInCheck()
    {
        return this.inCheck;
    }

    public boolean IsInCheckMate()
    {
        return this.inCheckMate;
    }
}
