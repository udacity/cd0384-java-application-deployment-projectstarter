package com.udacity.catpoint.application;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CatpointGui extends JFrame {
    private DisplayPanel displayPanel = new DisplayPanel();
    private ControlPanel controlPanel = new ControlPanel();

    public CatpointGui() {
        setLocation(100, 100);
        setSize(400, 600);
        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());
        mainPanel.add(displayPanel, "width 100%, height 50%, wrap");
        mainPanel.add(controlPanel, "width 100%");

        getContentPane().add(mainPanel);

    }
}
