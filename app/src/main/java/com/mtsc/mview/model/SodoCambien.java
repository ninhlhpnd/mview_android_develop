package com.mtsc.mview.model;

import com.clj.fastble.data.BleDevice;

public class SodoCambien {
    public BleDevice bleDevice;
    public String tencambien;
    public String[] donvi;
    int icon;
    public double[][] heso;
    public int[] tanso;
    public int dophangiai;

    public SodoCambien(BleDevice bleDevice, String tencambien, String[] donvi, int icon, double[][] heso, int[] tanso, int dophangiai) {
        this.bleDevice = bleDevice;
        this.tencambien = tencambien;
        this.donvi = donvi;
        this.icon = icon;
        this.heso = heso;
        this.tanso = tanso;
        this.dophangiai = dophangiai;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getTencambien() {
        return tencambien;
    }

    public void setTencambien(String tencambien) {
        this.tencambien = tencambien;
    }

    public String[] getDonvi() {
        return donvi;
    }

    public void setDonvi(String[] donvi) {
        this.donvi = donvi;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public double[][] getHeso() {
        return heso;
    }

    public void setHeso(double[][] heso) {
        this.heso = heso;
    }

    public int[] getTanso() {
        return tanso;
    }

    public void setTanso(int[] tanso) {
        this.tanso = tanso;
    }

    public int getDophangiai() {
        return dophangiai;
    }

    public void setDophangiai(int dophangiai) {
        this.dophangiai = dophangiai;
    }
}
