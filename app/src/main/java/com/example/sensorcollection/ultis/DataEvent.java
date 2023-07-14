package com.example.sensorcollection.ultis;

import android.os.Bundle;

import com.example.sensorcollection.model.DulieuCacCamBien;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataEvent {
    private Map<String,Float[]> data;

    public DataEvent(Map<String,Float[]> data) {
        this.data = data;
    }

    public Map<String,Float[]> getData() {
        return data;
    }
}
