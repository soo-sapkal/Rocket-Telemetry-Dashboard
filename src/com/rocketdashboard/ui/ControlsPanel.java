package com.rocketdashboard.ui;

import com.rocketdashboard.model.Stage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlsPanel extends UIComponent {
    private JButton playButton, pauseButton, resetButton;
    private JComboBox<String> stageSelector;
    private JCheckBox darkModeCheckbox;
    private JLabel statusLabel;
    private boolean isDarkMode = false;

    public ControlsPanel() {
        setLayout(new FlowLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        // Playback controls
        playButton = new JButton("▶ Play");
        pauseButton = new JButton("⏸ Pause");
        resetButton = new JButton("⏹ Reset");

        // Style buttons
        playButton.setBackground(new Color(0, 150, 0));
        playButton.setForeground(Color.WHITE);
        pauseButton.setBackground(new Color(200, 150, 0));
        pauseButton.setForeground(Color.WHITE);
        resetButton.setBackground(new Color(150, 0, 0));
        resetButton.setForeground(Color.WHITE);

        add(playButton);
        add(pauseButton);
        add(resetButton);

        // Stage selector - only point events for jumping
        add(new JLabel("Jump to:"));
        stageSelector = new JComboBox<>(new String[]{
                "Launch", "Apogee", "Parachute Deployed", "Landing"
        });
        add(stageSelector);

        // Theme selector
        darkModeCheckbox = new JCheckBox("Dark Mode");
        add(darkModeCheckbox);

        // Status label
        statusLabel = new JLabel("Ready to load CSV");
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel);
    }

    public void setPlayAction(ActionListener listener) {
        playButton.addActionListener(listener);
    }

    public void setPauseAction(ActionListener listener) {
        pauseButton.addActionListener(listener);
    }

    public void setResetAction(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void setStageChangeAction(ActionListener listener) {
        stageSelector.addActionListener(listener);
    }

    public void setThemeChangeAction(ActionListener listener) {
        darkModeCheckbox.addActionListener(listener);
    }

    public Stage getSelectedStage() {
        String selected = (String) stageSelector.getSelectedItem();
        switch (selected) {
            case "Launch": return Stage.LAUNCH;
            case "Apogee": return Stage.APOGEE;
            case "Parachute Deployed": return Stage.PARACHUTE_DEPLOYED;
            case "Landing": return Stage.LANDING;
            default: return Stage.LAUNCH;
        }
    }

    public boolean isDarkMode() {
        return darkModeCheckbox.isSelected();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    protected void updateTheme() {
        // Update component colors based on theme
        Component[] components = getComponents();
        for (Component comp : components) {
            comp.setBackground(backgroundColor);
            comp.setForeground(textColor);
        }

        // Update status label color based on theme
        if (isDarkMode) {
            statusLabel.setForeground(Color.CYAN);
        } else {
            statusLabel.setForeground(Color.BLUE);
        }

        // Keep button colors consistent
        if (!isDarkMode) {
            playButton.setBackground(new Color(0, 150, 0));
            pauseButton.setBackground(new Color(200, 150, 0));
            resetButton.setBackground(new Color(150, 0, 0));
        } else {
            playButton.setBackground(new Color(0, 100, 0));
            pauseButton.setBackground(new Color(150, 100, 0));
            resetButton.setBackground(new Color(100, 0, 0));
        }
    }

    @Override
    public void setTheme(boolean darkMode) {
        super.setTheme(darkMode);
        this.isDarkMode = darkMode;
        updateTheme();
    }
}