package com.View;

import com.Controller.Executor;
import com.Controller.ProgressWriter;
import com.Controller.Recorder;
import com.Model.Option;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Optional;

class ProgressOption extends Option{
    Text name;
    Text timeAndResult;
    int id;

    boolean selected;

    public ProgressOption(int x){
        super();
        name= new Text();
        timeAndResult=new Text();
        name.setTextOrigin(VPos.TOP);
        timeAndResult.setTextOrigin(VPos.TOP);
        id=x;
    }

    public void setPos(double x,double y,double h,double w){
        super.setPos(x,y,h,w);
        name.setX(x+10);
        name.setY(y+4);
        name.setFont(Font.font("华文宋体",h*0.3));
        timeAndResult.setX(x+10);
        timeAndResult.setY(y+0.6*h);
 //       timeAndResult.setFont(Font.font("华文宋体",h*0.2));
        name.setMouseTransparent(true);
        timeAndResult.setMouseTransparent(true);
        super.setHandler(e->{
            e.consume();
            ProgressPainter.select(id);
        });
    }

    public void setText(String nam,String res){
        name.setText(nam);
        timeAndResult.setText(res);
    }

    public void paint(Pane pane){
        super.paint(pane);
        pane.getChildren().addAll(name,timeAndResult);
    }

    public void show(){
        super.show();
        name.setFill(Color.valueOf(ProgressPainter.textColor));
        timeAndResult.setFill(Color.valueOf(ProgressPainter.textColor));
    }

    public void hide(){
        super.hide();
        selected=false;
        name.setFill(Color.TRANSPARENT);
        timeAndResult.setFill(Color.TRANSPARENT);
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

public class ProgressPainter{

    static Rectangle bg;
    static Text title;
    static final String bgColor="#000000aa",highlightColor="#999999aa",textColor="#ccccccdd",buttonColor1="#7C64E0",buttonColor2="#3979D8";
    static final int height=600,width=400,posX=120,posY=150,size=80,originY=230,length=6,buttonOrigin=720;

    static ArrayList<Option> options=new ArrayList<>();

    static int pos,sId=-1;

    static public void init(){
        options.clear();
        buttons.clear();
        pos=0;
        sId=-1;
    }

    static public void paint(Pane pane){
        bg=new Rectangle(posX,posY,width,height);
        bg.setFill(Color.TRANSPARENT);
        pane.getChildren().add(bg);
        title=new Text(posX+width*0.5-100,posY+10,"管理存档");
        title.setFont(Font.font("华文楷体",50));
        title.setFill(Color.TRANSPARENT);
        title.setTextOrigin(VPos.TOP);
        pane.getChildren().add(title);

        for (int i = 0; i < length; i++) {
            addOption(pane);
        }
        addButton(pane,300,"<",buttonColor1,e->{
            if(pos>0)
                pos--;
            sId=-1;
            show();
        });
        addButton(pane,340,">",buttonColor1,e->{
            if((pos+1)*length<Recorder.length())
                pos++;
            sId=-1;
            show();
        });
        addButton(pane,470,"返回",buttonColor2,e->{
            hide();
            if(MainMenu.isShowing())
                Executor.redrawMainMenu();
            else Menu.show();
        });
        addButton(pane,130,"载入",buttonColor1,e->{
            if(sId==-1)return;
            Recorder.loadFile(sId);
            hide();
        });
        addButton(pane,240,"删除",buttonColor1,e->{
            if(sId==-1)return;
            Recorder.deleteFile(sId);
            Recorder.save();
            if((pos+1)*length>= Recorder.length())pos--;
            if(pos<0)pos=0;
            show();
        });
        addButton(pane,180,"重命名",buttonColor1,e->{
            if(sId==-1)return;
            TextInputDialog dialog=new TextInputDialog(Recorder.getName(sId));
            dialog.setTitle("命名");
            dialog.setHeaderText("输入存档名称");
            Optional<String> res=dialog.showAndWait();
            if(res.isEmpty())
                return;
            Recorder.setName(sId,res.get());
            Recorder.save();
            show();
        });
        addButton(pane,380,"导出",buttonColor1,e->{
            if(sId==-1)return;
            Recorder.buildArchiveFile(sId);
        });
        addButton(pane,425,"导入",buttonColor1,e->{
            Recorder.readArchiveFile();
            Recorder.save();
            show();
        });
    }

    static public void addOption(Pane pane){
        int counter=options.size();
        ProgressOption op=new ProgressOption(counter);
        op.setPos(posX,originY+size*counter,size,width);
        op.hide();
        op.paint(pane);
        options.add(op);
    }

    static public void addButton(Pane pane, double x, String name,String color, EventHandler<ActionEvent> handler){
        Button button=new Button(name);
        button.setLayoutY(buttonOrigin);
        button.setLayoutX(x);
        button.setBackground(Painter.getBackground(Color.valueOf(color)));
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font(12));
        button.setVisible(false);
        button.setManaged(false);
        buttons.add(button);
        button.setOnAction(handler);
        pane.getChildren().add(button);
    }

    static public void setText(){
        ArrayList<String> name= Recorder.getNameList(), date=Recorder.getDateList();
        int cnt=Math.min(name.size()-pos*length,length),offset=pos*length;
        for (int i = offset; i < offset+cnt; i++) {
            ((ProgressOption)(options.get(i-offset))).setText(name.get(i),date.get(i));
        }
    }

    static public void show(){
        setText();
        title.setFill(Color.valueOf(textColor));
        bg.setFill(Color.valueOf(bgColor));
        int cnt=Math.min(Recorder.length()-pos*length,length);
        for (int i = 0; i < cnt; i++) {
            options.get(i).show();
        }
        for(int i=cnt;i<length;++i)
            options.get(i).hide();
        for (Button button : buttons) {
            button.setManaged(true);
            button.setVisible(true);
        }
        Background bk=Painter.getBackground(Color.gray(0.7));
        System.out.println("Pos:"+pos);
        if(pos==0)buttons.get(0).setBackground(bk);
        else buttons.get(0).setBackground(Painter.getBackground(Color.valueOf(buttonColor1)));
        if((pos+1)*length>=Recorder.length())buttons.get(1).setBackground(bk);
        else buttons.get(1).setBackground(Painter.getBackground(Color.valueOf(buttonColor1)));
    }

    static public void hide(){
        pos=0;
        sId=-1;
        bg.setFill(Color.TRANSPARENT);
        title.setFill(Color.TRANSPARENT);
        for(Option op:options)
            op.hide();
        for (Button button : buttons) {
            button.setVisible(false);
            button.setManaged(false);
        }
    }

    static void select(int x){
        for (Option option : options) {
            ((ProgressOption)option).unselect();
        }
        sId=x+pos*length;
        ((ProgressOption)(options.get(x))).select();
    }

    static ArrayList<Button> buttons=new ArrayList<>();
}
