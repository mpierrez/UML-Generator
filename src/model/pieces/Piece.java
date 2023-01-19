package model.pieces;

import model.Coordinate;
import model.Direction;
import model.GameColor;

import javax.swing.*;
import java.awt.*;

public abstract class Piece
{
    protected boolean moved = false;

    protected boolean moveTop = true;
    protected boolean moveBottom = true;
    protected boolean moveLeft = true;
    protected boolean moveRight = true;

    protected boolean moveTopLeft = true;
    protected boolean moveTopRight = true;
    protected boolean moveBottomLeft = true;
    protected boolean moveBottomRight = true;

    protected final GameColor color;
    protected String imagePath;

    public Piece(GameColor color)
    {
        this.color = color;
    }

    public GameColor getColor()
    {
        return this.color;
    }

    public abstract Coordinate[] move();

    public Icon getImage()
    {
        return new ImageIcon(new ImageIcon(getClass().getResource(this.imagePath)).getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
    }

    public boolean canMove(Direction direction)
    {
        return switch (direction) {
            case TOP -> this.moveTop;
            case BOTTOM -> this.moveBottom;
            case LEFT -> this.moveLeft;
            case RIGHT -> this.moveRight;
            case TOPLEFT -> this.moveTopLeft;
            case TOPRIGHT -> this.moveTopRight;
            case BOTTOMLEFT -> this.moveBottomLeft;
            case BOTTOMRIGHT -> this.moveBottomRight;
        };
    }

    public void defineMove(Direction direction, boolean b)
    {
        switch (direction) {
            case TOP -> this.moveTop = b;
            case BOTTOM -> this.moveBottom = b;
            case LEFT -> this.moveLeft = b;
            case RIGHT -> this.moveRight = b;
            case TOPLEFT -> this.moveTopLeft = b;
            case TOPRIGHT -> this.moveTopRight = b;
            case BOTTOMLEFT -> this.moveBottomLeft = b;
            case BOTTOMRIGHT -> this.moveBottomRight = b;
        };
    }
    public void setMoved(boolean moved)
    {
        this.moved = moved;
    }
}
