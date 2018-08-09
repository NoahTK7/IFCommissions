package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SetupView {
    private JPanel setupPanel;
    private JTextField pathTextField;
    private JButton browseButton;
    private JLabel fileFolderLabel;
    private JList filesList;
    private JButton runButton;
    private JButton cancelButton;
    private JCheckBox outputSpreadsheetCheckBox;
    private JCheckBox outputEmployeeSpreadsheetCheckBox;
    private JButton editConfigButton;

    private File currentDirectory;

    private IFCommissions instance;

    public SetupView() {
        instance = IFCommissions.getInstance();

        currentDirectory = new File(".");

        browseButton.addActionListener(e -> {
            selectFile();
        });
        runButton.addActionListener(e -> {
            saveData();
            instance.run();
        });
        cancelButton.addActionListener(e -> {
            IFCommissions.getGui().close();
        });
        editConfigButton.addActionListener(e -> {
            //TODO
        });
    }

    public JPanel getSetupPanel() {
        return setupPanel;
    }

    //adapted from https://stackoverflow.com/questions/32723173/how-to-open-a-file-after-clicking-the-open-button-in-jfilechooser?lq=1
    private void selectFile() {
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // optionally set chooser options ...
        if (chooser.showOpenDialog(setupPanel.getParent()) == JFileChooser.APPROVE_OPTION) {
            File direc = chooser.getSelectedFile();
            try {
                pathTextField.setText(direc.getCanonicalPath());
            } catch (IOException e) {
                //TODO: error
                e.printStackTrace();
            }
            currentDirectory = direc;
            populateFileList();
        }
    }

    private void populateFileList() {
        DefaultListModel listModel = new DefaultListModel();
        if (currentDirectory.listFiles() == null){
            //TODO error: no files
            return;
        }
        for (File file: currentDirectory.listFiles()) {
            if (file.getName().contains(new String(".xlsx"))){
                listModel.addElement(new CheckListItem(file));
            }
        }
        filesList.setModel(listModel);
    }

    private void createUIComponents() {
        filesList = new JList();
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
            }
        });
    }

    private void saveData() {
        //active files
        ArrayList<File> temp = new ArrayList<>();
        for (int i = 0; i < filesList.getModel().getSize(); i++) {
            CheckListItem item = (CheckListItem) filesList.getModel().getElementAt(i);
            if (item.isSelected()){
                temp.add(item.getFile());
            }
        }
        instance.setActiveFiles(temp);
        //spreadsheet
        instance.setSpreadsheet(outputSpreadsheetCheckBox.isSelected());
        //employee spreadsheet
        instance.setEmployeeSpreadsheet(outputEmployeeSpreadsheetCheckBox.isSelected());
        //directory
        instance.setDirectory(currentDirectory);
    }
}