package ru.nsu.fit.g14205.ryzhakov.view;

import ru.nsu.fit.g14205.ryzhakov.CellSettings;
import ru.nsu.fit.g14205.ryzhakov.model.cell.CellInterface;
import ru.nsu.fit.g14205.ryzhakov.model.CellModel;

public interface CellView {
    void connectCellModel(CellModel CellModel);
    void updateCell(CellInterface Cell);
    void updateCellViewSettings(CellSettings cellSettings);
}
