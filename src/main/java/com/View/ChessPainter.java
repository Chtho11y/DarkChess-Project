package com.View;

import com.Controller.ChessExecutor;
import com.Model.Piece;
import com.Model.PieceColor;
import com.Model.PieceType;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * The {@code ChessPainter} class add the board and pieces into the Pane.
 * @author Zhou Yixian
 */
public class ChessPainter {
    static Stage stage;

    static int posX,posY,size,pieceSize;
    static Rectangle selectCell;
    static Rectangle[] nextCell;
    static final int width=4;
    static final int height=8;
    final static String boardColor="#dd9222",
                        pieceColor="#D9C381",
                        coveredColor="#EBB865",
                        selectedColor="#85CEE0",
                        validColor="#FFC266",
                        killColor="#E98E8E";
    static RemovedPieceNode[] redPieceList,blackPieceList;

    static class PieceNode{
        Circle bg,c1,c2;
        Text t;

        Piece p;

        public PieceNode(Circle bg, Circle c1, Circle c2, Text t,Piece p) {
            this.bg = bg;
            this.c1 = c1;
            this.c2 = c2;
            this.t = t;
            this.p = p;
        }

        public void hide(){
            bg.setFill(Color.TRANSPARENT);
            c1.setFill(Color.TRANSPARENT);
            c2.setFill(Color.TRANSPARENT);
            c1.setStroke(Color.TRANSPARENT);
            c2.setStroke(Color.TRANSPARENT);
            t.setFill(Color.TRANSPARENT);
            bg.setEffect(Painter.getNoShadow());
        }

        public void cover(){
            bg.setFill(Color.valueOf(coveredColor));
            c1.setStroke(Color.TRANSPARENT);
            c2.setStroke(Color.TRANSPARENT);
            t.setFill(Color.TRANSPARENT);
            bg.setEffect(Painter.getStandardShadow());
        }

        public void show(){
            bg.setFill(Color.valueOf(pieceColor));
            c1.setStroke(p.getColor().getValue());
            c2.setStroke(p.getColor().getValue());
            t.setFill(p.getColor().getValue());
            System.out.println(t.getText()+":"+((t.getFill().toString().equals("0x000000ff"))?"黑方":"红方"));
            bg.setEffect(Painter.getStandardShadow());
        }

        public void setPos(int x,int y){
            setX((x+0.5)*size+posX);
            setY((y+0.5)*size+posY);
        }

        public void setX(double x){
            bg.setCenterX(x);
            c1.setCenterX(x);
            c2.setCenterX(x);
            t.setX(x-0.3*pieceSize);
        }

        public void setY(double y){
            bg.setCenterY(y);
            c1.setCenterY(y);
            c2.setCenterY(y);
            t.setY(y-0.3*pieceSize);
        }
    }

    static class RemovedPieceNode{
        int counter;
        Text num;
        Circle bg;
        PieceNode piece;

        public RemovedPieceNode(){

        }

        public void modify(int n){
            counter+=n;
            num.setText(Integer.toString(counter));
            if(counter==0)piece.hide();
            else if(counter==1){
                bg.setFill(Color.TRANSPARENT);
                num.setFill(Color.TRANSPARENT);
                piece.show();
            }else{
                bg.setFill(piece.p.getColor()== PieceColor.BLACK? Color.BLUE:Color.RED);
                num.setFill(Color.WHITE);
                piece.show();
            }
        }
    }

    static PieceNode[][] pieces;

    static final int sizeRate=1;

     public static void setStage(Stage stage,int posX,int posY,int size,int pieceSize){
        ChessPainter.stage=stage;
        ChessPainter.posX=posX;
        ChessPainter.posY=posY;
        ChessPainter.size=size;
        ChessPainter.pieceSize=pieceSize;
        init();
    }

    static public void init(){
        pieces=new PieceNode[4][8];
        redPieceList=new RemovedPieceNode[8];
        blackPieceList=new RemovedPieceNode[8];
        selectCell=new Rectangle();
        nextCell=new Rectangle[4];
    }

    public static void paintAll(Pane pane){
        paintBoard(pane);
        paintSelectedCell(pane);
        paintPieces(pane);
        paintRemovedPiece(pane);
    }

