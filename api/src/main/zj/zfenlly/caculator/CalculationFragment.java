package zj.zfenlly.caculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.other.Name;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/4/12.
 */
@SuppressLint("ValidFragment")
public class CalculationFragment extends Fragment implements Name, Observer {
    private final String TAG = this.getClass().getName();
    public String mName;
    @ViewInject(R.id.edit_from)
    public EditText edit_from;
    @ViewInject(R.id.edit_to)
    public EditText edit_to;

    @ViewInject(R.id.result_calculate)
    public TextView result_calculate;
    private int mColorRes = -1;

    public CalculationFragment() {
        this(R.color.white, "wd");
    }

    public CalculationFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    public static float convertToFloat(String number) {
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return 0;
        }

    }

    @OnClick(R.id.button_calculate)
    public void button_calculate(View v) {
        String s = edit_from.getText().toString();
        if (s == null || s.equals("")) {
            Toast.makeText(getActivity(), "from is null!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        float x_from = convertToFloat(s);

        s = edit_to.getText().toString();
        if (s == null || s.equals("")) {
            Toast.makeText(getActivity(), "to is null!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        float x_to = convertToFloat(s);

        float molecule = x_to - x_from;
        float denominator = 100 + x_from;
        float result = molecule / denominator;
        result_calculate.setText("" + result);
        Log.e("TAG", "" + x_from + ":" + x_to);
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
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
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
