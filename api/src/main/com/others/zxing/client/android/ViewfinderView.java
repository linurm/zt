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

package com.others.zxing.client.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.others.zxing.ResultPoint;
import com.others.zxing.client.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.tools.R;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
	private static final long ANIMATION_DELAY = 80L;
	private static final int CURRENT_POINT_OPACITY = 0xA0;
	private static final int MAX_RESULT_POINTS = 20;
	private static final int POINT_SIZE = 6;

	private CameraManager cameraManager;
	private final Paint paint;
	private Bitmap resultBitmap;
	private final String TAG = "ViewfinderView";
	private final int maskColor;
	private final int resultColor;
	private final int crossColor;
	private final int crossWidth = 100;
	private final int crossHeight = 5;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private List<ResultPoint> possibleResultPoints;
	private List<ResultPoint> lastPossibleResultPoints;
	private int width, height;
	private Rect frame;
	private static int ii = 0;

	// ivate boolean ss = false;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		crossColor = resources.getColor(R.color.cross_view);
		// laserColor = resources.getColor(R.color.cross_view);
		laserColor = resources.getColor(R.color.viewfinder_laser);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		// resultPointColor2 =
		// resources.getColor(R.color.possible_result_points2);
		scannerAlpha = 0;
		possibleResultPoints = new ArrayList<>(5);
		lastPossibleResultPoints = null;
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		frame = cameraManager.getFramingRect();
		Rect previewFrame = cameraManager.getFramingRectInPreview();

		if (frame == null || previewFrame == null) {
			return;
		}
		width = canvas.getWidth();
		height = canvas.getHeight();
		// frame:Rect(202, 622 - 877, 1297)
		// Log.e("viewfinder", "frame:" + frame);
		// previewFrame:Rect(623, 202 - 1298, 877)
		// Log.e("viewfinder", "previewFrame:" + previewFrame);
		// width:1080 height:1920
		// Log.e("viewfinder", "width:" + width + " height:" + height);
		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// Log.e(TAG, "drawRect: " + 0 + "," + 0 + "," + width + "," +
		// frame.top);
		// Log.e(TAG, "drawRect: " + 0 + "," + frame.top + "," + frame.left +
		// ","
		// + frame.bottom + 1);

		// Log.e(TAG, "drawRect: " + frame.right + 1 + "," + frame.top + ","
		// + width + "," + frame.bottom + 1);

		// Log.e(TAG, "drawRect: " + 0 + "," + frame.bottom + 1 + "," + width
		// + "," + height);

		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		//
		paint.setColor(crossColor);
		canvas.drawRect(frame.left, frame.top, frame.left + crossWidth,
				frame.top + crossHeight, paint);
		canvas.drawRect(frame.right - crossWidth, frame.top, frame.right,
				frame.top + crossHeight, paint);

		canvas.drawRect(frame.left, frame.top, frame.left + crossHeight,
				frame.top + crossWidth, paint);
		canvas.drawRect(frame.right - crossHeight, frame.top, frame.right,
				frame.top + crossWidth, paint);

		canvas.drawRect(frame.left, frame.bottom - crossHeight, frame.left
				+ crossWidth, frame.bottom, paint);
		canvas.drawRect(frame.right - crossWidth, frame.bottom - crossHeight,
				frame.right, frame.bottom, paint);

		canvas.drawRect(frame.left, frame.bottom - crossWidth, frame.left
				+ crossHeight, frame.bottom, paint);
		canvas.drawRect(frame.right - crossHeight, frame.bottom - crossWidth,
				frame.right, frame.bottom, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			// Log.e(TAG, "drawBitmap");
			paint.setAlpha(CURRENT_POINT_OPACITY);
			canvas.drawBitmap(resultBitmap, null, frame, paint);
			// ok
		} else {

			// Log.e(TAG, "resultBitmap NULL");

			// Draw a red "laser scanner" line through the middle to show
			// decoding is active
			paint.setColor(laserColor);
			paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
			scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
			int a = frame.height() / 50;

			if (ii++ >= 50)
				ii = 0;
			int middle = a * ii + frame.top;
			// int middle = frame.height() / 2 + frame.top;
			// draw red line in middle
			canvas.drawRect(frame.left + 5, middle - 5, frame.right - 5,
					middle + 5, paint);

			float scaleX = frame.width() / (float) previewFrame.width();
			float scaleY = frame.height() / (float) previewFrame.height();

			List<ResultPoint> currentPossible = possibleResultPoints;
			List<ResultPoint> currentLast = lastPossibleResultPoints;
			int frameLeft = frame.left;
			int frameTop = frame.top;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new ArrayList<>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(CURRENT_POINT_OPACITY);
				paint.setColor(resultPointColor);

				synchronized (currentPossible) {
					for (ResultPoint point : currentPossible) {
						// yellow point
						canvas.drawCircle(frameLeft
								+ (int) (point.getX() * scaleX), frameTop
								+ (int) (point.getY() * scaleY), POINT_SIZE,
								paint);
						// Log.e(TAG, "drawCircle1" + point.toString() + ":"
						// + scaleX + ":" + scaleY);
					}
				}
			}
			if (currentLast != null) {
				paint.setAlpha(CURRENT_POINT_OPACITY / 2);
				paint.setColor(resultPointColor);
				synchronized (currentLast) {
					float radius = POINT_SIZE / 2.0f;

					for (ResultPoint point : currentLast) {
						canvas.drawCircle(frameLeft
								+ (int) (point.getX() * scaleX), frameTop
								+ (int) (point.getY() * scaleY), radius, paint);
						// Log.e(TAG, "drawCircle2" + point.toString());
					}
				}
			}

			// Request another update at the animation interval, but only
			// repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed(ANIMATION_DELAY, frame.left - POINT_SIZE,
					frame.top - POINT_SIZE, frame.right + POINT_SIZE,
					frame.bottom + POINT_SIZE);
		}
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			int rotation = cameraManager.getCameraConfigurationManager()
					.getCWNeededRotation();

//			Log.e(TAG, "addPossibleResultPoint:" + point.toString());
			// points.add(point);
			points.add(ResultPoint.rotationPoint(point, frame.width(),
					frame.height(), rotation));
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

}
