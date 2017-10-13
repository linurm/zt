package com.zj.stock;

public class UserData {
	public float stock_market;
	public float balance;
	public float stock_value;
	public float stock_buy_price;
	public float gains;
	public int stock_num;
	
	public UserData(float n){
		stock_value = n;
		balance = stock_value;
	}
}
