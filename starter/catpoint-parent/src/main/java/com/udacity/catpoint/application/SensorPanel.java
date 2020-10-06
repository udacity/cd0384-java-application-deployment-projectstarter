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

    JPanel sensorListPanel = new JPanel();
    JPanel newSensorPanel;

    public SensorPanel(SecurityService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = securityService;

        panelLabel.setFont(StyleService.HEADING_FONT);
        addNewSensorButton.addActionListener(e ->
                addSensor(new Sensor(
                        newSensorNameField.getText(),
                        SensorType.valueOf(newSensorTypeDropdown.getSelectedItem().toString()))));

        newSensorPanel = buildAddSensorPanel();
        sensorListPanel = new JPanel();
        sensorListPanel.setLayout(new MigLayout());

        updateSensorList(sensorListPanel);

        add(panelLabel, "wrap");
        add(newSensorPanel, "span");
        add(sensorListPanel, "span");
    }

    private JPanel buildAddSensorPanel() {
        JPanel p = new JPanel();
        p.setLayout(new MigLayout());
        p.add(newSensorName);
        p.add(newSensorNameField, "width 50:100:200");
        p.add(newSensorType);
        p.add(newSensorTypeDropdown, "wrap");
        p.add(addNewSensorButton, "span 3");
        return p;
    }

    private void updateSensorList(JPanel p) {
        p.removeAll();
        for(Sensor s : securityService.getSensors()) {
            JLabel sensorLabel = new JLabel(String.format("%s(%s): %s", s.getName(),  s.getSensorType().toString(),(s.getActive() ? "Active" : "Inactive")));
            JButton sensorToggleButton = new JButton((s.getActive() ? "Deactivate" : "Activate"));
            JButton sensorRemoveButton = new JButton("Remove Sensor");

            sensorToggleButton.addActionListener(e -> setSensorActivity(s, !s.getActive()) );
            sensorRemoveButton.addActionListener(e -> removeSensor(s));

            p.add(sensorLabel, "width 300:300:300");
            p.add(sensorToggleButton, "width 100:100:100");
            p.add(sensorRemoveButton, "wrap");
        }

        repaint();
        revalidate();
    }

    private void setSensorActivity(Sensor sensor, Boolean isActive) {
        sensor.setActive(isActive);
        updateSensorList(sensorListPanel);
    }


    public void addSensor(Sensor sensor) {
        securityService.addSensor(sensor);
        updateSensorList(sensorListPanel);
    }

    public void removeSensor(Sensor sensor) {
        securityService.removeSensor(sensor);
        updateSensorList(sensorListPanel);
    }
}
