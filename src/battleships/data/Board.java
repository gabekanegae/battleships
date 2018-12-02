package battleships.data;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

import battleships.main.Main;

//Class to manage board state and sprites
public class Board {
    private boolean hidden; //Should non-destroyed cells be hidden? (Player board: no, Enemy board: yes)
    private ImageView[][] imageViewBoard; //ImageView matrix ready to be displayed

    //All matrices for board state and sprite handling
    private int[][] ID, part, type;
    private boolean[][] destroyed, rotated;

    private int countID; //Current ship ID to be placed
    
    private Pair<Integer, Integer> mostRecent; //Last shot to hit

    public Board() {
        this.hidden = false;
        this.destroyed = new boolean[15][15];
        this.type = new int[15][15];
        this.part = new int[15][15];
        this.rotated = new boolean[15][15];
        this.ID = new int[15][15];
        this.countID = 0;
        
        //All ImageView objects must be initialized
        this.imageViewBoard = new ImageView[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                this.imageViewBoard[i][j] = new ImageView();
            }
        }

        generateImageViewBoard();
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isDestroyed(int i, int j) {
        return destroyed[i][j];
    }

    public int getType(int i, int j) {
        return type[i][j];
    }

    public int getID(int i, int j) {
        return ID[i][j];
    }
    
    public int getPart(int i, int j) {
        return part[i][j];
    }

    public boolean isRotated(int i, int j) {
        return rotated[i][j];
    }

