package zj.zfenlly.http;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.converter.ColumnConverterFactory;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.HttpHandler.State;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: wyouflf Date: 13-11-10 Time: 下午8:10
 */
public class DownloadManager {

	private List<DownloadInfo> downloadInfoList;

	private int maxDownloadThread = 4;
	private int download_running = 0;

	private Context mContext;
	private DbUtils db;

	DownloadManager(Context appContext) {
		ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class,
				new HttpHandlerStateConverter());
		mContext = appContext;
		db = DbUtils.create(mContext);
		try {
			downloadInfoList = db.findAll(Selector.from(DownloadInfo.class));
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		if (downloadInfoList == null) {
			downloadInfoList = new ArrayList<DownloadInfo>();
		}
	}

	public int getDownloadInfoListCount() {
		return downloadInfoList.size();
	}

	public DownloadInfo getDownloadInfo(int index) {
		return downloadInfoList.get(index);
	}

	public DownloadInfo isDownloadExist(String url, String fileName,
			String target) {
		print("isDownloadExist:" + getDownloadInfoListCount());
		for (DownloadInfo downloadInfo : downloadInfoList) {
			// print("test " + downloadInfo.getDownloadUrl());
			if ((downloadInfo.getDownloadUrl().equals(url))
					&& (downloadInfo.getFileName().equals(fileName) && (downloadInfo
							.getFileSavePath().equals(target)))) {
				return downloadInfo;
			}
		}

		return null;
	}

	public void addNewDownload(String url, String fileName, String target,
			boolean autoResume, boolean autoRename,
			final RequestCallBack<File> callback) throws DbException {

		if (download_running == 1) {
			print("==1");
			return;
		}
		final DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.setDownloadUrl(url);
		downloadInfo.setAutoRename(autoRename);
		downloadInfo.setAutoResume(autoResume);
		downloadInfo.setFileName(fileName);
		downloadInfo.setFileSavePath(target);
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(url, target, autoResume,
				autoRename, new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		downloadInfoList.add(downloadInfo);
		db.saveBindingId(downloadInfo);

		download_running = 1;
		print("1");

		print("url:" + downloadInfo.getDownloadUrl());
		print("filename:" + downloadInfo.getFileName());
		print("save path:" + downloadInfo.getFileSavePath());
		print("isAutoResume:" + downloadInfo.isAutoResume());
		print("isAutoRename:" + downloadInfo.isAutoRename());

		// resumeDownload(downloadInfo, callback);
	}

	private final String TAG = this.getClass().getName()
			.substring(this.getClass().getName().lastIndexOf(".") + 1);

	public static final State LOADING = null;

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	public void resumeDownload(int index, final RequestCallBack<File> callback)
			throws DbException {

		final DownloadInfo downloadInfo = downloadInfoList.get(index);
		resumeDownload(downloadInfo, callback);

	}

	public enum DState {
		IDLE(0), DOWNLOADED(1), DOWNLOADING(2), REDOWNLOAD(3);
		private int value = 0;

		DState(int value) {
			this.value = value;
		}

		public static DState valueOf(int value) {
			switch (value) {
			case 0:
				return IDLE;// 未下载
			case 1:
				return DOWNLOADED;// 已经下载完成
			case 2:
				return DOWNLOADING;// 继续下载
			case 3:
				return REDOWNLOAD;// 重新下载
			default:
				return IDLE;
			}
		}

		public int value() {
			return this.value;
		}
	}

	@SuppressWarnings({ "unused", "resource" })
	public DState checkForDownload(DownloadInfo downloadInfo) {
		HttpHandler.State state = downloadInfo.getState();

		// switch (state) {
		// case SUCCESS:
		print("记录的已经下载大小:" + downloadInfo.getFileLength());

		File dF = new File(downloadInfo.getFileSavePath());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(dF);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			print("error 1");
		}
		long fileLen = 0;
		if (fis != null) {
			try {
				fileLen = fis.available();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				print("error 2");
			}
		}
		print("实际下载文件大小:" + fileLen);
		if ((fileLen == downloadInfo.getFileLength()) && (fileLen != 0)) {
			print("不需要再下载了");
			return DState.DOWNLOADED;// 不需要再下载了
		} else if ((fileLen < downloadInfo.getFileLength()) && (fileLen != 0)) {
			print("需要继续下载了");// 需要继续下载了
			return DState.DOWNLOADING;
		} else {
			print("需要重新下载");
			try {
				removeDownload(downloadInfo);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return DState.REDOWNLOAD;
		}
		// }

	}

	public void resumeDownload(DownloadInfo downloadInfo,
			final RequestCallBack<File> callback) throws DbException {

		// if (download_running == 1) {
		// print("== 1");
		// return;
		// }

		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(
				downloadInfo.getDownloadUrl(), downloadInfo.getFileSavePath(),
				downloadInfo.isAutoResume(), downloadInfo.isAutoRename(),
				new ManagerCallBack(downloadInfo, callback));
		downloadInfo.setHandler(handler);
		downloadInfo.setState(handler.getState());
		db.saveOrUpdate(downloadInfo);

		// download_running = 1;
		// print("1111");
	}

	public void removeDownload(int index) throws DbException {
		DownloadInfo downloadInfo = downloadInfoList.get(index);
		removeDownload(downloadInfo);
	}

	public void removeDownload(DownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		}
		downloadInfoList.remove(downloadInfo);
		db.delete(downloadInfo);
	}

