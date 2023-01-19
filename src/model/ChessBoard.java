package model;

import model.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessBoard {
    private Square[] squares; // Cases du plateau de jeu actuel
    private List<Piece> pieces; // Liste des pièces présentes sur le plateau de jeu actuel

    public static final int LENGHT = 8; // Longueur du plateau de jeu (en cases)
    public static final int HEIGHT = 8; // Hauteur du plateau de jeu (en cases)

    public ChessBoard(List<Piece> pieces) {
        squares = new Square[LENGHT*HEIGHT];
        this.pieces = pieces;
    }

    /*
        createSquares() : proc : void
        Cette procédure permet de remplir le tableau de cases

        var:
        - x : int : Coordonnée X de la case crée
        - y : int : Coordonnée Y de la case crée
     */
    public void createSquares(){
        int x = 0;
        int y = 0;
        for (int i = 0; i < this.squares.length; i++){
            // On crée une case et on l'ajoute au tableau de cases, on lui assigne également la pièce dont il dispose (null si aucune)
            this.squares[i] = new Square(new Coordinate(x, y), pieces.get(i));
            x++;

            // Permet de faire un retour à la ligne
            if(x%LENGHT == 0)
            {
                x = 0;
                y++;
            }
        }
    }

    /*
        getSquares() : func : Square[]
        Cette fonction permet de retourner toutes les cases du plateau de jeu actuel
     */
    public Square[] getSquares()
    {
        return this.squares;
    }

    /*
        getSquareWithCoords(int x, int y) : func : Square
        Cette fonction permet de retrouver une case à l'aide des coordonnées passées en paramètres

        param:
        - x : int : Coordonnée X de la case recherchée
        - y : int : Coordonnée Y de la case recherchée
     */
    public Square getSquareWithCoords(int x, int y)
    {
        // On regarde chaque case du plateau
        for(Square square : squares)
        {
            // Si les coordonnées de la case correspondent à celle recherchée en X et en Y
            if(square.getCoords().getX() == x && square.getCoords().getY() == y)
            {
                // On renvoie cette case, c'est celle qu'on cherche
                return square;
            }
        }
        // La case est en dehors du plateau / n'existe pas
        return null;
    }

    /*
        getSquaresOccupiedByColor(GameColor color) : func : List<Square>
        Cette fonction permet d'obtenir toutes les cases occupées par les pièces d'une certaine couleur

        param:
        - color : GameColor : Couleur que doivent avoir les pièces des cases renvoyées
     */
    public List<Square> getSquaresOccupiedByColor(GameColor color)
    {
        // Création de la liste
        List<Square> res = new ArrayList<>();

        // On regarde chaque case du plateau
        for(Square square : squares)
        {
            try{
                // Est-ce que la case contient une pièce de la couleur recherchée ?
                if(square.getPiece().getColor() == color)
                {
                    // Si oui, on l'ajoute à la liste
                    res.add(square);
                }
            } catch(NullPointerException ex)
            {
                // On tombe sur une case avec aucune pièce
            }
        }

        // On retourne le résultat
        return res;
    }

    /*
        getPieces() : func : List<Piece>
        Cette fonction permet de retourner toutes les pièces présentes sur le plateau de jeu actuel
     */
    public List<Piece> getPieces()
    {
        return this.pieces;
    }

    /*
        getKingLocation() : func : Square
        Cette fonction permet d'obtenir la case où se trouve le roi de la couleur passée en paramètre

        param:
        - color : GameColor : Couleur du roi recherché
     */
    public Square getKingLocation(GameColor color)
    {
        // On regarde chaque case du plateau
        for(Square square : squares)
        {
            // Est-ce que la case contient un roi de la couleur recherchée ?
            if(square.getPiece() instanceof King && square.getPiece().getColor() == color)
            {
                // Si oui, on retourne ce roi
                return square;
            }
        }

        // Le roi n'a pas été trouvé et/ou a déjà été mangé
        return null;
    }
}
