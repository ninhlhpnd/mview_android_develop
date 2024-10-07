package com.mtsc.mview.model;

import java.util.List;

public class DulieuCB {
        private String macambien;
        private String tencambien;
        private List<Float> giatricambien;

    public String getMacambien() {
        return macambien;
    }

    public void setMacambien(String macambien) {
        this.macambien = macambien;
    }

    public String getTencambien() {
        return tencambien;
    }

    public void setTencambien(String tencambien) {
        this.tencambien = tencambien;
    }

    public List<Float> getGiatricambien() {
        return giatricambien;
    }

    public void setGiatricambien(List<Float> giatricambien) {
        this.giatricambien = giatricambien;
    }

    public DulieuCB(String macambien, String tencambien, List<Float> giatricambien) {
        this.macambien = macambien;
        this.tencambien = tencambien;
        this.giatricambien = giatricambien;
    }
}
