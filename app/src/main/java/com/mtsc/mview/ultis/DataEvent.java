package com.mtsc.mview.ultis;

import com.mtsc.mview.model.DulieuCB;

import java.util.Map;

public class DataEvent {
    private DulieuCB data;

    public DataEvent(DulieuCB data) {
        this.data = data;
    }

    public DulieuCB getData() {
        return data;
    }
}
