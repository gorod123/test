package ru.nsu.fit.g14205.ryzhakov.view;

import ru.nsu.fit.g14205.ryzhakov.CellSettings;
import ru.nsu.fit.g14205.ryzhakov.Main;
import ru.nsu.fit.g14205.ryzhakov.view.cellPanel.CellPanel;
import ru.nsu.fit.g14205.ryzhakov.model.cell.CellInterface;
import ru.nsu.fit.g14205.ryzhakov.view.cellPanel.CellPanelClickListener;
import ru.nsu.fit.g14205.ryzhakov.view.settingsWindow.SettingsWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class GeneralWindow extends JFrame implements CellView {
    private CellPanel cellPanel;
    private StatusBar statusBar;
    private JToolBar toolBar;
    private MenuBar menuBar;
    private CellInterface Cell;
    private JScrollPane scrollPane;

    private Map<String, Menu> menus = new TreeMap<>();
    private Map<String, AbstractButton> buttons = new TreeMap<>();
    private Map<String, MenuItem> menuItems = new TreeMap<>();

    private boolean autoRunEnabled = false;

    private void addButton(String name, String menuName, String tooltip, boolean autoDisengaging, Runnable onClickAction){
        AbstractButton button;
        MenuItem menuItem;

        if(autoDisengaging){
            button = new JButton(name);
            menuItem = new MenuItem(name);

            menuItem.addActionListener(e -> {
                if(menuItem.isEnabled()) {
                    onClickAction.run();
                }
            });
        }
        else{
            button = new JToggleButton(name);
            CheckboxMenuItem cmenuItem = new CheckboxMenuItem(name);

            cmenuItem.addItemListener(e -> {
                if(cmenuItem.isEnabled()) {
                    onClickAction.run();
                }
            });

            menuItem = cmenuItem;
        }

        button.setToolTipText(tooltip);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(button.isEnabled()) {
                    onClickAction.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                statusBar.setText(tooltip);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                statusBar.setText("");
            }
        };

        button.addMouseListener(mouseAdapter);
        toolBar.add(button);

        if(!menus.containsKey(menuName)){
            Menu menu = new Menu(menuName);
            menus.put(menuName, menu);
            menuBar.add(menu);
        }

        menus.get(menuName).add(menuItem);

        menuItems.put(name, menuItem);
        buttons.put(name, button);
    }

    public GeneralWindow()
    {
        setSize(800, 600);
        setLocationByPlatform(true);
        setTitle("Life");

        cellPanel = new CellPanel(new CellPanelClickListener() {
            @Override
            public void onClickOnCell(int x, int y) {
                if(null != CellModel && !autoRunEnabled){
                    CellModel.cellClicked(x, y);
                }
            }
        });

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                onExit();
            }
        });

        scrollPane = new JScrollPane(cellPanel);

        add(scrollPane, BorderLayout.CENTER);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        getContentPane().setBackground(Color.WHITE);

        menuBar = new MenuBar();
        this.setMenuBar(menuBar);

        statusBar = new StatusBar();
        toolBar = new JToolBar();

        addButton("Save", "File", "Сохранить текущую сессию", true, () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "\\Data\\");
            fileChooser.setDialogTitle("Сохранить сессию");
            int chosenFile = fileChooser.showSaveDialog(GeneralWindow.this);
            if (chosenFile == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if(null != CellModel){
                    CellModel.save(file.getAbsolutePath(), cellPanel.getCellSettings());
                }
            }
        });

        addButton("Load", "File", "Загрузить сессию", true, () -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "\\Data\\");
            fileChooser.setDialogTitle("Загрузить сессию");
            int chosenFile = fileChooser.showOpenDialog(GeneralWindow.this);
            if (chosenFile == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if(null != CellModel){
                    CellModel.load(file.getAbsolutePath());
                }
            }
        });

        toolBar.addSeparator();


        addButton("XOR", "Edit", "Если вы активируете этот модуль и проводите мышкой по клетке, то ее состояние инвертируется", false, () -> {
            buttons.get("Replace").setSelected(false);
            ((CheckboxMenuItem)menuItems.get("Replace")).setState(false);

            buttons.get("XOR").setSelected(true);
            ((CheckboxMenuItem)menuItems.get("XOR")).setState(true);

            if(null != CellModel){
                CellModel.setXorMode(true);
            }
        });

        addButton("Replace", "Edit", "Если вы активируете этот модуль и проводите мышкой по клетке, то она станет живой", false, () -> {
                buttons.get("XOR").setSelected(false);
                ((CheckboxMenuItem)menuItems.get("XOR")).setState(false);

                buttons.get("Replace").setSelected(true);
                ((CheckboxMenuItem)menuItems.get("Replace")).setState(true);

                if(null != CellModel) {
                    CellModel.setXorMode(false);
                }
        });

        buttons.get("Replace").doClick();

        toolBar.addSeparator();

        addButton("Impact", "View", "Показать влияние клеток", false, new Runnable() {
            boolean pressed = false;

            @Override
            public void run() {
                pressed = !pressed;

                buttons.get("Impact").setSelected(pressed);
                ((CheckboxMenuItem)menuItems.get("Impact")).setState(pressed);

                cellPanel.drawImpactValues(pressed);
                cellPanel.repaint();
            }
        });

        addButton("Clear", "Edit", "Убить все клетки", true, () -> {
            if(null != CellModel){
                CellModel.clear();
            }
        });

        addButton("Step", "Edit", "Сделать шаг автомата", true, () -> {
            if(null != CellModel){
                CellModel.doStep();
            }
        });

        addButton("Run", "Edit", "Запустить ход автомата", false, new Runnable() {
            boolean pressed = false;

            @Override
            public void run() {
                pressed = !pressed;

                autoRunEnabled = pressed;

                buttons.get("Step").setEnabled(!pressed);
                menuItems.get("Step").setEnabled(!pressed);

                buttons.get("Clear").setEnabled(!pressed);
                menuItems.get("Clear").setEnabled(!pressed);

                buttons.get("Save").setEnabled(!pressed);
                menuItems.get("Save").setEnabled(!pressed);

                buttons.get("Load").setEnabled(!pressed);
                menuItems.get("Load").setEnabled(!pressed);

                if(null != CellModel){
                    CellModel.setTimerMode(pressed);
                }
            }
        });

        toolBar.addSeparator();

        addButton("Settings", "View", "Показать настройки поля и автомата", true, () -> {
            SettingsWindow settingsWindow = new SettingsWindow(Cell, cellPanel.getCellSettings(), CellModel.getGameSettings(), (CellViewSettings, width, height, workCycleSettings) -> {
                updateCellViewSettings(CellViewSettings);

                CellModel.setCellSize(width, height);
                CellModel.setGameSettings(workCycleSettings);

                cellPanel.repaint();
            });
            settingsWindow.setVisible(true);
        });

        addButton("Info", "Help", "Показать информацию об авторе", true, () ->
                JOptionPane.showMessageDialog(null, "Задание: \t Жизнь на гексогонах\n" +
                                            "Автор:\t Рыжаков Игорь\n" +
                                            "Группа: \t 14205\n" +
                                            "Версия: \t " + Double.toString(Main.VERSION),
                "Информация об авторе", JOptionPane.INFORMATION_MESSAGE));

        add(toolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);
    }

    @Override
    public void updateCellViewSettings(CellSettings cellSettings){
        cellPanel.setCellSettings(cellSettings);
    }


    public void onExit()
    {
        System.exit(0);
    }

    private ru.nsu.fit.g14205.ryzhakov.model.CellModel CellModel;

    @Override
    public void connectCellModel(ru.nsu.fit.g14205.ryzhakov.model.CellModel CellModel) {
        this.CellModel = CellModel;
    }

    @Override
    public void updateCell(CellInterface Cell) {
        this.Cell = Cell;
        cellPanel.updateCell(Cell);

        EventQueue.invokeLater(() -> scrollPane.setViewportView(cellPanel));
    }
}
