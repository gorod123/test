package ru.nsu.fit.g14205.ryzhakov.view.settingsWindow;

import ru.nsu.fit.g14205.ryzhakov.CellSettings;
import ru.nsu.fit.g14205.ryzhakov.model.GameSettings;

public interface ChangeListener {
    void run(CellSettings cellSettings, int width, int height, GameSettings gameSettings);
}
