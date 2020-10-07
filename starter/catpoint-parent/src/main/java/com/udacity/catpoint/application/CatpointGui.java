package com.udacity.catpoint.application;

import com.udacity.catpoint.data.PretendDatabaseSecurityRepositoryImpl;
import com.udacity.catpoint.data.SecurityRepository;
import com.udacity.catpoint.service.ImageService;
import com.udacity.catpoint.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CatpointGui extends JFrame {
    private SecurityRepository securityRepository = new PretendDatabaseSecurityRepositoryImpl();
    private SecurityService securityService = new SecurityService(securityRepository);
    private ImageService imageService = new ImageService();
    private DisplayPanel displayPanel = new DisplayPanel(securityService);
    private ControlPanel controlPanel = new ControlPanel(securityService);
    private SensorPanel sensorPanel = new SensorPanel(securityService);
    private ImagePanel imagePanel = new ImagePanel(imageService, securityService);

    public CatpointGui() {
        setLocation(100, 100);
        setSize(600, 850);
        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());
        mainPanel.add(displayPanel, "wrap");
        mainPanel.add(imagePanel, "wrap");
        mainPanel.add(controlPanel, "wrap");
        mainPanel.add(sensorPanel);

        getContentPane().add(mainPanel);

    }
}
