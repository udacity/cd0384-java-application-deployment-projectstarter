package com.udacity.security.service;

import com.udacity.image.service.ImageService;
import com.udacity.security.application.StatusListener;
import com.udacity.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private SecurityService securityService;

    @Mock
    private ImageService imageService;
    @Mock
    private SecurityRepository securityRepository;
    @Mock
    private Sensor sensor;
    @Mock
    private Sensor sensor2;
    @Mock
    BufferedImage bufferedImage;
    @Mock
    StatusListener statusListener;

    @BeforeEach
    void init(){
        securityService = new SecurityService(securityRepository, imageService);
    }

    //If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    //If alarm is armed and a sensor becomes activated and the system is already pending alarm, set the alarm status to alarm.
    //if is disarmed, then don't change alarm status
    @ParameterizedTest
    @MethodSource("ifArmed_whenASensorIsActivated_oldAlarmStatusAndNewAlarmStatus")
    void changeSensorActivationStatus_ifArmed_whenASensorIsActivated_setAlarm(
            ArmingStatus armingStatus, AlarmStatus oldAlarmStatus, AlarmStatus newAlarmStatus
    ){
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(armingStatus);
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(oldAlarmStatus);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(newAlarmStatus);
    }

    private static Stream<Arguments> ifArmed_whenASensorIsActivated_oldAlarmStatusAndNewAlarmStatus(){
        return Stream.of(
                Arguments.of(ArmingStatus.ARMED_HOME, AlarmStatus.NO_ALARM, AlarmStatus.PENDING_ALARM),
                Arguments.of(ArmingStatus.ARMED_HOME, AlarmStatus.PENDING_ALARM, AlarmStatus.ALARM),
                Arguments.of(ArmingStatus.ARMED_AWAY, AlarmStatus.NO_ALARM, AlarmStatus.PENDING_ALARM),
                Arguments.of(ArmingStatus.ARMED_AWAY, AlarmStatus.PENDING_ALARM, AlarmStatus.ALARM)
        );
    }

    @Test
    void changeSensorActivationStatus_ifDisarmed_whenSensorIsActivated_doNothing(){
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(0)).setAlarmStatus(any());
    }

    //If pending alarm, if one sensor deactivate but others are still active, do nothing.
    //If pending alarm, and all sensors are inactive, return to no alarm state.
    @ParameterizedTest
    @CsvSource({
            "true, true, 0",
            "true, false, 1"
    })
    void changeSensorActivationStatus_whenASensorIsDeactivated_ifAllSensorsInactive_setAlarmToNoAlarm
    (boolean sensor1Active, boolean sensor2Active, int numberOfInvocations){
        lenient().when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        Set<Sensor> sensors = new HashSet<>(List.of(sensor, sensor2));
        Mockito.when(securityRepository.getSensors()).thenReturn(sensors);
        Mockito.when(sensor.getActive()).thenReturn(sensor1Active);
        Mockito.when(sensor2.getActive()).thenReturn(sensor2Active);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, times(numberOfInvocations)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    //If alarm is active, change in sensor state should not affect the alarm state.
    @ParameterizedTest
    @CsvSource({
            "false, true",
            "true, false"
    })
    void changeSensorActivationStatus_ifAlarm_sensorStatusDoesNotAffectAlarm(
            boolean oldSensorStatus, boolean newSensorStatus){
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        Mockito.when(sensor.getActive()).thenReturn(oldSensorStatus);

        securityService.changeSensorActivationStatus(sensor, newSensorStatus);

        verify(securityRepository, times(0)).setAlarmStatus(any());
    }

    //If a sensor is activated while already active and the system is in pending state, change it to alarm state.
    @Test
    void changeSensorActivationStatus_ifPendingAlarm_andSensorIsAlreadyActivated_whenSensorActivate_setAlarmToAlarm(){
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        Mockito.when(sensor.getActive()).thenReturn(true);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    //If a sensor is deactivated while already inactive, make no changes to the alarm state.
    @Test
    void changeSensorActivationStatus_ifSensorIsAlreadyInactive_whenSensorDeactivate_doNothing(){
        Mockito.when(sensor.getActive()).thenReturn(false);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, times(0)).setAlarmStatus(any());
    }

    //If the image service identifies an image containing a cat while the system is armed-home, put the system into alarm status.
    //If system is armed_away or no alarm, then no alarm?
    @Test
    void processImage_ArmedHome_ifImageContainsCat_setAlarmStatusToAlarm(){
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.processImage(bufferedImage);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    //If the image service identifies an image that does not contain a cat, change the status to no alarm as long as the sensors are not active.
    //if no cat, sensors are active, do nothing?
    @ParameterizedTest
    @CsvSource({
            "true, false, 0",
            "true, true, 0",
            "false, false, 1"
    })
    void processImage_ifImageDoesNotContainCat_ifAllSensorsInactive_setAlarmToNoAlarm(
            boolean sensor1Active, boolean sensor2Active, int numberOfInvocations
    ){
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        Set<Sensor> sensors = new HashSet<>(List.of(sensor, sensor2));
        Mockito.when(securityRepository.getSensors()).thenReturn(sensors);
        lenient().when(sensor.getActive()).thenReturn(sensor1Active);
        lenient().when(sensor2.getActive()).thenReturn(sensor2Active);

        securityService.processImage(bufferedImage);

        verify(securityRepository, times(numberOfInvocations)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    //If the system is disarmed, set the status to no alarm.
    @Test
    void setArmingStatus_disarmed_setAlarmStatusToNoAlarm(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    //If the system is armed, reset all sensors to inactive.
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void setArmingStatus_Armed_deactivateAllSensors(ArmingStatus armingStatus){
        Set<Sensor> sensors = new HashSet<>(List.of(sensor, sensor2));
        Mockito.when(securityRepository.getSensors()).thenReturn(sensors);

        securityService.setArmingStatus(armingStatus);

        verify(sensor, times(1)).setActive(false);
        verify(sensor2, times(1)).setActive(false);
    }

    //If the system is armed-home while the camera shows a cat, set the alarm status to alarm.
    @Test
    void setArmingStatus_ArmedHome_whenImageContainsCat_setAlarmStatusToAlarm(){
        Mockito.when(securityRepository.getCurrentImage()).thenReturn(bufferedImage);
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void addSensor_notifyEachStatusListener(){
        securityService.addStatusListener(statusListener);

        securityService.addSensor(sensor);

        verify(statusListener, times(1)).sensorStatusChanged();
    }

    @Test
    void removeSensor_notifyEachStatusListener(){
        securityService.addStatusListener(statusListener);

        securityService.removeSensor(sensor);

        verify(statusListener, times(1)).sensorStatusChanged();
    }
}