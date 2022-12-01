package com.Model;

public enum PieceType {
    EMPTY(0,0,0),
    GENERAL(1,30,1),
    ADVISOR(2,10,2),
    MINISTER(3,5,2),
    CHARIOT(4,5,2),
    HORSE(5,5,2),
    SOLDIER(7,1,5),
    CANNON(6,5,2);

    final int id;
    final int score;
    final int num;

    PieceType(int id,int score,int num){
        this.id=id;
        this.score=score;
        this.num=num;
    }

    public int getId(){
        return id;
    }

    public int getScore(){return score;}
}