    public static void modifyPieceNumber(Piece p,int n){
        if(p.getColor()==PieceColor.RED)redPieceList[p.getType().getId()].modify(n);
        else blackPieceList[p.getType().getId()].modify(n);
    }

    static void paintBoard(Pane pane){
        int lenX=size*width,lenY=size*height;
        Rectangle rect=new Rectangle(posX-20,posY-50,lenX+40,lenY+70);
        rect.setFill(Color.valueOf(boardColor));
        pane.getChildren().add(rect);
        for(int i=0;i<=width;++i){
            Line l=new Line();
            l.setStartX(posX+size*i);
            l.setEndX(posX+size*i);
            l.setStartY(posY);
            l.setEndY(posY+lenY);
            pane.getChildren().add(l);
        }
        for(int i=0;i<=height;++i){
            Line l=new Line();
            l.setStartX(posX);
            l.setEndX(posX+lenX);
            l.setStartY(posY+i*size);
            l.setEndY(posY+i*size);
            pane.getChildren().add(l);
        }
    }

    static void paintSelectedCell(Pane pane){
        for (int i = 0; i < 4; i++) {
            nextCell[i]=new Rectangle();
            nextCell[i].setFill(Color.TRANSPARENT);
            pane.getChildren().add(nextCell[i]);
        }
        selectCell.setFill(Color.TRANSPARENT);
        pane.getChildren().add(selectCell);
    }

    static void paintCell(Rectangle rect,int x,int y,Color color){
        if(!ChessExecutor.inRange(x,y))return;
        rect.setX(posX+x*size+1);
        rect.setY(posY+y*size+1);
        rect.setHeight(size-2);
        rect.setWidth(size-2);
        rect.setFill(color);
    }

    public static void setSelectCell(int x, int y){
        paintCell(selectCell,x,y, Color.valueOf(selectedColor));
        int[] dis=ChessExecutor.getValidPlace(x,y);
        for(int i=0;i<4;++i){
            int nx=x+dis[i]*ChessExecutor.dx[i];
            int ny=y+dis[i]*ChessExecutor.dy[i];
            if((!ChessExecutor.inRange(nx,ny))||(dis[i]==0))continue;
            if(ChessExecutor.getPiece(nx,ny).isEmpty())
                paintCell(nextCell[i],nx,ny, Color.valueOf(validColor));
            else paintCell(nextCell[i],nx,ny, Color.valueOf(killColor));
        }
    }

    public static void eraseSelectCell(){
        selectCell.setFill(Color.TRANSPARENT);
        for(Rectangle rect:nextCell)
            rect.setFill(Color.TRANSPARENT);
    }

    static void paintPieces(Pane pane){
        for(int i=0;i<4;++i)
            for (int j = 0; j < 8; j++) {
                pieces[i][j]=createNewPiece((i+0.5)*size+posX,(j+0.5)*size+posY,pieceSize,ChessExecutor.getPiece(i,j));
                pane.getChildren().addAll(pieces[i][j].bg,pieces[i][j].c1,pieces[i][j].c2,pieces[i][j].t);
                pieces[i][j].cover();
            }
        Animation.piece=createNewPiece(0,0,pieceSize,new Piece(PieceColor.RED,PieceType.EMPTY,false));
        PieceNode p= Animation.piece;

        pane.getChildren().addAll(p.bg,p.c1,p.c2,p.t);
    }

    static PieceNode createNewPiece(double x,double y,int size,Piece p){
        Circle circle=new Circle(x,y,size/2.0,Color.TRANSPARENT);
        circle.setEffect(Painter.getStandardShadow());
        circle.setStroke(Color.TRANSPARENT);
        Circle hide1=new Circle(x,y,size/2.3, Color.TRANSPARENT),
               hide2=new Circle(x,y,size/2.5, Color.TRANSPARENT);
        hide1.setStroke(Color.TRANSPARENT);
        hide2.setStroke(Color.TRANSPARENT);
        Text text=Painter.text(size*0.6,p.getText(),x-size*0.3,y-size*0.3, Color.TRANSPARENT);
        return new PieceNode(circle,hide1,hide2,text,p);
    }

