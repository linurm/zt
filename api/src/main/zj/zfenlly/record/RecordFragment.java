package zj.zfenlly.record;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zfenlly.db.MSC;
import com.zfenlly.db.WB;

import zj.zfenlly.camera.CameraJni;
import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class RecordFragment extends Fragment implements Name {

    private final String TAG = this.getClass().getSimpleName();

    public String mName;
    @ViewInject(R.id.music_seven_day_minites)
    TextView mMusic7DayMinites;
    @ViewInject(R.id.weibo_score)
    TextView mWeiboScore;
    @ViewInject(R.id.next_score)
    TextView mNextScore;
    @ViewInject(R.id.weibo_level)
    TextView mWeiboLevel;
    @ViewInject(R.id.wbListView)
    ListView mWbListView;
    @ViewInject(R.id.mscListView)
    ListView mMscListView;

    WBDataAdapter mWBDataAdapter;
    MSCDataAdapter mMSCDataAdapter;
    private int mColorRes = -1;

    public RecordFragment() {
        this(R.color.black, "color");
    }

    public RecordFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
        builder.setDatePickerButton("data picker", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MSCDialog) dialog).openDialogDate();
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
        Log.e(TAG, msg);
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
        print("" + dip2px(getActivity(), 128));
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

        mWBDataAdapter = new WBDataAdapter(getActivity().getApplicationContext());
        mWbListView.setAdapter(mWBDataAdapter);
        mWbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //DataBaseImpl.MSCDataBaseOp mMSCOp = new DataBaseImpl.MSCDataBaseOp();
                final int key = (int) arg3;
                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("WB")//在这里把写好的这个listview的布局加载dialog中
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print(":" + key);
                                WB mwb = DataBaseImpl.WBDataBaseOp.getWB(getActivity(), key);
                                print(mwb.toString());
                                DataBaseImpl.WBDataBaseOp.delete(getActivity(), mwb);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
                dialog.show();
            }
        });

        mMSCDataAdapter = new MSCDataAdapter(getActivity().getApplicationContext());
        mMscListView.setAdapter(mMSCDataAdapter);
        mMscListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //DataBaseImpl.MSCDataBaseOp mMSCOp = new DataBaseImpl.MSCDataBaseOp();
                final int key = (int) arg3;
                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("MSC")//在这里把写好的这个listview的布局加载dialog中
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print(":" + key);
                                MSC mmsc = DataBaseImpl.MSCDataBaseOp.getMSC(getActivity(), key);

                                DataBaseImpl.MSCDataBaseOp.delete(getActivity(), mmsc);

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击
                dialog.show();

            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MSC mMSC = DataBaseImpl.MSCDataBaseOp.getCurrMSC(getActivity());
        if (mMSC != null) {
            mMusic7DayMinites.setText(mMSC.getLast7minites()); //.setText(mMSC.getLast7minites() != null ? mMSC.getLast7minites().toString() : "e");
            mMSCDataAdapter.setAliases(DataBaseImpl.MSCDataBaseOp.getListMSC(getActivity()));
        }

        WB mWB = DataBaseImpl.WBDataBaseOp.getCurrWB(getActivity());
        if (mWB != null) {
            mWeiboScore.setText(mWB.getTotalscore());
            mNextScore.setText(mWB.getNextscore());
            String lv = CaculateLevel.getCaculateLevel(mWB.getTotalscore());
            mWeiboLevel.setText(lv);
            mWBDataAdapter.setAliases(DataBaseImpl.WBDataBaseOp.getListWB(getActivity()));
        }

        CameraJni camerajni = new CameraJni();
        print("" + camerajni.getCLanguageString());
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
