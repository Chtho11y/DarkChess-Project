package com.Model;

public enum RoundStatus{
    OFFENSIVE,DEFENSIVE,PLAYING,DRAW;

    public PieceColor getColor(PieceColor offensiveColor){
        if(this==OFFENSIVE)return offensiveColor;
        else if(this==DRAW)return PieceColor.UNKNOWN;
        else if(this==DEFENSIVE)return offensiveColor==PieceColor.RED?PieceColor.BLACK:PieceColor.RED;
        else return PieceColor.UNKNOWN;
    }
}
