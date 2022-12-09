package com.example.demo;

import com.Controller.Executor;
import com.Controller.Recorder;
import com.View.MainMenu;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Executor.setStage(stage);
      Executor.run(this.getClass().getResourceAsStream("billy.jpeg"));
    }

    public static void main(String[] args) {
        launch();
    }
}