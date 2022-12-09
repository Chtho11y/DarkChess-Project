package com.Model;

public enum PieceType {
    EMPTY(0,0,0,0),
    GENERAL(1,30,1,55),
    ADVISOR(2,10,2,50),
    MINISTER(3,5,2,25),
    CHARIOT(4,5,2,10),
    HORSE(5,5,2,8),
    SOLDIER(7,1,5,8),
    CANNON(6,5,2,30);

    final int id;
    final int score;
    final int num;
    final int value;

    PieceType(int id,int score,int num,int value){
        this.id=id;
        this.score=score;
        this.num=num;
        this.value=value;
    }

    public int getId(){
        return id;
    }

    public int getScore(){return score;}

    public int getNum(){return num;}

    public int getValue(){return value;}
}
