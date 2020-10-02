package com.udacity.catpoint.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

/**
 * Fake repository implementation for demo purposes only. Stores state information in local
 * memory and writes it to user preferences between app loads. Not a recommended storage solution!
 */
public class PretendDatabaseSecurityRepositoryImpl implements SecurityRepository{

    private Boolean armed;
    private Set<Sensor> sensors;
    private AlarmStatus alarmStatus;

    private static final String ARMED = "ARMED";
    private static final String SENSORS = "SENSORS";
    private static final String ALARM_STATUS = "ALARM_STATUS";

    private static final Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepositoryImpl.class);
    private static final Gson gson = new Gson();

    public PretendDatabaseSecurityRepositoryImpl() {
        armed = prefs.getBoolean(ARMED, Boolean.FALSE);
        alarmStatus = AlarmStatus.valueOf(prefs.get(ALARM_STATUS, AlarmStatus.NO_ALARM.toString()));

        String sensorString = prefs.get(SENSORS, null);
        if(sensorString == null) {
            sensors = new TreeSet<>();
        } else {
            Type type = new TypeToken<Set<Sensor>>() {
            }.getType();
            sensors = gson.fromJson(sensorString, type);
        }
    }

    @Override
    public void setArmed(Boolean isArmed) {
        this.armed = isArmed;
        prefs.putBoolean(ARMED, this.armed);
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public void updateSensor(Sensor sensor) {
        sensors.remove(sensor);
        sensors.add(sensor);
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public void setAlarmStatus(AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
        prefs.put(ALARM_STATUS, this.alarmStatus.toString());
    }

    @Override
    public Set<Sensor> getSensors() {
        return sensors;
    }

    @Override
    public AlarmStatus getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    public Boolean isArmed() {
        return armed;
    }
}
