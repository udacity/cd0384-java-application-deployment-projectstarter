package com.udacity.catpoint.application;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class DisplayPanel extends JPanel {
    public DisplayPanel() {
        super();
        setLayout(new MigLayout());

        JLabel systemStatusLabel = new JLabel("System Status");
        JLabel cameraLabel = new JLabel("camera");

        add(systemStatusLabel);
        add(cameraLabel);

    }
}
