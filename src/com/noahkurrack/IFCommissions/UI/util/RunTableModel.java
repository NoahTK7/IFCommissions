/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI.util;

import com.noahkurrack.IFCommissions.data.Contract;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class RunTableModel extends AbstractTableModel {

    private final ArrayList<Contract> contractList;

    private final String[] columnNames = new String[] {
            "Customer Info", "Sales Rep", "Subtotal", "Profit", "Commission %", "Commission"
    };
    private final Class[] columnClass = new Class[] {
            String.class, String.class, Double.class, Double.class, Double.class, Double.class
    };

    public RunTableModel(ArrayList<Contract> contracts) {
        this.contractList = contracts;
    }

    public RunTableModel() {
        this.contractList = new ArrayList<>();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return contractList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contract row = contractList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getCustomerInfo();
            case 1:
                return row.getSalesRep();
            case 2:
                return row.getSubtotal();
            case 3:
                return row.getProfit();
            case 4:
                return row.getCommissionPercent();
            case 5:
                return row.getCommission();
            default: return null;

        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public ArrayList<Contract> getContractList() {
        return contractList;
    }

    public void addContract(Contract contract) {
        contractList.add(contract);
        this.fireTableDataChanged();
    }
}
