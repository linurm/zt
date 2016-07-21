package zj.zfenlly.record;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zfenlly.msc.MSC;
import com.zfenlly.wb.WB;

import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class RecordFragment extends Fragment implements Name {

    private final String TAG = this.getClass().getName();

    public String mName;
    @ViewInject(R.id.music_seven_day_minites)
    TextView mMusic7DayMinites;
    @ViewInject(R.id.weibo_score)
    TextView mWeiboScore;
    @ViewInject(R.id.next_score)
    TextView mNextScore;
    @ViewInject(R.id.weibo_level)
    TextView mWeiboLevel;


    private int mColorRes = -1;

    public RecordFragment() {
        this(R.color.white, "color");
    }

    public RecordFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    @OnClick(R.id.mscEdit_btn)
    public void mscEdit_btn(View v) {
        final MSCDialog.Builder builder = new MSCDialog.Builder(getActivity());
        //builder.setMessage("MSC");
        builder.setTitle("MSC");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //builder.addMSCDate();
                ((MSCDialog) dialog).addMSCDate();
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @OnClick(R.id.wbEdit_btn)
    public void wbEdit_btn(View v) {

        WBDialog.Builder builder = new WBDialog.Builder(getActivity());
        builder.setTitle("WB");
        //builder.setDateButton("",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //builder.addMSCDate();//.addMSCDate();
                ((WBDialog) dialog).addWBDate();
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setDatePickerButton("data picker", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((WBDialog) dialog).openDialogDate();
            }
        });

        builder.create().show();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");
        print("onCreateView");
        int color = getResources().getColor(mColorRes);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_record, null);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);


        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        DataBaseImpl.MSCDataBaseOp mMSCOp = new DataBaseImpl.MSCDataBaseOp();
        MSC mMSC = mMSCOp.getCurrMSC(getActivity());
        //mMSC.getLast7minites().toCharArray();
        if (mMSC != null)
            mMusic7DayMinites.setText(mMSC.getLast7minites()); //.setText(mMSC.getLast7minites() != null ? mMSC.getLast7minites().toString() : "e");

        DataBaseImpl.WBDataBaseOp mWBOp = new DataBaseImpl.WBDataBaseOp();
        WB mWB = mWBOp.getCurrWB(getActivity());
        if (mWB != null) {
            mWeiboScore.setText(mWB.getTotalscore());
            mNextScore.setText(mWB.getNextscore());
            String lv = CaculateLevel.getCaculateLevel(mWB.getTotalscore());
            mWeiboLevel.setText(lv);
        }

        Configuration config = getResources().getConfiguration();
        int smallestScreenWidth = config.smallestScreenWidthDp;

        Toast.makeText(getActivity(), "small: " + smallestScreenWidth, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
