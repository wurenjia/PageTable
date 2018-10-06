package com.example.administrator.newproject002;

import org.litepal.crud.LitePalSupport;

public class Datas extends LitePalSupport {
    private int id;
    private Double TempData;
    private String TimeData;

    public Double getTempData() {
        return TempData;
    }

    public void setTempData(Double tempData) {
        TempData = tempData;
    }

    public String getTimeData() {
        return TimeData;
    }

    public void setTimeData(String timeData) {
        TimeData = timeData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}