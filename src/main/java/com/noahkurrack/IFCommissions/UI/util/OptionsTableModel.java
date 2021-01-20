/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI.util;

import com.noahkurrack.IFCommissions.data.Contract;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class OptionsTableModel extends AbstractTableModel {

    private final ArrayList<Contract> contractList;

    private final String[] columnNames = new String[] {
            "Customer Info", "Subtotal", "Add Percentage", "Flat 10% (subtotal)", "Flat 12% (profit)", "Manual Cost Adjustment"
    };
    private final Class[] columnClass = new Class[] {
            String.class, Double.class, Double.class, Boolean.class, Boolean.class, Double.class
    };

    public OptionsTableModel() {
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
                return row.getSubtotal();
            case 2:
                return row.getAddPercentage();
            case 3:
                return row.getIsFlat10();
            case 4:
                return row.getIsFlat12();
            case 5:
                return row.getManualCostAdjustment();
            default: return null;

        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex >= 2);
    }

    public void reset() {
        contractList.clear();
        this.fireTableDataChanged();
    }

    public void addContract(Contract contract) {
        contractList.add(contract);
        this.fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //TODO: validate data
        Contract row = contractList.get(rowIndex);
        if(columnIndex == 2) {
            row.setAddPercentage((double) aValue);
        } else if (columnIndex == 3) {
            boolean val = (boolean) aValue;
            if (val) {
                this.setValueAt(false, rowIndex, 4);
                this.fireTableDataChanged();
            }
            row.setIsFlat10(val);
        } else if (columnIndex == 4) {
            boolean val = (boolean) aValue;
            if (val) {
                this.setValueAt(false, rowIndex, 3);
                this.fireTableDataChanged();
            }
            row.setIsFlat12(val);
        } else if (columnIndex == 5) {
            row.setManualCostAdjustment((double) aValue);
        }
    }
}