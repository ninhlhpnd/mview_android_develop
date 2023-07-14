package com.example.sensorcollection.model;

public class CamBien {
    String id;
    String name;
    String[] donvi;
    int icon;
    double[][] heso;

    public CamBien(String id, String name, String[] donvi, int icon, double[][] heso) {
        this.id = id;
        this.name = name;
        this.donvi = donvi;
        this.icon = icon;
        this.heso = heso;
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
}
