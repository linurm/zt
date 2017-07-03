package com.zj.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PhotoManager {

	public Handler mHandler;
	public static final int TASK_COMPLETE = 0;
	//
	public static PhotoManager sInstance = null;
	private static int NUMBER_OF_CORES;

	private static final int KEEP_ALIVE_TIME = 1;
	// Sets the Time Unit to seconds
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
	private static BlockingQueue<Runnable> mDecodeWorkQueue;

	static {
		// ...
		// Creates a single static instance of PhotoManager
		sInstance = new PhotoManager();
		NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

	}

	//
	public static PhotoManager getInstance() {
		return sInstance;
	}

	// static public PhotoTask startDownload(
	// PhotoView imageView,
	// boolean cacheFlag) {
	// //...
	// // Adds a download task to the thread pool for execution
	// sInstance.
	// mDownloadThreadPool.
	// execute(downloadTask.getHTTPDownloadRunnable());
	// //...
	// }

	private PhotoManager() {
		// ...
		// A queue of Runnables

		// ...
		// Instantiates the queue of Runnables as a LinkedBlockingQueue
		mDecodeWorkQueue = new LinkedBlockingQueue<Runnable>();

		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message inputMessage) {
				// Gets the task from the incoming Message object.
				PhotoTask photoTask = (PhotoTask) inputMessage.obj;
				// Gets the ImageView for this task
				// PhotoView localView = photoTask.getPhotoView();
				// ...
				switch (inputMessage.what) {
				// ...
				// The decoding is done
				case TASK_COMPLETE:
					/*
					 * Moves the Bitmap from the task to the View
					 */
					// localView.setImageBitmap(photoTask.getImage());
					break;
				// ...
				default:
					/*
					 * Pass along other messages from the UI
					 */
					super.handleMessage(inputMessage);
				}
				// ...
			}
			// ...
		};

		// Creates a thread pool manager
		// mDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, //
		// Initial
		// // pool size
		// NUMBER_OF_CORES, // Max pool size
		// KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);
		// ...
	}

	// Handle status messages from tasks
	public void handleState(PhotoTask photoTask, int state) {
		switch (state) {
		// ...
		// The task finished downloading and decoding the image
		case TASK_COMPLETE:
			/*
			 * Creates a message for the Handler with the state and the task
			 * object
			 */
			Message completeMessage = mHandler.obtainMessage(state, photoTask);
			completeMessage.sendToTarget();
			break;
		// ...
		}
		// ...
	}

	public static void cancelAll() {

		Runnable[] runnableArray = new Runnable[mDecodeWorkQueue.size()];
		// Populates the array with the Runnables in the queue
		mDecodeWorkQueue.toArray(runnableArray);
		// Stores the array length in order to iterate over the array
		int len = runnableArray.length;
		/*
		 * Iterates over the array of Runnables and interrupts each one's
		 * Thread.
		 */
		synchronized (sInstance) {
			// Iterates over the array of tasks
			for (int runnableIndex = 0; runnableIndex < len; runnableIndex++) {
				// Gets the current thread
				// Thread thread = runnableArray[runnableIndex].th;
				// // if the Thread exists, post an interrupt to it
				// if (null != thread) {
				// thread.interrupt();
				// }
			}
		}
	}
}
