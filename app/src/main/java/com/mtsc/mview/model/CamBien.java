package com.mtsc.mview.model;

public class CamBien {
    String id;
    String name;
    String[] donvi;
    int icon;
    double[][] heso;
    int[] tanso;
    double[] daido;

    public CamBien(String id, String name, String[] donvi, int icon, double[][] heso, int[] tanso, double[] daido) {
        this.id = id;
        this.name = name;
        this.donvi = donvi;
        this.icon = icon;
        this.heso = heso;
        this.tanso = tanso;
        this.daido = daido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double[] getDaido() {
        return daido;
    }

    public void setDaido(double[] daido) {
        this.daido = daido;
    }
}
