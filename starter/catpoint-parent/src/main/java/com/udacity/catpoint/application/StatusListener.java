package com.udacity.catpoint.application;

import com.udacity.catpoint.data.AlarmStatus;

/**
 * Identifies a component that should be notified whenever the system alarm status changes.
 */
public interface StatusListener {
    void notify(AlarmStatus status);
}
