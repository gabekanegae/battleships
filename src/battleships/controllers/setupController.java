package battleships.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

import battleships.data.Player;
import battleships.main.Main;

//Class for the setup scene
public class setupController implements Initializable {
    //Import elements from FXML
    @FXML GridPane playerGridPane;
    @FXML ImageView carrier0, carrier1, tanker0, tanker1,
                    torpedo0, torpedo1, torpedo2, torpedo3,
                    submarine0, submarine1, submarine2, submarine3, buoy0;
    @FXML Pane carrier0Pane, carrier1Pane, tanker0Pane, tanker1Pane,
               torpedo0Pane, torpedo1Pane, torpedo2Pane, torpedo3Pane,
               submarine0Pane, submarine1Pane, submarine2Pane, submarine3Pane, buoy0Pane;
    @FXML Button rotateButton, randomButton, resetButton, battleButton;

    //Transparent ship sprites
    private static final Image carrierShipImage = new Image("/images/icons/carrier.png");
    private static final Image tankerShipImage = new Image("/images/icons/tanker.png");
    private static final Image torpedoShipImage = new Image("/images/icons/torpedo.png");
    private static final Image submarineShipImage = new Image("/images/icons/submarine.png");
    private static final Image buoyShipImage = new Image("/images/icons/buoy.png");

    private int shipSelected = -1; //Current selected ship part
    private int shipsPlaced = 0; //Placed ships counter
    private boolean rotated = false; //State of the Rotate button

    private Player player = new Player(); //Create new player

    public void initialize(URL location, ResourceBundle resources) {
        battleButton.setDisable(true); //Battle! button starts disabled

        final ImageView[] shipIcons = {carrier0, carrier1, tanker0, tanker1,
                                       torpedo0, torpedo1, torpedo2, torpedo3,
                                       submarine0, submarine1, submarine2, submarine3, buoy0};

        final Pane[] shipPanes = {carrier0Pane, carrier1Pane, tanker0Pane, tanker1Pane,
                                  torpedo0Pane, torpedo1Pane, torpedo2Pane, torpedo3Pane,
                                  submarine0Pane, submarine1Pane, submarine2Pane, submarine3Pane, buoy0Pane};

        //Load all images into their ImageViews
        for (int i = 0; i < 2; i++) shipIcons[i].setImage(carrierShipImage);
        for (int i = 2; i < 4; i++) shipIcons[i].setImage(tankerShipImage);
        for (int i = 4; i < 8; i++) shipIcons[i].setImage(torpedoShipImage);
        for (int i = 8; i < 12; i++) shipIcons[i].setImage(submarineShipImage);
        shipIcons[12].setImage(buoyShipImage);

        //Define click on all ships
        for (int i = 0; i < 13; i++) {
            final int f = i;
            shipIcons[i].setOnMouseClicked(e -> {
                clearAll();
                shipPanes[f].setStyle(Main.HIGHLIGHT);
                shipSelected = f;
            });
        }

        //Define click on Rotate button
        rotateButton.setOnAction(e -> {
            rotated = !rotated; //Toggle rotation
            rotateButton.setText(rotated ? "\uD83E\uDC51" : "\uD83E\uDC50"); //Set arrow as button text (^ : <)
        });

        //Define click on Random button
        randomButton.setOnAction(e -> {
            player.getBoard().generateRandomBoard();

            //Hide all ship icons
            for (int i = 0; i < 13; i++) shipIcons[i].setVisible(false);

            //Set as all ships placed and enable Battle! button
            shipsPlaced = 13;
            battleButton.setDisable(false);
        });

        //Define click on Reset button
        resetButton.setOnAction(e -> {
           player.getBoard().resetBoard();

           //Show all ship icons
           for (int i = 0; i < 13; i++) shipIcons[i].setVisible(true);

           clearAll();

           //Set as no ships placed and disable Battle! button
           shipsPlaced = 0;
           battleButton.setDisable(true);
        });

        //Define click and hover on all grid cells
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                //Mouse enter
                player.getBoard().getImageViewBoard(i, j).setOnMouseEntered(e -> {
                    int col = GridPane.getColumnIndex((Node) e.getSource());
                    int row = GridPane.getRowIndex((Node) e.getSource());

                    if (player.getBoard().getType(col, row) == 0) { //If water
                        player.getBoard().setImageViewBoard(Main.aimImage, col, row); //Show aim sprite
                    }
                });

                //Mouse exit
                player.getBoard().getImageViewBoard(i, j).setOnMouseExited(e -> {
                    int col = GridPane.getColumnIndex((Node) e.getSource());
                    int row = GridPane.getRowIndex((Node) e.getSource());

                    player.getBoard().updateImageViewBoard(col, row);
                });

                //Mouse click
                player.getBoard().getImageViewBoard(i, j).setOnMouseClicked(e -> {
                    int col = GridPane.getColumnIndex((Node) e.getSource());
                    int row = GridPane.getRowIndex((Node) e.getSource());

                    int ID = 0; //Get ship type
                    if (shipSelected == -1) { //None
                        ID = 0;
                    } else if (shipSelected < 2) { //0, 1
                        ID = 5;
                    } else if (shipSelected < 4) { //2, 3
                        ID = 4;
                    } else if (shipSelected < 8) { //4, 5, 6, 7
                        ID = 3;
                    } else if (shipSelected < 12) { //8, 9, 10, 11
                        ID = 2;
                    } else if (shipSelected == 12) { //12
                        ID = 1;
                    }

                    boolean placed = (ID != 0); //Flag to check if placed successfully

                    //Check if placement is valid
                    if (placed) placed = player.getBoard().checkValidPlacement(ID, col, row, rotated);

                    //Place ship and update board
                    if (placed) {
                        player.getBoard().placeShip(ID, col, row, rotated);
                        player.getBoard().generateImageViewBoard();

                        //Hide appropriate ship icon
                        shipIcons[shipSelected].setVisible(false);

                        clearAll();

                        //If all ships are placed, enable Battle! button
                        shipsPlaced++;
                        if (shipsPlaced == 13) battleButton.setDisable(false);
                    } else { //If not placed successfully
                        //Restore aim sprite
                        if (player.getBoard().getType(col, row) == 0) {
                            player.getBoard().setImageViewBoard(Main.aimImage, col, row);
                        }
                    }
                });

                //Apply player board ImageView matrix to the GridPane
                GridPane.setConstraints(player.getBoard().getImageViewBoard(i, j), i, j);
                playerGridPane.getChildren().add(player.getBoard().getImageViewBoard(i, j));
            }
        }
    }

    //Reset ship selection
    private void clearAll() {
        shipSelected = -1;
        carrier0Pane.setStyle("");
        carrier1Pane.setStyle("");
        tanker0Pane.setStyle("");
        tanker1Pane.setStyle("");
        torpedo0Pane.setStyle("");
        torpedo1Pane.setStyle("");
        torpedo2Pane.setStyle("");
        torpedo3Pane.setStyle("");
        submarine0Pane.setStyle("");
        submarine1Pane.setStyle("");
        submarine2Pane.setStyle("");
        submarine3Pane.setStyle("");
        buoy0Pane.setStyle("");
    }

    @FXML //Battle! button
    private void battle() {
        //Load battle scene
        FXMLLoader loader = Main.getLoader("battle", getClass());
        Scene scene = Main.loadScene(loader, "battle", getClass());

        //Sends data to battle scene
        battleController battle = loader.getController();
        battle.loadFromSetup(this.player);

        //Set battle scene
        Main.setScene(scene);
    }
}