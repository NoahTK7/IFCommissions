/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;
import com.noahkurrack.IFCommissions.UI.util.OptionsTableModel;
import com.noahkurrack.IFCommissions.data.Contract;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class OptionsView {
    private JPanel optionsPanel;
    private JTable contractTable;
    private JButton continueButton;
    private JButton backButton;

    public OptionsView() {
        attachListeners();
    }

    private void attachListeners() {
        continueButton.setEnabled(false);

        optionsPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                IFCommissions.getInstance().processContracts();
                continueButton.setEnabled(true);
            }
        });
        continueButton.addActionListener(e -> {
            IFCommissions.getGui().setRunView();
        });
        backButton.addActionListener(e -> {
            continueButton.setEnabled(false);
            IFCommissions.getGui().setSetupView();
        });
    }

    public JPanel getOptionsPanel() {
        return optionsPanel;
    }

    public void populateTable(ArrayList<Contract> contracts) {
        ((OptionsTableModel) contractTable.getModel()).reset();
        for (Contract contract : contracts)
        ((OptionsTableModel) contractTable.getModel()).addContract(contract);
    }

    private void createUIComponents() {
        contractTable = new JTable();

        OptionsTableModel model = new OptionsTableModel();
        contractTable.setModel(model);

        contractTable.getColumnModel().getColumn(0).setMinWidth(200);
        contractTable.getColumnModel().getColumn(1).setMaxWidth(60);
        contractTable.getColumnModel().getColumn(5).setMinWidth(125);
    }
}