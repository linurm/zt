package zj.zfenlly.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PreviewCallback {
	SurfaceHolder mHolder;
	Camera mCamera;

	private final String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		// Size size = camera.getParameters().getPreviewSize();
		// try {
		// YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,
		// size.height, null);
		// if (image != null) {
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// image.compressToJpeg(new Rect(0, 0, size.width, size.height),
		// 80, stream);
		// Bitmap bmp = BitmapFactory.decodeByteArray(
		// stream.toByteArray(), 0, stream.size());
		//
		// stream.close();
		// }
		// } catch (Exception ex) {
		// Log.e("Sys", "Error:" + ex.getMessage());
		// }
		print("onPreviewFrame");
	}

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		// mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		print("surfaceCreated");
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		print("surfaceDestroyed");
		// empty. Take care of releasing the Camera preview in your activity.
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		print("surfaceChanged: " + w + "*" + h);
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
