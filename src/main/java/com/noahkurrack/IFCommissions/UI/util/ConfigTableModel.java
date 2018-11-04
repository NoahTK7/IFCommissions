/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI.util;

import com.noahkurrack.IFCommissions.data.ConfigItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

// adapted from http://www.codejava.net/java-se/swing/editable-jtable-example
public class ConfigTableModel extends AbstractTableModel {

    private ArrayList<ConfigItem> configList;

    private final String[] columnNames = new String[] {
            "Part", "Description" ,"Cost"
    };
    private final Class[] columnClass = new Class[] {
            String.class, String.class, Double.class
    };

    public ConfigTableModel(ArrayList<ConfigItem> configList) {
        this.configList = configList;
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
        return configList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConfigItem row = configList.get(rowIndex);
        if(0 == columnIndex) {
            return row.getPart();
        }
        else if(1 == columnIndex) {
            return row.getDescription();
        }
        else if(2 == columnIndex) {
            return row.getCost();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ConfigItem row = configList.get(rowIndex);
        if(2 == columnIndex) {
            row.setCost((Double) aValue);
        }
    }

    public void addRow(String part, String description) {
        configList.add(new ConfigItem(part, description, 0));
        this.fireTableDataChanged();
    }

    public void removeRow(int index) {
        configList.remove(index);
        this.fireTableDataChanged();
    }

    public ArrayList<ConfigItem> getConfigList() {
        return configList;
    }

    public void setConfigList(ArrayList<ConfigItem> configList) {
        this.configList = configList;
        this.fireTableDataChanged();
    }
}