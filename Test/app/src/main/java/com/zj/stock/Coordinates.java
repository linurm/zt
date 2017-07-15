package com.zj.stock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Coordinates extends View {
    public final int mOnew = 18;// w
    private final String TAG = "Coordinates";
    private final boolean DEBUG = true;
    private final int V_PAD = 20;
    private final int mOnem = mOnew / 2;// middle
    private final int mOnepad = 1;// kong
    private final int mOnev = mOnem - mOnepad;
    public int dayNum = 0;
    private Paint mPaint;
    private Paint mRedPaint;
    private Paint mGreenPaint;
    private Paint mBluePaint;
    private Paint mYellowPaint;
    private Paint mPurplePaint;
    private List<Paint> mPaintList;
    private List<RectF> mRectFsList;
    private List<Paint> mVPaintList;
    private List<RectF> mVRectFsList;
    private List<Paint> mPPaintList;
    private List<PointF[]> mPointFList;
    private float hParam;
    private float mHParam, mLParam, mParam;

    public Coordinates(Context context) {
        super(context, null);
        initColorPaint();


    }

    public Coordinates(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColorPaint();
    }

    private void initColorPaint() {
        // 锟斤拷锟斤拷锟斤拷锟斤拷
        mPaint = new Paint();
        mPaint.setColor(Color.RED);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);

        mGreenPaint = new Paint();
        mGreenPaint.setColor(Color.GREEN);

        mBluePaint = new Paint();
        mYellowPaint = new Paint();
        mPurplePaint = new Paint();
        mBluePaint.setColor(Color.BLUE);
        mYellowPaint.setColor(Color.YELLOW);
        mPurplePaint.setColor(Color.MAGENTA);

    }

    private void addRectF(RectF rect, Paint paint) {
        if (rect == null)
            return;
        if (mRectFsList == null)
            mRectFsList = new ArrayList<RectF>();
        mRectFsList.add(rect);
        if (mPaintList == null)
            mPaintList = new ArrayList<Paint>();
        if (paint != null)
            mPaintList.add(paint);
        else {
            mPaintList.add(mPaint);
        }
    }

    private void addVRectF(RectF rect, Paint paint) {
        if (rect == null)
            return;
        if (mVRectFsList == null)
            mVRectFsList = new ArrayList<RectF>();
        mVRectFsList.add(rect);
        // Log.e("12", "34");
        if (mVPaintList == null)
            mVPaintList = new ArrayList<Paint>();
        if (paint != null)
            mVPaintList.add(paint);
        else {
            mVPaintList.add(mPaint);
        }
    }

    private void addLine(PointF[] pf, Paint paint) {
        if (pf == null)
            return;
        if (mPointFList == null)
            mPointFList = new ArrayList<PointF[]>();
        mPointFList.add(pf);
        if (mPPaintList == null)
            mPPaintList = new ArrayList<Paint>();
        if (paint != null)
            mPPaintList.add(paint);
        else {
            mPPaintList.add(mPaint);
        }
    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.e(TAG, "onMeasure" + widthMeasureSpec + " " + heightMeasureSpec);
//    }

    /*
     * 锟皆讹拷锟斤拷丶锟揭伙拷愣硷拷锟斤拷锟斤拷锟給nDraw(Canvas
     * canvas)锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟皆硷拷锟斤拷要锟斤拷图锟斤拷
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas == null) {
            return;
        }

        canvas.drawColor(Color.BLACK);

        drawMultiRect(canvas, mRectFsList, mPaintList);
        drawLines(canvas, mPointFList, mPPaintList);
        drawMultiVRect(canvas, mVRectFsList, mVPaintList);
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟�
     */
    private void drawRectFs(Canvas canvas, RectF rect, Paint paint) {

        if (rect != null) {
            canvas.drawRect(rect, paint);
            if (DEBUG)
                Log.e(TAG, "drawRectFs: " + rect.left + ":" + rect.top + ":"
                        + rect.right + ":" + rect.bottom);
        }
    }

    private void drawVRectFs(Canvas canvas, RectF rect, Paint paint) {

        if (rect != null) {
            canvas.drawRect(rect, paint);
            if (DEBUG)
                Log.e(TAG, "drawVRectFs: " + rect.left + ":" + rect.top + ":"
                        + rect.right + ":" + rect.bottom);
        }
    }

    /**
     * 锟斤拷锟斤拷
     */
    private void drawLine(Canvas canvas, PointF pa, PointF pb, Paint paint) {
        canvas.drawLine(pa.x, pa.y, pb.x, pb.y, paint);
        if (DEBUG)
            Log.i(TAG, "drawLine: " + pa.x + ":" + pa.y + ":" + pb.x + ":"
                    + pb.y);
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟�
     */
    private void drawLine(Canvas canvas, PointF[] point, Paint paint) {
        int len = (point == null) ? 0 : point.length;
        if (len > 0) {
            PointF pa = point[0];
            PointF pb = point[1];

            drawLine(canvas, pa, pb, paint);

        }
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
     */
    private void drawLines(Canvas canvas, List<PointF[]> pointList,
                           List<Paint> paintList) {
        int len = (pointList == null) ? 0 : pointList.size();
        for (int i = 0; i < len; i++) {
            drawLine(canvas, pointList.get(i), paintList.get(i));
        }
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷锟�
     */
    private void drawMultiRect(Canvas canvas, List<RectF> pointList,
                               List<Paint> paintList) {
        int len = (pointList == null) ? 0 : pointList.size();
        // Log.e("222", "111");
        for (int i = 0; i < len; i++) {
            drawRectFs(canvas, pointList.get(i), paintList.get(i));
        }
    }

    private void drawMultiVRect(Canvas canvas, List<RectF> pointList,
                                List<Paint> paintList) {
        int len = (pointList == null) ? 0 : pointList.size();
        // Log.e("222", "111");
        for (int i = 0; i < len; i++) {
            drawVRectFs(canvas, pointList.get(i), paintList.get(i));
        }
    }

    public void setPValue(float h, float l) {
        mHParam = (float) h;
        mLParam = (float) l;
        if (h == l) {
            mLParam = mLParam * (float)0.9;
            mHParam = mHParam * (float)1.1;
        }
        Log.e(TAG, "SET P" + h + ":" + l);
        // mParam = (float) (280) / (mHParam - mLParam);
    }

    public void clearList() {
        if (mPaintList != null)
            mPaintList.clear();
        if (mRectFsList != null)
            mRectFsList.clear();
        if (mPPaintList != null)
            mPPaintList.clear();
        if (mPointFList != null)
            mPointFList.clear();
        if (mVPaintList != null)
            mVPaintList.clear();
        if (mVRectFsList != null)
            mVRectFsList.clear();
        if (DEBUG)
            Log.e(TAG, "------------ " + getHeight());

        hParam = getHeight();
    }

    public void addVolume(StockData kl, int n, float maxvolume) {
        float x_left, x_right, y1, y2;

        if (kl == null)
            return;
        x_left = (float) (mOnem * (2 * n + 1) - mOnev);// left
        x_right = (x_left + 2 * mOnev);// right

        y1 = kl.volume;
//        if (DEBUG)
//            Log.e(TAG, "======================= " + DensityUtil.dip2px(getContext(), getHeight()) + " " + getHeight());
        y2 = hParam - hParam * y1 / maxvolume;
        RectF re = new RectF(x_left, y2, x_right, hParam);
        if (kl.open > kl.close) {
            addRectF(re, mGreenPaint);
        } else if (kl.open < kl.close) {
            addRectF(re, mRedPaint);
        } else {
            addRectF(re, mRedPaint);
        }
    }

    public void addKline(StockData kl, int n) {
        float x_left, x_right, x_middle, y_high, y_low, y_open, y_close;

        if (kl == null)
            return;
        x_left = (float) (mOnem * (2 * n + 1) - mOnev);// left
        x_right = (x_left + 2 * mOnev);// right
        x_middle = (x_left + mOnev);// middle

        // y_high = 150 - (kl.mhigh - kl.mopen) * 150 * 5 / (2 * kl.mopen);
        // y2 = 150 - (kl.mlow - kl.mopen) * 150 * 5 / (2 * kl.mopen);
        // y3 = 150 - (kl.mopen - kl.mopen) * 150 * 5 / (2 * kl.mopen);
        // y4 = 150 - (kl.mclose - kl.mopen) * 150 * 5 / (2 * kl.mopen);
        if (mHParam == 0 || mLParam == 0) {
            Log.i(TAG, "mParamValue is not set");
            return;
        }
        mParam = (float) ((hParam) / (mHParam - mLParam));
        // y_high = 120 - (kl.high / mParamValue - 1) * 120 * 4;// high 1/4=25%
        // y2 = 120 - (kl.low / mParamValue - 1) * 120 * 4;// low
        // y3 = 120 - (kl.open / mParamValue - 1) * 120 * 4;// open
        // y4 = 120 - (kl.close / mParamValue - 1) * 120 * 4;// close

        y_high = (hParam) - ((kl.high - mLParam) * mParam);// high 1/4=25%
        y_low = (hParam) - ((kl.low - mLParam) * mParam);// low
        y_open = hParam - ((kl.open - mLParam) * mParam);// open
        y_close = hParam - ((kl.close - mLParam) * mParam);// close
        // y_high=0;
        // y2=300;

        if (y_high == y_low)
            y_high = y_high + V_PAD;

        PointF[] points = new PointF[2];
        points[0] = new PointF(x_middle, y_high);
        points[1] = new PointF(x_middle, y_low);
        if (DEBUG)
            Log.e(TAG, "add kline: [" + x_left + ":" + x_right + "] [" + y_high + ":" + y_low
                    + "] " + mHParam + ":" + mLParam);

        if (kl.open > kl.close) {
            RectF re = new RectF(x_left, y_open, x_right, y_close);
            addRectF(re, mGreenPaint);
            addLine(points, mGreenPaint);
        } else if (kl.open < kl.close) {
            RectF re = new RectF(x_left, y_close, x_right, y_open);
            addRectF(re, mRedPaint);
            addLine(points, mRedPaint);
        } else if (kl.open == kl.close) {
            RectF re = new RectF(x_left, y_close + V_PAD, x_right, y_open);
            addRectF(re, mRedPaint);
            PointF[] points2 = new PointF[2];
            points2[0] = new PointF(x_left, y_open);
            points2[1] = new PointF(x_right, y_open);
            addLine(points, mRedPaint);
        }
    }

    public void addKDJ(KDJData kl1, KDJData kl2, int n) {
        float x1, x2, y1, y2, y3, y4, y5, y6;

        if (kl1 == null || kl2 == null)
            return;

        x1 = (float) (mOnem * (2 * n - 1));// middle
        x2 = (float) (mOnem * (2 * n + 1));// middle next

        mParam = (hParam / (mHParam - mLParam));

        y1 = hParam - (kl1.k * mParam);// close
        y2 = hParam - (kl2.k * mParam);// close
        y3 = hParam - (kl1.d * mParam);// close
        y4 = hParam - (kl2.d * mParam);// close
        y5 = hParam - (kl1.j * mParam);// close
        y6 = hParam - (kl2.j * mParam);// close

        PointF[] pointsk = new PointF[2];
        pointsk[0] = new PointF(x1, y1);
        pointsk[1] = new PointF(x2, y2);

        PointF[] pointsd = new PointF[2];
        pointsd[0] = new PointF(x1, y3);
        pointsd[1] = new PointF(x2, y4);

        PointF[] pointsj = new PointF[2];
        pointsj[0] = new PointF(x1, y5);
        pointsj[1] = new PointF(x2, y6);
        if (DEBUG)
            Log.e(TAG, "add kdj: " + x1 + " : " + y1 + "     " + x2 + " : "
                    + y2);

        addLine(pointsk, mGreenPaint);
        addLine(pointsd, mYellowPaint);
        addLine(pointsj, mPurplePaint);
    }

    public void addMACD(MACDData kl1, MACDData kl2, int n) {
        float x1, x2, x3, x4, y1, y2, y3, y4, y5, y6;

        if (kl1 == null || kl2 == null)
            return;

        x1 = (float) (mOnem * (2 * n - 1));// middle
        x2 = (x1 + mOnem * 2);// middle next

        x3 = (x2 - mOnev);// left
        x4 = (x3 + 2 * mOnev);// right

        // mParam = (float) (180 / (mHParam - mLParam));

        y1 = hParam / 2 - 5 * hParam / 6 * kl1.dif;// close
        y2 = hParam / 2 - 5 * hParam / 6 * (kl2.dif);// close
        y3 = hParam / 2 - 5 * hParam / 6 * (kl1.dea);// close
        y4 = hParam / 2 - 5 * hParam / 6 * (kl2.dea);// close
        // y5 = 90 - (kl1.j * mParam);// close
        y6 = hParam / 2 - 5 * hParam / 6 * (kl2.bar);// close

        PointF[] pointsdif = new PointF[2];
        pointsdif[0] = new PointF(x1, y1);
        pointsdif[1] = new PointF(x2, y2);

        PointF[] pointsdea = new PointF[2];
        pointsdea[0] = new PointF(x1, y3);
        pointsdea[1] = new PointF(x2, y4);

        // PointF[] pointsj = new PointF[2];
        // pointsj[0] = new PointF(x1, y5);
        // pointsj[1] = new PointF(x2, y6);
        // if (DEBUG)
        // Log.e(TAG, "add kdj: " + x1 + " : " + y1 + "     " + x2 + " : "
        // + y2);
        if (DEBUG)
            Log.e(TAG, "add macd:  " + x1 + " : " + y1 + "     " + x2 + " : "
                    + y2 + " bar: " + y6);
        addLine(pointsdif, mGreenPaint);
        addLine(pointsdea, mYellowPaint);

        RectF re;
        if (y6 > hParam / 2) {
            re = new RectF(x3, hParam / 2, x4, y6);
            addRectF(re, mGreenPaint);
        } else {
            re = new RectF(x3, y6, x4, hParam / 2);
            addRectF(re, mRedPaint);
        }

    }

}
