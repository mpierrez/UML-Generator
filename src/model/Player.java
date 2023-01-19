package model;

import model.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Player
{
    private final String name; // Nom du joueur
    private final GameColor color; // Couleur du joueur
    private boolean turn; // Est-ce que c'est au tour du joueur ?
    private List<Piece> pieces; // Pièces du joueurs
    private List<Piece> capturedPieces; // Pièces que le joueur a capturé

    /*
        Création du joueur
     */
    public Player(String name, GameColor color, boolean round)
    {
        this.name = name;
        this.turn = round;
        this.color = color;
        this.pieces = new ArrayList<>();
        this.capturedPieces = new ArrayList<>();
    }

    /*
        getName() : func : String
        Cette fonction permet d'obtenir le nom du joueur
     */
    public String getName()
    {
        return this.name;
    }

    /*
        isTurn() : func : boolean
        Cette fonction permet de savoir si c'est au joueur de jouer
     */
    public boolean isTurn()
    {
        return this.turn;
    }

    /*
        getColor() : func : GameColor
        Cette fonction permet d'obtenir la couleur du joueur
     */
    public GameColor getColor()
    {
        return this.color;
    }

    /*
        getPieces() : func : List<Pieces>
        Cette fonction permet d'obtenir les pièces du joueur
     */
    public List<Piece> getPieces()
    {
        return this.pieces;
    }

    /*
        getCapturedPieces() : func : List<Pieces>
        Cette fonction permet d'obtenir les pièces que le joueur a capturé
     */
    public List<Piece> getCapturedPieces()
    {
        return this.capturedPieces;
    }

    /*
        addPieces() : proc : void
        Cette procédure permet de définir/d'ajouter des pièces au joueur

        param:
        - pieces : List<Piece> : Pièces à ajouter au joueur
     */
    public void addPieces(List<Piece> pieces){
        this.pieces.addAll(pieces);
    }

    /*
        setTurn() : proc : void
        Cette procédure permet de retirer / d'obtenir le tour du joueur

        param:
        - turn : boolean : Est-ce qu'on retire ou on donne le tour au joueur ?
     */
    public void setTurn(boolean turn)
    {
        this.turn = turn;
    }

}
