package model;

public class Coordinate {
    private final int x, y; // Coordonnées X & Y

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    /*
        getX() : func : int
        Cette fonction retourne la position X de la coordonnée
     */
    public int getX(){
        return this.x;
    }

    /*
        getY() : func : int
        Cette fonction retourne la position Y de la coordonnée
     */
    public int getY(){
        return this.y;
    }
}
