package com.Model;

import javafx.scene.paint.Color;

public enum PieceColor{
    RED,BLACK,UNKNOWN;
    public Color getValue(){
        return this==RED?Color.RED: Color.BLACK;
    }

    public String getName(){
        return this==RED?"#ff0000":"#000000";
    }

    public PieceColor nextColor(){
        return this==RED?BLACK:RED;
    }

}
