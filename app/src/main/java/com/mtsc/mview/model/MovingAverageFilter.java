package com.mtsc.mview.model;

import java.util.ArrayList;
import java.util.List;

public class MovingAverageFilter {
    private int windowSize;
    private List<Double> window;
    private double sum;

    public MovingAverageFilter(int windowSize) {
        this.windowSize = windowSize;
        this.window = new ArrayList<>();
        this.sum = 0.0;
    }

    public double filter(double newValue) {
        if (window.size() == windowSize) {
            sum -= window.get(0);
            window.remove(0);
        }

        window.add(newValue);
        sum += newValue;

        return sum / window.size();
    }
}
