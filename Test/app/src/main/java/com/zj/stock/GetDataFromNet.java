package com.zj.stock;


import org.apache.commons.httpclient.HttpException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class GetDataFromNet {

    private static final String TAG = "GetDataFromNet";

    private static final String ST_CODE = "100844";

    private List<StockData> getStockCsvData(Stock2Client msc) {
        List<StockData> l = null;
        //run
        //do {
            try {
                l = msc.getStockInfoDB(new String[]{ST_CODE});
            } catch (HttpException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        //} while (l.size() == 0);

        return l;

		/*List<StockData> list = new ArrayList<StockData>();
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
		return list;*/
    }


    public List<StockData> getListStockData(Stock2Client msc) {
        List<StockData> list = getStockCsvData(msc);
        if (list == null)
            return null;
        // for (int i = 0; i < list.size(); i++) {
        // StockData a = list.get(i);
        // // Log.i(TAG, "volume:" + a.getVolume());
        // }
        // Log.i(TAG, "SIZE:" + list.size());
        return list;
    }

    public StockData getStockData(String stockName) {
        String date = String.format("%1$tF", new Date());

        List<StockData> list = null;//getStockCsvData(stockName, date, date);
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