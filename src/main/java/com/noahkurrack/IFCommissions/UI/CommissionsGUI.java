/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CommissionsGUI extends JFrame {

    private JPanel cards;
    private CardLayout cardLayout;

    private SetupView setupView;
    private ConfigView configView;
    private RunView runView;
    private OptionsView optionsView;

    private static final int width = 900;
    private static final int height = 550;

    public CommissionsGUI() {
        cards = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cards.getLayout();

        setupView = new SetupView();
        configView = new ConfigView();
        runView = new RunView();
        optionsView = new OptionsView();

        cards.add(setupView.getSetupPanel(), "setup");
        cards.add(configView.getConfigPanel(), "config");
        cards.add(runView.getRunPanel(), "run");
        cards.add(optionsView.getOptionsPanel(), "options");

        init();
    }

    private void init() {
        setTitle("Invisible Fence Commissions Calculator");
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setContentPane(cards);

        cardLayout.show(cards, "setup");

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("IFCommissions: exiting!");
            }
        });
    }

    public void setConfigView() {
        cardLayout.show(cards, "config");
    }

    public void setRunView() {
        cardLayout.show(cards, "run");
    }

    public void setSetupView() {
        cardLayout.show(cards, "setup");
    }

    public void setOptionsView() {
        cardLayout.show(cards, "options");
    }

    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public SetupView getSetupView() {
        return setupView;
    }

    public ConfigView getConfigView() {
        return configView;
    }

    public RunView getRunView() {
        return runView;
    }

    public OptionsView getOptionsView() {
        return optionsView;
    }
}