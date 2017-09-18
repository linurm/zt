package com.zj.stock;

public class MACDData {
    public float ema12;
    public float ema26;
    public float dif;
    public float dea;
    public float bar;

    public MACDData(float e12, float e26, float df, float d, float br) {
        ema12 = e12;
        ema26 = e26;
        dif = df;
        dea = d;
        bar = br;
    }


    public String toString() {
        String a;
        a = "ema12:" + ema12 + " ema26:" + ema26 + " dif:" + dif + " dea:" + dea + " bar:" + bar;
        return a;
    }
}
