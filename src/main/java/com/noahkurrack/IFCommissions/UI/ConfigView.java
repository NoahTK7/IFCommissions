/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;
import com.noahkurrack.IFCommissions.UI.util.ConfigTableModel;
import com.noahkurrack.IFCommissions.Utils;
import com.noahkurrack.IFCommissions.data.ConfigItem;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class ConfigView {
    private JPanel configPanel;
    private JTable configTable;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton restoreDefaultsButton;

    private ConfigTableModel configTableModel;
    private ArrayList<ConfigItem> configSnapshot;

    public ConfigView() {
        configSnapshot = new ArrayList<>();
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

        configPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                //record snapshot of table when panel shown
                configSnapshot = Utils.getDeepCopy(((ConfigTableModel) configTable.getModel()).getConfigList());
            }
        });
    }

    public JPanel getConfigPanel() {
        return configPanel;
    }

    private void createUIComponents() {
        configTable = new JTable();

        configTableModel = new ConfigTableModel(Utils.getDeepCopy(IFCommissions.getConfigManager().getItems()));
        configTable.setModel(configTableModel);

        configTable.getColumnModel().getColumn(2).setMaxWidth(100);
    }

    private void saveConfig() {
        IFCommissions.getConfigManager().save(configTableModel.getConfigList());
        IFCommissions.getGui().setSetupView();
    }

    private void cancelConfig() {
        //reset table model to snapshot
        configTableModel.setConfigList(configSnapshot);

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
        String description = (String)JOptionPane.showInputDialog(
                configPanel.getParent(),
                "Part description (exactly as appears in invoice):",
                "New Part",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);
        ((ConfigTableModel) configTable.getModel()).addRow(part, description);
    }

    private void removeRow() {
        ((ConfigTableModel) configTable.getModel()).removeRow(configTable.getSelectedRow());
    }

    private void restoreDefaults() {
        IFCommissions.getConfigManager().restoreDefaults();
        configTableModel.setConfigList(Utils.getDeepCopy(IFCommissions.getConfigManager().getItems()));
    }
}