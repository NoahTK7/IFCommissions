package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;
import com.noahkurrack.IFCommissions.UI.util.ConfigTableModel;
import com.noahkurrack.IFCommissions.data.ConfigItem;

import javax.swing.*;
import java.util.ArrayList;

public class ConfigView {
    private JPanel configPanel;
    private JTable configTable;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton restoreDefaultsButton;

    public ConfigView() {
        attachListeners();
    }

    private void attachListeners() {
        saveButton.addActionListener(e -> {
            saveConfig();
        });
        cancelButton.addActionListener(e -> {
            cancelConfig();
        });
        addButton.addActionListener(e -> {
            addRow();
        });
        removeButton.addActionListener(e -> {
            removeRow();
        });
        restoreDefaultsButton.addActionListener(e -> {
            restoreDefaults();
        });
    }

    public JPanel getConfigPanel() {
        return configPanel;
    }

    private void createUIComponents() {
        configTable = new JTable();

        ConfigTableModel model = new ConfigTableModel(IFCommissions.getConfigManager().getItems());
        configTable.setModel(model);

        configTable.getColumnModel().getColumn(1).setMaxWidth(100);
    }

    private void saveConfig() {
        ArrayList<ConfigItem> items = new ArrayList<>();
        for (int i = 0; i < configTable.getModel().getRowCount(); i++) {
            items.add(new ConfigItem((String) configTable.getModel().getValueAt(i, 0), (Double) configTable.getModel().getValueAt(i, 1)));
        }
        IFCommissions.getConfigManager().save(items);
        IFCommissions.getGui().setSetupView();
    }

    private void cancelConfig() {
        //reset table model
        ConfigTableModel model = new ConfigTableModel(IFCommissions.getConfigManager().getItems());
        configTable.setModel(model);

        IFCommissions.getGui().setSetupView();
    }

    private void addRow() {
        //Dialog
        String part = (String)JOptionPane.showInputDialog(
                configPanel.getParent(),
                "Part name (exactly as appears in invoice):",
                "New Part",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);
        ((ConfigTableModel) configTable.getModel()).addRow(part);
    }

    private void removeRow() {
        ((ConfigTableModel) configTable.getModel()).removeRow(configTable.getSelectedRow());
    }

    private void restoreDefaults() {
        IFCommissions.getConfigManager().restoreDefaults();

        ConfigTableModel model = new ConfigTableModel(IFCommissions.getConfigManager().getItems());
        configTable.setModel(model);
    }
}