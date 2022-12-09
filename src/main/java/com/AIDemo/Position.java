package com.AIDemo;

import com.Controller.Recorder;

import java.util.ArrayList;
import java.util.Optional;

public class Position {
    ArrayList<ChessNode> position;

    Position(){
        position=new ArrayList<>();
    }

    public ArrayList<ChessNode> getChildren(){
        return position;
    }

    public void add(ChessNode child){
        position.add(child);
    }

    Recorder.Operation op;

    public void setOp(Recorder.Operation x){
        op=x;
    }
}
