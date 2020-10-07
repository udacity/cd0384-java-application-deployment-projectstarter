package com.udacity.catpoint.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Image Recognition Service that can identify cats. Requires aws credentials to be entered in config.properties to work
 */
public class ImageService {

    private Logger log = LoggerFactory.getLogger(ImageService.class);

    private RekognitionClient rekognitionClient;

    public ImageService() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            props.load(is);
        } catch (IOException ioe ) {
            log.error("Unable to initialize AWS Rekognition, no properties file found", ioe);
            return;
        }

        String awsId = props.getProperty("aws.id");
        String awsSecret = props.getProperty("aws.secret");
        String awsRegion = props.getProperty("aws.region");

        AwsCredentials awsCredentials = AwsBasicCredentials.create(awsId, awsSecret);
        rekognitionClient = RekognitionClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(awsRegion))
                .build();
    }


    public boolean imageContainsCat(BufferedImage image, float confidenceThreshhold) {
        Image awsImage = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", os);
            awsImage = Image.builder().bytes(SdkBytes.fromByteArray(os.toByteArray())).build();
        } catch (IOException ioe) {
            log.error("Error building image byte array", ioe);
            return false;
        }
        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder().image(awsImage).minConfidence(confidenceThreshhold).build();
        DetectLabelsResponse response = rekognitionClient.detectLabels(detectLabelsRequest);
        return response.labels().stream().filter(l -> l.name().toLowerCase().contains("cat")).findFirst().isPresent();
    }
}
