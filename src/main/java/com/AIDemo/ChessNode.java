package com.AIDemo;

import com.Controller.Recorder;
import com.Model.Piece;
import com.Model.PieceColor;
import com.Model.PieceType;
import com.Model.RoundStatus;

import java.util.ArrayList;

public class ChessNode {

    Piece[][] piece;

    public static final int[] dy={-1,0,1,0};
    public static final int[] dx={0,1,0,-1};

    int coverCount,blackScore,redScore;
    int[] blackPieceCount;
    int[] redPieceCount;
    double rate;

    PieceColor round;

    public ChessNode(){
        piece=new Piece[4][8];
        blackPieceCount=new int[8];
        redPieceCount=new int[8];
        rate=1;
    }

    void clone(ChessNode c){
        for(int i=0;i<4;++i)
            for(int j=0;j<8;++j)
                piece[i][j]=new Piece(c.piece[i][j]);
        coverCount=c.coverCount;
        round=c.round;
        for(int i=0;i<8;++i){
            blackPieceCount[i]=c.blackPieceCount[i];
            redPieceCount[i]=c.redPieceCount[i];
        }
        blackScore=c.blackScore;
        redScore=c.redScore;
        rate=1;
    }

    public void init(Piece[][] x,int[] bPiece,int[] rPiece) {
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 8; ++j) {
                piece[i][j] = new Piece(x[i][j]);
                if (piece[i][j].isCovered()) coverCount++;
            }
        blackPieceCount=bPiece;
        redPieceCount=rPiece;
    }

    public double evaluate(){
        double x=(round== PieceColor.RED?1:-1),ans=0;
        for (PieceType value : PieceType.values()) {
            ans+=(value.getNum()-redPieceCount[value.getId()])*x*value.getValue();
            ans+=(value.getNum()-blackPieceCount[value.getId()])*(-x)* value.getValue();
        }
        return ans;
    }

    public double getRate(){
        return rate;
    }

    ArrayList<Position> next(boolean flip){
        ArrayList<Position> res=new ArrayList<>();
        int[] rc=new int[8];
        int[] bc=new int[8];
        for(int i=0;i<8;++i){
            rc[i]=redPieceCount[i];
            bc[i]=blackPieceCount[i];
        }
        for(int i=0;i<4;++i)
            for(int j=0;j<8;++j){
                Piece p=piece[i][j];
                if(!p.isEmpty()&&roundTest(p)&&!p.isCovered()){
                    int[] dis=getValidPlace(i,j);
                    for(int k=0;k<4;++k){
                        if(dis[k]==0)continue;
                        int nx=i+dx[k]*dis[k],ny=j+dy[k]*dis[k];
                        Piece np=piece[nx][ny];
                        if(np.isCovered()){
                            if(!flip)continue;
                            Position position=new Position();
                            position.setOp(new Recorder.Operation("move",new Recorder.Point(i,j),new Recorder.Point(nx,ny)));
                            for (PieceType value : PieceType.values()) {
                                if(value== PieceType.EMPTY)continue;
                                if(redPieceCount[value.getId()]<value.getNum()){
                                    ChessNode nxt=new ChessNode();
                                    nxt.clone(this);
                                    nxt.setRound(round.nextColor());
                                    nxt.piece[i][j]=new Piece();
                                    nxt.rate=(double)(value.getNum()-rc[value.getId()])/coverCount;
                                    nxt.coverCount--;
                                    nxt.setPiece(nx,ny,new Piece(PieceColor.RED,value,false));
                                    nxt.setPiece(nx,ny,p);
                                    position.add(nxt);
                                }
                                if(blackPieceCount[value.getId()]<value.getNum()){
                                    ChessNode nxt=new ChessNode();
                                    nxt.clone(this);
                                    nxt.setRound(round.nextColor());
                                    nxt.piece[i][j]=new Piece();
                                    nxt.rate=(double)(value.getNum()-bc[value.getId()])/coverCount;
                                    nxt.coverCount--;
                                    nxt.setPiece(nx,ny,new Piece(PieceColor.BLACK,value,false));
                                    nxt.setPiece(nx,ny,p);
                                    position.add(nxt);
                                }
                            }
                            res.add(position);
                        }else{
                            ChessNode nxt=new ChessNode();
                            nxt.clone(this);
                            nxt.setRound(round.nextColor());
                            nxt.piece[i][j]=new Piece();
                            nxt.setPiece(nx,ny,p);
                            Position position=new Position();
                            position.setOp(new Recorder.Operation("move",new Recorder.Point(i,j),new Recorder.Point(nx,ny)));
                            position.add(nxt);
                            res.add(position);
                        }
                    }
                }

            }
        return res;
    }

    public ArrayList<Position> getFlips(){
        ArrayList<Position> res=new ArrayList<>();
        int[] rc=new int[8];
        int[] bc=new int[8];
        for(int i=0;i<8;++i){
            rc[i]=redPieceCount[i];
            bc[i]=blackPieceCount[i];
        }
        for(int i=0;i<4;++i)
            for(int j=0;j<8;++j)
                if(!piece[i][j].isEmpty()&&!piece[i][j].isCovered())
                    if(piece[i][j].getColor()== PieceColor.RED)
                        rc[piece[i][j].getType().getId()]++;
                    else bc[piece[i][j].getType().getId()]++;
        for(int i=0;i<4;++i)
            for(int j=0;j<8;++j)
                if(!piece[i][j].isEmpty()&&piece[i][j].isCovered()){
                    Position position=new Position();
                    position.setOp(new Recorder.Operation("discover", new Recorder.Point(i, j)));
                    for (PieceType value : PieceType.values()) {
                        if(value== PieceType.EMPTY)continue;
                        if(rc[value.getId()]<value.getNum()){
                            ChessNode nxt=new ChessNode();
                            nxt.clone(this);
                            nxt.setRound(round.nextColor());
                            nxt.piece[i][j]=new Piece(PieceColor.RED,value,false);
                            nxt.rate=(double)(value.getNum()-rc[value.getId()])/coverCount;
                            nxt.coverCount--;
                            position.add(nxt);
                        }
                        if(bc[value.getId()]<value.getNum()){
                            ChessNode nxt=new ChessNode();
                            nxt.clone(this);
                            nxt.setRound(round.nextColor());
                            nxt.piece[i][j]=new Piece(PieceColor.BLACK,value,false);
                            nxt.rate=(double)(value.getNum()-bc[value.getId()])/coverCount;
                            nxt.coverCount--;
                            position.add(nxt);
                        }
                    }
                    res.add(position);
                }
        return res;
    }

    void setPiece(int x,int y,Piece p){
        if(!piece[x][y].isEmpty()&&!piece[x][y].isCovered()){
            remove(x,y);
        }
        piece[x][y]=p;
    }

    public void setRound(PieceColor color){
        round=color;
    }

    void remove(int x,int y){
        Piece p=piece[x][y];
        if(p.getColor()==PieceColor.RED){
            redPieceCount[p.getType().getId()]++;
            redScore-=p.getType().getScore();
        } else {
            blackPieceCount[p.getType().getId()]++;
            blackScore-=p.getType().getScore();
        }
    }

    boolean roundTest(Piece p){
        return p.getColor()==round;
    }

    public int[] getValidPlace(int x, int y){
        int[] dis=new int[4];

        Piece p=piece[x][y];
        if(p.isCovered()||p.isEmpty()||!roundTest(p))
            return dis;

        if(p.getType()!= PieceType.CANNON){
            for(int i=0;i<4;++i){
                int nx=x+dx[i],ny=y+dy[i];
                if(!inRange(nx,ny))continue;
                Piece nPiece=piece[nx][ny];
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
                    cnt+=piece[nx][ny].isEmpty()?0:1;
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

    public RoundStatus checkWinner(){
        if(blackScore>=60)return round==PieceColor.BLACK?RoundStatus.OFFENSIVE: RoundStatus.DEFENSIVE;
        if(redScore>=60)return round==PieceColor.RED?RoundStatus.OFFENSIVE: RoundStatus.DEFENSIVE;
        return RoundStatus.PLAYING;
    }

    public void setScore(int redScore,int blackScore){
        this.redScore=redScore;
        this.blackScore=blackScore;
    }

    public static boolean inRange(int x, int y){
        return x>=0&&y>=0&&x<4&&y<8;
    }
}
