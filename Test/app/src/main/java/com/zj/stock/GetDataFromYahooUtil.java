package com.zj.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class GetDataFromYahooUtil {

	private static final String TAG = "GetDataFromYahooUtil";

	public static final String YAHOO_FINANCE_URL = "http://table.finance.yahoo.com/table.csv?";
	public static final String YAHOO_FINANCE_URL_TODAY = "http://download.finance.yahoo.com/d/quotes.csv?";

	/**
	 * ��� ��Ʊ���롢��ʼ���ڡ��������� ��ȡ��Ʊ���
	 * 
	 * @author �����
	 * @param stockName
	 *            ���У�000000.ss ���У�000000.sz
	 * @param fromDate
	 *            ��ʼ����
	 * @param toDate
	 *            ��������
	 * @return List<StockData>
	 */
	private List<StockData> getStockCsvData(String stockName, String fromDate,
			String toDate) {
		List<StockData> list = new ArrayList<StockData>();
		String[] datefromInfo = fromDate.split("-");
		String[] toDateInfo = toDate.split("-");
		String code = stockName.substring(0, 6);
		;

		String a = (Integer.valueOf(datefromInfo[1]) - 1) + "";// a �C ��ʼʱ�䣬��
		String b = datefromInfo[2];// b �C ��ʼʱ�䣬��
		String c = datefromInfo[0];// c �C ��ʼʱ�䣬��
		String d = (Integer.valueOf(toDateInfo[1]) - 1) + "";// d �C ����ʱ�䣬��
		String e = toDateInfo[2];// e �C ����ʱ�䣬��
		String f = toDateInfo[0];// f �C ����ʱ�䣬��

		String params = "&d=" + d + "&e=" + e + "&f=" + f + "&g=d" + "&a=" + a
				+ "&b=" + b + "&c=" + c + "&ignore=.csv";
		String url = YAHOO_FINANCE_URL + "s=" + stockName + params;

		Log.i(TAG, "" + url);

		URL MyURL = null;
		URLConnection con = null;
		InputStreamReader ins = null;
		BufferedReader in = null;
		try {
			MyURL = new URL(url);
			con = MyURL.openConnection();
			Log.i(TAG, " :" + "openConnection");
			ins = new InputStreamReader(con.getInputStream(), "UTF-8");
			Log.i(TAG, " :" + "InputStreamReader");
			in = new BufferedReader(ins);
			String newLine = in.readLine();// ������

			Log.i(TAG, "newLine:" + newLine);

			while ((newLine = in.readLine()) != null) {
				String stockInfo[] = newLine.trim().split(",");
				StockData sd = new StockData();
				sd.code = code;
				sd.date = stockInfo[0];
				sd.open = Float.valueOf(stockInfo[1]);
				sd.high = Float.valueOf(stockInfo[2]);
				sd.low = Float.valueOf(stockInfo[3]);
				sd.close = Float.valueOf(stockInfo[4]);
				sd.volume = Float.valueOf(stockInfo[5]);
				sd.adj = Float.valueOf(stockInfo[6]);
				list.add(sd);
			}
			// for(Iterator<StockData> it = list.iterator(); it.hasNext(),i++; )
			// {
			// ;
			// }

		} catch (Exception ex) {
			Log.i(TAG, "ex:" + ex);
			return null; // �޽������
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		}
		return list;
	}

	/**
	 * ��� ��Ʊ���롢���� ��ȡ��Ʊ���
	 * 
	 * @author �����
	 * @param stockName
	 *            ���У�000000.ss ���У�000000.sz
	 * @param date
	 *            ����
	 * @return StockData
	 */
	public List<StockData> getListStockData(String stockName, String fdate,
			String tdate) {
		List<StockData> list = getStockCsvData(stockName, fdate, tdate);
		if (list == null)
			return null;
		// for (int i = 0; i < list.size(); i++) {
		// StockData a = list.get(i);
		// // Log.i(TAG, "volume:" + a.getVolume());
		// }
		// Log.i(TAG, "SIZE:" + list.size());
		return list;
	}

	/**
	 * ��� ��Ʊ���� ��ȡ�����Ʊ���
	 * 
	 * @author �����
	 * @param stockName
	 *            ���У�000000.ss ���У�000000.sz
	 * @return StockData
	 */
	public StockData getStockData(String stockName) {
		String date = String.format("%1$tF", new Date());

		List<StockData> list = getStockCsvData(stockName, date, date);
		if (list == null)
			return null;
		for (int i = 0; i < list.size(); i++) {
			StockData a = list.get(i);
			// Log.i(TAG, "volume:" + a.getVolume());
		}
		// Log.i(TAG, "SIZE:" + list.size());
		return ((list.size() > 0) ? list.get(0) : null);
	}
}