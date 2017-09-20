package com.zj.stock;

public class KDJData {
    public float k;
    public float d;
    public float j;
    public float rsv;

    public KDJData() {
        k = 50;
        d = 50;
        j = 50;
        rsv = 0;
    }

    public KDJData(float mk, float md, float mj, float mrsv) {
        k = mk;
        d = md;
        j = mj;
        rsv = mrsv;
    }

    public KDJData(StockData m, KDJData pre_kdj, float h, float l) {
        if (h == l)
            rsv = 50;
        else
            rsv = (float) ((m.close - l) / (float) (h - l)) * 100;
//        rsv = r;
        k = (float) (2 * pre_kdj.k + rsv) / 3;

        d = (float) (2 * pre_kdj.d + k) / 3;
        j = (float) (3 * k - 2 * d);
        j = j > 0 ? j : 0;
        j = j > 100 ? 100 : j;
    }
    // public float pre_k;

    public String toString() {
        String a;
        a = "k:" + k + " d:" + d + " j:" + j + " rsv:" + rsv;
        return a;
    }
}
