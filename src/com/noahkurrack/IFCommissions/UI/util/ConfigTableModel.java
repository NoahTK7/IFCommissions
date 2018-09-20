package com.noahkurrack.IFCommissions.UI.util;

import com.noahkurrack.IFCommissions.data.ConfigItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

// adapted from http://www.codejava.net/java-se/swing/editable-jtable-example
public class ConfigTableModel extends AbstractTableModel {

    private final ArrayList<ConfigItem> configList;

    private final String[] columnNames = new String[] {
            "Part", "Cost"
    };
    private final Class[] columnClass = new Class[] {
            String.class, Long.class
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
            return row.getCost();
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ConfigItem row = configList.get(rowIndex);
        if(1 == columnIndex) {
            row.setCost((Long) aValue);
        }
    }
}