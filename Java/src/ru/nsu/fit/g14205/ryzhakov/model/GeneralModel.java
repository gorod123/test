package ru.nsu.fit.g14205.ryzhakov.model;

import ru.nsu.fit.g14205.ryzhakov.CellSettings;
import ru.nsu.fit.g14205.ryzhakov.model.cell.CellField;
import ru.nsu.fit.g14205.ryzhakov.view.CellView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

public class GeneralModel extends TimerTask implements CellModel {
    static private final int FIRST_NEIGHBOURS_COUNT = 6;
    static private final int SECOND_NEIGHBOURS_COUNT = 6;


    private boolean stepOnTimerEnabled = false;
    private boolean XOREnabled = false;
    private CellField currentCellField = new CellField();
    private CellView cellView;
    private GameSettings gameSettings = new GameSettings();

    public GeneralModel() {
    }

    @Override
    public void connectCellView(CellView cellView) {
        this.cellView = cellView;
        if (null != cellView) {
            cellView.updateCell(currentCellField);
        }
    }

    @Override
    public void cellClicked(int x, int y) {
        if(isInCell(x, y)){
            if(XOREnabled){
                currentCellField.setCellState(x, y, (char)(1 - currentCellField.getCellState(x, y)));
            }
            else{
                currentCellField.setCellState(x, y, (char)1);
            }

            recountCellValues();

            if (null != cellView) {
                cellView.updateCell(currentCellField);
            }
        }
    }


    private int loadNextInt(Scanner in){
        while(!in.hasNextInt()){
            in.nextLine();
        }

        return in.nextInt();
    }

    @Override
    public void load(String path) {
        try{
            Scanner in = new Scanner(new File(path));

            int width = loadNextInt(in);
            int height = loadNextInt(in);

            CellField newCellField = new CellField(width, height);

            int lineWidth = loadNextInt(in);
            int cellSize = loadNextInt(in);

            CellSettings newCellSettings = new CellSettings(cellSize, lineWidth);

            int cellsCount = loadNextInt(in);

            for(int i = 0; i < cellsCount; ++i){
                int x = loadNextInt(in);
                int y = loadNextInt(in);

                newCellField.setCellState(x, y, (char)1);
            }

            in.close();

            currentCellField = newCellField;
            recountCellValues();

            if(null != cellView){
                cellView.updateCellViewSettings(newCellSettings);
                cellView.updateCell(currentCellField);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setGameSettings(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        recountCellValues();
    }

    @Override
    public void save(String path, CellSettings cellSettings) {
        try{
            PrintWriter out = new PrintWriter(new File(path));

            out.printf("%d %d\n", currentCellField.getWidth(), currentCellField.getHeight());
            out.printf("%d\n", cellSettings.getLineWidth());
            out.printf("%d\n", cellSettings.getCellSize());

            List<Integer> aliveCells = new ArrayList<>();
            for(int y = 0; y < currentCellField.getHeight(); ++y){
                for(int x = 0; x < currentCellField.getWidth() - y%2; ++x){
                    if(currentCellField.getCellState(x, y) == 1){
                        aliveCells.add(x);
                        aliveCells.add(y);
                    }
                }
            }

            out.printf("%d\n", aliveCells.size() / 2);

            for(int i = 0; i < aliveCells.size(); ++i){
                out.printf("%d %d\n", aliveCells.get(i), aliveCells.get(i+1));
                ++i;
            }

            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void clear(){
        for(int y = 0; y < currentCellField.getHeight(); ++y){
            for(int x = 0; x < currentCellField.getWidth() - y%2; ++x){
                currentCellField.setCellState(x, y, (char)0);
            }
        }

        recountCellValues();

        if (null != cellView) {
            cellView.updateCell(currentCellField);
        }
    }

    private boolean isInCell(int x, int y) {
        return x >= 0 && y >= 0 && y < currentCellField.getHeight() && x < (currentCellField.getWidth() - y % 2);
    }

    private double countNewCellValue(int x, int y) {
        double result = 0.0;

        for (int i = 0; i < FIRST_NEIGHBOURS_COUNT; ++i) {
            int neigh_y[] = {0, 1, -1};
            int neigh_x[] = {-1, 1, -1, 0, -1, 0};
            int dy = y + neigh_y[i / 2];
            int dx = x + neigh_x[i];

            if (y % 2 == 1 && Math.abs(neigh_y[i / 2]) % 2 == 1) {
                ++dx;
            }

            if (isInCell(dx, dy)) {
                result += currentCellField.getCellState(dx, dy) * gameSettings.getFirstImpact();
            }
        }

        for (int i = 0; i < SECOND_NEIGHBOURS_COUNT; ++i) {
            int neigh_y[] = {2, -2, 1, 1, -1, -1};
            int neigh_x[] = {0, 0, -2, 1, -2, 1};
            int dy = y + neigh_y[i];
            int dx = x + neigh_x[i];

            if (y % 2 == 1 && Math.abs(neigh_y[i]) % 2 == 1) {
                ++dx;
            }

            if (isInCell(dx, dy)) {
                result += currentCellField.getCellState(dx, dy) * gameSettings.getSecondImpact();
            }
        }

        return result;
    }

    private char countNewCellState(int x, int y, double newCellValue) {
        char oldState = currentCellField.getCellState(x, y);

        if (oldState == 1 &&
                (newCellValue < gameSettings.getLifeBegin() || newCellValue > gameSettings.getLifeEnd())) {
            return 0;
        }

        if (oldState == 0 &&
                (newCellValue >= gameSettings.getBirthBegin() && newCellValue <= gameSettings.getBirthEnd())) {
            return 1;
        }

        return oldState;
    }

    private void recountCellValues(){
        for (int y = 0; y < currentCellField.getHeight(); ++y) {
            for (int x = 0; x < currentCellField.getWidth() - y % 2; ++x) {
                currentCellField.setCellValue(x, y, countNewCellValue(x, y));
            }
        }
    }

    @Override
    public synchronized void doStep() {
        CellField nextCellField = new CellField(currentCellField.getWidth(), currentCellField.getHeight());

        for (int y = 0; y < currentCellField.getHeight(); ++y) {
            for (int x = 0; x < currentCellField.getWidth() - y % 2; ++x) {
                char newCellState = countNewCellState(x, y, currentCellField.getCellValue(x, y));

                nextCellField.setCellState(x, y, newCellState);
            }
        }

        currentCellField = nextCellField;

        recountCellValues();

        if (null != cellView) {
            cellView.updateCell(currentCellField);
        }
    }

    @Override
    public void setTimerMode(boolean enabled){
        stepOnTimerEnabled = enabled;
    };

    @Override
    public void setXorMode(boolean enabled) {
        XOREnabled = enabled;
    }

    @Override
    public void run() {
        if (stepOnTimerEnabled) {
            doStep();
        }
    }

    @Override
    public void setCellSize(int width, int height){
        CellField nextCellField = new CellField(width, height);

        for (int y = 0; y < Math.min(nextCellField.getHeight(), currentCellField.getHeight()); ++y) {
            for (int x = 0; x < Math.min(nextCellField.getWidth(), currentCellField.getWidth()) - y % 2; ++x) {
                nextCellField.setCellState(x, y, currentCellField.getCellState(x, y));
            }
        }

        currentCellField = nextCellField;

        recountCellValues();

        if (null != cellView) {
            cellView.updateCell(currentCellField);
        }
    }

    public GameSettings getGameSettings() {
        try {
            return gameSettings.clone();
        }
        catch (CloneNotSupportedException ex){
            return null;
        }
    }
}
