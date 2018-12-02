package battleships.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class AIPlayer extends Player {
    private Stack<Pair<Integer, Integer>> hitShots; //All shots hit

    public AIPlayer() {
        super(); //Calls Player() constructor
        this.hitShots = new Stack<>();
    }

    //Push hit shot to stack
    public void pushHitShot(int col, int row) {
        hitShots.push(new Pair<>(col, row));
    }

    //Empty stack of hit shots by ship ID
    public void emptyHitShots(Board board, int ID) {
        Stack<Pair<Integer, Integer>> clone = (Stack<Pair<Integer, Integer>>) hitShots.clone();

        for (Pair<Integer, Integer> c : clone) {
            if (ID == board.getID(c.getX(), c.getY())) {
                hitShots.remove(c);
            }
        }
    }

    //Returns a list of pairs not destroyed around a pair(col,row) in the enemy's board
    private List<Pair<Integer, Integer>> getPairsAround(int col, int row, Board enemyBoard) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();

        //Up
        if ((row-1) > 0 && !enemyBoard.isDestroyed(col, row-1)) {
            pairs.add(new Pair<>(col, row-1));
        }

        //Down
        if ((row+1) < 15 && !enemyBoard.isDestroyed(col, row+1)) {
            pairs.add(new Pair<>(col, row+1));
        }

        //Left
        if ((col-1) > 0 && !enemyBoard.isDestroyed(col-1, row)) {
            pairs.add(new Pair<>(col-1, row));
        }

        //Right
        if ((col+1) < 15 && !enemyBoard.isDestroyed(col+1, row)) {
            pairs.add(new Pair<>(col+1, row));
        }

        return pairs;
    }

    //Chooses a cell to shoot at based on the state of an enemy board
    public Pair<Integer, Integer> locationDecision(Board enemyBoard) {
        Random r = new Random();
        Pair<Integer, Integer> chosenPair;
        int col, row;

        //No partially destroyed ships
        if (hitShots.isEmpty()) {
            //Just random
            do {
                col = r.nextInt(15); //[0, 14]
                row = r.nextInt(15); //[0, 14]
            } while (enemyBoard.isDestroyed(col, row) || (getPairsAround(col, row, enemyBoard).size() == 0));

            chosenPair = new Pair<>(col, row);
        } else {
            Pair<Integer, Integer> last = hitShots.lastElement();
            List<Pair<Integer, Integer>> pairs = getPairsAround(last.getX(), last.getY(), enemyBoard);

            //Only one shot hit
            if (hitShots.size() == 1 && pairs.size() > 0) {
                chosenPair = pairs.get(r.nextInt(pairs.size())); //Choose randomly between the 4 around the last shot
            } else {
                boolean foundPair = false;

                do {
                    //Get the last shot
                    last = hitShots.lastElement();
                    //Get the possibilities around it
                    pairs = getPairsAround(last.getX(), last.getY(), enemyBoard);

                    //Check up, to choose down
                    Pair<Integer, Integer> choice = new Pair<>(last.getX(), last.getY() - 1);
                    chosenPair = new Pair<>(last.getX(), last.getY() + 1);
                    //If up is a hitShot or destroyed, and down is a possibility
                    if ((hitShots.contains(choice) || (choice.getY() > 0 && enemyBoard.isDestroyed(choice.getX(), choice.getY()))) &&
                         pairs.contains(chosenPair)) {
                        foundPair = true;
                    } else {
                        //Check down, to choose up
                        choice = new Pair<>(last.getX(), last.getY() + 1);
                        chosenPair = new Pair<>(last.getX(), last.getY() - 1);
                        //If down is a hitShot or destroyed, and up is a possibility
                        if ((hitShots.contains(choice) || (choice.getY() < 15 && enemyBoard.isDestroyed(choice.getX(), choice.getY()))) &&
                             pairs.contains(chosenPair)) {
                            foundPair = true;
                        } else {
                            //Check left, to choose right
                            choice = new Pair<>(last.getX() - 1, last.getY());
                            chosenPair = new Pair<>(last.getX() + 1, last.getY());
                            //If left is a hitShot or destroyed, and right is a possibility
                            if ((hitShots.contains(choice) || (choice.getX() > 0 && enemyBoard.isDestroyed(choice.getX(), choice.getY()))) &&
                                 pairs.contains(chosenPair)) {
                                foundPair = true;
                            } else {
                                //Check right, to choose left
                                choice = new Pair<>(last.getX() + 1, last.getY());
                                chosenPair = new Pair<>(last.getX() - 1, last.getY());
                                //If right is a hitShot or destroyed, and left is a possibility
                                if ((hitShots.contains(choice) || (choice.getX() < 15 && enemyBoard.isDestroyed(choice.getX(), choice.getY()))) &&
                                     pairs.contains(chosenPair)) {
                                    foundPair = true;
                                } else {
                                    //If none of the possibilities is a good shot, discard this cell
                                    hitShots.pop();
                                }
                            }
                        }
                    }
                } while (!foundPair && !hitShots.isEmpty());
            }
        }

        return chosenPair;
    }

    //Chooses a bomb to use based on the state of an enemy board
    public int bombDecision() {
        //If there's only one type of bomb available, there's no choice to make
        if (this.getJDAMAmount() < 1) return 2;
        if (this.getMissileAmount() < 1) return 1;

        //If there's at least 2 hitShots, it's pretty likely to be a ship indeed
        if (hitShots.size() >= 2) return 2;

        //Otherwise, always use JDAM
        return 1;
    }
}