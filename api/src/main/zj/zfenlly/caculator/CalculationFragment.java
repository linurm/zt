package zj.zfenlly.caculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.main.BaseFragment;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/4/12.
 */
@SuppressLint("ValidFragment")
public class CalculationFragment extends BaseFragment {
    private final String TAG = this.getClass().getName();

    @ViewInject(R.id.edit_from)
    public EditText edit_from;
    @ViewInject(R.id.edit_to)
    public EditText edit_to;

    @ViewInject(R.id.result_calculate)
    public TextView result_calculate;
    CalculationFragment mCalculationFragment;
    private int mColorRes = -1;

    public CalculationFragment() {
        this(R.color.white, "calculate");
    }

    public CalculationFragment(int colorRes, String name) {
        super(name, false);
        mColorRes = colorRes;
    }

    public static float convertToFloat(String number) {
        try {
            return Float.parseFloat(number);
        } catch (Exception e) {
            return 0;
        }

    }

    public CalculationFragment newInstance() {
        if (mCalculationFragment == null) {
            mCalculationFragment = new CalculationFragment();
        }
        return mCalculationFragment;
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
        float result = molecule / denominator * 100;
        result_calculate.setText("" + result);
        Log.e("TAG", "" + x_from + ":" + x_to);
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
        print("onCreateView");
        ViewUtils.inject(this, view);
        return view;
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

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }


}
