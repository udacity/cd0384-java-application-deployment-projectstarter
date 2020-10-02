package com.udacity.catpoint.data;

import java.util.Set;

public interface SecurityRepository {
    void setArmed(Boolean isArmed);
    void addSensor(Sensor sensor);
    void removeSensor(Sensor sensor);
    void updateSensor(Sensor sensor);
    void setAlarmStatus(AlarmStatus alarmStatus);
    Boolean isArmed();
    Set<Sensor> getSensors();
    AlarmStatus getAlarmStatus();


}
