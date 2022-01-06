package com.udacity.catpoint.data;

import java.awt.*;

/**
 * List of potential states the alarm can have. Also contains metadata about what
 * text and color is associated with the alarm.
 */
public enum AlarmStatus {
    NO_ALARM("Cool and Good", new Color(120,200,30)),
    PENDING_ALARM("I'm in Danger...", new Color(200,150,20)),
    ALARM("Awooga!", new Color(250,80,50));

    private final String description;
    private final Color color;

    AlarmStatus(String description, Color color) {
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
