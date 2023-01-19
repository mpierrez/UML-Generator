package model;

import model.pieces.Piece;

public class Square{

    private final Coordinate coords;
    private Piece piece;
    private SquareObserver observer;

    public Square(Coordinate coords, Piece piece)
    {
        this.coords = coords;
        this.piece = piece;
    }

    /*
        getCoords() : func : Coordinate
        Cette fonction permet d'obtenir les coordonnées de la case
     */
    public Coordinate getCoords(){
        return this.coords;
    }

    /*
        getPiece() : func : Piece
        Cette fonction permet d'obtenir la pièce présente sur la case
     */
    public Piece getPiece(){
        return this.piece;
    }

    /*
        setPiece(Piece piece) : proc : void
        Cette procédure permet de définir une pièce sur la case

        param:
        - piece : Piece : Pièce à ajouter à la case
     */
    public void setPiece(Piece piece){
        this.piece = piece;
    }

    /*
        setObserver(SquareObserver observer) : proc : void
        Cette procédure permet d'ajouter un observeur à la case

        param:
        - observer : SquareObserver : Observeur à ajouter à la case
     */
    public void setObserver(SquareObserver observer)
    {
        this.observer = observer;
    }

    /*
        notifyMove() : proc : void
        Cette procédure permet d'informer la vue que la pièce a bougée
     */
    public void notifyMove()
    {
        observer.updateGraphics(this);
    }

    /*
        notifyPieceCaptured() : proc : void
        Cette procédure permet d'informer la vue que la pièce de cette case a été capturée
     */
    public void notifyPieceCaptured()
    {
        observer.updateListOfCapturedPieces();
    }
}
