package battleships.data;

//Class to manage player board and state
public class Player {
    private int points, JDAMAmount, missileAmount, bombSelected;
    private Board board;

    public Player() {
        this.points = 0;
        this.JDAMAmount = 50;
        this.missileAmount = 5;
        this.bombSelected = 1;
        this.board = new Board();
    }

    public int getPoints() {
        return points;
    }

    public int getJDAMAmount() {
        return JDAMAmount;
    }

    public int getMissileAmount() {
        return missileAmount;
    }

    public int getBombSelected() {
        return bombSelected;
    }

    public Board getBoard() {
        return board;
    }

    public void setBombSelected(int bombSelected) {
        this.bombSelected = bombSelected;
    }

    public void addPoints(int x) {
        this.points += x;
    }

    public void consumeBomb() {
        if (bombSelected == 1) {
            subJDAMAmount(1);
        } else if (bombSelected == 2) {
            subMissileAmount(1);
        }
    }

    public void subJDAMAmount(int x) {
        JDAMAmount -= x;
        if (JDAMAmount < 0) JDAMAmount = 0; //Prevent negative values
    }

    public void subMissileAmount(int x) {
        missileAmount -= x;
        if (missileAmount < 0) missileAmount = 0; //Prevent negative values
    }
}