    static public void swapPiece(int xFrom,int yFrom,int xTo,int yTo){
        PieceNode piece=pieces[xFrom][yFrom];
        pieces[xFrom][yFrom]=pieces[xTo][yTo];
        pieces[xTo][yTo]=piece;
        pieces[xTo][yTo].setPos(xTo,yTo);
        pieces[xFrom][yFrom].setPos(xFrom,yFrom);
    }

    static public void showPiece(int x,int y){
         pieces[x][y].show();
    }

    static public void coverPiece(int x,int y){
         pieces[x][y].cover();
    }

    static public void hidePiece(int x,int y){
         pieces[x][y].hide();
    }

    static void paintRemovedPiece(Pane pane){
        for(PieceType x:PieceType.values()){
            redPieceList[x.getId()]=new RemovedPieceNode();
            redPieceList[x.getId()].piece=createNewPiece(posX-80,posY+size*x.getId(),pieceSize,new Piece(PieceColor.RED,x,false));
            RemovedPieceNode p=redPieceList[x.getId()];

            int xp=posX+(int)(size*sizeRate*0.45)-80,yp=posY+size*x.getId()-(int)(size*sizeRate*0.45);
            Circle circle=new Circle(xp+3,yp+6,7, Color.TRANSPARENT);
            Text text=Painter.text(10,"",xp,yp, Color.TRANSPARENT);
            text.setFont(new Font("宋体",10));

            p.bg=circle;
            p.num=text;

            pane.getChildren().addAll(circle,text,p.piece.bg,p.piece.c1,p.piece.c2,p.piece.t);
        }


        for(PieceType x:PieceType.values()){
            blackPieceList[x.getId()]=new RemovedPieceNode();
            blackPieceList[x.getId()].piece=createNewPiece(posX+size*4+80,posY+size*x.getId(),pieceSize,new Piece(PieceColor.BLACK,x,false));
            RemovedPieceNode p=blackPieceList[x.getId()];

            int xp=posX+(int)(size*sizeRate*0.45)+size*4+80,yp=posY+size*x.getId()-(int)(size*sizeRate*0.45);
            Circle circle=new Circle(xp+3,yp+6,7, Color.TRANSPARENT);
            Text text=Painter.text(10,"",xp,yp, Color.TRANSPARENT);
            text.setFont(new Font("宋体",10));

            p.bg=circle;
            p.num=text;

            pane.getChildren().addAll(circle,text,p.piece.bg,p.piece.c1,p.piece.c2,p.piece.t);
        }
    }

    //Animation
    static class Animation{
         static PieceNode piece;
         static final int interval=30;
         static boolean killed;

         static AnimationTimer timer=new AnimationTimer() {
             @Override
             public void handle(long l) {
                 counter--;
                 if(counter<=0){
                     swapPiece(xFrom,yFrom,xTo,yTo);
                     showPiece(xTo,yTo);
                     if (killed)hidePiece(xFrom,yFrom);
                     piece.c1.setStroke(Color.TRANSPARENT);
                     piece.c2.setStroke(Color.TRANSPARENT);
                     piece.bg.setFill(Color.TRANSPARENT);
                     piece.t.setFill(Color.TRANSPARENT);
                     timer.stop();
                 }
                 piece.setX(getPos(xFrom,xTo,1-(double)counter/interval,posX));
                 piece.setY(getPos(yFrom,yTo,1-(double)counter/interval,posY));
             }
         };

         static int counter,xFrom,xTo,yFrom,yTo;

         public static void start(Piece p,int x,int y,int x1,int y1,boolean kill){
             counter=interval;
             piece.setPos(x,y);
             piece.c1.setStroke(p.getColor().getValue());
             piece.c2.setStroke(p.getColor().getValue());
             piece.bg.setFill(Color.valueOf(pieceColor));
             piece.t.setFill(p.getColor().getValue());
             piece.t.setText(p.getText());
             timer.start();
             xFrom=x;
             yFrom=y;
             xTo=x1;
             yTo=y1;
             killed=kill;
         }

         static double getPos(int from,int to,double rate,double origin){
             return (from+(to-from)*speed(rate)+0.5)*size+origin;
         }

         static double speed(double pos){
             return 0.5*(1-Math.cos(pos*Math.acos(-1)));
         }
    }

    public static void travelPiece(int x,int y,int x1,int y1,Piece p,boolean killed){
        Animation.start(p,x,y,x1,y1,killed);
    }
}
