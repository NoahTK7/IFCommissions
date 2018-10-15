/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;
import com.noahkurrack.IFCommissions.UI.util.CheckListItem;
import com.noahkurrack.IFCommissions.UI.util.CheckListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SetupView {
    private JPanel setupPanel;
    private JTextField inPathTextField;
    private JButton browseButton;
    private JList<CheckListItem> filesList;
    private JButton runButton;
    private JButton cancelButton;
    private JCheckBox outputSpreadsheetCheckBox;
    private JCheckBox outputEmployeeSpreadsheetCheckBox;
    private JButton editConfigButton;
    private JTextField outPathTextField;
    private JButton browseOutButton;
    private JLabel errorLabel;

    private File currentDirectory;
    private File outDirectory;

    private IFCommissions instance;

    public SetupView() {
        instance = IFCommissions.getInstance();

        currentDirectory = new File(".");

        attachListeners();
    }

    public JPanel getSetupPanel() {
        return setupPanel;
    }

    private void attachListeners() {
        browseButton.addActionListener(e -> {
            selectInputFolder();
        });
        browseOutButton.addActionListener(e -> {
            selectOutputFolder();
        });
        runButton.addActionListener(e -> {
            if (saveData()) {
                IFCommissions.getGui().setOptionsView();
            }
        });
        cancelButton.addActionListener(e -> {
            IFCommissions.getGui().close();
        });
        editConfigButton.addActionListener(e -> {
            IFCommissions.getGui().setConfigView();
        });
    }

    //adapted from https://stackoverflow.com/questions/32723173/how-to-open-a-file-after-clicking-the-open-button-in-jfilechooser?lq=1
    private void selectInputFolder() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(setupPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
            File directory = chooser.getSelectedFile();
            try {
                inPathTextField.setText(directory.getCanonicalPath());
            } catch (IOException e) {
                //error (ignore?)
                e.printStackTrace();
            }
            currentDirectory = directory;
            populateFileList();
        }
    }

    private void selectOutputFolder() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(setupPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
            File directory = chooser.getSelectedFile();
            try {
                outPathTextField.setText(directory.getCanonicalPath());
            } catch (IOException e) {
                //error (ignore?)
                e.printStackTrace();
            }
            outDirectory = directory;
        }
    }

    private void populateFileList() {
        DefaultListModel<CheckListItem> listModel = new DefaultListModel<>();
        if (currentDirectory.listFiles() != null){
            listModel.add(0, new CheckListItem());
            for (File file: currentDirectory.listFiles()) {
                if (file.getName().contains(".xlsx")){
                    listModel.addElement(new CheckListItem(file));
                }
            }
        } else {
            //ignore
        }

        filesList.setModel(listModel);
    }

    //custom creation of list component to support check box selection
    private void createUIComponents() {
        filesList = new JList<CheckListItem>();
        filesList.setCellRenderer(new CheckListRenderer());
        filesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filesList.addMouseListener(new MouseAdapter() {
            //http://www.java2s.com/Tutorials/Java/Swing_How_to/JList/Create_JList_of_CheckBox.htm
            @Override
            public void mouseClicked(MouseEvent event) {
                JList list = (JList) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(index, index));
                //logic for select all item at top (algorithmic thinking)
                if (index == 0) {
                    for (int i = 1; i < list.getModel().getSize(); i++) {
                        CheckListItem item1 = (CheckListItem) list.getModel().getElementAt(i);
                        item1.setSelected(item.isSelected());
                        list.repaint(list.getCellBounds(i, i));
                    }
                } else {
                    if (!item.isSelected()) {
                        CheckListItem item1 = (CheckListItem) list.getModel().getElementAt(0);
                        item1.setSelected(false);
                        list.repaint(list.getCellBounds(0, 0));
                    } else {
                        boolean allSelected = true;
                        for (int i = 1; i < list.getModel().getSize(); i++) {
                            CheckListItem item2 = (CheckListItem) list.getModel().getElementAt(i);
                            if (!item2.isSelected()) allSelected = false;
                        }
                        if (allSelected) {
                            CheckListItem item1 = (CheckListItem) list.getModel().getElementAt(0);
                            item1.setSelected(true);
                            list.repaint(list.getCellBounds(0, 0));
                        }
                    }
                }
            }
        });

        inPathTextField = new JTextField();
        inPathTextField.setEditable(false);

        outPathTextField = new JTextField();
        outPathTextField.setEditable(false);

        errorLabel = new JLabel();
        errorLabel.setFont(new Font(errorLabel.getFont().getFontName(), errorLabel.getFont().getStyle(), 11));
        errorLabel.setForeground(Color.RED);
    }

    private boolean saveData() {
        //active files
        ArrayList<File> temp = new ArrayList<>();
        for (int i = 1; i < filesList.getModel().getSize(); i++) {
            CheckListItem item = filesList.getModel().getElementAt(i);
            if (item.isSelected()){
                temp.add(item.getFile());
            }
        }
        //validation
        boolean valid = true;
        if (temp.size() < 1) {
            valid = false;
            errorLabel.setText("Please select at least one invoice.");
        }

        boolean spreadsheet = outputSpreadsheetCheckBox.isSelected();
        boolean employeeSpreadsheet = outputEmployeeSpreadsheetCheckBox.isSelected();
        if (outDirectory == null && (spreadsheet || employeeSpreadsheet)) {
            valid = false;
            errorLabel.setText("Please select an output location.");
        }

        if (valid) {
            instance.setSpreadsheet(spreadsheet);
            instance.setEmployeeSpreadsheet(employeeSpreadsheet);
            instance.setActiveFiles(temp);
            instance.setOutputDirectory(outDirectory);
        }

        return valid;
    }
}