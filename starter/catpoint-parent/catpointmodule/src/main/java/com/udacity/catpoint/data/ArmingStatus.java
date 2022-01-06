package com.udacity.catpoint.data;

import java.awt.*;

/**
 * List of potential states the security system can use to describe how the system is armed.
 * Also contains metadata about what text and color is associated with the arming status.
 */
public enum ArmingStatus {
    DISARMED("Disarmed", new Color(120,200,30)),
    ARMED_HOME("Armed - At Home", new Color(190,180,50)),
    ARMED_AWAY("Armed - Away", new Color(170,30,150));

    private final String description;
    private final Color color;

    ArmingStatus(String description, Color color) {
        this.description = description;
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }
}
