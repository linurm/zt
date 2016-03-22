/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.others.zxing.client.android.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.others.zxing.PlanarYUVLuminanceSource;
import com.others.zxing.client.android.CaptureActivity;
import com.others.zxing.client.android.camera.open.OpenCamera;
import com.others.zxing.client.android.camera.open.OpenCameraInterface;

import java.io.IOException;

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	private static final int MIN_FRAME_WIDTH = 240;
	private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 675; // = 5/8 * 1920
	private static final int MAX_FRAME_HEIGHT = 1200; // = 5/8 * 1080

	private final Context context;
	private final CameraConfigurationManager configManager;
	private OpenCamera camera;
	private AutoFocusManager autoFocusManager;
	private Rect framingRect;
	private Rect framingRectInPreview;
	private boolean initialized;
	private boolean previewing;
	private int requestedCameraId = OpenCameraInterface.NO_REQUESTED_CAMERA;
	private int requestedFramingRectWidth;
	private int requestedFramingRectHeight;
	private int cameraRotation;

	// MyOrientationEventListnener myOrientationEventListnener;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;

	public CameraManager(Context context) {
		this.context = context;
		this.configManager = new CameraConfigurationManager(context);
		previewCallback = new PreviewCallback(configManager);
	}

	public CameraConfigurationManager getCameraConfigurationManager() {
		return configManager;
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder)
			throws IOException {
		OpenCamera theCamera = camera;
		if (theCamera == null) {
			theCamera = OpenCameraInterface.open(requestedCameraId);
			if (theCamera == null) {
				throw new IOException(
						"Camera.open() failed to return object from driver");
			}
			camera = theCamera;
		}

		if (!initialized) {
			initialized = true;
			configManager.initFromCameraParameters(theCamera);
			if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
				setManualFramingRect(requestedFramingRectWidth,
						requestedFramingRectHeight);
				requestedFramingRectWidth = 0;
				requestedFramingRectHeight = 0;
			}
		}

		Camera cameraObject = theCamera.getCamera();
		Camera.Parameters parameters = cameraObject.getParameters();
		String parametersFlattened = parameters == null ? null : parameters
				.flatten(); // Save these, temporarily
		// Log.e(TAG, "parametersFlattened=" + parametersFlattened);
		try {
			configManager.setDesiredCameraParameters(theCamera, false);
		} catch (RuntimeException re) {
			// Driver failed
			Log.w(TAG,
					"Camera rejected parameters. Setting only minimal safe-mode parameters");
			Log.i(TAG, "Resetting to saved camera params: "
					+ parametersFlattened);
			// Reset:
			if (parametersFlattened != null) {
				parameters = cameraObject.getParameters();
				parameters.unflatten(parametersFlattened);
				try {
					cameraObject.setParameters(parameters);
					configManager.setDesiredCameraParameters(theCamera, true);
				} catch (RuntimeException re2) {
					// Well, darn. Give up
					Log.w(TAG,
							"Camera rejected even safe-mode parameters! No configuration");
				}
			}
		}
		cameraObject.setPreviewDisplay(holder);

	}

	public synchronized boolean isOpen() {
		return camera != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (camera != null) {
			camera.getCamera().release();
			camera = null;
			// Make sure to clear these each time we close the camera, so that
			// any scanning rect
			// requested by intent is forgotten.
			framingRect = null;
			framingRectInPreview = null;
		}
	}

	// class MyOrientationEventListnener extends OrientationEventListener {
	// public MyOrientationEventListnener(Context context) {
	// super(context);
	// }
	//
	// @Override
	// public void onOrientationChanged(int orientation) {
	//
	// if (orientation == ORIENTATION_UNKNOWN)
	// return;
	// CameraInfo info = new CameraInfo();
	// Camera.getCameraInfo(camera.getIndex(), info);
	// orientation = (orientation + 45) / 90 * 90;
	// int rotation = 0;
	// if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	// rotation = (info.orientation - orientation + 360) % 360;
	// } else { // back-facing camera
	// rotation = (info.orientation + orientation) % 360;
	// }
	//
	//
	// if (isOpen()) {
	// setRotation(rotation);
	//
	// }
	// }
	// }

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public synchronized void startPreview() {
		OpenCamera theCamera = camera;
		if (theCamera != null && !previewing) {
			theCamera.getCamera().startPreview();
			previewing = true;
			// myOrientationEventListnener = new MyOrientationEventListnener(
			// context);
			// if (myOrientationEventListnener.canDetectOrientation()) {
			// myOrientationEventListnener.enable();
			// } else {
			//
			// }

			autoFocusManager = new AutoFocusManager(context,
					theCamera.getCamera());
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public synchronized void stopPreview() {
		if (autoFocusManager != null) {
			autoFocusManager.stop();
			autoFocusManager = null;
		}
		if (camera != null && previewing) {
			// myOrientationEventListnener.disable();
			camera.getCamera().stopPreview();
			previewCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * Convenience method for
	 * {@link CaptureActivity}
	 * 
	 * @param newSetting
	 *            if {@code true}, light should be turned on if currently off.
	 *            And vice versa.
	 */
	public synchronized void setTorch(boolean newSetting) {
		OpenCamera theCamera = camera;
		if (theCamera != null) {
			if (newSetting != configManager
					.getTorchState(theCamera.getCamera())) {
				boolean wasAutoFocusManager = autoFocusManager != null;
				if (wasAutoFocusManager) {
					autoFocusManager.stop();
					autoFocusManager = null;
				}
				configManager.setTorch(theCamera.getCamera(), newSetting);
				if (wasAutoFocusManager) {
					autoFocusManager = new AutoFocusManager(context,
							theCamera.getCamera());
					autoFocusManager.start();
				}
			}
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 * 
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		OpenCamera theCamera = camera;
		if (theCamera != null && previewing) {
			previewCallback.setHandler(handler, message);
			theCamera.getCamera().setOneShotPreviewCallback(previewCallback);
		}
	}

	public synchronized Rect getFramingRectInPreview1() {
		if (framingRectInPreview == null) {
			Rect framingRect = getFramingRect();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				// Called early, before init even finished
				return null;
			}

			// Log.e(TAG, "rect: " + rect);
			// rect: Rect(202, 360 - 877, 1560)
			// Log.e(TAG, "screenResolution: " + screenResolution);
			// screenResolution: Point(1080, 1920)
			// Log.e(TAG, "cameraResolution: " + cameraResolution);
			// cameraResolution: Point(1920, 1080)
			rect.left = rect.left * cameraResolution.x / screenResolution.x;
			rect.right = rect.right * cameraResolution.x / screenResolution.x;
			rect.top = rect.top * cameraResolution.y / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
			// Log.e(TAG, "rect: " + rect);
			// rect: Rect(359, 202 - 1559, 877)
			framingRectInPreview = rect;
		}
		return framingRectInPreview;
	}

	// public synchronized void setRotation(int rotation) {
	// if (camera == null) {
	// return;
	// }
	// // Parameters parameter = camera.getCamera().getParameters();
	// // parameter.setRotation(rotation);
	// //Log.e(TAG, "setRotation: " + rotation);
	// cameraRotation = rotation;
	// // camera.getCamera().setParameters(parameter);
	// return;
	// }

	public int getCameraRotation() {
		return cameraRotation;
	}

	/**
	 * Calculates the framing rect which the UI should draw to show the user
	 * where to place the barcode. This target helps with alignment as well as
	 * forces the user to hold the device far enough away to ensure the image
	 * will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public synchronized Rect getFramingRect() {
		if (framingRect == null) {
			if (camera == null) {
				return null;
			}
			Point screenResolution = configManager.getScreenResolution();
			if (screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			// 1080*1920
			// Log.e(TAG, "screenResolution:" + screenResolution);
			int x, y;
			// if (screenResolution.x > screenResolution.y) {
			x = screenResolution.x;
			y = screenResolution.y;
			// } else {
			// x = screenResolution.x;
			// y = screenResolution.y;
			// }
			int width = findDesiredDimensionInRange(x, MIN_FRAME_WIDTH,
					MAX_FRAME_WIDTH);
			int height = findDesiredDimensionInRange(y, MIN_FRAME_HEIGHT,
					MAX_FRAME_HEIGHT);
			// display a fine window
			if (width > height) {
				if (width >= 1.3 * height) {
					width = height;
				}
			} else {
				if (height >= 1.3 * width) {
					height = width;
				}
			}
			// Log.d(TAG, "width=" + width + " height=" + height);
			int leftOffset = (x - width) / 2;
			int topOffset = (y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			// Log.e(TAG, "getFramingRect: " + framingRect);
		}
		return framingRect;
	}

	private static int findDesiredDimensionInRange(int resolution, int hardMin,
			int hardMax) {
		int dim = 5 * resolution / 8; // Target 5/8 of each dimension
		if (dim < hardMin) {
			return hardMin;
		}
		if (dim > hardMax) {
			return hardMax;
		}
		return dim;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 * 
	 * @return {@link Rect} expressing barcode scan area in terms of the preview
	 *         size
	 */
	public synchronized Rect getFramingRectInPreview() {
		if (framingRectInPreview == null) {
			Rect framingRect = getFramingRect();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			// Log.d(TAG, "rect: " + rect);
			Rect rect2 = new Rect(framingRect);
			Point cameraResolution = configManager.getCameraResolution();
			Point screenResolution = configManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			//
//			Log.e(TAG, "Orientation:"
//					+ configManager.cwRotationFromNaturalToDisplay);

			int cx = 0, cy = 0;
			int sx = 0, sy = 0;
			switch (configManager.cwRotationFromNaturalToDisplay) {
			case 90:// l
			case 270:
				cx = cameraResolution.x;// 1920
				cy = cameraResolution.y;// 1080
				sx = screenResolution.x;// 1920
				sy = screenResolution.y;// 1080
				rect2.left = rect.left * cx / sx;
				rect2.right = rect.right * cx / sx;
				rect2.top = rect.top * cy / sy;
				rect2.bottom = rect.bottom * cy / sy;

				break;
			case 0:// p
			case 180:
			default:
				cx = cameraResolution.x;// 1920
				cy = cameraResolution.y;// 1080
				sx = screenResolution.x;// 1080
				sy = screenResolution.y;// 1920
				rect2.left = (sy - rect.bottom) * cx / sy;
				rect2.right = (sy - rect.top) * cx / sy;
				rect2.top = rect.left * cy / sx;
				rect2.bottom = rect.right * cy / sx;
				break;
			}
			// getFramingRect: Rect(622, 202 - 1297, 877)
//			Log.d(TAG, "rect: " + rect);
			// rect: Rect(202, 622 - 877, 1297)
//			Log.d(TAG, "screenResolution: " + screenResolution);
			// screenResolution: Point(1080, 1920)
			// Log.d(TAG, "cx: " + cx + " cy: " + cy);
			// cameraResolution: Point(1920, 1080)

			// previewFrame:Rect(359, 349 - 1559, 729)
//			Log.e(TAG, "getFramingRectInPreview: " + rect2);
			// getFramingRectInPreview: Rect(359, 349 - 1559, 729)
			framingRectInPreview = rect2;
		}
		return framingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the camera ID, rather than determine
	 * it automatically based on available cameras and their orientation.
	 * 
	 * @param cameraId
	 *            camera ID of the camera to use. A negative value means
	 *            "no preference".
	 */
	public synchronized void setManualCameraId(int cameraId) {
		requestedCameraId = cameraId;
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions,
	 * rather than determine them automatically based on screen resolution.
	 * 
	 * @param width
	 *            The width in pixels to scan.
	 * @param height
	 *            The height in pixels to scan.
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (initialized) {
			Point screenResolution = configManager.getScreenResolution();
			if (width > screenResolution.x) {
				width = screenResolution.x;
			}
			if (height > screenResolution.y) {
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(TAG, "Calculated manual framing rect: " + framingRect);
			framingRectInPreview = null;
		} else {
			requestedFramingRectWidth = width;
			requestedFramingRectHeight = height;
		}
	}

	/**
	 * A factory method to build the appropriate LuminanceSource object based on
	 * the format of the preview buffers, as described by Camera.Parameters.
	 * 
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
			int width, int height) {
		Rect rect = getFramingRectInPreview();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.
		// Log.e(TAG,
		// "width=" + width + " height=" + height + " rect.left="
		// + rect.left + " rect.top=" + rect.top
		// + " rect.width()=" + rect.width() + " rect.height()="
		// + rect.height());
		return new PlanarYUVLuminanceSource(data, width, height, rect.left,
				rect.top, rect.width(), rect.height(), false);
	}
}
