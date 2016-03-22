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
package zj.zfenlly.stock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.zfenlly.dao.Note;

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

import zj.zfenlly.stock.StockInfo.ParseStockInfoException;

/**
 * 新浪数据获取客户端，提供个股行情查询、大盘指数查询、个股分时图、K线图等基本数据查询服务。
 *
 * @author <a href="http://www.blogjava.net/zh-weir/"
 *         target="_blank">zh.weir的技术博客</a> </br> </br> <a
 *         href="http://weibo.com/1779382071?s=6uyXnP" target="_blank"><img
 *         border="0" src=
 *         "http://service.t.sina.com.cn/widget/qmd/1779382071/9c2d28b9/1.png"
 *         /></a>
 */
public final class StockClient {

    private final static String STOCK_URL = "http://hq.sinajs.cn/list=";

    private final static String STOCK_MINITE_URL = "http://image.sinajs.cn/newchart/min/n/";
    private final static String STOCK_DAILY_URL = "http://image.sinajs.cn/newchart/daily/n/";
    private final static String STOCK_WEEKLY_URL = "http://image.sinajs.cn/newchart/weekly/n/";
    private final static String STOCK_MONTHLY_URL = "http://image.sinajs.cn/newchart/monthly/n/";

    public final static int IMAGE_TYPE_MINITE = 0x85;
    public final static int IMAGE_TYPE_DAILY = 0x86;
    public final static int IMAGE_TYPE_WEEKLY = 0x87;
    public final static int IMAGE_TYPE_MONTHLY = 0x88;

    private final static int CONNECTION_TIMEOUT = 5000;
    private final static int SO_TIMEOUT = 30000;
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    private HttpClient mHttpClient;

    private static StockClient mInstance;

    private StockClient() {
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
    public synchronized static StockClient getInstance() {

        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new StockClient();
        }
        return mInstance;
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    /**
     * 通过股票代码，获取行情信息。
     *
     * @param stockCodes 股票代码数组。沪市股票代码以"sh+股票代码", 深市股票代码以"sz+股票代码"。
     * @return 成功返回List<SinaStockInfo>，失败返回null。
     * @throws IOException
     * @throws HttpException
     * @throws ParseStockInfoException
     */
    public List<StockInfo> getStockInfo(String[] stockCodes)
            throws HttpException, IOException, ParseStockInfoException {
        String url = STOCK_URL + generateStockCodeRequest(stockCodes);
        // print("url: " + url);
        HttpMethod method = new GetMethod(url);
        int statusCode = mHttpClient.executeMethod(method);
        if (statusCode != HttpStatus.SC_OK) {
            method.releaseConnection();
            return null;
        }

        InputStream is = method.getResponseBodyAsStream();
        InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(is), Charset.forName("gbk"));
        BufferedReader bReader = new BufferedReader(reader);

        List<StockInfo> list = parseSinaStockInfosFromReader(bReader);
        bReader.close();
        method.releaseConnection();

        return list;
    }

    public List<Note> getStockInfoDB(String[] stockCodes) throws HttpException,
            IOException, ParseStockInfoException {
        String url = STOCK_URL + generateStockCodeRequest(stockCodes);
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

        List<Note> list = parseStockInfosDBFromReader(bReader);
        bReader.close();
        method.releaseConnection();

        return list;
    }

    /**
     * 获取股票分时图或K线图。
     *
     * @param stockCode 股票代码。沪市股票代码以"sh+股票代码", 深市股票代码以"sz+股票代码"。
     * @param imageType 指明请求的图像类型。 IMAGE_TYPE_MINITE 为分时图。 IMAGE_TYPE_DAILY 为日K线图。
     *                  IMAGE_TYPE_WEEKLY 为周K线图。 IMAGE_TYPE_MONTHLY 为月K线图。
     * @return 如果成功则返回Bitmap图像，失败返回null。
     * @throws IOException
     * @throws HttpException
     */
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

    private List<Note> parseStockInfosDBFromReader(BufferedReader reader)
            throws IOException, ParseStockInfoException {

        ArrayList<Note> list = new ArrayList<Note>(10);
        String sourceLine = null;

        while ((sourceLine = reader.readLine()) != null) {
            // print("sourceLine: " + sourceLine);
            list.add(StockInfo.parseStockInfoDB(sourceLine));
        }

        return list;
    }

    private List<StockInfo> parseSinaStockInfosFromReader(BufferedReader reader)
            throws IOException, ParseStockInfoException {

        ArrayList<StockInfo> list = new ArrayList<StockInfo>(10);
        String sourceLine = null;

        while ((sourceLine = reader.readLine()) != null) {
            // print("sourceLine: " + sourceLine);
            list.add(StockInfo.parseStockInfo(sourceLine));
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
