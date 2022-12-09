package com.View;

import com.Model.Option;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

class RoomMember extends Option {
    Text name;
    int id;

    boolean selected;

    public RoomMember(int x){
        super();
        name= new Text();
        name.setTextOrigin(VPos.TOP);
        id=x;
    }

    public void setPos(double x,double y,double h,double w){
        super.setPos(x,y,h,w);
        name.setX(x+10);
        name.setY(y+4);
        name.setFont(Font.font("华文宋体",h*0.3));
        name.setMouseTransparent(true);
        super.setHandler(e->{
            e.consume();
            ProgressPainter.select(id);
        });
    }

    public void setText(String nam){
        name.setText(nam);
    }

    public void paint(Pane pane){
        super.paint(pane);
        pane.getChildren().addAll(name);
    }

    public void show(){
        super.show();
        name.setFill(Color.valueOf(ProgressPainter.textColor));
    }

    public void hide(){
        super.hide();
        selected=false;
        name.setFill(Color.TRANSPARENT);
    }

    public void select(){
        selected=true;
        super.getBg().setFill(Color.valueOf(ProgressPainter.highlightColor));
    }

    public void unselect(){
        selected=false;
        super.getBg().setFill(Color.TRANSPARENT);
    }
}

public class RoomPainter {

    static Text title;
    static Text message;
    static Text prepare;
    static ArrayList<RoomMember> members;

    static void paint(Pane pane){
        title=new Text();
    }
}
