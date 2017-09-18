package com.zj.stock;

public class MACDData {
    public float ema12;
    public float ema26;
    public float dif;
    public float dea;
    public float bar;

    public MACDData(float e12, float e26, float df, float de, float br) {
        ema12 = e12;
        ema26 = e26;
        dif = df;
        dea = de;
        bar = br;
    }

    public MACDData(StockData m, MACDData p_macd) {

        // Log.e(TAG,"*************************");

        ema12 = (11 * p_macd.ema12 + 2 * m.close) / 13;
        ema26 = (25 * p_macd.ema26 + 2 * m.close) / 27;

        dif = ema12 - ema26;
        dea = (4 * p_macd.dea + dif) / 5;
        bar = 2 * (dif - dea);
//        p_macd.ema12 = ema12;
//        p_macd.ema26 = ema26;
//        p_macd.dea = dea;
    }

    public float getMaxValue() {
        float maxV = dif > dea ? dif : dea;
        maxV = maxV > bar ? maxV : bar;
        return maxV;
    }

    public float getMinValue() {
        float minV = dif < dea ? dif : dea;
        minV = minV < bar ? minV : bar;
        return minV;
    }


    public String toString() {
        String a;
        a = "ema12:" + ema12 + " ema26:" + ema26 + " dif:" + dif + " dea:" + dea + " bar:" + bar;
        return a;
    }
}
