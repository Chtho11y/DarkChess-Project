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

    public static final int[] dy={-1,0,1,0};
    public static final int[] dx={0,1,0,-1};
    static ChessBoard board;
    public static void init(int seed){
        Recorder.clear();
        Recorder.recordSeed(seed);
        Random rd=new Random(seed);
        Piece[] pieces =Piece.createBoard();
        for (int i = 0; i < pieces.length; i++) {
            int r=rd.nextInt(i+1);
            Piece tmp= pieces[r];
            pieces[r]= pieces[i];
            pieces[i]=tmp;
        }
        board=new ChessBoard(pieces);
    }

    static boolean isOffensive(){
        return board.isOffensive();
    }

    public static Piece getPiece(int x, int y){
        return board.getPiece(x,y);
    }

    static void setPiece(int x, int y, Piece p){
        board.setPiece(x,y,p);
    }

    public static boolean inRange(int x, int y){
        return x>=0&&y>=0&&x<4&&y<8;
    }

    public static boolean roundTest(Piece p){
        return board.roundTest(p);
    }

    static void setRoundStatus(PieceColor color){
        board.setRoundStatus(color);
    }

    public static PieceColor getRoundStatus(){
        return board.getRoundStatus();
    }

    static void nextRound() {
        UIPainter.setSymbol(getRoundStatus());
    }

    public static int getScore(PieceColor color){
        return board.getScore(color);
    }

    public static void calcScore(Piece p,int n){
        board.calcScore(p,n);
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
        return board.getValidPlace(x,y);
    }

    public static void move(int x, int y, int direction){
        int[] dis=getValidPlace(x,y);
        if(dis[direction]==0)return;

        int nx=x+dx[direction]*dis[direction],ny=y+dy[direction]*dis[direction];

        Piece nPiece=getPiece(nx,ny);

        Recorder.recordMove(x,y,nx,ny);
        ChessPainter.hidePiece(x,y);
        ChessPainter.travelPiece(x,y,nx,ny,getPiece(x,y),!nPiece.isEmpty());

        board.move(x,y,direction);

        if(!nPiece.isEmpty()){
            Recorder.recordReplace(nx,ny,nPiece);
            UIPainter.updateScore();
        }

        nextRound();
    }

    public static void discover(int x, int y){
        if(!getPiece(x,y).isCovered())
            return;

        Recorder.recordDiscover(x,y);
        ChessPainter.showPiece(x,y);

        board.discover(x,y);

        nextRound();
    }


    public static RoundStatus checkWinner(){
        return board.checkWinner();
    }

    //withdraw operation

    public static void cover(int x,int y){
        board.cover(x,y);
        ChessPainter.coverPiece(x,y);
        nextRound();
    }

    public static void reset(int x,int y,Piece piece){
        board.reset(x,y,piece);
        if(piece.isCovered())ChessPainter.coverPiece(x,y);
        else ChessPainter.showPiece(x,y);
        ChessPainter.modifyPieceNumber(piece,-1);
        UIPainter.updateScore();
    }

    public static void move(int x1,int y1,int x2,int y2){
        board.move(x1,y1,x2,y2);
        ChessPainter.swapPiece(x1,y1,x2,y2);
        nextRound();
    }

    public static Piece[][] getBoard(){
        return board.getBoard();
    }

    public static PieceColor getOffensiveColor(){
        return board.getOffensiveColor();
    }
}
