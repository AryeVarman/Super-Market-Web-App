package engine.src.SDMEngine;

import java.io.Serializable;
import java.util.Objects;

public class Coordinate implements Serializable {
    private int col;
    private int row;
    private Coordinatable element;

    public Coordinate(int col, int row, Coordinatable element) {
        this.col = col;
        this.row = row;
        this.element = element;
    }

    public Coordinate(int col, int row) {
        this.col = col;
        this.row = row;
        this.element = null;
    }

    public void setCol(int col) { this.col = col; }

    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }

    public int getRow() { return row; }

    public Coordinatable getElement() { return element; }

    public void setElement(Coordinatable element) { this.element = element; }

    public String showLocation() { return this.toString(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return col == that.col &&
                row == that.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return  "(" + col + ", " + row + ")";
    }
}
