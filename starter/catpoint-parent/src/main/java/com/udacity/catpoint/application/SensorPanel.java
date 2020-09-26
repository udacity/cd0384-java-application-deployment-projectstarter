package com.udacity.catpoint.application;

import com.udacity.catpoint.data.Sensor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class SensorPanel extends JPanel {

    List<Sensor> sensors;

    public SensorPanel() {
        super();
        setLayout(new MigLayout());



    }
}
