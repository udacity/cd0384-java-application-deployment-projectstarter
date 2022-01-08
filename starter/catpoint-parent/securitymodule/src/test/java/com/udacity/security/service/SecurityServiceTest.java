package com.udacity.security.service;

import com.amazonaws.services.lightsail.model.Alarm;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    BufferedImage bufferedImage;

    private Set<StatusListener> statusListeners = new HashSet<>();

    @BeforeEach
    void init(){
        securityService = new SecurityService(securityRepository, imageService);
    }

    //If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
    //If alarm is armed and a sensor becomes activated and the system is already pending alarm, set the alarm status to alarm.
    @ParameterizedTest
    @MethodSource("ifArmed_whenASensorBecomesActive_oldAlarmStatusAndNewAlarmStatus")
    void changeSensorActivationStatus_ifArmed_whenASensorBecomesActive_setAlarmStatus(
            AlarmStatus oldAlarmStatus, AlarmStatus newAlarmStatus
    ){
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        Mockito.when(sensor.getActive()).thenReturn(false);
        securityService.addSensor(sensor);

        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(oldAlarmStatus);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(eq(newAlarmStatus));
    }

    private static Stream<Arguments> ifArmed_whenASensorBecomesActive_oldAlarmStatusAndNewAlarmStatus(){
        return Stream.of(
                Arguments.of(AlarmStatus.NO_ALARM, AlarmStatus.PENDING_ALARM),
                Arguments.of(AlarmStatus.PENDING_ALARM, AlarmStatus.ALARM)
        );
    }

    //If pending alarm and all sensors are inactive, return to no alarm state.
    @Test
    void changeSensorActivationStatus_ifPendingAlarm_whenAllSensorsDeactivate_setAlarmStatusToNoAlarm(){
        List<Sensor> sensors = List.of(
                new Sensor("windowSensor", SensorType.WINDOW),
                new Sensor("doorSensor", SensorType.DOOR),
                new Sensor("motionSensor", SensorType.MOTION)
        );

        for (Sensor sensor : sensors) {
            sensor.setActive(true);
            securityService.addSensor(sensor);
        }

        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        for (Sensor sensor : sensors) {
            securityService.changeSensorActivationStatus(sensor, false);
        }

        verify(securityRepository, times(1)).setAlarmStatus(eq(AlarmStatus.NO_ALARM));
    }

    //    If alarm is active, change in sensor state should not affect the alarm state.
    @Test
    void changeSensorActivationStatus_ifAlarm_sensorStateDoesNotAffectAlarmStatus(){
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        Sensor realSensor = new Sensor("sensor", SensorType.WINDOW);
        securityService.addSensor(realSensor);
        securityService.changeSensorActivationStatus(realSensor, true);
        securityService.changeSensorActivationStatus(realSensor, false);

        verify(securityRepository, times(0)).setAlarmStatus(any());
    }

    //    If a sensor is activated while already active and the system is in pending state, change it to alarm state.
    @Test
    void changeSensorActivationStatus_ifPendingAlarm_andSensorIsAlreadyActivated_whenSensorActivate_setAlarmStatusToAlarm(){
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        Mockito.when(sensor.getActive()).thenReturn(true);

        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    //    If a sensor is deactivated while already inactive, make no changes to the alarm state.
    @Test
    void changeSensorActivationStatus_ifSensorIsAlreadyDeactivated_whenSensorDeactivate_doesNotAffectAlarmStatus(){
        Mockito.when(sensor.getActive()).thenReturn(false);

        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, times(0)).setAlarmStatus(any());
    }

    //If the image service identifies an image containing a cat while the system is armed-home, put the system into alarm status.
    //If the image service identifies an image that does not contain a cat, change the status to no alarm as long as the sensors are not active.

    @ParameterizedTest
    @MethodSource("imageContainsCat_sensorActive_setAlarmStatus")
    void processImage_ArmedHome_givenWhetherCatIsDetected_andSensorStatus_setAlarmStatus(
            boolean imageContainsCat,
            boolean sensorActive,
            AlarmStatus alarmStatus){
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(imageContainsCat);
        Mockito.when(sensor.getActive()).thenReturn(sensorActive);

        securityService.addSensor(sensor);
        securityService.processImage(bufferedImage);

        verify(securityRepository, times(1)).setAlarmStatus(alarmStatus);
    }

    private static Stream<Arguments> imageContainsCat_sensorActive_setAlarmStatus(){
        return Stream.of(
                Arguments.of(true, true, AlarmStatus.ALARM),
                Arguments.of(false, true, AlarmStatus.PENDING_ALARM),
                Arguments.of(false, false, AlarmStatus.NO_ALARM)
        );
    }

    //If the system is disarmed, set the status to no alarm.
    @Test
    void setArmingStatusToDisarmed_setAlarmStatusToNoAlarm(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    //If the system is armed, reset all sensors to inactive.
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void setArmingStatusToArmed_deactivateAllSensors(ArmingStatus armingStatus){
        securityService.addSensor(sensor);
        Mockito.when(sensor.getActive()).thenReturn(true);

        securityService.setArmingStatus(armingStatus);
        verify(sensor, times(1)).setActive(false);
    }

    //If the system is armed-home while the camera shows a cat, set the alarm status to alarm.
    @Test
    void setArmingStatus_whenImageContainsCat_ifSystemIsArmedHome_setAlarmStatusToAlarm(){
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }
}