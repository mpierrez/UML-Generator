package model;

import model.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessGame
{
    private final ChessBoard chessBoard; // Plateau où se déroule la partie
    private boolean checkSimulation = false; // Permet de vérifier si une simulation est actuellement en cours (évite les boucles infinies)
    private boolean checkProtection = false; // Permet de vérifier si une obtention des protecteurs est actuellement en cours (évite les boucles infinies)
    private List<Square> squaresToProtect = new ArrayList<>(); // Cases qui doivent être protégés pour que le roi ne soit plus en échec

    public ChessGame(ChessBoard chessBoard)
    {
        this.chessBoard = chessBoard;
    }

    /*
        isValidMovement(Square from, Square to) : func : boolean
        Cette fonction indique si le mouvement est réalisable par la pièce (si le mouvement est autorisé comme le prévoient les règles)

        param:
        - from : Square : Case de départ de la pièce (position actuelle)
        - to : Square : Case d'arrivée de la pièce (position future)
     */
    public boolean isValidMovement(Square from, Square to)
    {
        try{
            // Si la case de déplacement est la même que celle d'arrivée
            if(from.getCoords().getX() == to.getCoords().getX() && from.getCoords().getY() == to.getCoords().getY())
            {
                // On interdit le mouvement : une pièce ne peut pas se déplacer sur la même case
                return false;
            }

            // Seul le cavalier peut sauter des pièces, on effectue donc la vérification pour toutes les autres pièces
            if(!(from.getPiece() instanceof Knight))
            {
                // On détermine la direction
                Direction direction = determineDirection(from, to);

                // Est-ce que la pièce peut encore se déplacer dans cette direction ? (Est-ce qu'elle a déjà rencontré une pièce sur son passage ?)
                if(!from.getPiece().canMove(direction))
                {
                    // Si ce n'est pas le cas, on interdit le déplacement (évite de sauter par dessus les pièces)
                    return false;

                    // Si la pièce qui bouge rencontre une autre pièce sur son passage
                } else if(chessBoard.getSquareWithCoords(to.getCoords().getX(), to.getCoords().getY()).getPiece() != null)
                {
                    // On bloque les futurs mouvements dans cette direction
                    from.getPiece().defineMove(direction, false);
                }
            }

            // On va déterminer la position du roi dans le camp du joueur
            King king = ((King) chessBoard.getKingLocation(from.getPiece().getColor()).getPiece());

            // Est-ce que le roi est en échec ?
            if(!checkProtection && king.IsInCheck() && !(from.getPiece() instanceof King)) {
                // On va déterminer le/les pièces attaquante(s)
                List<Square> attackers = king.getAttackers();

                // S'il n'y a qu'un seul attaquant, alors le roi peut se faire protéger par une pièce alliée
                if (attackers.size() == 1) {
                    // Est-ce que la pièce sur laquelle on a cliquée peut protéger le roi ?
                    try {
                        if (king.getProtectors().contains(from)) {
                            // Si la case de déplacement correspond à la case où il y a le seul attaquant
                            if (to == attackers.get(0)) {
                                // On autorise le mouvement, pour que la pièce puisse manger l'attaquant
                                return true;
                            }
                        }
                        checkProtection = true;

                        // La pièce peut se sacrifier pour protéger son roi
                        if(squaresToProtect.contains(to) && isValidMovement(from, to))
                        {
                            checkProtection = false;
                            return true;
                        }
                        checkProtection = false;
                        // Sinon on refuse, on veut uniquement les pièces qui peuvent manger l'attaquant
                        return false;
                    } catch (NullPointerException ex)
                    {
                        // Le roi n'a aucun protecteur
                        return false;
                    }
                } // Dans le cas contraire (> 1 attaquant, alors c'est le roi qui doit se déplacer)
            }
            // Si la pièce est un pion
            if(from.getPiece() instanceof Pawn)
            {
                // Si le pion veut se déplacer en diagonale
                if(to.getCoords().getX() != from.getCoords().getX()) {

                    // Méthode pour la prise en passant
                    try {
                        // Permet de récupérer les deux cases en diagonale du pion qui va potentiellement faire la prise en passant
                        Square[] squares = {chessBoard.getSquareWithCoords(from.getCoords().getX() - 1, from.getCoords().getY()), chessBoard.getSquareWithCoords(from.getCoords().getX() + 1, from.getCoords().getY())};

                        // On va regarder pour toutes les cases
                        for(Square square : squares)
                        {
                            // Si la case est un pion et que ce pion vient de bouger de 2 cases le tour précédent
                            if (square.getPiece() instanceof Pawn && ((Pawn) square.getPiece()).hasMovedTwo()) {
                                Square topSquare = (from.getPiece().getColor() == GameColor.WHITE) ? chessBoard.getSquareWithCoords(square.getCoords().getX(), square.getCoords().getY()-1) : chessBoard.getSquareWithCoords(square.getCoords().getX(), square.getCoords().getY()+1);
                                // Est-ce que la case du haut de ce pion est une des destinations du pion qui va prendre en passant ?
                                if (topSquare == to) {
                                    // Si oui, on autorise "exceptionnellement" ce mouvement (prise en passant)
                                    return true;
                                }
                            }
                        }

                    } catch(NullPointerException ignored) {} // La case est en dehors du plateau
                    // S'il n'y a aucune pièce à la destination alors on bloque le mouvement (interdit)
                    if (to.getPiece() == null) {
                        return false;
                    }
                }
                // Si le pion essaye de manger une pièce sur le même X que lui-même
                if(to.getPiece() != null && to.getCoords().getX() == from.getCoords().getX())
                {
                    return false;
                }
            }

            // Empêche de manger ses propres pièces de couleur
            return from.getPiece().getColor() != to.getPiece().getColor();

        } catch(NullPointerException ex) {
            // La case de destination ne contient aucune piece
            return true;
        }
    }

    /*
        resetPiecesMovements() : proc : void
        Cette procédure va débloquer tous les mouvements des pièces sur le plateau
        Ainsi, les mouvements pourront être de nouveau bloqués suivant comment le plateau a évolué
     */
    public void resetPiecesMovements()
    {
        // On prend toutes les pièces sur le plateau
        for(Piece piece : chessBoard.getPieces())
        {
            if(piece != null)
            {
                // On prend toutes les directions possibles dans l'énumération Direction
                for(Direction direction : Direction.values())
                {
                    // On (ré)autorise le mouvement dans cette direction
                    piece.defineMove(direction, true);
                }
            }
        }
    }

    /*
        isValidSimulation(Square from, Square to) : func : boolean
        Cette fonction permet de simuler le déplacement d'une pièce
        Ainsi, cela est utile pour voir si le roi allié est mis en échec suite à un déplacement de la pièce

        param:
        - from : Square : Case de départ de la pièce (position actuelle)
        - to : Square : Case d'arrivée de la pièce (position future)
     */
    public boolean isValidSimulation(Square from, Square to)
    {
        // Premièrement, est-ce que le mouvement est bien valide ?
        if(!isValidMovement(from, to))
        {
            // S'il ne l'est pas, ça ne sert à rien de continuer
            return false;
        }
        boolean res = true;

        // On sauvegarde les pièces à l'instant T
        Piece savedFrom = from.getPiece();
        Piece savedTo = to.getPiece();

        // On effectue la simulation de mouvement pour la pièce de départ (comme si la pièce pouvait manger celle de départ)
        to.setPiece(savedFrom);
        from.setPiece(null);

        // Si on effectue la simulation sur un roi, on réinitialise les mouvements pour actualiser le plateau
        if(to.getPiece() instanceof King)
        {
            resetPiecesMovements();
        }

        // Détermination de la couleur ennemie
        GameColor ennemyColor = (to.getPiece().getColor() == GameColor.WHITE) ? GameColor.BLACK : GameColor.WHITE;

        // On regarde chaque pièce ennemie
        for(Square s : chessBoard.getSquaresOccupiedByColor(ennemyColor)) {
            try {
                // On regarde chaque destination où cette pièce peut se déplacer
                for (Square destinationSquare : getHighLightSquares(s)) {
                    try {

                        // Si la case d'arrivée est déjà dans le champ de vision d'une pièce ennemie
                        if (destinationSquare == chessBoard.getKingLocation(to.getPiece().getColor())) {
                            // On interdit le mouvement
                            res = false;
                        }
                    } catch (NullPointerException ignored) {
                    }
                }
            } catch (NullPointerException ex) {
                // La case de destination est nulle
            }
        }

        // On remet les pièces à leur position initiale (fin de la simulation)
        to.setPiece(savedTo);
        from.setPiece(savedFrom);

        return res;
    }


    /*
        determineDirection(Square from, Square to) : func : Direction
        Cette fonction va permettre de déterminer la direction dans laquelle se déplace la pièce
        Ainsi, cela permettra de bloquer (dans le isValidMovement) les mouvements si une pièce a déjà été détectée sur le chemin

        param:
        - from : Square : Case de départ de la pièce
        - to : Square : Case d'arrivée de la pièce
     */
    public Direction determineDirection(Square from, Square to)
    {
        // Si la pièce effectue un mouvement diagonal en haut à gauche
        if(from.getCoords().getX() > to.getCoords().getX() && from.getCoords().getY() > to.getCoords().getY())
        {
            return Direction.TOPLEFT;

            // Si la pièce effectue un mouvement diagonal en haut à droite
        } else if(from.getCoords().getX() < to.getCoords().getX() && from.getCoords().getY() > to.getCoords().getY())
        {
            return Direction.TOPRIGHT;

            // Si la pièce effectue un mouvement diagonal en bas à gauche
        } else if(from.getCoords().getX() > to.getCoords().getX() && from.getCoords().getY() < to.getCoords().getY())
        {
            return Direction.BOTTOMLEFT;

            // Si la pièce effectue un mouvement diagonal en bas à droite

        } else if(from.getCoords().getX() < to.getCoords().getX() && from.getCoords().getY() < to.getCoords().getY()){
            return Direction.BOTTOMRIGHT;
        }

        // Si la pièce effectue un mouvement vers la gauche
        else if(from.getCoords().getX() > to.getCoords().getX())
        {
            return Direction.LEFT;

            // Si la pièce effectue un mouvement vers la droite
        } else if(from.getCoords().getX() < to.getCoords().getX())
        {
            return Direction.RIGHT;

            // Si la pièce effectue un mouvement vers le haut
        } else if(from.getCoords().getY() > to.getCoords().getY())
        {
            return Direction.TOP;

            // Si la pièce effectue un mouvement vers le bas
        } else {
            return Direction.BOTTOM;
        }
    }

    /*
        setAllProtectors(King king) : proc : void
        Cette procédure permet de mettre à jour la liste des cases (et donc des pièces) qui sont en mesure de protéger le roi
        Ainsi, cela permettra d'autoriser le mouvement à certaines pièces et à certains endroits quand le roi est en échec

        param:
        - king : King : Roi dont on veut déterminer les protecteurs

        var:
        - attackers : List<Square> : Cases avec des pièces qui attaque le roi
        - protectors : List<Square> : Cases avec des pièces qui peuvent protéger le roi
        - ennemyColor : GameColor : Couleur ennemie par rapport au roi passé en paramètres
     */
    public void setAllProtectors(King king)
    {
        checkProtection = true;

        // On récupère les cases où les pièces attaquent le roi
        List<Square> attackers = king.getAttackers();

        // On définit une nouvelle liste de protecteurs
        List<Square> protectors = new ArrayList<>();

        // On détermine la couleur ennemie
        GameColor ennemyColor = (attackers.get(0).getPiece().getColor() == GameColor.WHITE) ? GameColor.BLACK : GameColor.WHITE;

        // Pour chaque pièce qui menace le roi
        for(Square attacker : attackers)
        {
            // On va regarder toutes les cases qui peuvent protéger le roi
            List<Square> squareOccupied = chessBoard.getSquaresOccupiedByColor(ennemyColor);
            for(int i = 0; i<getNumberOfValues(squareOccupied); i++)
            {
                try {
                    // On souhaite obtenir les protecteurs du roi, par conséquent le roi ne peut pas se protéger lui-même
                    if(!(squareOccupied.get(i).getPiece() instanceof King)) {
                        boolean kingCanBeProtected = false;

                        // On regarde toutes les cases où peut se déplacer la case sélectionnée
                        List<Square> destinationSquare = getHighLightSquares(squareOccupied.get(i));

                        for (int j = 0; j < getNumberOfValues(destinationSquare) && !kingCanBeProtected; j++) {

                            // Est-ce que la pièce peut se déplacer jusqu'à la case où la pièce attaque le roi ?
                            if (destinationSquare.get(j) == attacker || squaresToProtect.contains(destinationSquare.get(j))) {

                                // Le roi est protégeable, donc on l'ajoute à la liste de ses protecteurs
                                protectors.add(squareOccupied.get(i));

                                // On met à jour cette variable pour stopper la boucle for (optimisation)
                                kingCanBeProtected = true;
                            }
                        }
                    }
                } catch(NullPointerException ignored) {
                }
            }
        }
        // On réinitialise les protecteurs (entre temps, les pièces ont bougées)
        king.resetProtectors();

        // On ajoute les protecteurs au roi
        king.getProtectors().addAll(protectors);

        // On arrête la vérification
        checkProtection = false;
    }

    /*
        checkIfKingIsSafe(Square attacker) : func : boolean
        Cette fonction permet de savoir si la dernière pièce qui a bougé a mis en échec le roi

        param:
        - attacker : Square : Case où la dernière pièce a bougée

        var:
        - isSafe : boolean : Valeur de retour pour vérifier si le roi est en danger ou non
     */
    public boolean checkIfKingIsSafe(Square attacker)
    {
        boolean isSafe = true;
        // On regarde tous les nouveaux déplacements possibles pour voir si le roi est menacé
        for(Square square : getHighLightSquares(attacker))
        {
            try{
                // Si la pièce peut manger un roi ET que ce roi est bien le roi ennemi
                if(square.getPiece() instanceof King king && square.getPiece().getColor() != attacker.getPiece().getColor())
                {

                    // On met le roi en échec
                    king.setInCheck(true);
                    isSafe = false;
                    System.out.println("ROI EN ECHEC");

                    // On ajoute un attaquant par rapport au roi
                    king.getAttackers().add(attacker);

                    // On regarde tous les mouvements de la pièce qui attaque
                    Coordinate[] coords = attacker.getPiece().move();

                    // On réinitialise les cases à protéger pour en définir avec des nouvelles (voir ci-dessous)
                    squaresToProtect.clear();

                    // On détermine les cases à protéger
                    for(int i = 0; i<coords.length && coords[i] != null; i++)
                    {
                        try {
                            // Destination de l'attaquant
                            Square destination = chessBoard.getSquareWithCoords((attacker.getCoords().getX() + coords[i].getX()), (attacker.getCoords().getY() + coords[i].getY()));

                            // Si la direction de l'attaque est la même que la direction du mouvement
                            if (determineDirection(attacker, destination) == determineDirection(attacker, square)) {

                                // Alors c'est une case à protéger, c'est une des cases qui va permettre à l'attaquant de se frayer un chemin pour manger le roi
                                squaresToProtect.add(destination);
                            }
                        } catch(NullPointerException ignored)
                        {

                        }
                    }

                    // On va définir tous les protecteurs qui peuvent manger le(s) attaquant(s)
                    setAllProtectors(king);

                    // On va vérifier si le roi est en échec et mat, d'abord on regarde si le roi ne peut plus se déplacer
                    if(getNumberOfValues(getHighLightSquares(square)) == 0)
                    {
                        System.out.println("Mince ! Le roi ne peut plus se déplacer");

                        // Si le roi ne peut pas être protégé (aucune pièce pour le protéger)
                        if(king.getProtectors().size() == 0)
                        {
                            // Le roi est en échec et mat
                            System.out.println("ROI EN ECHEC ET MAT !!!!!");
                            king.setInCheckMate(true);
                        }
                    }
                }
            } catch(NullPointerException ex)
            {
                // Case contenant aucune pièce
            }
        }
        return isSafe;
    }

    /*
        movePieceTo(Player player, Square from, Square to) : proc : void
        Cette procédure permet de déplacer une piècé d'un point A à un point B

        param:
        - player : Player : Joueur qui déplace les pièces
        - from : Square : Case de départ de la pièce (position actuelle)
        - to : Square : Case d'arrivée de la pièce (position future)
     */
    public void movePieceTo(Player player, Square from, Square to)
    {
        // Ajout potentiel d'une pièce à la liste des pièces capturés
        try{
            // Si la case de destination contient une pièce ennemie
            if(to.getPiece() != null && to.getPiece().getColor() != player.getColor())
            {
                // On ajoute cette pièce à la liste des pièces capturées
                player.getCapturedPieces().add(to.getPiece());

                // On retire la pièce du plateau
                chessBoard.getPieces().remove(to.getPiece());

                // On informe à la vue pour mettre à jour la liste
                to.notifyPieceCaptured();
            }
        }catch(NullPointerException ignored)
        {
            // Le joueur s'est déplacé sur une case avec aucune pièce, donc on ne met pas à jour la liste
        }

        // On déplace la pièce
        to.setPiece(from.getPiece());
        from.setPiece(null);

        // Si la pièce déplacée était un roi (Méthode du roque)
        if(to.getPiece() instanceof King){

            // Si le roi s'est déplacée de 2 cases
            if(Math.abs(from.getCoords().getX() - to.getCoords().getX()) == 2){

                // Si la direction dans laquelle il s'est déplacée est la gauche
                if(determineDirection(from, to) == Direction.LEFT){

                    // On détermine la tour qui bougera pour le roque
                    Square sq_rook = chessBoard.getSquareWithCoords(0, from.getCoords().getY());

                    // On bouge la tour à droite du roi
                    movePieceTo(player, sq_rook, chessBoard.getSquareWithCoords(to.getCoords().getX() + 1, to.getCoords().getY()));
                }

                // Si la direction dans laquelle il s'est déplacée est la droite
                else if(determineDirection(from, to) == Direction.RIGHT){

                    // On détermine la tour qui bougera pour le roque
                    Square sq_rook = chessBoard.getSquareWithCoords(7, from.getCoords().getY());

                    // On bouge la tour à gauche du roi
                    movePieceTo(player, sq_rook, chessBoard.getSquareWithCoords(to.getCoords().getX() - 1, to.getCoords().getY()));
                }
            }
        }

        // Si la pièce déplacée était un pion
        if(to.getPiece() instanceof Pawn)
        {
            // Méthode pour la prise en passant
            Square bottomSquare = (to.getPiece().getColor() == GameColor.WHITE) ? chessBoard.getSquareWithCoords(to.getCoords().getX(), to.getCoords().getY()+1) : chessBoard.getSquareWithCoords(to.getCoords().getX(), to.getCoords().getY()-1);

            // Si la case en bas du pion est un pion et que ce pion s'est déplacée de 2 le tour précédent
            if(bottomSquare.getPiece() instanceof Pawn pawn && pawn.hasMovedTwo())
            {
                // On ajoute cette pièce à la liste des pièces capturées
                player.getCapturedPieces().add(pawn);

                // On retire la pièce du plateau
                chessBoard.getPieces().remove(pawn);
                bottomSquare.setPiece(null);

                // On informe à la vue pour mettre à jour la liste
                bottomSquare.notifyMove();
                bottomSquare.notifyPieceCaptured();
            }

            // Si le pion est arrivé a l'une des extrémitées
            if(to.getCoords().getY() == 0 || to.getCoords().getY() == 7)
            {
                // On retire le pion au joueur
                player.getPieces().remove(to.getPiece());

                // On le transforme en reine
                Queen queen = new Queen(to.getPiece().getColor());

                // On ajoute cette pièce au plateau et au joueur
                player.getPieces().add(queen);
                chessBoard.getPieces().add(queen);
                to.setPiece(queen);
            }
        }

        // Fin du tour, on enlève la possibilité de prise en passant si l'occasion n'a pas été saisie
        for(Piece piece : chessBoard.getPieces())
        {
            if(piece instanceof Pawn) ((Pawn) piece).setMovedTwo(false);
        }

        if(to.getPiece() instanceof Pawn) {
            // On regarde si le pion a bougé 2 fois (utile pour la prise en passant)
            ((Pawn) to.getPiece()).setMovedTwo(Math.abs(from.getCoords().getY() - to.getCoords().getY()) == 2);
        }

        // On notifie le mouvement pour mettre à jour la fenêtre
        to.notifyMove();
        from.notifyMove();

        // On dit que cette pièce a déjà bougée (très utile pour le pion)
        to.getPiece().setMoved(true);

        // On débloque les mouvements des pièces
        resetPiecesMovements();

    }

    /*
        getHighLightSquares(Square square) : func : List<Square>
        Cette fonction permet d'obtenir toutes les cases qui seront en surbrillance sur la vue (tout en prenant en compte si les mouvements sont valides)

        param:
        - square : Square : Case sur laquelle le joueur a cliqué pour avoir les informations de déplacements
     */
    public List<Square> getHighLightSquares(Square square)
    {
        List<Square> res = new ArrayList<>();

        // On détermine tous les mouvements de la pièce
        Coordinate[] coords = square.getPiece().move();
        for(int i = 0; i<coords.length && coords[i] != null; i++)
        {
            // Case de destination où la pièce espère arriver
            Square destination = chessBoard.getSquareWithCoords((square.getCoords().getX() + coords[i].getX()), (square.getCoords().getY() + coords[i].getY()));

            // Est-ce qu'une simulation est déjà en cours ?
            if (!checkSimulation) {
                // Si non, on en démarre une
                checkSimulation = true;
                try {
                    // Est-ce que la simulation est valide ?
                    if (isValidSimulation(square, destination)) {

                        // Si oui, on l'ajoute au tableau des cases surlignées
                        res.add(destination);
                    }
                } catch (NullPointerException ignored) {}
                // On arrête la simulation
                checkSimulation = false;
            }
            // Si une simulation est déjà en cours, on va se contenter de regarder si le mouvement est valide (autorisé)
            else if(isValidMovement(square, destination))
            {
                // On l'ajoute au tableau des cases surlignées
                res.add(destination);
            }
        }
        return res;
    }

    /*
        getNumberOfValues(List<T> list) : func : int
        Cette fonction permet de retourner le nombre d'éléments non-nuls d'une liste
     */
    public static <T> int getNumberOfValues(List<T> list)
    {
        int res = 0;
        // Pour chaque élément de la liste
        for(T val : list)
        {
            // Si l'élément n'est pas nul, on incrémente le compteur
            if(val != null) res++;
        }
        return res;
    }

}