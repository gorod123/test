package ru.nsu.fit.g14205.ryzhakov.view.settingsWindow;

import ru.nsu.fit.g14205.ryzhakov.CellSettings;
import ru.nsu.fit.g14205.ryzhakov.model.GameSettings;
import ru.nsu.fit.g14205.ryzhakov.model.cell.CellInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class SettingsWindow extends JFrame {
    static final int MIN_FIELD_SIZE = 1;
    static final int MAX_FIELD_SIZE = 50;
    JPanel panel = new JPanel();

    JTextField width, height;
    JTextField lineWidth;
    JTextField cellSize;

    JTextField lifeBegin, lifeEnd, birthBegin, birthEnd, firstImpact, secondImpact;

    public SettingsWindow(CellInterface currentCell, CellSettings cellSettings, GameSettings gameSettings, ChangeListener changeListener) {
        setSize(400, 500);
        setResizable(false);
        setTitle("Settings");

        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(panel);

        panel.setLayout(new GridLayout(19, 2, 10, 2));

        addLifeCycleSettings(gameSettings);
        addDelimiter();

        addCellSizeSettings(cellSettings);
        addLineWidthSettings(cellSettings);
        addDelimiter();

        addFieldSizeSettings(currentCell);
        addDelimiter();

        JButton accept = new JButton("OK");
        accept.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(tryAcceptChanges()){
                    changeListener.run(
                            new CellSettings(Integer.parseInt(cellSize.getText()), Integer.parseInt(lineWidth.getText())),
                            Integer.parseInt(width.getText()),Integer.parseInt(height.getText()),
                            new GameSettings(Double.parseDouble(lifeBegin.getText()), Double.parseDouble(lifeEnd.getText()), Double.parseDouble(birthBegin.getText()),
                                    Double.parseDouble(birthEnd.getText()), Double.parseDouble(firstImpact.getText()), Double.parseDouble(secondImpact.getText())));

                    SettingsWindow.this.dispose();
                }
            }
        });
        panel.add(accept);

        JButton cancel = new JButton("Cancel");
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SettingsWindow.this.dispose();
            }
        });
        panel.add(cancel);
    }

    private void addDelimiter() {
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
    }

    private void addFieldSizeSettings(CellInterface currentCell) {
        panel.add(new JLabel("Field size settings:"));
        panel.add(new JLabel(""));

        panel.add(new JLabel("Width:"));
        width = new JTextField();
        width.setText(Integer.toString(currentCell.getWidth()));
        panel.add(width);


        panel.add(new JLabel("Height:"));
        height = new JTextField();
        height.setText(Integer.toString(currentCell.getHeight()));
        panel.add(height);
    }

    static final int MIN_CELL_SIZE = 3;
    static final int MAX_CELL_SIZE = 20;

    private void addCellSizeSettings(CellSettings cellSettings){
        panel.add(new JLabel("Cell size:"));
        panel.add(new JLabel(""));
        cellSize = new JTextField();
        cellSize.setText(Integer.toString(cellSettings.getCellSize()));
        panel.add(cellSize);

        JSlider cellSizeSlider = new JSlider();
        cellSizeSlider.setMinimum(MIN_CELL_SIZE);
        cellSizeSlider.setMaximum(MAX_CELL_SIZE);
        cellSizeSlider.setValue(cellSettings.getCellSize());

        cellSizeSlider.addChangeListener(e -> {
            int value = ((JSlider)e.getSource()).getValue();

            if(cellSize.getText().equals(Integer.toString(value))){
                return;
            }

            EventQueue.invokeLater(() -> cellSize.setText(Integer.toString(value)));
        });

        cellSize.addCaretListener(e -> {

            String sValue = cellSize.getText();

            if(sValue.isEmpty()){
                return;
            }

            if(sValue.equals(Integer.toString(cellSizeSlider.getValue()))){
                return;
            }

            cellSizeSlider.setValue(Integer.parseInt(sValue));
        });

        panel.add(cellSizeSlider);
    }

    static final int MIN_LINE_WIDTH = 1;
    static final int MAX_LINE_WIDTH = 20;

    private void addLineWidthSettings(CellSettings cellSettings){
        panel.add(new JLabel("Line width:"));
        panel.add(new JLabel(""));
        lineWidth = new JTextField();
        lineWidth.setText(Integer.toString(cellSettings.getLineWidth()));
        panel.add(lineWidth);

        JSlider lineWidthSlider = new JSlider();
        lineWidthSlider.setMinimum(MIN_LINE_WIDTH);
        lineWidthSlider.setMaximum(MAX_LINE_WIDTH);
        lineWidthSlider.setValue(cellSettings.getLineWidth());

        lineWidthSlider.addChangeListener(e -> {
            int value = ((JSlider)e.getSource()).getValue();

            if(lineWidth.getText().equals(Integer.toString(value))){
                return;
            }

            EventQueue.invokeLater(() -> lineWidth.setText(Integer.toString(value)));
        });

        lineWidth.addCaretListener(e -> {

            String sValue = lineWidth.getText();

            if(sValue.isEmpty()){
                return;
            }

            if(sValue.equals(Integer.toString(lineWidthSlider.getValue()))){
                return;
            }

            lineWidthSlider.setValue(Integer.parseInt(sValue));
        });

        panel.add(lineWidthSlider);
    }

    private void addLifeCycleSettings(GameSettings gameSettings){
        panel.add(new JLabel("Life cycle settings:"));
        panel.add(new JLabel(""));

        panel.add(new JLabel("Life Begin:"));
        lifeBegin = new JTextField();
        lifeBegin.setText(Double.toString(gameSettings.getLifeBegin()));
        panel.add(lifeBegin);

        panel.add(new JLabel("Life End:"));
        lifeEnd = new JTextField();
        lifeEnd.setText(Double.toString(gameSettings.getLifeEnd()));
        panel.add(lifeEnd);

        panel.add(new JLabel("Birth Begin:"));
        birthBegin = new JTextField();
        birthBegin.setText(Double.toString(gameSettings.getBirthBegin()));
        panel.add(birthBegin);

        panel.add(new JLabel("Birth End:"));
        birthEnd = new JTextField();
        birthEnd.setText(Double.toString(gameSettings.getBirthEnd()));
        panel.add(birthEnd);

        panel.add(new JLabel("First Impact:"));
        firstImpact = new JTextField();
        firstImpact.setText(Double.toString(gameSettings.getFirstImpact()));
        panel.add(firstImpact);

        panel.add(new JLabel("Second Impact:"));
        secondImpact = new JTextField();
        secondImpact.setText(Double.toString(gameSettings.getSecondImpact()));
        panel.add(secondImpact);
    }

    boolean tryAcceptChanges(){
        try{
            if(width.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Width field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(Integer.parseInt(width.getText()) > MAX_FIELD_SIZE || Integer.parseInt(width.getText()) < MIN_FIELD_SIZE){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Width field must be more or equal " + MIN_FIELD_SIZE + " and less or equal " + MAX_FIELD_SIZE + "!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(height.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Height field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(Integer.parseInt(height.getText()) > MAX_FIELD_SIZE || Integer.parseInt(width.getText()) < MIN_FIELD_SIZE){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Height field must be more or equal " + MIN_FIELD_SIZE + " and less or equal " + MAX_FIELD_SIZE + "!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(lineWidth.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Line width field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(Integer.parseInt(lineWidth.getText()) > MAX_LINE_WIDTH || Integer.parseInt(lineWidth.getText()) < MIN_LINE_WIDTH){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Line width must be more or equal " + MIN_LINE_WIDTH + " and less or equal " + MAX_LINE_WIDTH + "!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(cellSize.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Cell size field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if(Integer.parseInt(cellSize.getText()) > MAX_CELL_SIZE || Integer.parseInt(cellSize.getText()) < MIN_CELL_SIZE){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Cell size must be more or equal " + MIN_CELL_SIZE + " and less or equal " + MAX_CELL_SIZE + "!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(SettingsWindow.this, e.getMessage() + "\nAll graphic setting must be integer.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try{
            if(lifeBegin.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Life begin must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Double.parseDouble(lifeBegin.getText());
            if(lifeEnd.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Life end field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Double.parseDouble(lifeEnd.getText());

            if(birthBegin.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Birth begin field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Double.parseDouble(birthBegin.getText());

            if(birthEnd.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Birth end must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Double.parseDouble(birthEnd.getText());

            if(firstImpact.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "First Impact field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Double.parseDouble(firstImpact.getText());

            if(secondImpact.getText().isEmpty()){
                JOptionPane.showMessageDialog(SettingsWindow.this, "Second Impact field must be not empty!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Double.parseDouble(secondImpact.getText());
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(SettingsWindow.this, e.getMessage() + "\nAll games setting must be double.", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}
