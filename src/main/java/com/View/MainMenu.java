package com.View;

import com.AIDemo.MinMax;
import com.Controller.Connector;
import com.Controller.Executor;
import com.Controller.Recorder;
import com.Model.InputMode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Random;

import static com.View.Painter.getBackground;


public class MainMenu {

    static boolean show;

    public static boolean isShowing(){
        return show;
    }

    public static void paint(Stage stage) throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        show=true;

        Group group=new Group();
        Group group1=new Group();
        Pane pane=new Pane();
        Scene scene=new Scene(group);
        Scene scene1=new Scene(group1);
        Scene scene2=new Scene(pane,640,860);
        ProgressPainter.init();

        ImageView imageView=new ImageView(new Image(MainMenu.class.getResourceAsStream("221.jpg"),640,860,true,true));
        ImageView imageView1=new ImageView(new Image(MainMenu.class.getResourceAsStream("221.jpg"),640,860,true,true));
        ImageView imageView2=new ImageView(new Image(MainMenu.class.getResourceAsStream("221.jpg"),640,860,true,true));
        group.getChildren().add(imageView);
        group1.getChildren().add(imageView1);
        pane.getChildren().add(imageView2);

        BufferedInputStream bif=new BufferedInputStream(MainMenu.class.getResourceAsStream("666.wav"));
        AudioInputStream ais= null;
        ais = AudioSystem.getAudioInputStream(bif);
        AudioFormat af=ais.getFormat();
        int len =(int) ais.getFrameLength();
        byte[]data=new byte[len];
        ais.read(data);
        ais.close();

        Clip clip=AudioSystem.getClip();
        clip.open(af,data,0,len);
        clip.start();

        int x=135;
        int y=100;
        Text text=new Text(x,y,"Dark Chess");
        Text text1=new Text(120,150,"Made By: ZhouYiXian & ShenWenKun");
        text.setFill(Color.rgb(77,77,77));
        text1.setFill(Color.rgb(77,77,77));
        group.getChildren().add(text1);
        group.getChildren().add(text);
        text.setFont(Font.font("华文楷体",60));
        text1.setFont(Font.font("华文楷体",20));

        Button button0=new Button("关闭音乐");
        button0.setLayoutX(0);
        button0.setLayoutY(10);
        button0.setLayoutY(1.5);
        group.getChildren().add(button0);
        button0.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button0.setOnMouseEntered(e->{
            button0.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button0.setOnMousePressed(e->{
            button0.setBackground(getBackground(Color.rgb(148,148,148,1)));
            clip.stop();
        });
        button0.setOnMouseReleased(e->{
            button0.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button0.setOnMouseExited(e->{
            button0.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Button button00=new Button("播放音乐");
        button00.setLayoutX(491);
        button00.setLayoutY(10);
        button00.setLayoutY(1.5);
        group.getChildren().add(button00);
        button00.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button00.setOnMouseEntered(e->{
            button00.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button00.setOnMousePressed(e->{
            button00.setBackground(getBackground(Color.rgb(148,148,148,1)));
            clip.start();
        });
        button00.setOnMouseReleased(e->{
            button00.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button00.setOnMouseExited(e->{
            button00.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Button button=new Button("开始游戏");
        button.setLayoutX(245);
        button.setLayoutY(205);
        button.setScaleX(4.5);
        button.setScaleY(4);
        group.getChildren().add(button);
        button.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button.setOnMouseEntered(e->{
            button.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button.setOnMousePressed(e->{
            button.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button.setOnMouseReleased(e->{
            button.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
            stage.setScene(scene1);
        });
        button.setOnMouseExited(e->{
            button.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Button button1=new Button("继续游戏");
        button1.setLayoutX(245);
        button1.setLayoutY(325);
        button1.setScaleX(4.5);
        button1.setScaleY(4);
        group.getChildren().add(button1);
        button1.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button1.setOnMouseEntered(e->{
            button1.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button1.setOnMousePressed(e->{
            button1.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button1.setOnMouseReleased(e->{
            button1.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
            int id=Recorder.getLastArchive();
            if(id==-1)return;
            Recorder.loadFile(id);
            show=false;
        });
        button1.setOnMouseExited(e->{
            button1.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Button button2=new Button("游戏存档");
        button2.setLayoutX(245);
        button2.setLayoutY(445);
        button2.setScaleX(4.5);
        button2.setScaleY(4);
        group.getChildren().add(button2);
        ProgressPainter.paint(pane);
        button2.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button2.setOnMouseEntered(e->{
            button2.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button2.setOnMousePressed(e->{
            button2.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button2.setOnMouseReleased(e->{
            button2.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
            stage.setScene(scene2);
            ProgressPainter.show();
        });
        button2.setOnMouseExited(e->{
            button2.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Text text2=new Text(155,100,"游戏模式");
        group1.getChildren().add(text2);
        text2.setFill(Color.rgb(77,77,77));
        text2.setFont(Font.font("华文楷体",60));


        Button button3=new Button("人机对战");
        button3.setLayoutX(245);
        button3.setLayoutY(445);
        button3.setScaleX(4.5);
        button3.setScaleY(4);
        group1.getChildren().add(button3);
        button3.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button3.setOnMouseEntered(e->{
            button3.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button3.setOnMousePressed(e->{
            button3.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button3.setOnMouseReleased(e->{
            button3.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
            Executor.gameStart(true,0, InputMode.AUTO);
            Random rd=new Random(System.currentTimeMillis());
            int mode=rd.nextInt(2);
            if(mode==1){
                Connector.setMode(InputMode.NORMAL, InputMode.AUTO);
            }else{
                Connector.setMode(InputMode.AUTO, InputMode.NORMAL);
                MinMax.operate();
            }
            show=false;
        });
        button3.setOnMouseExited(e->{
            button3.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Button button4=new Button("加入房间");
        button4.setLayoutX(245);
        button4.setLayoutY(205);
        button4.setScaleX(4.5);
        button4.setScaleY(4);
        group1.getChildren().add(button4);
        button4.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button4.setOnMouseEntered(e->{
            button4.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button4.setOnMousePressed(e->{
            button4.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button4.setOnMouseReleased(e->{
            button4.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button4.setOnMouseExited(e->{
            button4.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });

        Button button41=new Button("创建房间");
        button41.setLayoutX(245);
        button41.setLayoutY(325);
        button41.setScaleX(4.5);
        button41.setScaleY(4);
        group1.getChildren().add(button41);
        button41.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button41.setOnMouseEntered(e->{
            button41.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button41.setOnMousePressed(e->{
            button41.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button41.setOnMouseReleased(e->{
            button41.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button41.setOnMouseExited(e->{
            button41.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });


        Button button5=new Button(" 返回");
        button5.setLayoutX(0);
        button5.setLayoutY(820);
        button5.setScaleX(2);
        button5.setScaleY(2);
        group1.getChildren().add(button5);
        button5.setBackground(getBackground(Color.rgb(148,148,148,0)));
        button5.setOnMouseEntered(e->{
            button5.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
        });
        button5.setOnMousePressed(e->{
            button5.setBackground(getBackground(Color.rgb(148,148,148,1)));
        });
        button5.setOnMouseReleased(e->{
            button5.setBackground(getBackground(Color.rgb(148,148,148,0.5)));
            stage.setScene(scene);
        });
        button5.setOnMouseExited(e->{
            button5.setBackground(getBackground(Color.rgb(148,148,148,0)));
        });


        stage.setScene(scene);
        stage.show();
    }

}
