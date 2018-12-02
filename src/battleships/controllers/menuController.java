package battleships.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;

import battleships.main.Main;

//Class for the menu scene
public class menuController implements Initializable {
    @FXML //Play button
    private void play() {
        //Load and set setup scene
        FXMLLoader loader = Main.getLoader("setup", getClass());
        Scene scene = Main.loadScene(loader, "setup", getClass());
        Main.setScene(scene);
    }

    @FXML //Instructions button
    private void instructions() {
        //Load and set instructions scene
        FXMLLoader loader = Main.getLoader("instructions", getClass());
        Scene scene = Main.loadScene(loader, "instructions", getClass());
        Main.setScene(scene);
    }

    @FXML //Credits button
    private void credits() {
        //Load and set credits scene
        FXMLLoader loader = Main.getLoader("credits", getClass());
        Scene scene = Main.loadScene(loader, "credits", getClass());
        Main.setScene(scene);
    }

    @FXML //Quit button
    private void quit() {
        Main.mainStage.close();
    }

    public void initialize(URL location, ResourceBundle resources) { }
}