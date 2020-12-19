package com.udacity.catpoint.application;

import com.udacity.catpoint.data.AlarmStatus;
import com.udacity.catpoint.service.SecurityService;
import com.udacity.catpoint.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Panel containing the 'camera' output. Allows users to 'refresh' the camera
 * by uploading their own picture, and 'scan' the picture, sending it for image analysis
 */
public class ImagePanel extends JPanel implements StatusListener {
    private SecurityService securityService;

    private JLabel cameraHeader;
    private JLabel cameraLabel;
    private BufferedImage currentCameraImage;

    private int IMAGE_WIDTH = 300;
    private int IMAGE_HEIGHT = 225;

    public ImagePanel(SecurityService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = securityService;
        securityService.addStatusListener(this);

        cameraHeader = new JLabel("Camera Feed");
        cameraHeader.setFont(StyleService.HEADING_FONT);

        cameraLabel = new JLabel();
        cameraLabel.setBackground(Color.WHITE);
        cameraLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        cameraLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        //button allowing users to select a file to be the current camera image
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

        //button that sends the image to the image service
        JButton scanPictureButton = new JButton("Scan Picture");
        scanPictureButton.addActionListener(e -> {
            securityService.processImage(currentCameraImage);
        });

        add(cameraHeader, "span 3, wrap");
        add(cameraLabel, "span 3, wrap");
        add(addPictureButton);
        add(scanPictureButton);
    }

    @Override
    public void notify(AlarmStatus status) {
        //no behavior necessary
    }

    @Override
    public void catDetected(boolean catDetected) {
        if(catDetected) {
            cameraHeader.setText("DANGER - CAT DETECTED");
        } else {
            cameraHeader.setText("Camera Feed - No Cats Detected");
        }
    }

    @Override
    public void sensorStatusChanged() {
        //no behavior necessary
    }
}
