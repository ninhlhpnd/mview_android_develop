package com.mtsc.mview.model;

public class DulieuCacCamBien {
    int lanchay;
    Float tansolaymau;
    String tencambien;
    Float giatricambien;

    public DulieuCacCamBien(int lanchay, Float tansolaymau, String tencambien, Float giatricambien) {
        this.lanchay = lanchay;
        this.tansolaymau = tansolaymau;
        this.tencambien = tencambien;
        this.giatricambien = giatricambien;
    }

    public int getLanchay() {
        return lanchay;
    }

    public void setLanchay(int lanchay) {
        this.lanchay = lanchay;
    }

    public Float getTansolaymau() {
        return tansolaymau;
    }

    public void setTansolaymau(Float tansolaymau) {
        this.tansolaymau = tansolaymau;
    }

    public String getTencambien() {
        return tencambien;
    }

    public void setTencambien(String tencambien) {
        this.tencambien = tencambien;
    }

    public Float getGiatricambien() {
        return giatricambien;
    }

    public void setGiatricambien(Float giatricambien) {
        this.giatricambien = giatricambien;
    }
}
