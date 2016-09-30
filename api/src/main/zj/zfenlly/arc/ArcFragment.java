package zj.zfenlly.arc;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/9/27.
 */
@SuppressLint("ValidFragment")
public class ArcFragment extends Fragment implements Name {
    private final String TAG = this.getClass().getName();
    public String mName;
    public ArcView mArcView;
    @ViewInject(R.id.add_button)
    public Button add_bt;
    @ViewInject(R.id.dec_button)
    public Button dec_bt;
    private int mColorRes = -1;
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    public ArcFragment() {
        this(R.color.white, "color");
    }

    public ArcFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    private void print(String msg) {
        Log.i(TAG, msg);
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

    @OnClick(R.id.add_button)
    public void add_bt_fun(View v) {
        print("add button");
        //Toast.makeText(getActivity(), "add button", Toast.LENGTH_LONG).show();
        mArcView.add_text();
    }

    @OnClick(R.id.dec_button)
    public void dec_bt_fun(View v) {
        print("dec button");
        //Toast.makeText(getActivity(), "dec button", Toast.LENGTH_LONG).show();
        mArcView.dec_text();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");
        int color = getResources().getColor(mColorRes);
        ScreenUtils.initScreen(getActivity());
        // construct the RelativeLayout
        View view = inflater.inflate(R.layout.fragment_arc, container, false);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);
        mArcView = (ArcView) view.findViewById(R.id.arc_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        print("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        print("onPause");
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
