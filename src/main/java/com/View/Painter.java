package com.View;

import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Painter {
    public static Font font(double size){
        return Font.font("华文行楷", FontWeight.NORMAL, FontPosture.REGULAR,size);
    }

    public static Text text(double size, String t,double posX,double posY,Color color){
        Text text=new Text(posX,posY,t);
        text.setTextOrigin(VPos.TOP);
        text.setFont(font(size));
        text.setFill(color);
        return text;
    }

    public static DropShadow getStandardShadow(){
        DropShadow ds=new DropShadow();
        ds.setOffsetX(6);
        ds.setOffsetY(6);
        ds.setRadius(5);
        ds.setColor(Color.gray(0.3));
        return ds;
    }

    public static DropShadow getNoShadow(){
        DropShadow ds=new DropShadow();
        ds.setOffsetX(6);
        ds.setOffsetY(6);
        ds.setRadius(5);
        ds.setColor(Color.TRANSPARENT);
        return ds;
    }

    public static Background getBackground(Color color){
        return new Background( new BackgroundFill(color,new CornerRadii(5), Insets.EMPTY));
    }
}
