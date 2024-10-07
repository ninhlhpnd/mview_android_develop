package com.mtsc.mview.model;

public class CamBienUSB {
    CamBien camBien;
    int id;

    public CamBien getCamBien() {
        return camBien;
    }

    public void setCamBien(CamBien camBien) {
        this.camBien = camBien;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CamBienUSB(CamBien camBien, int id) {
        this.camBien = camBien;
        this.id = id;
    }
}
