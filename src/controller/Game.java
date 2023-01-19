package controller;

import model.*;
import model.pieces.*;
import view.ChessWindow;

import java.util.ArrayList;

public class Game
{
    private final GameFacade gameFacade; // Façade qui nous sert à démarrer / stopper la partie / créer les pièces...
    private ChessBoard chessBoard; // Plateau où se déroulera la partie
    private ChessGame chessGame; // Méthodes logiques du jeu
    private Player[] players; // Tableau de joueurs qui seront impliqués dans la partie
    private String[] names; // Tableau des noms des joueurs qui seront impliqués dans la partie
    private boolean whitesFirst; // Détermine si les blancs jouent en premier ou non

    public Game(String[] names, boolean whitesFirst)
    {
        gameFacade = new GameFacade();
        this.names = names;
        this.whitesFirst = whitesFirst;
    }


    /*
        startGame() : proc : void
        Cette procédure permet de lancer une partie

        var:
        - view : ChessWindows : fenêtre de jeu où sera joué la partie
    */
    public void startGame(){
        // Création du plateau
        this.chessBoard = gameFacade.createChessBoard();

        // Création des joueurs
        this.players = gameFacade.createsPlayers(names, whitesFirst);

        // Création de l'instance de jeu
        this.chessGame = new ChessGame(chessBoard);

        // Remplissage du plateau avec des cases
        chessBoard.createSquares();

        // Ajout des pièces blanches
        players[0].addPieces(new ArrayList<>(chessBoard.getPieces()).subList(0, 15));

        // Ajout des pièces noires
        players[1].addPieces(new ArrayList<>(chessBoard.getPieces()).subList(48, 63));

        // Création de la fenêtre de jeu
        ChessWindow view = new ChessWindow(chessBoard, this);

        // Chaque case se fait observer par la vue
        for(Square square : chessBoard.getSquares())
        {
            // Ajout de l'observeur à la case
            square.setObserver(view);
        }
    }

    /*
        stopGame() : proc : void
        Cette procédure permet d'arrêter une partie
     */
    public void stopGame(){}

    /*
        getChessGame() : func : ChessGame
        Cette fonction permet de récupérer l'instance du jeu
     */
    public ChessGame getChessGame()
    {
        return this.chessGame;
    }

    /*
        getPlayers() : func : Player[]
        Cette fonction permet de retourner les joueurs de la partie
     */
    public Player[] getPlayers()
    {
        return this.players;
    }

    /*
        getPlayerTurn() : func : Player
        Cette fonction permet de retourner le joueur qui doit jouer actuellement
     */
    public Player getPlayerTurn()
    {
        return (players[0].isTurn()) ? players[0] : players[1];
    }

    /*
        changeTurn() : proc : void
        Cette fonction permet de changer le tour d'un joueur et de l'accorder à l'autre
     */
    public void changeTurn()
    {
        // Si c'était le tour du premier joueur
        if(players[0].isTurn())
        {
            // On enlève le tour à ce jour
            players[0].setTurn(false);

            // Le roi ne possède plus de pièces qui l'attaque (sinon il serait en échec et mat)
            ((King) chessBoard.getKingLocation(players[0].getColor()).getPiece()).resetAttackers();

            // On l'accorde à l'autre
            players[1].setTurn(true);

        // Si c'était le tour du deuxième joueur
        } else {
            // On enlève le tour à ce jour
            players[0].setTurn(true);

            // On l'accorde à l'autre
            players[1].setTurn(false);

            // Le roi ne possède plus de pièces qui l'attaque (sinon il serait en échec et mat)
            ((King) chessBoard.getKingLocation(players[1].getColor()).getPiece()).resetAttackers();
        }

    }

}
