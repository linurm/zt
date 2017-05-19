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

import zj.zfenlly.daodb.MSC;
import zj.zfenlly.daodb.WB;
import zj.zfenlly.daodb.WB2;
import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class RecordFragment extends Fragment implements Name {

    private final String TAG = this.getClass().getSimpleName();

    public String mName;
    @ViewInject(R.id.music_seven_day_minites)
    TextView mMusic7DayMinites;
    @ViewInject(R.id.mscListView)
    ListView mMscListView;

    @ViewInject(R.id.weibo_score)
    TextView mWeiboScore;
    @ViewInject(R.id.next_score)
    TextView mNextScore;
    @ViewInject(R.id.weibo_level)
    TextView mWeiboLevel;
    @ViewInject(R.id.wbListView)
    ListView mWbListView;

    @ViewInject(R.id.weibo2_score)
    TextView mWeibo2Score;
    @ViewInject(R.id.next2_score)
    TextView mNext2Score;
    @ViewInject(R.id.weibo2_level)
    TextView mWeibo2Level;
    @ViewInject(R.id.wb2ListView)
    ListView mWb2ListView;

    WBDataAdapter mWBDataAdapter;
    WB2DataAdapter mWB2DataAdapter;
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

    @OnClick(R.id.wb2Edit_btn)
    public void wb2Edit_btn(View v) {
        WB2Dialog.Builder builder = new WB2Dialog.Builder(getActivity());
        builder.setTitle("WB2");
        //builder.setDateButton("",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //builder.addMSCDate();//.addMSCDate();
                ((WB2Dialog) dialog).addWBDate();
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
                ((WB2Dialog) dialog).openDialogDate();
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

        mWB2DataAdapter = new WB2DataAdapter(getActivity().getApplicationContext());
        mWb2ListView.setAdapter(mWB2DataAdapter);
        mWb2ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //DataBaseImpl.MSCDataBaseOp mMSCOp = new DataBaseImpl.MSCDataBaseOp();
                final int key = (int) arg3;
                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("WB2")//在这里把写好的这个listview的布局加载dialog中
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                print(":" + key);
                                WB2 mwb = DataBaseImpl.WB2DataBaseOp.getWB(getActivity(), key);
                                print(mwb.toString());
                                DataBaseImpl.WB2DataBaseOp.delete(getActivity(), mwb);

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

        mMSCDataAdapter = new MSCDataAdapter(getActivity().getApplicationContext(), R.layout.mw_item_list);
        mMSCDataAdapter.setAliases(DataBaseImpl.MSCDataBaseOp.getListMSC(getActivity()));
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

        WB2 mWB2 = DataBaseImpl.WB2DataBaseOp.getCurrWB(getActivity());
        if (mWB2 != null) {
            mWeibo2Score.setText(mWB2.getTotalscore());
            mNext2Score.setText(mWB2.getNextscore());
            String lv = CaculateLevel.getCaculateLevel(mWB2.getTotalscore());
            mWeibo2Level.setText(lv);
            mWB2DataAdapter.setAliases(DataBaseImpl.WB2DataBaseOp.getListWB(getActivity()));
        }
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
