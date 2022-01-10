module com.udacity.security {
    exports com.udacity.security.service;
    exports com.udacity.security.data;
    exports com.udacity.security.application;
    requires com.udacity.image;
    requires java.prefs;
    requires com.google.gson;
    requires com.google.common;
    requires java.desktop;
    requires miglayout;
    opens com.udacity.security.data to com.google.gson;
}
