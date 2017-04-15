package zj.zfenlly.wifidevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.other.Name;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;
import zj.zfenlly.wifidevice.home.MyFileManager;
import zj.zfenlly.wifidevice.service.MainService;
import zj.zfenlly.wifidevice.util.Constant;
import zj.zfenlly.wifidevice.util.Person;


/**
 * Created by Administrator on 2017/4/12.
 */
@SuppressLint("ValidFragment")
public class WifiDeviceFragment extends Fragment implements Name, Observer, View.OnClickListener {

    private final String TAG = this.getClass().getName();
    private String[] groupIndicatorLabeles = null;
    public String mName;
    public MainApplication mStockApplication;

    private Intent mMainServiceIntent = null;
    private int mColorRes = -1;


    public static MainService mService = null;
    private ServiceConnection sConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MainService.ServiceBinder) service).getService();
            System.out.println("Service connected to activity...");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            System.out.println("Service disconnected to activity...");
        }
    };
    private MyBroadcastRecv broadcastRecv = null;
    private IntentFilter bFilter = null;
    @ViewInject(R.id.main_list)
    private ExpandableListView ev;
    private ExListAdapter adapter = null;
    private ArrayList<Map<Integer, Person>> children = null;
    private ArrayList<Integer> personKeys = null;
    private AlertDialog dialog = null;

    public WifiDeviceFragment() {
        this(R.color.white, "wd");
    }

    public WifiDeviceFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.long_send_file:
                Intent intent = new Intent(getActivity(), MyFileManager.class);
                intent.putExtra("selectType", Constant.SELECT_FILES);
                startActivityForResult(intent, 0);
                dialog.dismiss();
                break;
            case R.id.long_click_cancel:
                dialog.dismiss();
                break;
        }
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
        mStockApplication = (MainApplication) getActivity().getApplication();
        mStockApplication.addObserver(this);
        groupIndicatorLabeles = getResources().getStringArray(R.array.groupIndicatorLabeles);
        mMainServiceIntent = new Intent(getActivity(), MainService.class);
        getActivity().bindService(mMainServiceIntent, sConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(mMainServiceIntent);
        regBroadcastRecv();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");

        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_wifi_client, container, false);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);
        return view;
    }


    private void regBroadcastRecv() {
        broadcastRecv = new MyBroadcastRecv();
        bFilter = new IntentFilter();
        bFilter.addAction(Constant.personHasChangedAction);
        getActivity().registerReceiver(broadcastRecv, bFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");
        mStockApplication.ReleaseMulticast();
        getActivity().unregisterReceiver(broadcastRecv);
        getActivity().stopService(mMainServiceIntent);
        getActivity().unbindService(sConnection);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    private class MyBroadcastRecv extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.personHasChangedAction)) {
                children = mService.getChildren();
                personKeys = mService.getPersonKeys();
                if (null == adapter) {
                    adapter = new ExListAdapter(getActivity());
                    ev.setAdapter(adapter);
                    ev.expandGroup(0);
                    ev.setGroupIndicator(getResources().getDrawable(R.drawable.turkey));
                }
                Log.e("TAG", "onReceive");
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class ExListAdapter extends BaseExpandableListAdapter implements View.OnLongClickListener {
        private Context context = null;
        private Person person = null;


        public ExListAdapter(Context context) {
            this.context = context;
        }

        //获得某个用户对象
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(personKeys.get(childPosition));
        }

        //获得用户在用户列表中的序号
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return personKeys.get(childPosition);
        }

        //生成用户布局View
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parentView) {
            View view = null;
            if (groupPosition < children.size()) {//如果groupPosition的序号能从children列表中获得一个children对象
                Person person = children.get(groupPosition).get(personKeys.get(childPosition));//获得当前用户实例
                view = getActivity().getLayoutInflater().inflate(R.layout.person_item_layout, null);//生成List用户条目布局对象
                view.setOnLongClickListener(this);//添加长按事件
                view.setOnClickListener(WifiDeviceFragment.this);
                view.setTag(person);//添加一个tag标记以便在长按事件和点击事件中根据该标记进行相关处理
                view.setPadding(30, 0, 0, 0);//设置左边填充空白距离
                ImageView headIconView = (ImageView) view.findViewById(R.id.person_head_icon);//头像
                TextView nickeNameView = (TextView) view.findViewById(R.id.person_nickename);//昵称
                TextView loginTimeView = (TextView) view.findViewById(R.id.person_login_time);//登录时间
                TextView msgCountView = (TextView) view.findViewById(R.id.person_msg_count);//未读信息计数
                //	TextView ipaddressView = (TextView)view.findViewById(R.id.person_ipaddress);//IP地址
                headIconView.setImageResource(person.personHeadIconId);
                nickeNameView.setText(person.personNickeName);
                loginTimeView.setText(person.loginTime);
                String msgCountStr = getString(R.string.init_msg_count);
                //根据用户id从service层获得该用户的消息数量
                msgCountView.setText(String.format(msgCountStr, mService.getMessagesCountById(person.personId)));
                //	ipaddressView.setText(person.ipAddress);
            }
            return view;
        }

        //获得某个用户组中的用户数
        @Override
        public int getChildrenCount(int groupPosition) {
            int childrenCount = 0;
            if (groupPosition < children.size()) childrenCount = children.get(groupPosition).size();
            return childrenCount;
        }

        //获得媒个用户组对象
        @Override
        public Object getGroup(int groupPosition) {
            return children.get(groupPosition);
        }

        //获得用户组数量,该处的用户组数量返回的是组名称的数量
        @Override
        public int getGroupCount() {
            return groupIndicatorLabeles.length;
        }

        //获得用户组序号
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //生成用户组布局View
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 60);
            TextView textView = new TextView(context);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setPadding(50, 0, 0, 0);
            int childrenCount = 0;
            if (groupPosition < children.size()) {//如果groupPosition序号能从children列表中获得children对象，则获得该children对象中的用户数量
                childrenCount = children.get(groupPosition).size();
            }
            textView.setText(groupIndicatorLabeles[groupPosition] + "(" + childrenCount + ")");
            return textView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        /*当用户列表被长时间按下时会触发该事件*/
        @Override
        public boolean onLongClick(View view) {
            person = (Person) view.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(person.personNickeName);
            builder.setMessage(R.string.pls_select_opr);
            builder.setIcon(person.personHeadIconId);
            View vi = getActivity().getLayoutInflater().inflate(R.layout.person_long_click_layout, null);
            builder.setView(vi);
            dialog = builder.show();

//            Button sendMsgBtn = (Button) vi.findViewById(R.id.long_send_msg);
//            sendMsgBtn.setTag(person);
//            sendMsgBtn.setOnClickListener(FlyPigeonMainActivity.this);

            Button sendFileBtn = (Button) vi.findViewById(R.id.long_send_file);
            sendFileBtn.setTag(person);
            sendFileBtn.setOnClickListener(WifiDeviceFragment.this);

//            Button callBtn = (Button) vi.findViewById(R.id.long_click_call);
//            callBtn.setTag(person);
//            callBtn.setOnClickListener(FlyPigeonMainActivity.this);
//
//            Button cancelBtn = (Button) vi.findViewById(R.id.long_click_cancel);
//            cancelBtn.setTag(person);
//            cancelBtn.setOnClickListener(FlyPigeonMainActivity.this);

            return true;
        }
    }
}
