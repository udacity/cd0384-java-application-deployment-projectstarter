package com.udacity.catpoint.application;

import com.udacity.catpoint.data.AlarmStatus;
import com.udacity.catpoint.service.SecurityService;
import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

/**
 * Displays the current status of the system. Implements the StatusListener
 * interface so that it can be notified whenever the status changes.
 */
public class DisplayPanel extends JPanel implements StatusListener {

    private JLabel currentStatusLabel;

    public DisplayPanel(SecurityService securityService) {
        super();
        setLayout(new MigLayout());

        securityService.addStatusListener(this);

        JLabel panelLabel = new JLabel("Very Secure Home Security");
        JLabel systemStatusLabel = new JLabel("System Status:");
        currentStatusLabel = new JLabel();

        panelLabel.setFont(StyleService.HEADING_FONT);

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

    @Override
    public void catDetected(boolean catDetected) {
        // no behavior necessary
    }

    @Override
    public void sensorStatusChanged() {
        // no behavior necessary
    }
}