    public ImageView getImageViewBoard(int i, int j) {
        return imageViewBoard[i][j];
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setDestroyed(boolean destroyed, int i, int j) {
        this.destroyed[i][j] = destroyed;
        mostRecent = new Pair<>(i, j);
    }

    public void setType(int type, int i, int j) {
        this.type[i][j] = type;
    }
    
    public void setID(int ID, int i, int j) {
        this.ID[i][j] = ID;
    }

    public void setPart(int part, int i, int j) {
        this.part[i][j] = part;
    }

    public void setRotated(boolean rotated, int i, int j) {
        this.rotated[i][j] = rotated;
    }

    public void setImageViewBoard(Image img, int i, int j) {
        this.imageViewBoard[i][j].setImage(img);
    }

    //Clears all content from the board
    public void resetBoard() {
        //Resets all state matrices
        this.destroyed = new boolean[15][15];
        this.type = new int[15][15];
        this.part = new int[15][15];
        this.ID = new int[15][15];
        this.rotated = new boolean[15][15];
        this.mostRecent = null;

        generateImageViewBoard();
    }

    //Place all ship parts, with [col][row] as the first part
    public void placeShip(int type, int col, int row, boolean rotated) {
        for (int i = 0; i < type; i++) {
            if (rotated) { // ^
                this.type[col][row + i] = type;
                this.part[col][row + i] = i + 1;
                this.rotated[col][row + i] = true;
                this.ID[col][row + i] = this.countID;
            } else { // <
                this.type[col + i][row] = type;
                this.part[col + i][row] = i + 1;
                this.rotated[col + i][row] = false;
                this.ID[col + i][row] = this.countID;
            }
        }
        
        this.countID++; //Increments ID for the next placement
    }

    //Check and returns if a ship placement is valid
    public boolean checkValidPlacement(int type, int col, int row, boolean rotated) {
        //Check if it's inside grid bounds
        if ((rotated && row > 15-type) || (!rotated && col > 15-type)) {
            return false;
        }

        //Check if it's not overlapping other ships
        for (int i = 0; i < type; i++) {
            if ((rotated && this.type[col][row + i] != 0) || (!rotated && this.type[col + i][row] != 0)) {
                return false;
            }
        }

        return true;
    }

    public Pair<Integer, Integer> getFirstPartOfShip(int col, int row) {
        int partIndex = part[col][row] - 1; //Zero-based conversion

        //Go to the ship's first part
        if (rotated[col][row]) { // ^
            row -= partIndex;
        } else { // <
            col -= partIndex;
        }

        return new Pair<>(col, row);
    }

    public boolean checkWholeShipDestroyed(int col, int row) {
        Pair<Integer, Integer> cur = getFirstPartOfShip(col, row);

        int firstID = type[cur.getX()][cur.getY()];
        boolean firstRotated = rotated[cur.getX()][cur.getY()];

        for (int k = 0; k < firstID; k++) { //Check all parts
            if (type[cur.getX()][cur.getY()] == firstID && !destroyed[cur.getX()][cur.getY()]) return false;

            //Go to the next part
            if (firstRotated) { // ^
                cur.setY(cur.getY()+1);
            } else { // <
                cur.setX(cur.getX()+1);
            }
        }

        return true;
    }

    //Update a cell from the ImageView matrix with corresponding sprite and rotation
    public void updateImageViewBoard(int i, int j) {
        int partIndex = part[i][j] - 1; //Zero-based conversion

        //Rotate if rotated, not water and not hidden
        if (rotated[i][j] && type[i][j] != 0 && !hidden) {
            imageViewBoard[i][j].setRotate(90);
        } else {
            imageViewBoard[i][j].setRotate(0);
        }

        if (!destroyed[i][j]) {
            //Show appropriate ship part
            if (type[i][j] == 1 && !hidden) {
                imageViewBoard[i][j].setImage(Main.buoyImage[partIndex]);
            } else if (type[i][j] == 2 && !hidden) {
                imageViewBoard[i][j].setImage(Main.submarineImage[partIndex]);
            } else if (type[i][j] == 3 && !hidden) {
                imageViewBoard[i][j].setImage(Main.torpedoImage[partIndex]);
            } else if (type[i][j] == 4 && !hidden) {
                imageViewBoard[i][j].setImage(Main.tankerImage[partIndex]);
            } else if (type[i][j] == 5 && !hidden) {
                imageViewBoard[i][j].setImage(Main.carrierImage[partIndex]);
            } else { //Show water
                imageViewBoard[i][j].setImage(Main.waterImage);
            }
        } else if (destroyed[i][j]) {
            if (type[i][j] == 0) { //If water
                imageViewBoard[i][j].setImage(Main.wrongImage); //Show X
            } else { //If ship
                imageViewBoard[i][j].setImage(Main.debrisImage); //Show generic debris
            }
        }

        //Check if ship sprites can be changed to specific debris sprites
        if (destroyed[i][j] && type[i][j] != 0) {
            //Only set specific debris if all parts are destroyed
            boolean canBeUpdated = checkWholeShipDestroyed(i, j); //Flag to check if they can be changed

            if (canBeUpdated && type[i][j] == 1) { //Buoy
                //If it's the enemy board, only set specific debris if all 4 cells around it are destroyed
                if (hidden && ((i + 1 <= 14 && !destroyed[i + 1][j]) || (i - 1 >= 0 && !destroyed[i - 1][j]) ||
                               (j + 1 <= 14 && !destroyed[i][j + 1]) || (j - 1 >= 0 && !destroyed[i][j - 1]))) {
                    canBeUpdated = false;
                }
            }

            if (canBeUpdated) { //All checks passed, flag still up
                if (rotated[i][j]) imageViewBoard[i][j].setRotate(90); //Rotate if rotated

                //Show appropriate ship part
                if (type[i][j] == 1) {
                    imageViewBoard[i][j].setImage(Main.buoyDestroyedImage[partIndex]);
                } else if (type[i][j] == 2) {
                    imageViewBoard[i][j].setImage(Main.submarineDestroyedImage[partIndex]);
                } else if (type[i][j] == 3) {
                    imageViewBoard[i][j].setImage(Main.torpedoDestroyedImage[partIndex]);
                } else if (type[i][j] == 4) {
                    imageViewBoard[i][j].setImage(Main.tankerDestroyedImage[partIndex]);
                } else if (type[i][j] == 5) {
                    imageViewBoard[i][j].setImage(Main.carrierDestroyedImage[partIndex]);
                }
            }
        }

        //Show explosion on the last hit
        if (mostRecent != null && type[mostRecent.getX()][mostRecent.getY()] != 0) {
            imageViewBoard[mostRecent.getX()][mostRecent.getY()].setImage(Main.explosionImage);
        }
    }

    //Update all cells
    public void generateImageViewBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                updateImageViewBoard(i, j);
            }
        }
    }

    //Randomize board
    public void generateRandomBoard() {
        Random r = new Random();
        boolean valid; //Flag for ship placement
        int[] availableShips = new int[] {1, 4, 4, 2, 2}; //Amount of ships to place
        int shipsLeft = 13; //Auxiliary counter, sum(availableShips)

        //Variables for random results
        boolean chosenRotated;
        int chosenShip = 0, chosenCol = 0, chosenRow = 0;

        resetBoard();

        //Ship placement loop, until no ships are left
        while (shipsLeft != 0) {
            chosenRotated = (r.nextInt(2) == 1); //[0, 1]

            //Choose a ship from the available ones
            valid = false;
            while (!valid) {
                chosenShip = r.nextInt(5); //[0, 4]
                if (availableShips[chosenShip] != 0) valid = true;
            }
            chosenShip += 1; //One-based conversion

            //Choose a cell to place the first part
            valid = false;
            while (!valid) {
                chosenCol = r.nextInt(15); //[0, 14]
                chosenRow = r.nextInt(15); //[0, 14]

                //Check if position is valid
                valid = checkValidPlacement(chosenShip, chosenCol, chosenRow, chosenRotated);
            }

            placeShip(chosenShip, chosenCol, chosenRow, chosenRotated); //Place ship
            availableShips[chosenShip-1]--; //Remove placed ship from available array

            //Recalculate amount of ships left
            shipsLeft = 0;
            for (int i = 0; i < 5; i++) shipsLeft += availableShips[i];
        }

        generateImageViewBoard();
    }
}