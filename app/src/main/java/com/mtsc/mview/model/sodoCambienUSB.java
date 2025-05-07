package com.mtsc.mview.model;

public class sodoCambienUSB {
    public String macambien;
    public int id;
    public String tencambien;
    public String[] donvi;
    int icon;
    public double[][] heso;
    public int[] tanso;
    public int dophangiai;

    public sodoCambienUSB(String macambien, int id, String tencambien, String[] donvi, int icon, double[][] heso, int[] tanso, int dophangiai) {
        this.macambien = macambien;
        this.id = id;
        this.tencambien = tencambien;
        this.donvi = donvi;
        this.icon = icon;
        this.heso = heso;
        this.tanso = tanso;
        this.dophangiai = dophangiai;
    }

    public String getMacambien() {
        return macambien;
    }

    public void setMacambien(String macambien) {
        this.macambien = macambien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
