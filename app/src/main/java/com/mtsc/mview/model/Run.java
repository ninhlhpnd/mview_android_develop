package com.mtsc.mview.model;

import java.util.ArrayList;
import java.util.List;

public class Run {
    private int runNumber;
    private double frequency;
    private List<SensorData> sensors;

    public Run(int runNumber, double frequency) {
        this.runNumber = runNumber;
        this.frequency = frequency;
        this.sensors = new ArrayList<>();
    }

    public int getRunNumber() {
        return runNumber;
    }

    public double getFrequency() {
        return frequency;
    }

    public List<SensorData> getSensors() {
        return sensors;
    }

    public void addSensorData(SensorData sensorData) {
        sensors.add(sensorData);
    }

    public SensorData getSensorDataByName(String name) {
        for (SensorData sd : sensors) {
            if (sd.getSensorName().equals(name)) {
                return sd;
            }
        }
        return null;
    }
}

