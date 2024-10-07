package com.mtsc.mview.model;

import com.clj.fastble.data.BleDevice;

public class ConnectedDevice {
    BleDevice device;
    String serviceUuid;
    String readUuid;

    public ConnectedDevice(BleDevice device, String serviceUuid, String readUuid) {
        this.device = device;
        this.serviceUuid = serviceUuid;
        this.readUuid = readUuid;
    }

    public BleDevice getDevice() {
        return device;
    }

    public void setDevice(BleDevice device) {
        this.device = device;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public String getReadUuid() {
        return readUuid;
    }

    public void setReadUuid(String readUuid) {
        this.readUuid = readUuid;
    }
}
