package com.Controller;

import com.AIDemo.MinMax;
import com.Model.InputMode;
import com.Model.RoundStatus;
import com.View.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class Executor {
    static Stage stage;

    static InputMode gameMode;

    static final int boardX=140,boardY=120,cellSize=90,pieceSize=80;

    static boolean pause;

    public static void setStage(Stage stage){
        Executor.stage=stage;
        ChessPainter.setStage(stage,boardX,boardY,cellSize,pieceSize);
    }

    public static void run(InputStream in){
        try {
            MainMenu.paint(stage);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        Recorder.load();
        stage.setTitle("dark♂chess");
        stage.getIcons().add(new Image(in));
    }

    public static void gameStart(boolean newGame,int seed,InputMode mode){
        //Handler.init(114514);
        gameMode=mode;
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

    public static InputMode getGameMode(){
        return gameMode;
    }

    static void drawPlaying(){
        Pane pane=new Pane();
        UIPainter.PaintUI(pane);
        ChessPainter.paintAll(pane);
        UIPainter.paintPlayerMessage(pane);
        UIPainter.paintPlayingSymbol(pane);
        ProgressPainter.paint(pane);
        Menu.paint(pane);
        Replay.paint(pane);

        Scene sc=new Scene(pane,640,860);
        sc.setFill(Color.valueOf("#A3B0C2"));

        sc.setOnMouseClicked(e-> {
            if (Handler.getStatus() != RoundStatus.PLAYING || pause)
                return;
            double x = e.getSceneX(), y = e.getSceneY();
            if (x > boardX && y > boardY)
                Connector.normalInput((int) ((x - boardX) / cellSize), (int) ((y - boardY) / cellSize));

            if(Handler.getStatus()!= RoundStatus.PLAYING){
                drawEnd(Handler.getStatus());
                return;
            }

            if(Connector.getMode()== InputMode.AUTO)
                MinMax.operate();
        });
        stage.setScene(sc);
        stage.show();
    }

    static void drawEnd(RoundStatus status){
        UIPainter.paintEndMessage(status);
        ChessPainter.eraseSelectCell();
        UIPainter.paintEnd();
    }

    public static void loadFile(){
        Recorder.load();
    }

    public static void redrawMenu(){
        setPause(true);
        Menu.show();
    }

    public static void redrawMainMenu() {
        try {
            MainMenu.paint(stage);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
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
