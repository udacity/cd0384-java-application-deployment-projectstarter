package com.udacity.catpoint.application;

import com.udacity.catpoint.data.Sensor;
import com.udacity.catpoint.data.SensorType;
import com.udacity.catpoint.service.SecurityService;
import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class SensorPanel extends JPanel {

    private SecurityService securityService;

    Logger log = LoggerFactory.getLogger(SensorPanel.class);

    JLabel panelLabel = new JLabel("Sensor Management");

    JLabel newSensorName = new JLabel("Name:");
    JLabel newSensorType = new JLabel("Sensor Type:");
    JTextField newSensorNameField = new JTextField();
    JComboBox newSensorTypeDropdown = new JComboBox(SensorType.values());
    JButton addNewSensorButton = new JButton("Add New Sensor");

    public SensorPanel(SecurityService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = securityService;

        panelLabel.setFont(StyleService.HEADING_FONT);
        addNewSensorButton.addActionListener(e ->
                addSensor(new Sensor(
                        newSensorNameField.getText(),
                        SensorType.valueOf(newSensorTypeDropdown.getSelectedItem().toString()))));


        updateLayout();
    }

    private void updateLayout() {
        removeAll();
        add(panelLabel, "wrap");
        for(Sensor s : securityService.getSensors()) {
            JLabel sensorLabel = new JLabel(String.format("%s(%s): %s", s.getSensorType().toString(), s.getName(), (s.getActive() ? "active" : "inactive")));
            JButton sensorToggleButton = new JButton((s.getActive() ? "deactivate" : "activate"));
            JButton sensorRemoveButton = new JButton("Remove Sensor");

            sensorToggleButton.addActionListener(e -> setSensorActivity(s, !s.getActive()) );
            sensorRemoveButton.addActionListener(e -> removeSensor(s));

            add(sensorLabel);
            add(sensorToggleButton);
            add(sensorRemoveButton, "wrap");
        }
        add(newSensorName);
        add(newSensorNameField, "width 50:100:200");
        add(newSensorType);
        add(newSensorTypeDropdown, "wrap");
        add(addNewSensorButton);

        repaint();
        revalidate();
    }

    private void setSensorActivity(Sensor sensor, Boolean isActive) {
        sensor.setActive(isActive);
        updateLayout();
    }


    public void addSensor(Sensor sensor) {
        securityService.addSensor(sensor);
        updateLayout();
    }

    public void removeSensor(Sensor sensor) {
        securityService.removeSensor(sensor);
        updateLayout();
    }
}
