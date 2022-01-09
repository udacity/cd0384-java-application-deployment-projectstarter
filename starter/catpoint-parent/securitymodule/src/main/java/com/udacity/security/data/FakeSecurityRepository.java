package com.udacity.security.data;

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.TreeSet;

public class FakeSecurityRepository implements SecurityRepository{

    private Set<Sensor> sensors;
    private AlarmStatus alarmStatus;
    private ArmingStatus armingStatus;
    private BufferedImage currentImage;

    public FakeSecurityRepository() {
        alarmStatus = AlarmStatus.NO_ALARM;
        armingStatus = ArmingStatus.DISARMED;
        sensors = new TreeSet<>();
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor);
    }

    @Override
    public void updateSensor(Sensor sensor) {
        sensors.remove(sensor);
        sensors.add(sensor);
    }

    @Override
    public void setAlarmStatus(AlarmStatus alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    @Override
    public void setArmingStatus(ArmingStatus armingStatus) {
        this.armingStatus = armingStatus;
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

    @Override
    public void setCurrentImage(BufferedImage image) {
        this.currentImage = image;
    }

    @Override
    public BufferedImage getCurrentImage() {
        return currentImage;
    }
}
