package com.noahkurrack.IFCommissions.UI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CommissionsGUI extends JFrame {

    private SetupView setupView;
    private ConfigView configView;
    private RunView runView;

    private int width = 650;
    private int height = 400;

    public CommissionsGUI() {
        setupView = new SetupView();
        configView = new ConfigView();
        runView = new RunView();
        init();
    }

    private void init() {
        setTitle("Invisible Fence Commissions Calculator");
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(setupView.getSetupPanel());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("IFCommissions: exiting!");
            }
        });
    }

    /*
    public void setConfigView() {
        this.removeAll();
        this.setContentPane(this.configView.getConfigPanel());
        this.revalidate();
        this.setVisible(true);
    }

    public void setRunView() {
        this.getContentPane().removeAll();
        this.getContentPane().add(this.runView.getRunPanel());
    }

    public void setSetupView() {
        this.getContentPane().removeAll();
        this.getContentPane().add(this.setupView.getSetupPanel());
    }
    */

    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}