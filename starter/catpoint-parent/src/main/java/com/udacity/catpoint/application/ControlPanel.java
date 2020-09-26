package com.udacity.catpoint.application;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ControlPanel extends JPanel {
    public ControlPanel() {
        super();
        setLayout(new MigLayout());

        JButton armButton = new JButton("Arm");
        JButton disarmButton = new JButton("Disarm");

        add(armButton);
        add(disarmButton);
    }
}
