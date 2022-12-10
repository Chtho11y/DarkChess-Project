package com.AIDemo;

import com.Controller.*;
import com.Model.PieceColor;
import com.Model.RoundStatus;
import com.View.ChessPainter;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.FutureTask;

public class MinMax {

    static final int maxDepth=2,moveDepth=4;

    static final double inf=1e9;

    static double moveOnlySearch(ChessNode position,int depth,double alpha,double beta){
        if(position.checkWinner()!= RoundStatus.PLAYING){
            return position.checkWinner()== RoundStatus.OFFENSIVE?inf:-inf;
        }
        if(depth==0)return position.evaluate();
        ArrayList<Position> nxt=position.next(false);
        if(nxt.size()==0)return position.evaluate();
        double v;
        v=-inf;
        for (Position pos : nxt) {
            double tmp=0;
            for (ChessNode child : pos.getChildren()) {
                tmp-=moveOnlySearch(child,depth-1,-beta,-alpha)*child.getRate();
            }
            v= Math.max(v,tmp);
            alpha=Math.max(alpha,v);
            if(beta<=alpha)break;
        }
        return v;
    }

    static double search(ChessNode position,int depth,double alpha,double beta){
        if(position.checkWinner()!= RoundStatus.PLAYING){
            return position.checkWinner()== RoundStatus.OFFENSIVE?inf:-inf;
        }
        double c=position.evaluate();
        if(depth==0)return c;
        ArrayList<Position> nxt=position.next(true);
        if(nxt.size()==0)return c;
        double v=-inf;

        for (Position pos : nxt) {
            double tmp=0;
            for (ChessNode child : pos.getChildren()) {
                tmp-=moveOnlySearch(child,moveDepth,-beta,-alpha)*child.getRate();
            }
            v= Math.max(v,tmp);
            alpha=Math.max(alpha,v);
            if(beta<=alpha)break;
        }

        if(position.coverCount!=0){
            nxt=position.getFlips();
            for (Position pos : nxt) {
                double tmp=0;
                for (ChessNode child : pos.getChildren()) {
                    tmp -= search(child, depth - 1, -beta, -alpha) * child.getRate();
                }
                v= Math.max(v,tmp);
                alpha=Math.max(alpha,v);
                if(beta<=alpha)return v;
            }
        }
        return v;
    }

    public static Recorder.Operation minMax(ChessNode position, double alpha, double beta){
        ArrayList<Position> nxt=position.next(true);
        double v=-inf;
        Recorder.Operation op=null;
        for (Position pos : nxt) {
            double tmp=0;
            for (ChessNode child : pos.getChildren()) {
                tmp-=search(child,maxDepth,-beta,-alpha)*child.getRate();
            }
            if(pos.getChildren().size()>1)tmp-=40;
            if(v<tmp){
                v=tmp;
                op=pos.op;
            }
            alpha=Math.max(alpha,v);
            if(beta<=alpha)break;
        }
        double c=position.evaluate();
        System.out.printf("c=%.2f, v=%.2f\n",c,v);
        if(position.coverCount!=0){
            nxt=position.getFlips();
            for (Position pos : nxt) {
                double tmp=0;
                for (ChessNode child : pos.getChildren()) {
                    double ans=search(child,maxDepth,-beta,-alpha);
                    tmp-=ans*child.getRate();
                }
                if(v<tmp){
                    v=tmp;
                    op=pos.op;
                }
                alpha= Math.max(v,tmp);
                if(beta<=alpha)return op;
                System.out.printf("(%d,%d)=%.2f\n",pos.op.getPoint().x,pos.op.getPoint().y,tmp);
            }
        }
        return op;
    }

    static Search task;
    public static void stop(){
        if(task==null)return;
        if(task.isAlive())
            task.interrupt();
    }

    static int interval=60,counter;
    public static void operate(){
        counter=interval;
        waitTimer.start();
    }

    static AnimationTimer waitTimer=new AnimationTimer() {
        @Override
        public void handle(long l) {
            counter--;
            if(counter>=0)return;
            Search task=new Search();
            task.start();
            stop();
        }
    };
}

class Search extends Thread{

    @Override
    public void run(){
        ChessNode cn=new ChessNode();
        cn.init(ChessExecutor.getBoard(), ChessPainter.getBlackRemovedPiece(), ChessPainter.getRedRemovedPiece());
        cn.setRound(ChessExecutor.getRoundStatus());
        cn.setScore(ChessExecutor.getScore(PieceColor.RED), ChessExecutor.getScore(PieceColor.BLACK));
        Recorder.Operation op= MinMax.minMax(cn,-1e9,1e9);

        if(interrupted())return;

        if(op.getOp().equals("discover"))
            Connector.autoInput(op.getPoint().x,op.getPoint().y);
        else {
            Connector.autoInput(op.getPoint().x,op.getPoint().y);
            Connector.autoInput(((Recorder.Point)op.getArg()).x,((Recorder.Point)op.getArg()).y);
        }
    }
}