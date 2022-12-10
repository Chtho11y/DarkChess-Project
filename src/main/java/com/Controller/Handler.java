package com.Controller;

import com.Model.PieceColor;
import com.Model.RoundStatus;
import com.View.ChessPainter;

public class Handler {

    static int selectX,selectY;

    static RoundStatus status;

    static void init(int seed){
        ChessExecutor.init(seed);
        selectX=selectY=-1;
        status= RoundStatus.PLAYING;
    }

    static public RoundStatus getStatus(){
        return status;
    }

    static public void select(int x, int y){
        if(!ChessExecutor.inRange(x,y))return;
        if(selectX==-1){
            System.out.printf("Selected %d,%d\n",x,y);
            if(ChessExecutor.getPiece(x,y).isCovered()){
                if(ChessExecutor.getOffensiveColor()== PieceColor.UNKNOWN){
                    ChessExecutor.discover(x,y);
                    ChessPainter.setHighlight(x,y);
                    return;
                }
                ChessExecutor.discover(x,y);
                unselect();
                ChessPainter.setHighlight(x,y);
                return;
            }
            if(ChessExecutor.getPiece(x,y).isEmpty()||!ChessExecutor.roundTest(ChessExecutor.getPiece(x,y)))
                return;
            ChessPainter.setSelectCell(x,y);
            selectX=x;
            selectY=y;
            int[] dis=ChessExecutor.getValidPlace(selectX,selectY);
            for(int i=0;i<4;++i){
                int nx=selectX+ChessExecutor.dx[i]*dis[i],ny=selectY+ChessExecutor.dy[i]*dis[i];
                if(nx==selectX&&ny==selectY)
                    continue;
                System.out.printf("(%d,%d) \n",nx,ny);
            }
            return;
        }

        if(x==selectX&&y==selectY){
            unselect();
            return;
        }
        int[] dis=ChessExecutor.getValidPlace(selectX,selectY);
        for(int i=0;i<4;++i){
            int nx=selectX+ChessExecutor.dx[i]*dis[i],ny=selectY+ChessExecutor.dy[i]*dis[i];
            System.out.printf("(%d,%d) ",nx,ny);
            if(nx!=x||ny!=y)
                continue;
            if(!ChessExecutor.getPiece(nx,ny).isEmpty())
                ChessPainter.modifyPieceNumber(ChessExecutor.getPiece(nx,ny),1);
            ChessExecutor.move(selectX,selectY,i);
            RoundStatus state=ChessExecutor.checkWinner();
            if(state!=RoundStatus.PLAYING){
                finishRound(state);
                unselect();
                return;
            }
            unselect();
            ChessPainter.setHighlight(selectX,selectY);
            return;
        }
        System.out.println();
        unselect();
    }

    public static void unselect(){
        ChessPainter.eraseSelectCell();
        System.out.println("unselect");
        selectX=selectY=-1;
    }

    static void finishRound(RoundStatus state){
        if(Replay.showing)return;
        System.out.println("finished.");
        status=state;
        Executor.drawEnd(state);
    }
}
