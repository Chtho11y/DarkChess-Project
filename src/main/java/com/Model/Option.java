package com.Model;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Option {

    Rectangle bg;
    Line div1, div2;
    boolean activate;

    public Option() {
        bg = new Rectangle();
        div1 = new Line();
        div2 = new Line();
        activate = false;
    }

    public void setPos(double x, double y, double h, double w) {
        bg.setWidth(w);
        bg.setHeight(h);
        bg.setX(x);
        bg.setY(y);
        div1.setStartX(x + 5);
        div1.setStartY(y);
        div1.setEndX(x + w - 5);
        div1.setEndY(y);
        div2.setStartX(x + 5);
        div2.setStartY(y + h);
        div2.setEndX(x + w - 5);
        div2.setEndY(y + h);
        div1.setStroke(Color.gray(0.3));
        div2.setStroke(Color.gray(0.3));
        div1.setMouseTransparent(true);
        div2.setMouseTransparent(true);
    }

     public void setHandler(EventHandler<? super MouseEvent> handler) {
        bg.setOnMouseClicked(e -> {
            if (!activate) return;
            handler.handle(e);
        });
    }

    public void paint(Pane pane) {
        pane.getChildren().addAll(div1, div2, bg);
    }

    public void show() {
        activate = true;
        bg.setFill(Color.TRANSPARENT);
        div1.setVisible(true);
        div2.setVisible(true);
        bg.setVisible(true);
        bg.setMouseTransparent(false);
    }

    public void hide() {
        activate = false;
        bg.setVisible(false);
        div1.setVisible(false);
        div2.setVisible(false);
        bg.setMouseTransparent(true);
    }

    public Rectangle getBg(){
        return bg;
    }

    public boolean activated(){
        return activate;
    }

    public void setActivate(boolean b){
        activate=b;
    }
}
