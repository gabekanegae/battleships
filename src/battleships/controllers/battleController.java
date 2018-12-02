package battleships.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

import battleships.data.AIPlayer;
import battleships.data.Pair;
import battleships.data.Player;
import battleships.main.Main;

//Class for the battle scene
public class battleController implements Initializable {
    //Import elements from FXML
    @FXML GridPane playerGridPane, enemyGridPane;
    @FXML Pane playerJDAMPane, playerMissilePane, enemyJDAMPane, enemyMissilePane;
    @FXML ImageView playerJDAMImage, playerMissileImage, playerTurnImage,
                    enemyJDAMImage, enemyMissileImage, enemyTurnImage;
    @FXML Label playerJDAMAmount, playerMissileAmount, playerPointsAmount,
                enemyJDAMAmount, enemyMissileAmount, enemyPointsAmount;

    private Player player;
    private AIPlayer enemy;
    private boolean playerTurn = true; //Player goes first

    private void setPlayerJDAMAmount(int x) {
        this.playerJDAMAmount.setText(String.valueOf(x));
    }

    private void setPlayerMissileAmount(int x) {
        this.playerMissileAmount.setText(String.valueOf(x));
    }

    private void setPlayerPointsAmount(int x) {
        this.playerPointsAmount.setText(String.valueOf(x));
    }

    private void setEnemyJDAMAmount(int x) {
        this.enemyJDAMAmount.setText(String.valueOf(x));
    }

    private void setEnemyMissileAmount(int x) {
        this.enemyMissileAmount.setText(String.valueOf(x));
    }

    private void setEnemyPointsAmount(int x) {
        this.enemyPointsAmount.setText(String.valueOf(x));
    }

