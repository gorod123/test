package ru.nsu.fit.g14205.ryzhakov.view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class StatusBar extends JLabel {
        public StatusBar() {
            super();
            super.setPreferredSize(new Dimension(150, 15));
            setBorder(new BevelBorder(BevelBorder.LOWERED));
            setBackground(Color.LIGHT_GRAY);
        }

        public void setText(String text) {
            super.setText("  " + text);
        }
}
