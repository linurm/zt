/*
 * Copyright zh.weir.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zj.stock;


import android.util.Log;


/**
 * 新浪股票信息。
 *
 * @author <a href="http://www.blogjava.net/zh-weir/"
 *         target="_blank">zh.weir的技术博客</a> </br> </br> <a
 *         href="http://weibo.com/1779382071?s=6uyXnP" target="_blank"><img
 *         border="0" src=
 *         "http://service.t.sina.com.cn/widget/qmd/1779382071/9c2d28b9/1.png"
 *         /></a>
 */
public class Stock2Info {

    private final String TAG = this.getClass().getName();
    // 股票名字
    private String mName;
    // 今日开盘价
    private float mTodayPrice;
    // 昨日收盘价
    private float mYestodayPrice;
    // 当前价
    private float mNowPrice;
    // 今日最高价
    private float mHighestPrice;
    // 今日最低价
    private float mLowestPrice;

    // 成交股票数，单位“股”。100股为1手。
    private long mTradeCount;
    // 成交额，单位“元”。一般需要转换成“万元”。
    private long mTradeMoney;

    // 日期
    private String mDate;
    // 时间
    private String mTime;
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private Stock2Info(String name, float todayPrice, float yestodayPrice,
                       float nowPrice, float highestPrice, float lowestPrice,
                       long tradeCount,
                       long tradeMoney,
                       String date, String time) {

        mName = name;
        mTodayPrice = todayPrice;
        mYestodayPrice = yestodayPrice;

        mNowPrice = nowPrice;
        mHighestPrice = highestPrice;
        mLowestPrice = lowestPrice;


        mTradeCount = tradeCount;
        mTradeMoney = tradeMoney;


        mDate = date;
        mTime = time;
    }

    /**
     * 从一行响应字符串中解析得到SinaStockInfo数据结构。
     *
     * @param source 参数的格式如： var hq_str_sh601006=
     *               "大秦铁路,7.69,7.70,7.62,7.72,7.61,7.61,7.62,46358694,355190642,565201,7.61,984000,7.60,211900,7.59,476600,7.58,238500,7.57,295518,7.62,217137,7.63,241500,7.64,345900,7.65,419400,7.66,2012-02-29,15:03:07"
     *               ;
     * @return SinaStockInfo
     * @throws ParseStockInfoException
     */

    public static StockData parseStockData(String source)
            throws ParseStockInfoException {

//        Log.e("TAG", "" + source);

        String[] infoStr = source.split(",");

        StockData sd = new StockData();
        sd.code = infoStr[1];
        sd.date = infoStr[0];
        sd.name = infoStr[2];
        sd.open = Float.valueOf(infoStr[6]);
        sd.high = Float.valueOf(infoStr[4]);
        sd.low = Float.valueOf(infoStr[5]);
        sd.close = Float.valueOf(infoStr[3]);
        sd.pre_close = Float.valueOf(infoStr[7]);
        sd.volume = Float.valueOf(infoStr[8]);
        sd.adj = Float.valueOf(infoStr[9]);
        if (sd.low == 0 && sd.high == 0) {
            return null;
        }
        //Note note = new Note(null, idString, d, time, null);
        // print("idString: " + idString);
        // print("contentString: " + contentString);
        // print(" : " + infoStr[31] + "," + infoStr[32] + " : "
        // + source.length());
        // if (infoStr.length != 32) {
        // ;// throw new ParseStockInfoException();
        // }

        return sd;
    }

//    public static Stock2Info parseNote2Info(Note note) {
//        String s = note.getContent();
//        String n = note.getStockid();
//        String d = note.getDate();
//
//        String[] infoStr = s.split(",");
//        String[] dateStr = d.split(",");
//
//        if (infoStr.length != 32) {
//            ;// return null;// throw new ParseStockInfoException();
//        }
//        final String name = n;
//        final float todayPrice = Float.parseFloat(infoStr[3]);
//        final float yestodayPrice = Float.parseFloat(infoStr[4]);
//        final float nowPrice = Float.parseFloat(infoStr[0]);
//        final float highestPrice = Float.parseFloat(infoStr[1]);
//        final float lowestPrice = Float.parseFloat(infoStr[2]);
//        final long tradeCount = Long.parseLong(infoStr[5]);
//        final long tradeMoney = Long.parseLong(infoStr[6].split("\\.")[0]);
//
//        final String date = dateStr[0];
//
//        Stock2Info stockInfo = new Stock2Info(name, todayPrice, yestodayPrice,
//                nowPrice, highestPrice, lowestPrice,
//                tradeCount, tradeMoney, date, null);
//
//        return stockInfo;
//    }

