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
    private JButton cancelButton;

    public OptionsView() {
        attachListeners();
    }

    private void attachListeners() {
        optionsPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                IFCommissions.getInstance().processContracts();
            }
        });
        continueButton.addActionListener(e -> {
            IFCommissions.getGui().setRunView();
        });
        cancelButton.addActionListener(e -> {
            IFCommissions.getGui().close();
        });
    }

    public JPanel getOptionsPanel() {
        return optionsPanel;
    }

    public void populateTable(ArrayList<Contract> contracts) {
        for (Contract contract : contracts)
        ((OptionsTableModel) contractTable.getModel()).addContract(contract);
    }

    private void createUIComponents() {
        contractTable = new JTable();

        OptionsTableModel model = new OptionsTableModel();
        contractTable.setModel(model);

        contractTable.getColumnModel().getColumn(0).setMinWidth(200);
    }
}