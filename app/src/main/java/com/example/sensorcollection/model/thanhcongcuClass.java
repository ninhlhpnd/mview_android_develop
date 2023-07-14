package com.example.sensorcollection.model;

public class thanhcongcuClass {
    int id, hinh;
    String ten;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHinh() {
        return hinh;
    }

    public void setHinh(int hinh) {
        this.hinh = hinh;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public thanhcongcuClass(int id, int hinh, String ten) {
        this.id = id;
        this.hinh = hinh;
        this.ten = ten;
    }
}