    public static Stock2Info parseStockInfo(String source)
            throws ParseStockInfoException {
        // 取""中数据
        int start = source.indexOf(' ');
        int first = source.indexOf("=");
        String idString = source.substring(start + 1, first);
        String contentString = source.substring(first + 2, source.length() - 2);
        String[] infoStr = contentString.split(",");
        // print("idString: " + idString);
        // print("contentString: " + contentString);
        // print(" : " + infoStr.length + " : " + source.length());
        if (infoStr.length != 32) {
            ;// throw new ParseStockInfoException();
        }

        final String name = infoStr[0];
        final float todayPrice = Float.parseFloat(infoStr[1]);
        final float yestodayPrice = Float.parseFloat(infoStr[2]);
        final float nowPrice = Float.parseFloat(infoStr[3]);
        final float highestPrice = Float.parseFloat(infoStr[4]);
        final float lowestPrice = Float.parseFloat(infoStr[5]);
        final long tradeCount = Long.parseLong(infoStr[8]);
        final long tradeMoney = Long.parseLong(infoStr[9]);


        final String date = infoStr[30];
        final String time = infoStr[31];

        Stock2Info stockInfo = new Stock2Info(name, todayPrice, yestodayPrice,
                nowPrice, highestPrice, lowestPrice,
                tradeCount, tradeMoney, date, time);

        return stockInfo;
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    /**
     * 获取股票名称
     *
     * @return 股票名称
     */
    public String getName() {
        return mName;
    }

    /**
     * 获取今日开盘价
     *
     * @return 今日股票开盘价
     */
    public float getTodayPrice() {
        return mTodayPrice;
    }

    /**
     * 获取昨日收盘价
     *
     * @return 昨日收盘价
     */
    public float getYestodayPrice() {
        return mYestodayPrice;
    }

    /**
     * 获取当前股价
     *
     * @return 当前股价
     */
    public float getNowPrice() {
        return mNowPrice;
    }

    /**
     * 获取今日最高价
     *
     * @return 今日最高价
     */
    public float getHighestPrice() {
        return mHighestPrice;
    }

    /**
     * 获取今日最低价
     *
     * @return 今日最低价
     */
    public float getLowestPrice() {
        return mLowestPrice;
    }

    /**
     * 获取股票交易量。单位为“股”，100股为1手，请注意转换。
     *
     * @return 股票交易量
     */
    public long getTradeCount() {
        return mTradeCount;
    }

    /**
     * 获取股票交易额。单位为“元”，如需显示“万元”，请注意转换。
     *
     * @return 股票交易额
     */
    public long getTradeMoney() {
        return mTradeMoney;
    }


    /**
     * 获取对应股票信息的日期。例如周末，或者其他休市期间获取的数据将不是实时的。
     *
     * @return 获取对应股票信息的日期。
     */
    public String getDate() {
        return mDate;
    }

    /**
     * 获取对应股票信息的时间。例如周末，或者其他休市期间获取的数据将不是实时的。
     *
     * @return 获取对应股票信息的时间。
     */
    public String getTime() {
        return mTime;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("股票名称： " + getName() + "\n");
        sb.append("今日开盘价： " + getTodayPrice() + "元\n");
        sb.append("昨日收盘价： " + getYestodayPrice() + "元\n");
        sb.append("当前股价： " + getNowPrice() + "元\n");
        sb.append("今日最高价： " + getHighestPrice() + "元\n");
        sb.append("今日最低价： " + getLowestPrice() + "元\n");
        sb.append("今日交易量： " + getTradeCount() + "股\n");
        sb.append("今日成交量： " + getTradeMoney() + "元\n");

        sb.append("时间： " + getTime() + "\n");
        sb.append("日期： " + getDate() + "\n");

        return sb.toString();
    }

    public static class ParseStockInfoException extends Exception {
        public ParseStockInfoException() {
            super("Parse StockInfo error!");
        }
    }

    // 买单或卖单信息。
    public static class BuyOrSellInfo {
        // 数量。单位为“股”。100股为1手。
        long mCount;
        // 价格。
        float mPrice;

        public BuyOrSellInfo(long count, float price) {
            mCount = count;
            mPrice = price;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("数量： " + mCount + "股    ");
            sb.append("价格： " + mPrice + "元");
            return sb.toString();
        }
    }
}
