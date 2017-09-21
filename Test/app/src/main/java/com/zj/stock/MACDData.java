package com.zj.stock;

public class MACDData {
    public float ema12;
    public float ema26;
    public float dif;
    public float dea;
    public float bar;
    public float maxV;
    public float minV;
    public int num;

    public MACDData(float e12, float e26, float df, float de, float br, int n) {
        ema12 = e12;
        ema26 = e26;
        dif = df;
        dea = de;
        bar = br;
        num = n;
        getMaxMin();
    }

    public MACDData(StockData m, MACDData p_macd, int n) {

        // Log.e(TAG,"*************************");
        num = n;
        ema12 = (11 * p_macd.ema12 + 2 * m.close) / 13;
        ema26 = (25 * p_macd.ema26 + 2 * m.close) / 27;

        dif = ema12 - ema26;
        dea = (4 * p_macd.dea + dif) / 5;
        bar = 2 * (dif - dea);
//        p_macd.ema12 = ema12;
//        p_macd.ema26 = ema26;
//        p_macd.dea = dea;
        getMaxMin();
    }

    public void getMaxMin() {
//        maxV = 0;
//        minV = 0;
        maxV = dif > dea ? dif : dea;
        maxV = maxV > bar ? maxV : bar;

        minV = dif < dea ? dif : dea;
        minV = minV < bar ? minV : bar;
    }

    public String toString() {
        String a;
        a = " num:" + num + " ema12:" + ema12 + " ema26:" + ema26 + " dif:" + dif + " dea:" + dea + " bar:" + bar;
        return a;
    }
}
