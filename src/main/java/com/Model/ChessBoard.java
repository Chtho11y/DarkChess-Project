package com.Model;

import com.Controller.ChessExecutor;
import com.Controller.Recorder;
import com.View.ChessPainter;

import java.util.Random;

public class ChessBoard {
    Piece[][] board;

    int offensiveScore,defensiveScore;

    PieceColor offensiveColor;

    public static final int[] dy={-1,0,1,0};
    public static final int[] dx={0,1,0,-1};
    PieceColor round;

    public PieceColor getOffensiveColor(){
        return offensiveColor;
    }

    public ChessBoard(Piece[] pieces){
        board=new Piece[4][8];
        for(int i=0;i<4;++i)
            for(int j=0;j<8;++j)
                board[i][j]=pieces[i*8+j];
        round=offensiveColor= PieceColor.UNKNOWN;
        offensiveScore=defensiveScore=0;
    }

    public boolean isOffensive(){
        return round==offensiveColor||round== PieceColor.UNKNOWN;
    }

    public boolean inRange(int x, int y){
        return x>=0&&y>=0&&x<4&&y<8;
    }

    public boolean roundTest(Piece p){
        return p.getColor()==round||round==PieceColor.UNKNOWN;
    }

     public void setRoundStatus(PieceColor color){
        round=color;
    }

    public PieceColor getRoundStatus(){
        return round;
    }


    public int getScore(PieceColor color){
        return color==offensiveColor?offensiveScore:defensiveScore;
    }


    public void  calcScore(Piece p, int rate){
        if(p.getColor()==offensiveColor)
            defensiveScore+=p.getType().getScore()*rate;
        else offensiveScore+=p.getType().getScore()*rate;
    }

    void nextRound() {
        setRoundStatus(round== PieceColor.RED? PieceColor.BLACK: PieceColor.RED);
    }

    public int[] getValidPlace(int x, int y){
        int[] dis=new int[4];

        Piece p=board[x][y];
        if(p.isCovered()||p.isEmpty()||!roundTest(p))
            return dis;

        if(p.getType()!= PieceType.CANNON){
            for(int i=0;i<4;++i){
                int nx=x+dx[i],ny=y+dy[i];
                if(!inRange(nx,ny))continue;
                Piece nPiece=board[nx][ny];
                if(!nPiece.isCovered()){
                    if(nPiece.isEmpty())dis[i]=1;
                    else if(p.compareTo(nPiece))dis[i]=1;
                }
            }
        }else{
            for(int i=0;i<4;++i){
                int cnt=0,nx=x+dx[i],ny=y+dy[i];
                do{
                    if(!inRange(nx,ny))break;
                    cnt+=board[nx][ny].isEmpty()?0:1;
                    if(cnt==2)break;
                    nx+=dx[i];
                    ny+=dy[i];
                }while(true);
                if(cnt==2)
                    dis[i]=Math.max(Math.abs(nx-x),Math.abs(ny-y));
            }
        }
        return dis;
    }

    public Piece getPiece(int x,int y){
        return board[x][y];
    }

    public void setPiece(int x,int y,Piece piece){
        board[x][y]=piece;
    }

    public void move(int x, int y, int direction){
        int[] dis=getValidPlace(x,y);
        if(dis[direction]==0)return;

        int nx=x+dx[direction]*dis[direction],ny=y+dy[direction]*dis[direction];

        Piece nPiece=getPiece(nx,ny),piece=getPiece(x,y);

        if(!nPiece.isEmpty())
            calcScore(nPiece,1);

        setPiece(nx,ny,piece);
        setPiece(x,y,new Piece());

        nextRound();
    }

    public void discover(int x, int y){
        if(!getPiece(x,y).isCovered())
            return;

        if(offensiveColor==PieceColor.UNKNOWN)
            offensiveColor=round=getPiece(x,y).getColor();
        getPiece(x,y).discover();

        nextRound();
    }

    boolean isDraw(){
        for(int i=0;i<4;++i)
            for(int j=0;j<8;++j){
                Piece p=getPiece(i,j);
                if(p.isCovered())return false;
                if(p.isEmpty()||!roundTest(p))continue;
                int[] dis=getValidPlace(i,j);
                for(int x:dis)
                    if(x!=0)return false;
            }
        return true;
    }

    public RoundStatus checkWinner(){
        if(offensiveScore>=60)return RoundStatus.OFFENSIVE;
        if(defensiveScore>=60)return RoundStatus.DEFENSIVE;
        if(isDraw())return RoundStatus.DRAW;
        return RoundStatus.PLAYING;
    }

    //withdraw operation

    public void cover(int x,int y){
        getPiece(x,y).cover();
        nextRound();
    }

    public void reset(int x,int y,Piece piece){
        setPiece(x,y,piece);
        calcScore(piece,-1);
    }

    public void move(int x1,int y1,int x2,int y2){
        setPiece(x2,y2,getPiece(x1,y1));
        setPiece(x1,y1,new Piece());
        nextRound();
    }

    public Piece[][] getBoard(){
        return board;
    }

    public static ChessBoard createCB(int seed){
        Random rd=new Random(seed);
        Piece[] pieces =Piece.createBoard();
        for (int i = 0; i < pieces.length; i++) {
            int r=rd.nextInt(i+1);
            Piece tmp= pieces[r];
            pieces[r]= pieces[i];
            pieces[i]=tmp;
        }
        return new ChessBoard(pieces);
    }

    public boolean flipCheck(int x,int y){
        return inRange(x,y)&&!board[x][y].isEmpty()&&board[x][y].isCovered();
    }

    public boolean moveCheck(int x,int y,int x1,int y1){
        if(!inRange(x,y))return false;
        int[] dis=getValidPlace(x,y);
        for(int k=0;k<4;++k){
            int nx=x+dis[k]*dx[k],ny=y+dis[k]*dy[k];
            if(nx==x1&&ny==y1)
                return true;
        }
        return false;
    }
}
