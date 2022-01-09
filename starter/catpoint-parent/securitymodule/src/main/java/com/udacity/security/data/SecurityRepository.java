package com.udacity.security.data;

import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * Interface showing the methods our security repository will need to support
 */
public interface SecurityRepository {
    void addSensor(Sensor sensor);
    void removeSensor(Sensor sensor);
    void updateSensor(Sensor sensor);
    void setAlarmStatus(AlarmStatus alarmStatus);
    void setArmingStatus(ArmingStatus armingStatus);
    Set<Sensor> getSensors();
    AlarmStatus getAlarmStatus();
    ArmingStatus getArmingStatus();
    void setCurrentImage(BufferedImage image);
    BufferedImage getCurrentImage();

}
