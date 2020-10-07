package com.udacity.catpoint.application;

import com.udacity.catpoint.service.ImageService;
import com.udacity.catpoint.service.SecurityService;
import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private ImageService imageService;
    private SecurityService securityService;

    private JLabel cameraHeader;
    private JLabel cameraLabel;
    private BufferedImage currentCameraImage;

    private int IMAGE_WIDTH = 300;
    private int IMAGE_HEIGHT = 225;

    public ImagePanel(ImageService imageService, SecurityService securityService) {
        super();
        setLayout(new MigLayout());
        this.imageService = imageService;
        this.securityService = securityService;

        cameraHeader = new JLabel("Camera Feed");
        cameraHeader.setFont(StyleService.HEADING_FONT);

        cameraLabel = new JLabel();
        cameraLabel.setBackground(Color.WHITE);
        cameraLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        cameraLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JButton addPictureButton = new JButton("Refresh Camera");
        addPictureButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Select Picture");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if(chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            try {
                currentCameraImage = ImageIO.read(chooser.getSelectedFile());
                Image tmp = new ImageIcon(currentCameraImage).getImage();
                cameraLabel.setIcon(new ImageIcon(tmp.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
            } catch (IOException |NullPointerException ioe) {
                JOptionPane.showMessageDialog(null, "Invalid image selected.");
            }
            repaint();
        });

        JButton scanPictureButton = new JButton("Scan Picture");
        scanPictureButton.addActionListener(e -> {
            if(imageService.imageContainsCat(currentCameraImage, 50.0f)) {
                cameraHeader.setText("DANGER - CAT DETECTED");
            } else {
                cameraHeader.setText("Camera Feed - No Cats Detected");
            }
        });

        add(cameraHeader, "span 3, wrap");
        add(cameraLabel, "span 3, wrap");
        add(addPictureButton);
        add(scanPictureButton);

    }


}
