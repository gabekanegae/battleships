package battleships.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;

import battleships.main.Main;

//Class for the instructions scene
public class instructionsController implements Initializable {
    @FXML //Back button
    private void back() {
        //Load and set menu scene
        FXMLLoader loader = Main.getLoader("menu", getClass());
        Scene scene = Main.loadScene(loader, "menu", getClass());
        Main.setScene(scene);
    }

    public void initialize(URL location, ResourceBundle resources) { }
}