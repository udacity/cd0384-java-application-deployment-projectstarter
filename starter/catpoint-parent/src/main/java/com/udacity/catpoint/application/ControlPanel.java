package com.udacity.catpoint.application;

import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ControlPanel extends JPanel {

    JLabel panelLabel = new JLabel("System Control");

    JButton armButton = new JButton("Arm");
    JButton disarmButton = new JButton("Disarm");


    public ControlPanel() {
        super();
        setLayout(new MigLayout());

        panelLabel.setFont(StyleService.HEADING_FONT);
        add(panelLabel, "wrap");

        add(armButton);
        add(disarmButton);
    }
}
