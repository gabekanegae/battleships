package battleships.data;

import java.util.Objects;

//Class to define and manage a pair of elements
public class Pair<X, Y> {
    private X x;
    private Y y;

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    public void setX(X x) {
        this.x = x;
    }

    public void setY(Y y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        // Self check
        if (this == o)
            return true;
        // Null check
        if (o == null)
            return false;
        // Type check and cast
        if (getClass() != o.getClass())
            return false;

        Pair<X,Y> p = (Pair<X,Y>) o;

        // field comparison
        return Objects.equals(x, p.getX())
                && Objects.equals(y, p.getY());
    }
}
