module com.udacity.image {
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.services.rekognition;
    requires java.desktop;
    requires org.slf4j;
    exports com.udacity.image.service;
}