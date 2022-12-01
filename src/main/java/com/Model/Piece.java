package com.Model;

import javafx.scene.paint.Color;

import java.lang.reflect.Type;


public class Piece{

    private PieceColor color;
    private PieceType type;

    private boolean covered;
    static String[] textBlack={"","将","士","象","车","马","炮","卒"};
    static String[] textRed={"","帅","仕","相","车","马","炮","兵"};

    static PieceType[] pieceList={PieceType.EMPTY, PieceType.GENERAL, PieceType.ADVISOR, PieceType.MINISTER,
                                PieceType.CHARIOT, PieceType.HORSE, PieceType.CHARIOT, PieceType.SOLDIER};

    public Piece(PieceColor pieceColor, PieceType pieceType, boolean covered){
        color=pieceColor;
        type=pieceType;
        this.covered=covered;
    }

    public Piece(){
        color= PieceColor.RED;
        type=PieceType.EMPTY;
        covered=false;
    }

    public Piece(int id,String color,int cover){
        type=pieceList[id];
        this.color=(color.equals("RED")? PieceColor.RED: PieceColor.BLACK);
        this.covered=cover==1;
    }

    public String getText(){
        return Piece.getText(type,color);
    }

    public static String getText(PieceType x,PieceColor color){
        if(color==PieceColor.RED)return textRed[x.id];
        return textBlack[x.id];
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public void cover(){
        covered=true;
    }

    public void discover(){
        covered=false;
    }

    public boolean isCovered(){
        return covered;
    }

    public boolean isEmpty(){
        return PieceType.EMPTY==type;
    }

    public static Piece[] createBoard(){
        Piece[] board=new Piece[32];
        int pos=0;
        for(PieceType pt:PieceType.values()){
            for(int i=0;i<pt.num;++i)
                board[pos++]=new Piece(PieceColor.RED,pt,true);
        }
        for(PieceType pt:PieceType.values()){
            for(int i=0;i<pt.num;++i)
                board[pos++]=new Piece(PieceColor.BLACK,pt,true);
        }

        return board;
    }

    public boolean compareTo(Piece x){
        if(color==x.color)return false;
        if(x.type==PieceType.GENERAL)return type==PieceType.SOLDIER;
        if(type==PieceType.GENERAL)return PieceType.SOLDIER!=x.type;
        return type.id<=x.type.id;
    }

    @Override
    public String toString(){
        return type.id+" "+color+" "+(covered?1:0);
    }
}
