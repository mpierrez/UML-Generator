package controller;

import model.*;
import model.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class GameFacade {

    /*
        createsPieces() : func : List<Piece>
        Cette fonction permet d'initialiser les pièces afin de les placer sur le plateau de jeu
    */
    public List<Piece> createsPieces(){
        return new ArrayList<>() {{
            add(new Rook(GameColor.BLACK)); add(new Knight(GameColor.BLACK)); add(new Bishop(GameColor.BLACK)); add(new Queen(GameColor.BLACK)); add(new King(GameColor.BLACK)); add(new Bishop(GameColor.BLACK)); add(new Knight(GameColor.BLACK)); add(new Rook(GameColor.BLACK));
            add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK)); add(new Pawn(GameColor.BLACK));
            add(null); add(null); add(null); add(null); add(null); add(null); add(null); add(null);
            add(null); add(null); add(null); add(null); add(null); add(null); add(null); add(null);
            add(null); add(null); add(null); add(null); add(null); add(null); add(null); add(null);
            add(null); add(null); add(null); add(null); add(null); add(null); add(null); add(null);
            add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE)); add(new Pawn(GameColor.WHITE));
            add(new Rook(GameColor.WHITE)); add(new Knight(GameColor.WHITE)); add(new Bishop(GameColor.WHITE)); add(new Queen(GameColor.WHITE)); add(new King(GameColor.WHITE)); add(new Bishop(GameColor.WHITE)); add(new Knight(GameColor.WHITE)); add(new Rook(GameColor.WHITE));

        }};
    }
    /*
        createPlayers(String[] names, boolean whitesFirst) : func : Player[]
        Cette fonction permet d'initialiser les joueurs qui joueront à la partie

        param:
        - names : String[] : Noms qui seront assignés aux joueurs crées
        - whitesFirst : boolean : détermine si les blancs jouent en premier ou non
    */
    public Player[] createsPlayers(String[] names, boolean whitesFirst){
        return new Player[]{
                // Si les Blancs jouent en premier VS si les Noirs jouent en premier
                (whitesFirst) ? new Player(names[0], GameColor.WHITE, true) : new Player(names[0], GameColor.WHITE, false),
                (whitesFirst) ? new Player(names[1], GameColor.BLACK, false) : new Player(names[1], GameColor.BLACK, true)
        };
    }

    /*
        createChessBoard() : func : ChessBoard
        Cette fonction permet d'initialiser le plateau de jeu où se déroulera la partie
    */
    public ChessBoard createChessBoard(){
        return new ChessBoard(createsPieces());
    }

}
