package ru.nsu.fit.g14205.ryzhakov.view.cellPanel;

import ru.nsu.fit.g14205.ryzhakov.CellSettings;
import ru.nsu.fit.g14205.ryzhakov.Drawer;
import ru.nsu.fit.g14205.ryzhakov.model.cell.CellInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CellPanel extends JPanel {
    private boolean needDrawImpactValues = false;
    private static final double HexK = Math.sqrt(3.0) / 2.0;
    private static final Color BORDER_COLOR = Color.black;
    private CellSettings cellSettings = new CellSettings();
    private CellInterface Cell = null;
    private BufferedImage imageBuffer;
    private int start_x = 0;
    private int start_y = 0;
    private int cell_size = 0;
    private int half_cell_size = 0;
    private int horizontal_size = 0;

    public CellPanel(CellPanelClickListener cellPanelClickListener) {
        if (null == cellPanelClickListener) {
            return;
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            List<Integer> lastCell;

            private List<Integer> findNearest(int mouseX, int mouseY){
                if(mouseX < 0 || mouseY < 0 || mouseX >= imageBuffer.getWidth() || mouseY >= imageBuffer.getHeight()){
                    return null;
                }

                if (imageBuffer.getRGB(mouseX, mouseY) != BORDER_COLOR.getRGB()) {
                    int nearestUpYLine = (mouseY - start_y) / (cell_size + half_cell_size);
                    int nearestLeftXLine = (mouseX - start_x) / (2 * horizontal_size);

                    int realX = -1;
                    int realY = -1;
                    double distance = -1.0;

                    for (int y = nearestUpYLine - 1; y <= nearestUpYLine + 1; ++y) {
                        for (int x = nearestLeftXLine - 1 ; x <= nearestLeftXLine + 1; ++x) {
                            int centerX = start_x + horizontal_size + 2 * x * horizontal_size;
                            int centerY = start_y + cell_size + y * (cell_size + half_cell_size);

                            if (y % 2 == 1) {
                                centerX += horizontal_size;
                            }

                            double currentDistance = Math.sqrt((centerX - mouseX) * (centerX - mouseX) + (centerY - mouseY) * (centerY - mouseY));
                            if (distance < -0.0001 || currentDistance < distance) {
                                distance = currentDistance;
                                realX = x;
                                realY = y;
                            }
                        }
                    }

                    if (realX >= 0 && realY >= 0 && realY <= Cell.getHeight() && realX <= Cell.getWidth() - realY % 2) {
                        List<Integer> res = new ArrayList<>();
                        res.add(realX);
                        res.add(realY);

                        return res;
                    }
                }

                return null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                lastCell = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                List<Integer> cell = findNearest(e.getX(), e.getY());


                if(null != cell && (null == lastCell || !cell.equals(lastCell))){
                    lastCell = cell;
                    cellPanelClickListener.onClickOnCell(cell.get(0), cell.get(1));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseDragged(e);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public CellSettings getCellSettings() {
        return cellSettings.getClone();
    }

    public void setCellSettings(CellSettings cellSettings) {
        this.cellSettings = cellSettings;
    }

    private void drawHexagon(Drawer drawer){
        int y0 = start_y;
        for (int y = 0; y < Cell.getHeight(); ++y) {
            int x0 = start_x - 2 * horizontal_size;
            if (y % 2 == 1) {
                x0 += horizontal_size;
            }
            for (int x = 0; x < Cell.getWidth() - (y % 2); ++x) {
                x0 += 2 * horizontal_size;

                drawer.drawLine(x0, y0 + half_cell_size, x0, y0 + half_cell_size + cell_size);
                drawer.drawLine(x0, y0 + half_cell_size, x0 + horizontal_size, y0);
                if (y == 0 || x == Cell.getWidth() - 1) {
                    drawer.drawLine(x0 + horizontal_size, y0, x0 + 2 * horizontal_size, y0 + half_cell_size);
                }
                drawer.drawLine(x0, y0 + half_cell_size + cell_size, x0 + horizontal_size, y0 + 2 * cell_size);
                if (y == Cell.getHeight() - 1 && x != Cell.getWidth() - (y % 2) - 1){
                    drawer.drawLine(x0 + horizontal_size, y0 + 2 * cell_size, x0 + 2 * horizontal_size, y0 + half_cell_size + cell_size);
                }

            }
            drawer.drawLine(x0 + 2 * horizontal_size, y0 + half_cell_size, x0 + 2 * horizontal_size, y0 + half_cell_size + cell_size);
            if (y % 2 == 0 || y == Cell.getHeight() - 1) {
                drawer.drawLine(x0 + 2 * horizontal_size, y0 + half_cell_size + cell_size, x0 + horizontal_size, y0 + 2 * cell_size);
            }
            y0 += cell_size + half_cell_size;
        }
    }

    private void fillHexagon(Drawer drawer){
        int y0 = start_y;
        for (int y = 0; y < Cell.getHeight(); ++y) {
            int x0 = start_x - 2 * horizontal_size;
            if (y % 2 == 1) {
                x0 += horizontal_size;
            }
            for (int x = 0; x < Cell.getWidth() - (y % 2); ++x) {
                x0 += 2 * horizontal_size;
                if (Cell.getCellState(x, y) == 1) {
                    drawer.setColor(new Color(70, 255, 23));
                    drawer.paintArea(x0 + cellSettings.getLineWidth() / 2 + 1, y0 + cell_size + 1);
                    drawer.setColor(Color.BLACK);
                }

                final int DRAW_IMPACTS_MIN_SIZE = 10;
                if(needDrawImpactValues && cellSettings.getCellSize() >= DRAW_IMPACTS_MIN_SIZE) {
                    String text;
                    double value = Cell.getCellValue(x, y);

                    if ((value - (long) value) < 0.0001) {
                        text = String.format("%.0f", value);
                    } else {
                        text = String.format("%.1f", value);
                    }

                    drawer.drawText(text, (int) (x0 + horizontal_size - (text.length() / 2.0) * (Drawer.FONT_SIZE / 2.0)), y0 + cell_size + Drawer.FONT_SIZE / 2);
                }
            }
            y0 += cell_size + half_cell_size;
        }
    }

    private void drawCell(Drawer drawer) {
        drawer.setColor(BORDER_COLOR);
        drawer.setLineWidth(cellSettings.getLineWidth());
        drawHexagon(drawer);
        fillHexagon(drawer);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        cell_size = cellSettings.getCellSize() + cellSettings.getLineWidth() / 2;

        horizontal_size = (int) Math.round(HexK * cell_size);
        half_cell_size = cell_size / 2;

        start_x = cellSettings.getLineWidth() / 2;
        start_y = cellSettings.getLineWidth() / 2;

        int imageHeight = (Cell.getHeight() + 1) * (cell_size + half_cell_size);
        int imageWidth =  2 * (Cell.getWidth()) * horizontal_size + cellSettings.getLineWidth();

        imageBuffer = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imageBuffer.createGraphics();

        Drawer drawer = new Drawer(g2d, imageBuffer);

        if (null != Cell) {
            drawCell(drawer);
        }

        setPreferredSize(new Dimension(imageWidth, imageHeight));
        g.drawImage(imageBuffer, 0, 0, imageWidth, imageHeight, this);
    }

    public void updateCell(CellInterface Cell) {
        this.Cell = Cell;
        this.repaint();
    }

    public void drawImpactValues(boolean enabled){
        needDrawImpactValues = enabled;
    }
}
