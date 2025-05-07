package com.mtsc.mview.model;

import java.util.ArrayList;
import java.util.List;

public class SensorData {
    private String sensorName;
    private List<Double> values;

    public SensorData(String sensorName) {
        this.sensorName = sensorName;
        this.values = new ArrayList<>();
    }

    public String getSensorName() {
        return sensorName;
    }

    public List<Double> getValues() {
        return values;
    }

    public void addValue(double value) {
        values.add(value);
    }
}

