package com.View;
import com.AIDemo.MinMax;
import com.Controller.*;
import com.Model.*;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Random;

public class UIPainter {
    static int height=70,lBound=270,rBound=370;

    static Polygon poly1,poly2;
    static Text textMenu,textWithdraw;

    public static void PaintUI(Pane pane){
        Stop[] stop1={new Stop(0, Color.valueOf("#00ccff")),new Stop(1,Color.valueOf("#1169ee"))};
        Stop[] stop2={new Stop(0,Color.valueOf("#f70909")),new Stop(1,Color.valueOf("#ee6911"))};
        LinearGradient lg1=new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,stop1);
        LinearGradient lg2=new LinearGradient(0,0,1,0,true, CycleMethod.NO_CYCLE,stop2);
        Rectangle rect=new Rectangle(0,0,lBound,860);
        rect.setFill(lg1);
        pane.getChildren().add(rect);
        rect=new Rectangle(rBound,0,lBound,860);
        rect.setFill(lg2);
        pane.getChildren().add(rect);

        poly2=new Polygon(rBound,0,rBound,height,lBound,height);
        poly2.setFill(Color.valueOf("#f70909"));
        poly2.setStroke(Paint.valueOf("#f70909"));

        poly2.setOnMouseEntered(e->{
            poly2.setFill(Color.valueOf("#b50707"));
            poly2.setStroke(Paint.valueOf("#b50707"));
        });
        poly2.setOnMouseExited(e->{
            poly2.setFill(Color.valueOf("#f70909"));
            poly2.setStroke(Paint.valueOf("#f70909"));
        });
        poly2.setOnMouseClicked(e->{
            if(Connector.getNext()==InputMode.AUTO) {
                Recorder.withdraw2();
            }
            else {
                if(Connector.getMode()==InputMode.AUTO)
                    return;
                Recorder.withdraw();
            }
            Handler.unselect();
        });
        pane.getChildren().add(poly2);

        poly1=new Polygon(lBound,0,rBound,0,lBound,height);
        poly1.setFill(Color.valueOf("#1169ee"));
        poly1.setStroke(Paint.valueOf("#1169ee"));

        poly1.setOnMouseEntered(e->{
            poly1.setFill(Color.valueOf("#0c2daf"));
            poly1.setStroke(Paint.valueOf("#0c2daf"));
        });
        poly1.setOnMouseExited(e->{
            poly1.setFill(Color.valueOf("#1169ee"));
            poly1.setStroke(Paint.valueOf("#1169ee"));
        });
        poly1.setOnMouseClicked(e->{
            Executor.redrawMenu();
        });

        pane.getChildren().add(poly1);

        textMenu=Painter.text(25,"菜单",lBound,5, Color.BLACK);
        textMenu.setMouseTransparent(true);
        pane.getChildren().add(textMenu);

        textWithdraw=Painter.text(25,"悔棋",rBound-50,height/2.0,Color.BLACK);
        textWithdraw.setMouseTransparent(true);
        pane.getChildren().add(textWithdraw);

