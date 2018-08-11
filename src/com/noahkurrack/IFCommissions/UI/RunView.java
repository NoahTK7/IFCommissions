package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.IFCommissions;
import com.noahkurrack.IFCommissions.data.Contract;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class RunView {
    private JPanel runPanel;
    private JProgressBar progressBar1;
    private JTable table1;
    private JButton finishButton;
    private JLabel progressLabel;
    private JLabel outputFilesLabel;

    private ArrayList<String> fileNames;

    private int currentNum;
    private int totalNum;

    public RunView() {
        attachListeners();

        currentNum = 0;
        totalNum = 0;
    }

    public JPanel getRunPanel() {
        return runPanel;
    }

    private void attachListeners() {
        runPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                IFCommissions.getInstance().run();
            }
        });
        finishButton.addActionListener(e -> {
            IFCommissions.getGui().close();
        });
    }

    public void submit(Contract contract) {
        currentNum++;

        paint();

        // TODO: display in table
    }

    public void setStats(ArrayList<String> fileNames, int contracts) {
        this.fileNames = fileNames;
        this.totalNum = contracts;

        StringBuilder filesText = new StringBuilder();
        if (fileNames.size()>1) {
            filesText.append(fileNames.get(0));
            for (int i = 1; i < fileNames.size(); i++) {
                filesText.append(", ").append(fileNames.get(i));
            }
        } else if (fileNames.size()==1) {
            filesText.append(fileNames.get(0));
        } else {
            filesText.append("none");
        }

        outputFilesLabel.setText(filesText.toString());

        progressBar1.setMinimum(0);
        progressBar1.setMaximum(totalNum);
    }

    private void paint() {
        //label
        progressLabel.setText(currentNum + " of " + totalNum);
        //progress bar
        progressBar1.setValue(currentNum);
    }
}