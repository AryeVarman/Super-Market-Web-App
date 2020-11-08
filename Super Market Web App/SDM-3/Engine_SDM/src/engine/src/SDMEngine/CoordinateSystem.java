package engine.src.SDMEngine;

import java.util.*;

public class CoordinateSystem {

    private final int START_X = 0;
    private final int START_Y = 0;

    private final int MAX_COL;
    private final int MAX_ROW;
    private final Coordinate[][] matrixCoordinate;
    private List<Coordinate> takenPlaces = new ArrayList<>();

    public CoordinateSystem(int col, int row) {
        MAX_COL = col;
        MAX_ROW = row;
        matrixCoordinate = new Coordinate[MAX_ROW][MAX_COL];

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                matrixCoordinate[i][j] = new Coordinate(j + 1, i + 1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateSystem that = (CoordinateSystem) o;
        return MAX_COL == that.MAX_COL &&
                MAX_ROW == that.MAX_ROW &&
                Arrays.equals(matrixCoordinate, that.matrixCoordinate) &&
                takenPlaces.equals(that.takenPlaces);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(MAX_COL, MAX_ROW, takenPlaces);
        result = 31 * result + Arrays.hashCode(matrixCoordinate);
        return result;
    }

    public int getMAX_COL() {
        return MAX_COL;
    }

    public int getMAX_ROW() {
        return MAX_ROW;
    }

    public final Coordinate[][] getMatrixCoordinate() {
        return matrixCoordinate;
    }

    public boolean changeCoordinateInMap(Coordinate newCoordinate) {
        boolean succeed = false;

        if(newCoordinate.getElement() != null && newCoordinate.getCol() <= MAX_COL && newCoordinate.getRow() <= MAX_ROW &&
                newCoordinate.getCol() > START_X && newCoordinate.getRow() > START_Y) {
            succeed = true;
            matrixCoordinate[newCoordinate.getRow() - 1][newCoordinate.getCol() - 1] = newCoordinate;
        }

        return succeed;
    }

    public boolean changeElementInCoordinate(int col, int row, Coordinatable newElement) {
        boolean succeed = false;

        if(col <= MAX_COL && col > START_X && row <= MAX_ROW && row > START_Y && newElement != null) {
            succeed = true;
            matrixCoordinate[row - 1][col - 1].setElement(newElement);
        }

        return succeed;
    }

    public List<Coordinate> getTakenPlaces() {
        return takenPlaces;
    }

    public Coordinate getCoordinate(int col, int row) { return this.matrixCoordinate[row - 1][col - 1]; }

    public final Coordinatable getElementInLocationXY(int col, int row) {
        return matrixCoordinate[row - 1][col - 1].getElement();
    }

    public boolean isValidCoordinateInTheSystem(Coordinate location) throws Exception {
        if(location.getCol() > MAX_COL || location.getCol() < 1){
            throw new Exception("X location must be between " + 1 + " - " + MAX_COL);
        }
        if(location.getRow() > MAX_ROW || location.getRow() < 1){
            throw new Exception("Y location must be between " + 1 + " - " + MAX_ROW);
        }
        return true;
    }

    public void addElementToMatrix(Coordinate location)  {
        matrixCoordinate[location.getRow()][location.getCol()] = location;
    }

    public boolean isCoordinateTaken(int col, int row) {
        boolean taken = true;

        if(this.getElementInLocationXY(col, row) == null) {
            taken = false;
        }

        return taken;
    }
}
