package com.View;

import com.Controller.ChessExecutor;
import com.Controller.Executor;
import com.Controller.Recorder;
import com.Model.Option;
import com.Model.PieceColor;
import com.Model.RoundStatus;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Optional;

class MenuOption extends Option {

    Text text;

    public MenuOption(){
        super();
        text=new Text();
    }

    public void setPos(double x,double y,double h,double w){
        super.setPos(x,y,h,w);
        text.setFont(Font.font("华文楷体",h*0.5));
        text.setFill(Color.WHITE);
        text.setTextOrigin(VPos.CENTER);
        text.setY(y+h*0.5);
        text.setMouseTransparent(true);
        super.getBg().setOnMouseEntered(e->{
            if(!activated())return;
            super.getBg().setFill(Color.valueOf(Menu.highlightColor));
        });
        super.getBg().setOnMouseExited(e->{
            if(!activated())return;
            super.getBg().setFill(Color.TRANSPARENT);
        });
    }

    public void setText(String s){
        text.setX(super.getBg().getX()+super.getBg().getWidth()*0.5-text.getFont().getSize()*0.5*s.length());
        text.setText(s);
    }

    public void paint(Pane pane){
        super.paint(pane);
        pane.getChildren().addAll(text);
    }

    public void show() {
        super.show();
        text.setFill(Color.valueOf(Menu.textColor));
    }

    public void hide(){
        super.hide();
        text.setFill(Color.TRANSPARENT);
    }
}

public class Menu{

    static Rectangle bg;
    static Text title;
    static final String bgColor="#000000aa",highlightColor="#999999aa",textColor="#ccccccdd";
    static final int height=600,width=400,posX=120,posY=150,size=80,originY=250;

    static ArrayList<Option> options=new ArrayList<>();

    static public void init(){
        options.clear();
    }

    static public void paint(Pane pane){
        bg=new Rectangle(posX,posY,width,height);
        bg.setFill(Color.TRANSPARENT);
        bg.setMouseTransparent(true);
        pane.getChildren().add(bg);
        title=new Text(posX+width*0.5-50,posY+10,"菜单");
        title.setFont(Font.font("华文楷体",50));
        title.setFill(Color.TRANSPARENT);
        title.setTextOrigin(VPos.TOP);
        title.setMouseTransparent(true);
        pane.getChildren().add(title);

        addOption(pane,"返回游戏",e->{
            Executor.setPause(false);
            e.consume();
            hide();
        });

        addOption(pane,"重新开始",e->{
            e.consume();
            Executor.gameStart(true,0);
            hide();
        });

        addOption(pane,"保存对局",e->{
            e.consume();
            Recorder.buildProgress();
            Recorder.save();
            hide();
            Executor.setPause(false);
        });

        addOption(pane,"管理存档",e->{
            e.consume();
            hide();
            ProgressPainter.show();
        });

        addOption(pane,"主菜单",e->{
            e.consume();
        });

        addOption(pane,"退出游戏",e->{
            e.consume();
            if(ChessExecutor.getRoundStatus()== PieceColor.UNKNOWN) {
                Executor.exit();
                return;
            }

            Alert alert=new Alert(Alert.AlertType.NONE,"是否保存？",new ButtonType("保存", ButtonBar.ButtonData.YES),
                    new ButtonType("不保存", ButtonBar.ButtonData.NO),new ButtonType("取消", ButtonBar.ButtonData.OTHER));
            alert.setTitle("警告");
            alert.initOwner(Executor.getStage());
            Optional<ButtonType> result=alert.showAndWait();
            if(result.isEmpty())
                return;
            if(result.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
                Recorder.buildProgress();
                Recorder.save();
                Executor.exit();
            }else if(result.get().getButtonData().equals(ButtonBar.ButtonData.NO)){
                Executor.exit();
            }
        });
    }

    static public void addOption(Pane pane,String name,EventHandler<? super MouseEvent> handler){
        int counter=options.size();
        MenuOption op=new MenuOption();
        op.setPos(posX,originY+size*counter,size,width);
        op.setHandler(handler);
        op.setText(name);
        op.hide();
        op.paint(pane);
        options.add(op);
    }

    static public void show(){
        title.setFill(Color.valueOf(textColor));
        bg.setFill(Color.valueOf(bgColor));
        for(Option op:options)
            op.show();
    }

    static public void hide(){
        bg.setFill(Color.TRANSPARENT);
        title.setFill(Color.TRANSPARENT);
        for(Option op:options)
            op.hide();
    }
}
