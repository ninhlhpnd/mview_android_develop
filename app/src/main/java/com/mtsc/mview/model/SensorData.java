package com.mtsc.mview.model;

import com.mtsc.mview.MainActivity;
import com.mtsc.mview.ultis.Uuid;

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
    public String getDonvi(){
        String donvi ="";
        int vitri = sensorName.indexOf('-');
        for (int i = 0; i < Uuid.camBiens.size(); i++) {
            if (Uuid.camBiens.get(i).getId().equals(sensorName.substring(0,vitri))) {
                donvi = Uuid.camBiens.get(i).getDonvi()[0];

                break;
            }
        }
        return donvi;
    }
}

