package ru.nsu.fit.g14205.ryzhakov;

public class CellSettings implements Cloneable {
    private int cellSize = 15;
    private int lineWidth = 1;

    public CellSettings() {
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public CellSettings(int cellSize, int lineWidth) {
        this.cellSize = cellSize;
        this.lineWidth = lineWidth;
    }

    public CellSettings getClone(){
        try {
            return (CellSettings)this.clone();
        }catch(CloneNotSupportedException ex){
            return null;
        }
    }
}
