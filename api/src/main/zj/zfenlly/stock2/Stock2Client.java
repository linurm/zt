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
package zj.zfenlly.stock2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.daodb.Note;


public final class Stock2Client {

    public final static int IMAGE_TYPE_MINITE = 0x85;
    public final static int IMAGE_TYPE_DAILY = 0x86;
    public final static int IMAGE_TYPE_WEEKLY = 0x87;
    public final static int IMAGE_TYPE_MONTHLY = 0x88;
    private final static String STOCK_URL = "http://hq.sinajs.cn/list=";
    private final static String STOCK_MINITE_URL = "http://image.sinajs.cn/newchart/min/n/";
    private final static String STOCK_DAILY_URL = "http://image.sinajs.cn/newchart/daily/n/";
    private final static String STOCK_WEEKLY_URL = "http://image.sinajs.cn/newchart/weekly/n/";
    private final static String STOCK_MONTHLY_URL = "http://image.sinajs.cn/newchart/monthly/n/";
    private final static int CONNECTION_TIMEOUT = 5000;
    private final static int SO_TIMEOUT = 30000;
    private static Stock2Client mInstance;
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    private HttpClient mHttpClient;

    private Stock2Client() {
        mHttpClient = new HttpClient();

        mHttpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(CONNECTION_TIMEOUT);
        mHttpClient.getHttpConnectionManager().getParams()
                .setSoTimeout(SO_TIMEOUT);

    }

    /**
     * 获取客户端实例。
     *
     * @return SinaStockClient
     */
    public synchronized static Stock2Client getInstance() {

        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new Stock2Client();
        }
        return mInstance;
    }

    private void print(String msg) {
        Log.e(TAG, msg);
    }

//    public List<StockInfo> getStockInfo2(String[] stockCodes)
//            throws HttpException, IOException, ParseStockInfoException {
//        String url = STOCK_URL + generateStockCodeRequest(stockCodes);
//        print("url: " + url);
//        HttpMethod method = new GetMethod(url);
//        int statusCode = mHttpClient.executeMethod(method);
//        if (statusCode != HttpStatus.SC_OK) {
//            method.releaseConnection();
//            return null;
//        }
//
//        InputStream is = method.getResponseBodyAsStream();
//        InputStreamReader reader = new InputStreamReader(
//                new BufferedInputStream(is), Charset.forName("gbk"));
//        BufferedReader bReader = new BufferedReader(reader);
//
//        List<StockInfo> list = parseSinaStockInfosFromReader(bReader);
//        bReader.close();
//        method.releaseConnection();
//
//        return list;
//    }

    public String getUrlString(String[] stockCodes) {
        int istock = Integer.parseInt(stockCodes[0]);
        String iCurStart = "19980101";
        String iCurEnd = "20170526";
        String strUrl = new String();
        if (istock >= 600000) {
            strUrl = String.format("http://quotes.money.163.com/service/chddata.html?code=0%06d&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;VOTURNOVER;VATURNOVER", istock, iCurStart, iCurEnd);
        } else {
            strUrl = String.format("http://quotes.money.163.com/service/chddata.html?code=1%06d&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;VOTURNOVER;VATURNOVER", istock, iCurStart, iCurEnd);
        }
        //print("+++++++++++++++++++++++++++++++" + strUrl);
        return strUrl;
    }

    public List<Note> getStockInfoDB(String[] stockCodes) throws HttpException,
            IOException, Stock2Info.ParseStockInfoException {
        //String url = STOCK_URL + generateStockCodeRequest(stockCodes);
        String url = getUrlString(stockCodes);

        //print("url: " + url);
        HttpMethod method = new GetMethod(url);
        int statusCode = 0;
        try {
            statusCode = mHttpClient.executeMethod(method);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (statusCode != HttpStatus.SC_OK) {
            method.releaseConnection();
            return null;
        }

        InputStream is = method.getResponseBodyAsStream();
        InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(is), Charset.forName("gbk"));
        BufferedReader bReader = new BufferedReader(reader);

        List<Note> list = parseStockInfo2DBFromReader(bReader);
        bReader.close();
        method.releaseConnection();

        return list;
    }


    public Bitmap getStockImage(String stockCode, int imageType)
            throws HttpException, IOException {
        String baseRequestUrl = null;
        switch (imageType) {
            case IMAGE_TYPE_MINITE:
                baseRequestUrl = STOCK_MINITE_URL;
                break;
            case IMAGE_TYPE_DAILY:
                baseRequestUrl = STOCK_DAILY_URL;
                break;
            case IMAGE_TYPE_WEEKLY:
                baseRequestUrl = STOCK_WEEKLY_URL;
                break;
            case IMAGE_TYPE_MONTHLY:
                baseRequestUrl = STOCK_MONTHLY_URL;
                break;
        }

        if (TextUtils.isEmpty(baseRequestUrl)) {
            return null;
        }

        String fullRequestUrl = baseRequestUrl + stockCode + ".gif";

        return getBitmapFromUrl(fullRequestUrl);
    }

    private String generateStockCodeRequest(String[] stockCodes) {

        if (stockCodes == null || stockCodes.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder(stockCodes[0]);
        final int length = stockCodes.length;

        for (int i = 1; i != length; ++i) {
            sb.append(',');
            sb.append(stockCodes[i]);
        }

        return sb.toString();
    }

    private List<Note> parseStockInfo2DBFromReader(BufferedReader reader)
            throws IOException, Stock2Info.ParseStockInfoException {

        ArrayList<Note> list = new ArrayList<Note>(10);
        String sourceLine = null;
        reader.readLine();//skip first row
        while ((sourceLine = reader.readLine()) != null) {
            //print("sourceLine: " + sourceLine);
            //Stock2Info.parseStockInfoDB(sourceLine);
            list.add(Stock2Info.parseStockInfoDB(sourceLine));
        }

        return list;
    }

    private List<Stock2Info> parseSinaStockInfosFromReader(BufferedReader reader)
            throws IOException, Stock2Info.ParseStockInfoException {

        ArrayList<Stock2Info> list = new ArrayList<Stock2Info>(10);
        String sourceLine = null;

        while ((sourceLine = reader.readLine()) != null) {
            // print("sourceLine: " + sourceLine);
            list.add(Stock2Info.parseStockInfo(sourceLine));
        }

        return list;
    }

    private Bitmap getBitmapFromUrl(String url) throws HttpException,
            IOException {

        HttpMethod method = new GetMethod(url);
        int statusCode = mHttpClient.executeMethod(method);
        if (statusCode != HttpStatus.SC_OK) {
            method.releaseConnection();
            return null;
        }

        InputStream in = method.getResponseBodyAsStream();
        BufferedInputStream bis = new BufferedInputStream(in);

        Bitmap bm = BitmapFactory.decodeStream(bis);

        bis.close();
        method.releaseConnection();

        return bm;
    }
}
