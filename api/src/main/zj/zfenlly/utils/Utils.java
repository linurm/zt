package zj.zfenlly.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;

import zj.zfenlly.other.Name;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/4/12.
 */
@SuppressLint("ValidFragment")
public class Utils extends Fragment implements Name, Observer {
    private final String TAG = this.getClass().getName();
    public String mName;
    private int mColorRes = -1;
    public Utils() {
        this(R.color.white, "wd");
    }

    public Utils(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return mName;
    }

    @Override
    public void setName(String name) {
        // TODO Auto-generated method stub
        mName = name;
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        // Log.i(TAG, "update(" + arg + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");

        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_color, container, false);
        view.setBackgroundColor(color);

        ViewUtils.inject(this, view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }

}
