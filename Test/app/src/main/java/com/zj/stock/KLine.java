package com.zj.stock;

/*public class KLine {
 public float mopen;
 public float mclose;
 public float mhigh;
 public float mlow;

 public KLine(double open, double close, double high, double low) {
 mopen = (float)open;
 mclose = (float)close;
 mhigh = (float)high;
 mlow = (float)low;
 }
 }*/

public class KLine {
	public float mopen;
	public float mclose;
	public float mhigh;
	public float mlow;

	public KLine(float open, float high, float low, float close) {
		mopen = open;
		mclose = close;
		mhigh = high;
		mlow = low;
	}
}
