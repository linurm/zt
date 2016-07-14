package zj.zfenlly.record;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class RecordFragment extends Fragment implements Name {

    private final String TAG = this.getClass().getName();
    //private String mTag = Tag;
    public String mName;
    private int mNumHeaders = 0;
    private int mNumFooters = 0;
    private boolean mRemoveEnabled = true;
    private boolean mSortEnabled = true;
    private boolean mDragEnabled = true;
    private int mColorRes = -1;

    //    public RecordFragment() {
//        RecordFragmentInit(R.color.white, "color");
//    }
//
//    public void RecordFragmentInit(int colorRes, String name) {
//        mColorRes = colorRes;
//        setName(name);
//        setRetainInstance(true);
//    }
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
        MSCDialog.Builder builder = new MSCDialog.Builder(getActivity());
        builder.setMessage("MSC");
        builder.setTitle("MSC");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
        builder.setMessage("这个就是自定义的提示框");
        builder.setTitle("WB");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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

//
//        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//
//        List<ActivityManager.AppTask> lrap = am.getAppTasks();
//        //
//        for (int i = 0; i < lrap.size(); i++) {
//            ActivityManager.AppTask rti = lrap.get(i);
//            Log.d("    ", "AppTask:" + rti.getTaskInfo().origActivity);
//        }
//
//        List<ActivityManager.RecentTaskInfo> lrti = am.getRecentTasks(5, 0);
//        for (int i = 0; i < lrti.size(); i++) {
//            ActivityManager.RecentTaskInfo rti = lrti.get(i);
//            Log.d("    ", "RecentTaskInfo:" + rti.origActivity);
//        }
//
//        List<ActivityManager.RunningTaskInfo> rt = am.getRunningTasks(10);
//        for (int i = 0; i < rt.size(); i++) {
//            ActivityManager.RunningTaskInfo rti = rt.get(i);
//            Log.d("    ", "RunningTaskInfo:" + rti.baseActivity);
//        }
//        Log.d("+++++++++", "end:");

//        final PackageManager pm = getActivity().getPackageManager();
//        final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
//        final ActivityInfo activityInfo = resolveInfo.activityInfo;
//        Drawable icon = activityInfo.loadIcon(pm);
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
