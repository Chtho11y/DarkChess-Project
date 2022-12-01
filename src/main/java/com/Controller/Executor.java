package com.Controller;

import com.Model.RoundStatus;
import com.View.ChessPainter;
import com.View.Menu;
import com.View.ProgressPainter;
import com.View.UIPainter;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.InputStream;

public class Executor {
    static Stage stage;

    static final int boardX=140,boardY=120,cellSize=90,pieceSize=80;

    static boolean pause;

    public static void setStage(Stage stage){
        Executor.stage=stage;
        ChessPainter.setStage(stage,boardX,boardY,cellSize,pieceSize);
    }

    public static void run(InputStream inputStream){
        stage.setTitle("Dark♂Chess");
        stage.getIcons().add(new Image(inputStream));
        Recorder.load();
        gameStart(true,0);
    }

    public static void gameStart(boolean newGame,int seed){
        //Handler.init(114514);
        if(newGame)
            seed=(int)System.currentTimeMillis();
        Handler.init(seed);
        ChessPainter.init();
        UIPainter.init();
        ProgressPainter.init();
        Menu.init();
        drawPlaying();
        pause=false;
    }

    static void drawPlaying(){
        Pane pane=new Pane();
        UIPainter.PaintUI(pane);
        ChessPainter.paintAll(pane);
        UIPainter.paintPlayerMessage(pane);
        UIPainter.paintPlayingSymbol(pane);
        ProgressPainter.paint(pane);
        Menu.paint(pane);

        Scene sc=new Scene(pane,640,860);
        sc.setFill(Color.valueOf("#A3B0C2"));

        sc.setOnMouseClicked(e->{
            if(Handler.getStatus()!=RoundStatus.PLAYING||pause)
                return;
            double x=e.getSceneX(),y=e.getSceneY();
            if(x>boardX&&y>boardY)
                Handler.select((int)((x-boardX)/cellSize),(int)((y-boardY)/cellSize));
            if(Handler.getStatus()!= RoundStatus.PLAYING)
                drawEnd(Handler.getStatus());
        });
        stage.setScene(sc);
        stage.show();
    }

    static void drawEnd(RoundStatus status){
        UIPainter.paintEndMessage(status);
        ChessPainter.eraseSelectCell();
        UIPainter.paintEnd();
    }

    public static void redrawMenu(){
        setPause(true);
        Menu.show();
    }

    public static void setPause(boolean pause){
        Executor.pause=pause;
    }

    public static void exit(){
        stage.close();
    }

    public static Stage getStage(){
        return stage;
    }
}
