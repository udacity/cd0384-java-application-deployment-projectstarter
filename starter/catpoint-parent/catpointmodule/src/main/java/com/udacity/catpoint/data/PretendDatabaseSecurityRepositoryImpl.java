package com.udacity.catpoint.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

/**
 * Fake repository implementation for demo purposes. Stores state information in local
 * memory and writes it to user preferences between app loads. This implementation is
 * intentionally a little hard to use in unit tests, so watch out!
 */
public class PretendDatabaseSecurityRepositoryImpl implements SecurityRepository{

    private Set<Sensor> sensors;
    private AlarmStatus alarmStatus;
    private ArmingStatus armingStatus;

    //preference keys
    private static final String SENSORS = "SENSORS";
    private static final String ALARM_STATUS = "ALARM_STATUS";
    private static final String ARMING_STATUS = "ARMING_STATUS";

    private static final Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepositoryImpl.class);
    private static final Gson gson = new Gson(); //used to serialize objects into JSON

    public PretendDatabaseSecurityRepositoryImpl() {
        //load system state from prefs, or else default
        alarmStatus = AlarmStatus.valueOf(prefs.get(ALARM_STATUS, AlarmStatus.NO_ALARM.toString()));
        armingStatus = ArmingStatus.valueOf(prefs.get(ARMING_STATUS, ArmingStatus.DISARMED.toString()));

        //we've serialized our sensor objects for storage, which should be a good warning sign that
        // this is likely an impractical solution for a real system
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
    public void setArmingStatus(ArmingStatus armingStatus) {
        this.armingStatus = armingStatus;
        prefs.put(ARMING_STATUS, this.armingStatus.toString());
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
    public ArmingStatus getArmingStatus() {
        return armingStatus;
    }
}
