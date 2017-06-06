package zj.zfenlly.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zj.zfenlly.other.Name;

/**
 * Created by Administrator on 2017/6/5.
 */
@SuppressLint("ValidFragment")
public class BaseFragment extends Fragment implements Name {

    public String mName;
    public boolean isLandscape = false;

    public BaseFragment() {
        this("no name", false);
    }

    public BaseFragment(String name, boolean land) {
        this.isLandscape = land;
        setName(name);
        setRetainInstance(true);
    }

    public boolean getOrientation() {
        return isLandscape;
    }

    private void print(String msg) {
        Log.e(mName, msg);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        print("onStart");
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
    public void onStop() {
        super.onStop();
        print("onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy");
        //stopCam();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().setContentView(R.layout.download);
        print("onCreate");
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
}
