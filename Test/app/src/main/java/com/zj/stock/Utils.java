package com.zj.stock;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/9/22.
 */

public class Utils {

    public static String f2String(float price){

        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(price);//format 返回的是字符串

        return p;
    }
}
