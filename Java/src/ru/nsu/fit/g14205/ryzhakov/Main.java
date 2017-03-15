package ru.nsu.fit.g14205.ryzhakov;

import ru.nsu.fit.g14205.ryzhakov.model.GeneralModel;
import ru.nsu.fit.g14205.ryzhakov.view.GeneralWindow;

import java.util.Timer;

public class Main {
    public static double VERSION = 0.9;
    private static int TIMER_PERIOD = 1000;

    public static void main(String[] args) {
        GeneralWindow view = new GeneralWindow();

        GeneralModel model = new GeneralModel();

        view.connectCellModel(model);
        model.connectCellView(view);

        view.setVisible(true);

        Timer timer = new Timer();
        timer.schedule(model, 0, TIMER_PERIOD);
    }
}
