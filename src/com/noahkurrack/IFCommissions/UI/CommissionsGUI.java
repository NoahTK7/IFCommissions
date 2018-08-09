package com.noahkurrack.IFCommissions.UI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CommissionsGUI extends JFrame {

    SetupView setupView;

    public CommissionsGUI() {
        setupView = new SetupView();
        init();
    }

    private void init() {
        setTitle("IFCommissions UI");
        setSize(650,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(setupView.getSetupPanel());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("IFCommissions: exiting!");
            }
        });
    }

    public SetupView getSetupView() {
        return setupView;
    }

    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}