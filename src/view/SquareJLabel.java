package view;

import model.Square;

import javax.swing.*;

public class SquareJLabel extends JLabel
{
    private Square square;

    public SquareJLabel(Square square)
    {
        this.square = square;
    }

    public Square getSquare()
    {
        return this.square;
    }
}
