package zj.zfenlly.other;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import zj.zfenlly.main.MainApplication;

public class ZfenllyService extends Service implements Observer {

	public MainApplication mApplication;
	private String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);



	private boolean isstart = false;

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



	public void onCreate() {
		mApplication = (MainApplication) getApplication();
		mApplication.addObserver(this);
		isstart = true;
		//mTcpPollThread.start();

		Log.i(TAG, "onCreate");
	}

	private Thread mTcpPollThread = new Thread() {
		Socket client;

		private void PaseData(String d) {
			Log.i(TAG, "R  " + d.split("\\+").length);
			mApplication.clearFind();
			for (int i = 0; i < d.split("\\+").length; i++) {
				// Log.i(TAG, "R  " + d.split("\\+")[i]);
				mApplication.addFind(d.split("\\+")[i]);
			}

			// Log.i(TAG,"R  "+ d.split("\\+")[2]);
			// Log.i(TAG,"R  "+ d.split("\\+")[3]);
			// Log.i(TAG,"R  "+ d.split("\\+")[4]);
			//

			mApplication.notify_find();
		}

		@Override
		public void run() {
			// rs.Poll(1);
			Log.i(TAG, "mTcpPollThread thread run");
			// while (isstart) {
			try {
				Thread.sleep(20);
				@SuppressWarnings("resource")
				ServerSocket serverSocket = new ServerSocket(mApplication.port);

				while (isstart) {
					client = serverSocket.accept();
					// System.out.println("S: Receiving...");

					InputStream inputStream = client.getInputStream();
					OutputStream outputStream = client.getOutputStream();
					byte buffer1[] = "end".getBytes();

					byte buffer[] = new byte[512];
					int temp = 0;
					if ((temp = inputStream.read(buffer)) != -1) {
						String a = new String(buffer, 0, temp);
						// System.out.println("1111111"+a);
						PaseData(a);

					}

					outputStream.write(buffer1);
					outputStream.flush();
					// System.out.println("S: send.");
					client.close();
					// System.out.println("S: Done.");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// }//
			Log.i(TAG, "mTcpPollThread thread exit");
		}
	};

	public void onDestroy() {
		mApplication.deleteObserver(this);
		isstart = false;
		if (mTcpPollThread.isAlive()) {
			try {

				mTcpPollThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.i(TAG, "onDestroy");
	}

}
