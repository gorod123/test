package ru.nsu.fit.g14205.ryzhakov.model.cell;

public class CellField implements CellInterface {
    static private final int DEFAULT_SIZE = 10;

    private int sizeX;
    private int sizeY;
    private double value[][];
    private char state[][];

    public CellField(){
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public CellField(int sizeX, int sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        value = new double[sizeX][sizeY];
        state = new char[sizeX][sizeY];
    }

    @Override
    public int getWidth(){
        return sizeX;
    }

    @Override
    public int getHeight(){
        return sizeY;
    }

    @Override
    public char getCellState(int x, int y){
        return state[x][y];
    }

    @Override
    public double getCellValue(int x, int y){
        return value[x][y];
    }

    public void setCellState(int x, int y, char newState) { state[x][y] = newState; }

    public void setCellValue(int x, int y, double newValue) { value[x][y] = newValue; }
}
