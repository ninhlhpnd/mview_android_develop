package com.mtsc.mview.my_interface;

import java.util.List;

public interface DataListener {
    void onDataReceived(String deviceName, String sensorname,List<Float> value);
}
