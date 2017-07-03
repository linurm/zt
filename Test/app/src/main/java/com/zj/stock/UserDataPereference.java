package com.zj.stock;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserDataPereference {
	public static final String PREFERENCES_NAME = "com.zj.stock.user";
	private static final String PREFERENCES_MONEY = "money";

	private static SharedPreferences getSharedPreferences(Context context) {
		// TODO Auto-generated method stub
		return context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
	}

	public static void setUserData(Context context, UserData userData) {
		Editor editor = getSharedPreferences(context).edit();
		editor.putFloat(PREFERENCES_MONEY, userData.stock_value);
		editor.commit();
		// Log.i(TAG, "setAppSettings");
	}

	public static void clear(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
		// Log.i(TAG, "CLEAR");
	}
	
	public static UserData getUserData(Context context) {
		UserData token = new UserData(1);
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_APPEND);
		token.stock_value = (pref.getFloat(PREFERENCES_MONEY, (float) 10000.0));
//
////		token.setID(0, pref.getString(PREFERENCES_ID1, "1676050114"));
////		token.setID(1, pref.getString(PREFERENCES_ID2, "1245651980"));
////		token.setID(2, pref.getString(PREFERENCES_ID3, "1310411632"));
////		token.setID(3, pref.getString(PREFERENCES_ID4, "1907046731"));
//		
//		token.setEmailName(pref.getString(PREFERENCES_ENAME, "1090626081@qq.com"));
//		token.setEmailPasswd(pref.getString(PREFERENCES_EPASSWD, "zhj1090626081"));
//
//		token.setID(0,pref.getString(PREFERENCES_ID1, "1726640331"));
//		token.setID(1,pref.getString(PREFERENCES_ID2, "1772392290"));
//		token.setID(2,pref.getString(PREFERENCES_ID3, "3373931552"));
		// Log.i(TAG, "getAppInfo");
		return token;
	}
}
