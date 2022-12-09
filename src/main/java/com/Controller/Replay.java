package com.Controller;

import com.Model.InputMode;
import com.View.*;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Replay {

    static final String buttonColor="#7C64E0";
    static Recorder.ReplayIterator iterator;

    static int roundCounter;
    static Text round;

    public static void load(Recorder.Progress progress){
        iterator=new Recorder.ReplayIterator(progress);
        Executor.gameStart(false,iterator.getSeed(),InputMode.REPLAY);
        Connector.setMode(InputMode.REPLAY,InputMode.REPLAY);
        UIPainter.replayMode();
        mode=true;
        roundCounter=0;
        show();
    }

    public static void paint(Pane pane){
        lButton=new Button("<");
        lButton.setBackground(Painter.getBackground(Color.valueOf(buttonColor)));
        lButton.setLayoutX(150);
        lButton.setLayoutY(80);
        lButton.setOnAction(e->{
            pre();
            changeReplayMode(true);
        });
        rButton=new Button(">");
        rButton.setBackground(Painter.getBackground(Color.valueOf(buttonColor)));
        rButton.setLayoutX(490);
        rButton.setLayoutY(80);
        rButton.setOnAction(e->{
            next();
            changeReplayMode(true);
        });
        round=new Text();
        round.setFont(Font.font("华文宋体",30));
        round.setX(260);
        round.setY(80);
        round.setTextOrigin(VPos.TOP);
        round.setMouseTransparent(true);
        round.setFill(Color.BLACK);
        hide();
        pane.getChildren().addAll(lButton,rButton,round);
    }

    public static void pre(){
        if(!iterator.havePre())return;
        roundCounter--;
        setRound();
        iterator.pre();
        Recorder.withdraw();
    }

    public static void clear(){
        while(iterator.havePre()){
            roundCounter--;
            setRound();
            iterator.pre();
            Recorder.withdraw();
        }
    }

    public static void next(){
        if(!iterator.haveNext())return;
        Recorder.Operation op=iterator.getValue();
        roundCounter++;
        setRound();

        if(op.getOp().equals("move")){
            Connector.autoInput(op.p.x, op.p.y);
            Connector.autoInput(((Recorder.Point) (op.arg)).x, ((Recorder.Point) (op.arg)).y);
        }else if(op.getOp().equals("discover")){
            Connector.autoInput(op.p.x, op.p.y);
        }
        iterator.next();
    }

    static Button lButton,rButton;
    public static void show(){
        showing=true;
        lButton.setMouseTransparent(false);
        rButton.setMouseTransparent(false);
        lButton.setVisible(true);
        rButton.setVisible(true);
        round.setVisible(true);
        setRound();
    }

    public static void hide(){
        showing=false;
        lButton.setMouseTransparent(true);
        rButton.setMouseTransparent(true);
        lButton.setVisible(false);
        rButton.setVisible(false);
        round.setVisible(false);
        timer.stop();
    }

    static int counter,interval=120;

    static AnimationTimer timer=new AnimationTimer() {
        @Override
        public void handle(long l) {
            counter--;
            if(counter>0)return;
            counter=interval;
            if(!iterator.haveNext())
                stop();
            next();
        }
    };

    static boolean mode,showing;
    public static void changeReplayMode(){
        if(mode){
            UIPainter.setTextWithdraw("手动");
            mode=false;
            counter=interval;
            timer.start();
        }
        else{
            UIPainter.setTextWithdraw("自动");
            mode=true;
            timer.stop();
        }
    }

    public static void changeReplayMode(boolean state){
        if(mode==state)return;
        changeReplayMode();
    }

    static void setRound(){
        round.setText(String.format("第 %d/%d 手",roundCounter, iterator.length()));
    }

    public static boolean getState(){
        return showing;
    }
}
