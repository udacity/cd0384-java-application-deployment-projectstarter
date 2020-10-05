package com.udacity.catpoint.application;

import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DisplayPanel extends JPanel {
    JLabel currentStatusLabel = new JLabel();
    JLabel cameraLabel = new JLabel();

    private int IMAGE_WIDTH = 300;
    private int IMAGE_HEIGHT = 200;
    public DisplayPanel() {
        super();
        setLayout(new MigLayout());

        JLabel panelLabel = new JLabel("Very Secure Home Security");
        panelLabel.setFont(StyleService.HEADING_FONT);

        JLabel systemStatusLabel = new JLabel("System Status");
        cameraLabel.setBackground(Color.WHITE);
        cameraLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        cameraLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JButton pictureButton = new JButton("Refresh Camera");
        pictureButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Select Picture");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if(chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                Image tmp = new ImageIcon(ImageIO.read(chooser.getSelectedFile())).getImage();
                cameraLabel.setIcon(new ImageIcon(tmp.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
            } catch (IOException|NullPointerException ioe) {
                JOptionPane.showMessageDialog(null, "Invalid image selected.");
            }
            repaint();
        });

        add(panelLabel, "wrap");
        add(systemStatusLabel);
        add(currentStatusLabel, "wrap");
        add(cameraLabel);
        add(pictureButton);

    }
}
