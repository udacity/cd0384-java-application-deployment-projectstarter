package com.udacity.catpoint.application;

import com.udacity.catpoint.data.AlarmStatus;
import com.udacity.catpoint.service.SecurityService;
import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class DisplayPanel extends JPanel implements StatusListener {

    private SecurityService securityService;

    private JLabel currentStatusLabel;

    public DisplayPanel(SecurityService securityService) {
        super();
        setLayout(new MigLayout());

        this.securityService = securityService;

        JLabel panelLabel = new JLabel("Very Secure Home Security");
        panelLabel.setFont(StyleService.HEADING_FONT);

        JLabel systemStatusLabel = new JLabel("System Status:");
        currentStatusLabel = new JLabel();

        notify(securityService.getAlarmStatus());

        add(panelLabel, "span 2, wrap");
        add(systemStatusLabel);
        add(currentStatusLabel, "wrap");

    }

    @Override
    public void notify(AlarmStatus status) {
        currentStatusLabel.setText(status.getDescription());
        currentStatusLabel.setBackground(status.getColor());
        currentStatusLabel.setOpaque(true);
    }
}
