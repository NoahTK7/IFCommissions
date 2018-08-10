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

    private int width = 650;
    private int height = 400;

    public CommissionsGUI() {
        cards = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cards.getLayout();

        setupView = new SetupView();
        configView = new ConfigView();
        runView = new RunView();

        cards.add(setupView.getSetupPanel(), "setup");
        cards.add(configView.getConfigPanel(), "config");
        cards.add(runView.getRunPanel(), "run");

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
}