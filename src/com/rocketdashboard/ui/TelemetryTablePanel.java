package com.rocketdashboard.ui;

import com.rocketdashboard.model.TelemetryData;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelemetryTablePanel extends UIComponent {
    private JTable table;
    private DefaultTableModel tableModel;

    public TelemetryTablePanel() {
        setLayout(new BorderLayout());
        initializeTable();
    }

    private void initializeTable() {
        // Create table model
        String[] columns = {"Timestamp", "Altitude (m)", "Velocity (m/s)", "Acceleration (m/s²)", "Latitude", "Longitude", "Recovery"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTelemetryData(List<TelemetryData> telemetryData) {
        tableModel.setRowCount(0);

        if (telemetryData != null) {
            for (TelemetryData data : telemetryData) {
                tableModel.addRow(new Object[]{
                        data.getTimestamp().toString(),
                        String.format("%.2f", data.getAltitude()),
                        String.format("%.2f", data.getVelocity()),
                        String.format("%.2f", data.getAcceleration()),
                        String.format("%.6f", data.getLatitude()),
                        String.format("%.6f", data.getLongitude()),
                        data.isRecoveryDeployed() ? "DEPLOYED" : "Not Deployed"
                });
            }
        }
    }

    @Override
    protected void updateTheme() {
        if (table != null) {
            table.setBackground(backgroundColor);
            table.setForeground(textColor);
            table.setGridColor(Color.GRAY);
        }
    }
}