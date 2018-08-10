package com.noahkurrack.IFCommissions.UI;

import com.noahkurrack.IFCommissions.data.Contract;

import javax.swing.*;

public class RunView {
    private JPanel runPanel;
    private JProgressBar progressBar1;
    private JTable table1;
    private JButton finishButton;
    private JLabel progressLabel;
    private JLabel outputFilesLabel;

    public RunView() {

    }

    public JPanel getRunPanel() {
        return runPanel;
    }

    public void increaseProgress() {
        //TODO
    }

    public void submit(Contract contract) {
        // TODO
        // add to array
        // display
    }

    public void displayOutputFiles(String[] fileNames) {
        // TODO
    }
}