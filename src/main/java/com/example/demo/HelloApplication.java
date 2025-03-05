package com.example.demo;

import com.almasb.fxgl.dev.Console;
import domain.*;
import repository.*;
import settings.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.RentalService;
import service.CarService;
import ui.ConsoleUI;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Interfata Grafica");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) throws RepositoryException {
        ConsoleUI consola = new ConsoleUI();
//        consola.run();
        launch();
    }
}