	public void stopDownload(int index) throws DbException {
		DownloadInfo downloadInfo = downloadInfoList.get(index);
		stopDownload(downloadInfo);
	}

	public void stopDownload(DownloadInfo downloadInfo) throws DbException {
		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		} else {
			downloadInfo.setState(HttpHandler.State.CANCELLED);
		}
		db.saveOrUpdate(downloadInfo);
	}

	public void stopAllDownload() throws DbException {
		for (DownloadInfo downloadInfo : downloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null && !handler.isCancelled()) {
				handler.cancel();
			} else {
				downloadInfo.setState(HttpHandler.State.CANCELLED);
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public void backupDownloadInfoList() throws DbException {
		for (DownloadInfo downloadInfo : downloadInfoList) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	public int getMaxDownloadThread() {
		return maxDownloadThread;
	}

	public void setMaxDownloadThread(int maxDownloadThread) {
		this.maxDownloadThread = maxDownloadThread;
	}

	public class ManagerCallBack extends RequestCallBack<File> {
		private DownloadInfo downloadInfo;
		private RequestCallBack<File> baseCallBack;

		public RequestCallBack<File> getBaseCallBack() {
			return baseCallBack;
		}

		public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
		}

		private ManagerCallBack(DownloadInfo downloadInfo,
				RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
			this.downloadInfo = downloadInfo;
		}

		@Override
		public Object getUserTag() {
			if (baseCallBack == null)
				return null;
			return baseCallBack.getUserTag();
		}

		@Override
		public void setUserTag(Object userTag) {
			if (baseCallBack == null)
				return;
			baseCallBack.setUserTag(userTag);
		}

		@Override
		public void onStart() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
				print("onStart");
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onStart();
			}
		}

		@Override
		public void onCancelled() {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
				download_running = 0;
				print("onCancelled 0");
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onCancelled();
			}
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			downloadInfo.setFileLength(total);
			downloadInfo.setProgress(current);
			try {
				db.saveOrUpdate(downloadInfo);
				// download_running = 0;
				print("onLoading:" + total);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onLoading(total, current, isUploading);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
				download_running = 0;
				print("onSuccess 0");
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onSuccess(responseInfo);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			HttpHandler<File> handler = downloadInfo.getHandler();
			if (handler != null) {
				downloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(downloadInfo);
				download_running = 0;
				print("onFailure 0");
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onFailure(error, msg);
			}
		}
	}

	private class HttpHandlerStateConverter implements
			ColumnConverter<HttpHandler.State> {

		@Override
		public HttpHandler.State getFieldValue(Cursor cursor, int index) {
			return HttpHandler.State.valueOf(cursor.getInt(index));
		}

		@Override
		public HttpHandler.State getFieldValue(String fieldStringValue) {
			if (fieldStringValue == null)
				return null;
			return HttpHandler.State.valueOf(fieldStringValue);
		}

		@Override
		public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
			return fieldValue.value();
		}

		@Override
		public ColumnDbType getColumnDbType() {
			return ColumnDbType.INTEGER;
		}
	}
}
