package com.mtsc.mview.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DulieuDothi implements Parcelable {
    int color;
    String tencambien;
    String macambien;
    String donvi;
    double[] heso;
    public DulieuDothi(int color, String tencambien, String macambien, String donvi, double[] heso) {
        this.color = color;
        this.tencambien = tencambien;
        this.macambien = macambien;
        this.donvi = donvi;
        this.heso = heso;
    }

    protected DulieuDothi(Parcel in) {
        color = in.readInt();
        tencambien = in.readString();
        macambien = in.readString();
        donvi = in.readString();
        heso= in.createDoubleArray();
    }

    public static final Creator<DulieuDothi> CREATOR = new Creator<DulieuDothi>() {
        @Override
        public DulieuDothi createFromParcel(Parcel in) {
            return new DulieuDothi(in);
        }

        @Override
        public DulieuDothi[] newArray(int size) {
            return new DulieuDothi[size];
        }
    };

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTencambien() {
        return tencambien;
    }

    public void setTencambien(String tencambien) {
        this.tencambien = tencambien;
    }

    public String getMacambien() {
        return macambien;
    }

    public void setMacambien(String macambien) {
        this.macambien = macambien;
    }

    public String getDonvi() {
        return donvi;
    }

    public void setDonvi(String donvi) {
        this.donvi = donvi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public double[] getHeso() {
        return heso;
    }
    public void setHeso(double[] heso) {
        this.heso = heso;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(color);
        parcel.writeString(tencambien);
        parcel.writeString(macambien);
        parcel.writeString(donvi);
        parcel.writeDoubleArray(heso);

    }
}
