package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;

import javax.swing.*;
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
            saveData();
            IFCommissions.getGui().setRunView();
            instance.run();
        });
        cancelButton.addActionListener(e -> {
            IFCommissions.getGui().close();
        });
        editConfigButton.addActionListener(e -> {
            IFCommissions.getGui().setConfigView();
            //TODO
        });
    }

    //adapted from https://stackoverflow.com/questions/32723173/how-to-open-a-file-after-clicking-the-open-button-in-jfilechooser?lq=1
    private void selectInputFolder() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(setupPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
            File direc = chooser.getSelectedFile();
            try {
                inPathTextField.setText(direc.getCanonicalPath());
            } catch (IOException e) {
                //TODO: error
                e.printStackTrace();
            }
            currentDirectory = direc;
            populateFileList();
        }
    }

    private void selectOutputFolder() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(setupPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
            File direc = chooser.getSelectedFile();
            try {
                outPathTextField.setText(direc.getCanonicalPath());
            } catch (IOException e) {
                //TODO: error
                e.printStackTrace();
            }
            outDirectory = direc;
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
            //TODO: error no files "please select at least one file"
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
                int index = list.locationToIndex(event.getPoint());// Get index of item clicked
                CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                item.setSelected(!item.isSelected()); // Toggle selected state
                list.repaint(list.getCellBounds(index, index));// Repaint cell
                //logic for select all item at top
                if (index == 0) {
                    for (int i = 1; i < list.getModel().getSize(); i++) {
                        CheckListItem item1 = (CheckListItem) list.getModel().getElementAt(i);
                        item1.setSelected(item.isSelected()); // Toggle selected state
                        list.repaint(list.getCellBounds(i, i));// Repaint cell
                    }
                } else {
                    if (!item.isSelected()) {
                        CheckListItem item1 = (CheckListItem) list.getModel().getElementAt(0);
                        item1.setSelected(false);
                        list.repaint(list.getCellBounds(0, 0));// Repaint cell
                    }
                }
            }
        });
    }

    private void saveData() {
        //active files
        ArrayList<File> temp = new ArrayList<>();
        for (int i = 1; i < filesList.getModel().getSize(); i++) {
            CheckListItem item = filesList.getModel().getElementAt(i);
            if (item.isSelected()){
                temp.add(item.getFile());
            }
        }
        //TODO: error no files "please select at least one file"
        instance.setActiveFiles(temp);
        //spreadsheet
        instance.setSpreadsheet(outputSpreadsheetCheckBox.isSelected());
        //employee spreadsheet
        instance.setEmployeeSpreadsheet(outputEmployeeSpreadsheetCheckBox.isSelected());
        //directory
        //TODO: check if exists. if not, make dir. handle errors
        instance.setOutputDirectory(outDirectory);
    }
}
//TODO: default output directory
//TODO: validate info before proceeding with run