package com.rocketdashboard.ui;

import javax.swing.*;
import java.awt.*;

public abstract class UIComponent extends JPanel {
    protected Color backgroundColor;
    protected Color textColor;

    public UIComponent() {
        this.backgroundColor = Color.WHITE;
        this.textColor = Color.BLACK;
        setBackground(backgroundColor);
        setForeground(textColor);
    }

    public void setTheme(boolean darkMode) {
        if (darkMode) {
            backgroundColor = Color.DARK_GRAY;
            textColor = Color.WHITE;
        } else {
            backgroundColor = Color.WHITE;
            textColor = Color.BLACK;
        }
        setBackground(backgroundColor);
        setForeground(textColor);
        updateTheme();
    }

    protected abstract void updateTheme();
}