    //Receives data from setup scene
    public void loadFromSetup(Player player) {
        this.player = player;

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                //Removes all click/hover action from player board
                player.getBoard().getImageViewBoard(i, j).setOnMouseEntered(e -> {});
                player.getBoard().getImageViewBoard(i, j).setOnMouseExited(e -> {});
                player.getBoard().getImageViewBoard(i, j).setOnMouseClicked(e -> {});

                //Apply player board ImageView matrix to the GridPane
                GridPane.setConstraints(player.getBoard().getImageViewBoard(i, j), i, j);
                playerGridPane.getChildren().add(player.getBoard().getImageViewBoard(i, j));
            }
        }

        updatePlayerUI();
    }

    public void initialize(URL location, ResourceBundle resources) {
        //Set bomb icons
        playerJDAMImage.setImage(Main.JDAMImage);
        playerMissileImage.setImage(Main.missileImage);
        enemyJDAMImage.setImage(Main.JDAMImage);
        enemyMissileImage.setImage(Main.missileImage);

        //Set turn arrows
        playerTurnImage.setImage(Main.turnImageOn);
        enemyTurnImage.setImage(Main.turnImageOff);

        enemyTurnImage.setScaleX(-1); //Mirror enemy turn arrow

        //Select JDAM by default
        playerJDAMPane.setStyle(Main.HIGHLIGHT);
        enemyJDAMPane.setStyle(Main.HIGHLIGHT);

        //Define click on player JDAM icon
        playerJDAMImage.setOnMouseClicked(e -> {
            if (player.getJDAMAmount() > 0) {
                playerMissilePane.setStyle("");
                playerJDAMPane.setStyle(Main.HIGHLIGHT);
                player.setBombSelected(1);
            }
        });

        //Define click on player missile icon
        playerMissileImage.setOnMouseClicked(e -> {
            if (player.getMissileAmount() > 0) {
                playerJDAMPane.setStyle("");
                playerMissilePane.setStyle(Main.HIGHLIGHT);
                player.setBombSelected(2);
            }
        });

        enemy = new AIPlayer(); //Create enemy AI object
        enemy.getBoard().setHidden(true); //Hide all non-destroyed cells
        enemy.getBoard().generateRandomBoard(); //Generate enemy board

        //Define click and hover on all grid cells
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                //Mouse enter
                enemy.getBoard().getImageViewBoard(i, j).setOnMouseEntered(e -> {
                    int col = GridPane.getColumnIndex((Node) e.getSource());
                    int row = GridPane.getRowIndex((Node) e.getSource());

                    if (!enemy.getBoard().isDestroyed(col, row) && playerTurn) { //If water and player turn
                        enemy.getBoard().setImageViewBoard(Main.aimImage, col, row); //Show aim sprite
                    }
                });

                //Mouse exit
                enemy.getBoard().getImageViewBoard(i, j).setOnMouseExited(e -> {
                    int col = GridPane.getColumnIndex((Node) e.getSource());
                    int row = GridPane.getRowIndex((Node) e.getSource());

                    enemy.getBoard().updateImageViewBoard(col, row);
                });

                //Mouse click
                enemy.getBoard().getImageViewBoard(i, j).setOnMouseClicked(e -> {
                    int col = GridPane.getColumnIndex((Node) e.getSource());
                    int row = GridPane.getRowIndex((Node) e.getSource());

                    if (playerTurn && !enemy.getBoard().isDestroyed(col, row)) runPlayerTurn(col, row);
                });

                //Apply enemy board ImageView matrix to the GridPane
                GridPane.setConstraints(enemy.getBoard().getImageViewBoard(i, j), i, j);
                enemyGridPane.getChildren().add(enemy.getBoard().getImageViewBoard(i, j));
            }
        }

        updateEnemyUI();
    }

    private void runPlayerTurn(int col, int row) {
        boolean hitShot = false; //Flag to check if landed a shot (so it's their turn again)
        
        //Consume selected bomb
        player.consumeBomb();
        enemy.getBoard().setDestroyed(true, col, row); //Hit chosen cell
        
        if (enemy.getBoard().getType(col, row) != 0) { //If hit a ship
            hitShot = true;
            player.addPoints(10);

            if (player.getBombSelected() == 2) { //Missile
                //Go to the ship's first part cell
                Pair<Integer, Integer> cur = enemy.getBoard().getFirstPartOfShip(col, row);

                //Destroy all ship cells
                for (int k = 0; k < enemy.getBoard().getType(col, row); k++) {
                    if (!enemy.getBoard().isDestroyed(cur.getX(), cur.getY())) {
                        enemy.getBoard().setDestroyed(true, cur.getX(), cur.getY());
                        player.addPoints(10);
                    }

                    if (enemy.getBoard().isRotated(col, row)) { // ^
                        cur.setY(cur.getY()+1);
                    } else { // <
                        cur.setX(cur.getX()+1);
                    }
                }
            }

            //Give bonus points for destroying the whole ship
            if (enemy.getBoard().checkWholeShipDestroyed(col, row)) {
                int shipID = enemy.getBoard().getType(col, row);

                if (shipID == 1) {
                    player.addPoints(0);
                } else if (shipID == 2) {
                    player.addPoints(20);
                } else if (shipID == 3) {
                    player.addPoints(40);
                } else if (shipID == 4) {
                    player.addPoints(60);
                    enemy.subMissileAmount(1);
                } else if (shipID == 5) {
                    player.addPoints(125);
                    enemy.subJDAMAmount(10);
                }
            }
        }

        playerTurn = hitShot; //If hit, it's the player's turn again

        updateEnemyUI();
        updatePlayerUI();
        checkEndCondition(); //Check if game should end

        //If player missed, run enemy turn after a delay
        if (!hitShot) Main.delay(this::runEnemyTurn);
    }

    private void runEnemyTurn() {
        boolean hitShot = false;

        //Chooses a location to hit
        Pair<Integer, Integer> locationChosen = enemy.locationDecision(player.getBoard());

        //Chooses and sets a bomb to use
        int bombChosen = enemy.bombDecision();
        enemy.setBombSelected(bombChosen);

        if (enemy.getBombSelected() == 1) {
            enemyMissilePane.setStyle("");
            enemyJDAMPane.setStyle(Main.HIGHLIGHT);
        } else if (enemy.getBombSelected() == 2) {
            enemyJDAMPane.setStyle("");
            enemyMissilePane.setStyle(Main.HIGHLIGHT);
        }

        enemy.consumeBomb();

        player.getBoard().setDestroyed(true, locationChosen.getX(), locationChosen.getY()); //Hit chosen cell

        if (player.getBoard().getType(locationChosen.getX(), locationChosen.getY()) != 0) { //If hit a ship
            enemy.pushHitShot(locationChosen.getX(), locationChosen.getY()); //Push hit to AI stack
            hitShot = true;
            enemy.addPoints(10);

            if (enemy.getBombSelected() == 2) { //Missile
                //Go to the ship's first part cell
                Pair<Integer, Integer> cur = player.getBoard().getFirstPartOfShip(locationChosen.getX(), locationChosen.getY());

                //Destroy all ship cells
                for (int k = 0; k < player.getBoard().getType(locationChosen.getX(), locationChosen.getY()); k++) {
                    if (!player.getBoard().isDestroyed(cur.getX(), cur.getY())) {
                        player.getBoard().setDestroyed(true, cur.getX(), cur.getY());
                        enemy.addPoints(10);
                    }

                    if (player.getBoard().isRotated(locationChosen.getX(), locationChosen.getY())) { // ^
                        cur.setY(cur.getY()+1);
                    } else { // <
                        cur.setX(cur.getX()+1);
                    }
                }
            }

            //Give bonus points for destroying the whole ship
            if (player.getBoard().checkWholeShipDestroyed(locationChosen.getX(), locationChosen.getY())) {
                int shipType = player.getBoard().getType(locationChosen.getX(), locationChosen.getY());
                int shipID = player.getBoard().getID(locationChosen.getX(), locationChosen.getY());

                enemy.emptyHitShots(player.getBoard(), shipID);
                
                if (shipType == 1) {
                    enemy.addPoints(0);
                } else if (shipType == 2) {
                    enemy.addPoints(20);
                } else if (shipType == 3) {
                    enemy.addPoints(40);
                } else if (shipType == 4) {
                    enemy.addPoints(60);
                    player.subMissileAmount(1);
                } else if (shipType == 5) {
                    enemy.addPoints(125);
                    player.subJDAMAmount(10);
                }
            }
        }

        playerTurn = !hitShot; //If enemy missed, it's the player's turn

        updateEnemyUI();
        updatePlayerUI();
        checkEndCondition(); //Check if game should end

        //If enemy hit, shoot again after a delay (else, player is able to shoot)
        if (hitShot) Main.delay(this::runEnemyTurn);
    }

    //Updates player UI according to its object data
    private void updatePlayerUI() {
        playerTurnImage.setImage(playerTurn ? Main.turnImageOn : Main.turnImageOff); //Turn arrow

        player.getBoard().generateImageViewBoard(); //Sprite grid

        //Bombs and points
        setPlayerJDAMAmount(player.getJDAMAmount());
        setPlayerMissileAmount(player.getMissileAmount());
        setPlayerPointsAmount(player.getPoints());

        //If no more JDAM left, select Missiles
        if (player.getJDAMAmount() <= 0) {
            playerJDAMPane.setStyle("");
            if (player.getMissileAmount() > 0) {
                playerMissilePane.setStyle(Main.HIGHLIGHT);
                player.setBombSelected(2);
            }
        }

        //If no more Missiles left, select JDAM
        if (player.getMissileAmount() <= 0) {
            playerMissilePane.setStyle("");
            if (player.getJDAMAmount() > 0) {
                playerJDAMPane.setStyle(Main.HIGHLIGHT);
                player.setBombSelected(1);
            }
        }
    }

    //Updates enemy UI according to its object data
    private void updateEnemyUI() {
        enemyTurnImage.setImage(!playerTurn ? Main.turnImageOn : Main.turnImageOff); //Turn arrow

        enemy.getBoard().generateImageViewBoard(); //Sprite grid

        //Bombs and points
        setEnemyJDAMAmount(enemy.getJDAMAmount());
        setEnemyMissileAmount(enemy.getMissileAmount());
        setEnemyPointsAmount(enemy.getPoints());
    }

    //Checks if game should end
    private void checkEndCondition() {
        //Ends the game if someone has 1000 points or is without bombs
        if (player.getJDAMAmount() + player.getMissileAmount() == 0 ||
            enemy.getJDAMAmount() + enemy.getMissileAmount() == 0 ||
            player.getPoints() == 1000 || enemy.getPoints() == 1000) {
            //Load end scene
            FXMLLoader loader = Main.getLoader("end", getClass());
            Scene scene = Main.loadScene(loader, "end", getClass());

            //Send data to end scene
            endController end = loader.getController();
            end.loadFromBattle(this.player, this.enemy);

            //Set end scene
            Main.setScene(scene);
        }
    }
}