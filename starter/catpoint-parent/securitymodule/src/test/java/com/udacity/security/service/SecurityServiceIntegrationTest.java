package com.udacity.security.service;

import com.udacity.image.service.ImageService;
import com.udacity.security.data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceIntegrationTest {

    private SecurityService securityService;
    @Mock
    private ImageService imageService;
    private SecurityRepository securityRepository;
    private Sensor sensor;
    private Sensor sensor2;
    @Mock
    BufferedImage bufferedImage;

    @BeforeEach
    void init(){
        securityRepository = new FakeSecurityRepository();
        sensor = new Sensor("sensor1", SensorType.DOOR);
        sensor2 = new Sensor("sensor2", SensorType.WINDOW);

        securityService = new SecurityService(securityRepository, imageService);
    }

    @ParameterizedTest
    @MethodSource("ifArmed_whenASensorIsActivated_oldAlarmStatusAndNewAlarmStatus")
    void changeSensorActivationStatus_ifArmed_whenASensorIsActivated_setAlarm(
            ArmingStatus armingStatus, AlarmStatus oldAlarmStatus, AlarmStatus newAlarmStatus
    ){
        securityService.addSensor(sensor);
        securityService.setArmingStatus(armingStatus);
        securityService.setAlarmStatus(oldAlarmStatus);

        securityService.changeSensorActivationStatus(sensor, true);

        Assertions.assertEquals(newAlarmStatus, securityService.getAlarmStatus());
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
        securityService.addSensor(sensor);
        securityService.setArmingStatus(ArmingStatus.DISARMED);

        securityService.changeSensorActivationStatus(sensor, true);

        Assertions.assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    @ParameterizedTest
    @MethodSource("whenASensorIsDeactivated_ifAllSensorsInactive_setAlarmToNoAlarm")
    void changeSensorActivationStatus_whenASensorIsDeactivated_ifAllSensorsInactive_setAlarmToNoAlarm
            (boolean sensor1Active, boolean sensor2Active, AlarmStatus expected){
        securityService.addSensor(sensor);
        securityService.addSensor(sensor2);
        securityService.changeSensorActivationStatus(sensor, sensor1Active);
        securityService.changeSensorActivationStatus(sensor2, sensor2Active);

        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, false);
        Assertions.assertEquals(expected, securityService.getAlarmStatus());
    }

    private static Stream<Arguments> whenASensorIsDeactivated_ifAllSensorsInactive_setAlarmToNoAlarm(){
        return Stream.of(
                Arguments.of(true, true, AlarmStatus.PENDING_ALARM),
                Arguments.of(true, false, AlarmStatus.NO_ALARM));
    }

    @ParameterizedTest
    @CsvSource({
            "false, true",
            "true, false"
    })
    void changeSensorActivationStatus_ifAlarm_sensorStatusDoesNotAffectAlarm(
            boolean oldSensorStatus, boolean newSensorStatus){
        securityService.addSensor(sensor);
        securityService.changeSensorActivationStatus(sensor, oldSensorStatus);

        securityService.setAlarmStatus(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(sensor, newSensorStatus);

        Assertions.assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

    @Test
    void changeSensorActivationStatus_ifPendingAlarm_andSensorIsAlreadyActivated_whenSensorActivate_setAlarmToAlarm(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.addSensor(sensor);
        securityService.changeSensorActivationStatus(sensor, true);

        securityService.setAlarmStatus(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        Assertions.assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

    @Test
    void changeSensorActivationStatus_ifSensorIsAlreadyInactive_whenSensorDeactivate_doNothing(){
        securityService.addSensor(sensor);

        securityService.changeSensorActivationStatus(sensor, false);

        Assertions.assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    @Test
    void processImage_ArmedHome_ifImageContainsCat_setAlarmStatusToAlarm(){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.processImage(bufferedImage);

        Assertions.assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

    @ParameterizedTest
    @MethodSource("ifImageDoesNotContainCat_ifAllSensorsInactive_setAlarmToNoAlarm")
    void processImage_ifImageDoesNotContainCat_ifAllSensorsInactive_setAlarmToNoAlarm(
            boolean sensor1Active, boolean sensor2Active, AlarmStatus alarmStatus
    ){
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        securityService.setAlarmStatus(AlarmStatus.ALARM);
        securityService.addSensor(sensor);
        securityService.addSensor(sensor2);
        securityService.changeSensorActivationStatus(sensor, sensor1Active);
        securityService.changeSensorActivationStatus(sensor2, sensor2Active);
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);

        securityService.processImage(bufferedImage);

        Assertions.assertEquals(alarmStatus, securityService.getAlarmStatus());
    }

    private static Stream<Arguments> ifImageDoesNotContainCat_ifAllSensorsInactive_setAlarmToNoAlarm(){
        return Stream.of(
                Arguments.of(true, false, AlarmStatus.ALARM),
                Arguments.of(true, true, AlarmStatus.ALARM),
                Arguments.of(false, false, AlarmStatus.NO_ALARM));
    }

    @Test
    void setArmingStatus_disarmed_setAlarmStatusToNoAlarm(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        Assertions.assertEquals(AlarmStatus.NO_ALARM, securityService.getAlarmStatus());
    }

    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void setArmingStatus_Armed_deactivateAllSensors(ArmingStatus armingStatus){
        securityService.addSensor(sensor);
        securityService.addSensor(sensor2);
        securityService.changeSensorActivationStatus(sensor, true);
        securityService.changeSensorActivationStatus(sensor2, true);

        securityService.setArmingStatus(armingStatus);

        Assertions.assertEquals(false, sensor.getActive());
        Assertions.assertEquals(false, sensor2.getActive());
    }

    @Test
    void setArmingStatus_ArmedHome_whenImageContainsCat_setAlarmStatusToAlarm(){
        securityService.processImage(bufferedImage);
        Mockito.when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        Assertions.assertEquals(AlarmStatus.ALARM, securityService.getAlarmStatus());
    }

}
