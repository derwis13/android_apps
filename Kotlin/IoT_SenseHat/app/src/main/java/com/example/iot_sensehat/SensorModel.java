package com.example.iot_sensehat;

public class SensorModel {

    String sensorName;
    String sensorDescribe;
    String sensorMeasurement;

    public SensorModel(String sensorName, String sensorDescribe, String sensorMeasurement) {
        this.sensorName = sensorName;
        this.sensorDescribe = sensorDescribe;
        this.sensorMeasurement = sensorMeasurement;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorDescribe() {
        return sensorDescribe;
    }

    public String getSensorMeasurement() {
        return sensorMeasurement;
    }
}
