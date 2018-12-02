package battleships.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import battleships.data.Player;
import battleships.main.Main;

//Class for the end scene
public class endController implements Initializable {
    //Import elements from FXML
    @FXML Label titleLabel, finalScoreAmountLabel;

    //Receives data from battle scene
    public void loadFromBattle(Player player, Player enemy) {
        boolean playerWin; //Flag to check win state

        if (player.getPoints() > enemy.getPoints()) { //Player has more points
            playerWin = true;
        } else if (player.getPoints() < enemy.getPoints()) { //Player has less points
            playerWin = false;
        } else { //Draw, check bomb amounts
            int playerBombs = player.getMissileAmount() + player.getJDAMAmount();
            int enemyBombs = enemy.getMissileAmount() + enemy.getJDAMAmount();

            playerWin = (playerBombs >= enemyBombs); //Player wins if equal or more total bombs
        }

        titleLabel.setText(playerWin ? "You Win!" : "You Lose!"); //Show scene title
        finalScoreAmountLabel.setText(player.getPoints() + " x " + enemy.getPoints()); //Show final score
    }

    @FXML //Menu button
    private void menu() {
        //Load and set menu scene
        FXMLLoader loader = Main.getLoader("menu", getClass());
        Scene scene = Main.loadScene(loader, "menu", getClass());
        Main.setScene(scene);
    }

    public void initialize(URL location, ResourceBundle resources) { }
}