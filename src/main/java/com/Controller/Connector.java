package com.Controller;

import com.Model.InputMode;

public class Connector {

    static InputMode firstMode,secondMode;

    public static void setMode(InputMode m1,InputMode m2){
        firstMode=m1;
        secondMode=m2;
    }

    public static void normalInput(int x,int y){
        if(getMode()!=InputMode.NORMAL)return;
        System.out.println("normal input:"+x+","+y);
        Handler.select(x,y);
    }

    public static void autoInput(int x,int y){
        Handler.select(x,y);
    }

    public static InputMode getMode(){
        return ChessExecutor.isOffensive()?firstMode:secondMode;
    }

    public static InputMode getNext(){return ChessExecutor.isOffensive()?secondMode:firstMode;}
}
