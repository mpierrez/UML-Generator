package model.pieces;

import model.GameColor;

public class PieceFactory
{
    public PieceFactory() {}

    public Piece createKing(GameColor color)
    {
        return new King(color);
    }

    public Piece createQueen(GameColor color)
    {
        return new Queen(color);
    }

    public Piece createKnight(GameColor color)
    {
        return new Knight(color);
    }

    public Piece createBishop(GameColor color)
    {
        return new Bishop(color);
    }

    public Piece createRook(GameColor color)
    {
        return new Rook(color);
    }

    public Piece createPawn(GameColor color)
    {
        return new Pawn(color);
    }
}