        Line line=new Line(0,height,640,height);
        line.setFill(Color.BLACK);
        pane.getChildren().add(line);
    }

    public static void replayMode(){
        textWithdraw.setText("自动");
        poly2.setOnMouseClicked(e->{
            Replay.changeReplayMode();
        });
    }

    public static void setTextWithdraw(String name){
        textWithdraw.setText(name);
    }

    static Text redScore,blackScore,result,redName,blackName;

    public static void paintPlayerMessage(Pane pane){
        redScore=Painter.text(50,"0",20,10, Color.RED);
        pane.getChildren().add(redScore);

        blackScore=Painter.text(50,"0",620-25,10, Color.BLACK);
        pane.getChildren().add(blackScore);

        redName=Painter.text(30,"红方",100,20, Color.RED);
        blackName=Painter.text(30,"黑方",480,20, Color.BLACK);
        pane.getChildren().add(redName);
        pane.getChildren().add(blackName);

        result=Painter.text(40,"",320,75, Color.TRANSPARENT);
        result.setTextOrigin(VPos.TOP);
        result.setFont(Font.font("Times New Roman",35));
        pane.getChildren().add(result);
    }

    public static void updateScore(){
        redScore.setText(Integer.toString(ChessExecutor.getScore(PieceColor.RED)));
        String s=Integer.toString(ChessExecutor.getScore(PieceColor.BLACK));
        blackScore.setText(s);
        blackScore.setX(620-25*s.length());
    }

    public static void init(){
    }

    public static void paintPlayingSymbol(Pane pane){
        PlayingSymbol.setPlayingSymbol(0,0,Color.TRANSPARENT);
        pane.getChildren().add(PlayingSymbol.symbol);
    }

    public static void setSymbol(PieceColor color){
        if(color==PieceColor.RED){
            PlayingSymbol.setPlayingSymbol(180,35,Color.RED);
            redName.setStroke(Color.gray(0.6));
            blackName.setStroke(Color.TRANSPARENT);
        }else{
            PlayingSymbol.setPlayingSymbol(460,35, Color.BLUE);
            redName.setStroke(Color.TRANSPARENT);
            blackName.setStroke(Color.gray(0.6));
        }
    }

    public static void paintEnd(){
        PlayingSymbol.delete();
    }

    static class PlayingSymbol{
        final static int interval=60;
        static int counter;
        static AnimationTimer timer=new AnimationTimer() {
            @Override
            public void handle(long l) {
                counter--;
                if(counter!=0)return;
                counter=interval;
                if(status)hide();
                else show();
            }
        };
        static Color color;
        static Circle symbol=new Circle();
        static boolean status;

        public static void setPlayingSymbol(double x,double y,Color color){
            counter=interval;
            PlayingSymbol.color=color;
            symbol.setCenterY(y);
            symbol.setCenterX(x);
            symbol.setRadius(3);
            show();
            timer.start();
        }

        public static void delete(){
            hide();
            timer.stop();
        }

        public static void show(){
            status=true;
            symbol.setFill(color);
        }

        public static void hide(){
            status=false;
            symbol.setFill(Color.TRANSPARENT);
        }
    }

    public static void paintEndMessage(RoundStatus status){
        PieceColor winner=status.getColor(ChessExecutor.offensiveColor);
        if(winner!= PieceColor.UNKNOWN){
            redScore.setText(winner==PieceColor.RED?"胜":"负");
            blackScore.setText(winner==PieceColor.BLACK?"胜":"负");
        }else{
            redScore.setText("平");
            blackScore.setText("平");
        }
        blackScore.setX(570);
        String res=ChessExecutor.getScore(PieceColor.RED)+"-"+ChessExecutor.getScore(PieceColor.BLACK);
        result.setText(res);
        result.setFill(Color.BLACK);
        result.setX(320-35/4.0*(res.length()));
        textWithdraw.setText("重开");
        poly2.setOnMouseClicked(e->{
            if(Executor.getGameMode()== InputMode.REPLAY)
                Replay.clear();
            if(Executor.getGameMode()==InputMode.AUTO){
                Executor.gameStart(true,0, InputMode.AUTO);
                Random rd=new Random(System.currentTimeMillis());
                int mode=rd.nextInt(2);
                if(mode==1){
                    Connector.setMode(InputMode.NORMAL, InputMode.AUTO);
                }else{
                    Connector.setMode(InputMode.AUTO, InputMode.NORMAL);
                    MinMax.operate();
                }
            }
            if(Executor.getGameMode()==InputMode.NORMAL) {
                Executor.gameStart(true, 0, InputMode.NORMAL);
                Connector.setMode(InputMode.NORMAL, InputMode.NORMAL);
            }
        });
    }
}
