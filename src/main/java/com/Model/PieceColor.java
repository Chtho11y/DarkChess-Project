package com.Model;

import javafx.scene.paint.Color;

public enum PieceColor{
    RED,BLACK,UNKNOWN;
    public Color getValue(){
        return this==RED?Color.RED: Color.BLACK;
    }

}
