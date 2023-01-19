package view;

import controller.Game;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ChessWindow extends JFrame implements SquareObserver {

    // Controllers fields
    private final Game game;
    private final ChessGame chessGame;

    // Graphics fields
    private final JPanel panel;
    private final JPanel left;
    private final JPanel right;
    private GridBagConstraints leftConstraints = new GridBagConstraints();
    private GridBagConstraints rightConstraints = new GridBagConstraints();
    private final JLabel instructionsLabel;
    private final SquareJLabel[][] labels = new SquareJLabel[8][8];
    private List<Square> highLightSquares;

    // Logical fields
    private Square previousSquare;
    private int nbClicks = 0;

    public ChessWindow(ChessBoard chessboard, Game game) {
        // On ajoute un titre à notre fenêtre
        super("Chess Window");

        // On définit les instances nécessaires
        this.game = game;
        this.chessGame = game.getChessGame();
        // Models fields
        this.panel = new JPanel();
        this.instructionsLabel = new JLabel("Début de la partie ! C'est à " + game.getPlayerTurn().getName() + " (" + game.getPlayerTurn().getColor() + ") de commencer !");

        // Configuration du panel central (celui où sera joué la partie)
        panel.setLayout(new GridLayout(8, 8)); // On applique un GridLayout car on connait à l'avance le nombre de ligne et de colonnes du plateau
        panel.setSize(150, 150); // On définit une taille de fenetre
        setExtendedState(JFrame.MAXIMIZED_BOTH); // On met en plein écran
        setDefaultCloseOperation(EXIT_ON_CLOSE); // On définit l'opération quand on clique sur la croix pour quitter le programme

        // On centre le label d'instructions
        instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionsLabel.setVerticalAlignment(SwingConstants.CENTER);


        // On va créer un label pour chaque case, cela nous permettra d'y ajouter les pièces
        for (int i = 0; i < 64; i++) {
            // On prend la case à la ième position
            Square square = chessboard.getSquares()[i];

            // On assigne un nouveau SquareJLabel à cette case
            SquareJLabel label = new SquareJLabel(square);

            // On ajoute ce label au tableau des labels, permettra de mettre à jour plus tard la fenêtre
            labels[square.getCoords().getX()][square.getCoords().getY()] = label;

            // On centre le contenu de label horizontallement
            label.setHorizontalAlignment(SwingConstants.CENTER);

            // On centre le contenu de label verticalement
            label.setVerticalAlignment(SwingConstants.CENTER);

            // Action qui se produit lorsque l'utilisateur clique sur ce label
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    // On récupère la case sur laquelle le joueur a cliqué
                    Square square = label.getSquare();
                    try{

                        // Messages de debug
                        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\nVous avez cliqué sur la case : x = " + square.getCoords().getX() + " | y = " + square.getCoords().getY());
                        System.out.println("NBCLICKS: " + nbClicks + " | PIECE: " + square.getPiece());

                        // Si le joueur est à son premier clic
                        if (nbClicks == 0) {
                            // Est-ce que le joueur a cliqué sur une pièce qui correspond bien au tour actuel
                            if(game.getPlayerTurn().getColor() == square.getPiece().getColor()) {
                                // Le joueur a bien cliqué, donc on incrémente la variable qui compte le nombre de clics
                                nbClicks++;

                                // On affiche toutes les cases où peut se déplacer la pièce
                                showPiecePath(square);

                            } else {
                                System.out.println("Ce n'est pas le tour de ces pièces !");
                                return;
                            }

                        // Le joueur a au moins cliqué deux fois
                        } else {

                            // On vérifie qu'il n'a pas cliqué sur la même case
                            if (square != previousSquare) {

                                // On va vérifier que le joueur a bien cliqué sur une case où la pièce peut se déplacer
                                if (highLightSquares.contains(square)) {
                                    // On déplace la pièce de la position ancienne à la nouvelle position
                                    chessGame.movePieceTo(game.getPlayerTurn(), previousSquare, square);

                                    // On regarde si le roi de ce tour est en danger
                                    if(!chessGame.checkIfKingIsSafe(square))
                                    {
                                        // On détermine la couleur ennemie
                                        GameColor ennemyColor = (game.getPlayerTurn().getColor() == GameColor.WHITE) ? GameColor.BLACK : GameColor.WHITE;

                                        // On détermine la position du roi ennemi
                                        Square kingLocation = chessboard.getKingLocation(ennemyColor);

                                        // On met la case en rouge pour signifier que le roi est en échec
                                        labels[kingLocation.getCoords().getX()][kingLocation.getCoords().getY()].setBackground(Color.RED);

                                        // On débloque les mouvements des pièces
                                        chessGame.resetPiecesMovements();
                                    }

                                    // On change de tour, pour que la partie puisse continuer
                                    game.changeTurn();

                                    // On indique le changement de tour
                                    instructionsLabel.setText("C'est au tour de " + game.getPlayerTurn().getName() + " (" + game.getPlayerTurn().getColor() + ")");

                                    // On rafraichit la fenêtre
                                    SwingUtilities.updateComponentTreeUI(panel);
                                } else {
                                    // Si le joueur a cliqué sur une de ses autres pièces
                                    if(square.getPiece().getColor() == game.getPlayerTurn().getColor())
                                    {
                                        // On enlève la surbrillance des cases
                                        unhighlightSquares();

                                        // On affiche les nouvelles cases en surbrillance
                                        showPiecePath(square);

                                        // On augmente le nombre de clics
                                        nbClicks++;

                                        return;
                                    } // Sinon le joueur a cliqué sur une case non surlignée
                                }
                            }
                            // On enlève la surbrillance des cases
                            unhighlightSquares();
                        }
                    } catch(NullPointerException ex) {
                        // Le joueur a cliqué sur une case avec aucune piece
                    }
                }
            });
            // On "place" les pièces sur le plateau, on définit l'icone des labels qui contiennent une pièce
            if (chessboard.getPieces().get(i) != null) {
                label.setIcon(chessboard.getPieces().get(i).getImage());
            }
            label.setOpaque(true);

            // On ajoute cette case visuelle à la fenêtre centrale
            panel.add(label);
        }

        // On construit le damier
        paintSquares();

        // On ajoute ce panel au centre de la fenêtre
        add(panel, BorderLayout.CENTER);

        // On ajoute une information en haut de la fenêtre
        add(instructionsLabel, BorderLayout.NORTH);

        // On ajoute la catégorie de gauche (pièces capturées par les blancs)
        left = new JPanel();
        left.setLayout(new GridBagLayout());
        left.setBackground(Color.darkGray);
        leftConstraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblLeft = new JLabel("Pièces capturées par " + game.getPlayers()[0].getName() + " (" + game.getPlayers()[0].getColor() + ")");
        lblLeft.setForeground(Color.white);
        leftConstraints.gridx = 0;
        leftConstraints.gridy = 0;
        leftConstraints.gridwidth = 5;
        left.add(lblLeft, leftConstraints);
        leftConstraints.gridwidth = 1;
        leftConstraints.gridy++;
        add(left, BorderLayout.LINE_START);

        // On ajoute la catégorie de droite (pièces capturées par les noirs)
        right = new JPanel();
        right.setLayout(new GridBagLayout());
        right.setBackground(Color.darkGray);
        rightConstraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblRight = new JLabel("Pièces capturées par " + game.getPlayers()[1].getName() + " (" + game.getPlayers()[1].getColor() + ")");
        lblRight.setForeground(Color.white);
        rightConstraints.gridx = 0;
        rightConstraints.gridy = 0;
        rightConstraints.gridwidth = 5;
        right.add(lblRight, rightConstraints);
        rightConstraints.gridwidth = 1;
        rightConstraints.gridy++;
        add(right, BorderLayout.LINE_END);

        // On rend la fenêtre visible
        setVisible(true);
    }

    /*
        paintSqures() : proc : void
        Cette procédure permet de (re)construire le damier du plateau de jeu

        var:
        - x : int : Position X de la case coloriée
        - y : int : Position Y de la case coloriée
     */
    public void paintSquares()
    {
        int x = 0;
        int y = 0;

        for(int i = 0; i<64; i++) {
            // On colorie les cases en damier
            if (y % 2 == 0 && x % 2 == 0 || y % 2 != 0 && x % 2 != 0) {
                labels[x][y].setBackground(Color.black);
            } else {
                labels[x][y].setBackground(Color.white);
            }
            x++;
            if (x % 8 == 0) {
                x = 0;
                y++;
            }
        }
    }

    /*
        showPiecePath(Square square) : proc : void
        Cette procédure permet d'afficher les cases en surbrillance par rapport à une case cliquée

        param:
        - square : Square : Case sur laquelle le joueur a cliqué
     */
    public void showPiecePath(Square square)
    {
        // On récupère les cases où la pièce peut se déplacer
        highLightSquares = chessGame.getHighLightSquares(square);

        // On va mettre en surbrillance les cases où le joueur peut se déplacer
        Border border = BorderFactory.createLineBorder(Color.RED, 3);
        for (Square s : highLightSquares) {
            if (s != null) {
                // On applique la bordure à ces cases
                labels[s.getCoords().getX()][s.getCoords().getY()].setBorder(border);
            }
        }
        // Fin de l'action, on enregistre donc le dernier carré cliqué à celui ci (permettra le calcul de distances)
        previousSquare = square;
    }

    /*
        updateGraphics(Square square) : proc : void
        Cette fonction permet d'effectuer le déplacement d'une pièce d'une case à une autre du coté de la vue

        param:
        - square : Square : Case où sera la nouvelle pièce / Précédente case de la pièce
     */
    @Override
    public void updateGraphics(Square square) {
        try{
            // On met à jour l'image du label aux coordonnées données
            labels[square.getCoords().getX()][square.getCoords().getY()].setIcon(square.getPiece().getImage());

        } catch(NullPointerException e) {
            // On efface la pièce de sa précédente position (en mettant l'icone à null)
            labels[square.getCoords().getX()][square.getCoords().getY()].setIcon(null);
        }

        // On reconstruit le damier (pour éventuellement retirer la case rouge dû à la mise en échec d'un roi)
        paintSquares();

        // On rafraichit la fenêtre
        SwingUtilities.updateComponentTreeUI(panel);
    }

    /*
        updateListOfCapturedPieces() : proc : void
        Cette fonction permet de mettre à jour la liste des pièces capturées par un joueur
     */
    @Override
    public void updateListOfCapturedPieces() {
        // On ajoute cette pièce à la liste visuelle sur la fenetre
        JLabel label = new JLabel();

        // On définit l'image à la pièce capturée
        label.setIcon(game.getPlayerTurn().getCapturedPieces().get(game.getPlayerTurn().getCapturedPieces().size() - 1).getImage());

        // Si la pièce capturée était noire
        if(game.getPlayerTurn().getColor() == GameColor.WHITE)
        {
            // Au bout de 5 pièces, on retourne à la ligne
            if(game.getPlayerTurn().getCapturedPieces().size() % 5 == 0)
            {
                leftConstraints.gridy++;
                leftConstraints.gridx = 0;
            }
            // On l'ajoute à gauche (pièce capturée par les blancs)
            left.add(label, leftConstraints);
            leftConstraints.gridx++;

            // Si la pièce capturée était blanche
        } else {
            // Au bout de 5 pièces, on retourne à la ligne
            if(game.getPlayerTurn().getCapturedPieces().size() % 5 == 0)
            {
                rightConstraints.gridy++;
                rightConstraints.gridx = 0;
            }
            // On l'ajoute à droite (pièce capturée par les noirs)
            right.add(label, rightConstraints);
            rightConstraints.gridx++;
        }
    }

    /*
        unhighlightSquares() : proc : void
        Cette procédure permet de déselectionner les déplacements possibles d'une pièce
     */
    public void unhighlightSquares()
    {
        // On débloque les mouvements des pièces
        chessGame.resetPiecesMovements();

        // On réinitialise le nombre de clics
        nbClicks = 0;

        // Pour toutes les cases surlignées
        for (Square s : highLightSquares) {
            if(s != null)
            {
                // On enlève la bordure en la définissant à null
                labels[s.getCoords().getX()][s.getCoords().getY()].setBorder(null);
            }
        }
    }
}
