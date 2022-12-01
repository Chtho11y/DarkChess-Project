package com.Controller;

import com.Model.*;
import com.View.ChessPainter;
import com.View.UIPainter;

import java.util.Random;

/**
 * The {@code ChessExecutor} class simulate the operation during the Dark Chess game.
 * The {@code ChessExecutor} class includes methods for creating an initial broad,
 * moving a chess, checking the possible moves and judging the winner.
 * Withdraw should use another method instead of the normal one.
 * @author    Zhou Yixian
 */
public class ChessExecutor {

    static final int width=4,height=8;
    public static final int[] dy={-1,0,1,0};
    public static final int[] dx={0,1,0,-1};

    static PieceColor roundStatus;
    static public PieceColor offensiveColor;
    static int offensiveScore,defensiveScore;

    static Piece[] board;

    public static void init(int seed){
        Recorder.clear();
        Recorder.recordSeed(seed);
        Random rd=new Random(seed);
        board=Piece.createBoard();
        for (int i = 0; i < board.length; i++) {
            int r=rd.nextInt(i+1);
            Piece tmp=board[r];
            board[r]=board[i];
            board[i]=tmp;
        }
        offensiveScore=defensiveScore=0;
        roundStatus=offensiveColor=PieceColor.UNKNOWN;
    }

    static int indexOf(int x,int y){
        return y*width+x;
    }

    public static Piece getPiece(int x, int y){
        return board[indexOf(x,y)];
    }

    static void setPiece(int x, int y, Piece p){
        board[indexOf(x,y)]=p;
    }

    public static boolean inRange(int x, int y){
        return x>=0&&y>=0&&x<4&&y<8;
    }

    public static boolean roundTest(Piece p){
        return p.getColor()==roundStatus;
    }

    static void setRoundStatus(PieceColor color){
        roundStatus=color;
    }

    public static PieceColor getRoundStatus(){
        return roundStatus;
    }

    static void nextRound(){
        setRoundStatus(roundStatus== PieceColor.RED? PieceColor.BLACK: PieceColor.RED);
        UIPainter.setSymbol(roundStatus);
    }

    public static int getScore(PieceColor color){
        return color==offensiveColor?offensiveScore:defensiveScore;
    }

    static void  calcScore(Piece p,int rate){
        if(p.getColor()==offensiveColor)
            defensiveScore+=p.getType().getScore()*rate;
        else offensiveScore+=p.getType().getScore()*rate;
        UIPainter.updateScore();
    }

    /**
     * get the positions that the piece of {@code (x,y)} can arrive.
     * @param x row id
     * @param y column id
     * @return {@code int[4]}, the distance this piece can move ({@code 0} if it can't move) in each direction:
     *          upward={@code 0}, right={@code 1}, downward={@code 2}, left={@code 3}.
     *          Negative distance means it can capture a piece.
     */
    public static int[] getValidPlace(int x, int y){
        int[] dis=new int[4];

        Piece p=getPiece(x,y);
        if(p.isCovered()||p.isEmpty()||!roundTest(p))
            return dis;

        if(p.getType()!= PieceType.CANNON){
            for(int i=0;i<4;++i){
                int nx=x+dx[i],ny=y+dy[i];
                if(!inRange(nx,ny))continue;
                Piece nPiece=getPiece(nx,ny);
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
                    cnt+=getPiece(nx,ny).isEmpty()?0:1;
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

    public static void move(int x, int y, int direction){
        int[] dis=getValidPlace(x,y);
        if(dis[direction]==0)return;

        int nx=x+dx[direction]*dis[direction],ny=y+dy[direction]*dis[direction];

        Piece nPiece=getPiece(nx,ny),piece=getPiece(x,y);

        Recorder.recordMove(x,y,nx,ny);
        ChessPainter.hidePiece(x,y);
        ChessPainter.travelPiece(x,y,nx,ny,getPiece(x,y),!nPiece.isEmpty());

        if(!nPiece.isEmpty()){
            calcScore(nPiece,1);
            Recorder.recordReplace(nx,ny,nPiece);
        }
        setPiece(nx,ny,piece);
        setPiece(x,y,new Piece());

        nextRound();
    }

    public static void discover(int x, int y){
        if(!getPiece(x,y).isCovered())
            return;

        Recorder.recordDiscover(x,y);
        ChessPainter.showPiece(x,y);

        if(offensiveColor==PieceColor.UNKNOWN)
            offensiveColor=roundStatus=getPiece(x,y).getColor();
        getPiece(x,y).discover();

        nextRound();
    }

    static boolean isDraw(){
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

    public static RoundStatus checkWinner(){
        if(offensiveScore>=60)return RoundStatus.OFFENSIVE;
        if(defensiveScore>=60)return RoundStatus.DEFENSIVE;
        if(isDraw())return RoundStatus.DRAW;
        return RoundStatus.PLAYING;
    }

    //withdraw operation

    public static void cover(int x,int y){
        getPiece(x,y).cover();
        ChessPainter.coverPiece(x,y);
        nextRound();
    }

    public static void reset(int x,int y,Piece piece){
        setPiece(x,y,piece);
        if(piece.isCovered())ChessPainter.coverPiece(x,y);
        else ChessPainter.showPiece(x,y);
        ChessPainter.modifyPieceNumber(piece,-1);
        calcScore(piece,-1);
    }

    public static void move(int x1,int y1,int x2,int y2){
        setPiece(x2,y2,getPiece(x1,y1));
        setPiece(x1,y1,new Piece());
        ChessPainter.swapPiece(x1,y1,x2,y2);
        nextRound();
    }
}
