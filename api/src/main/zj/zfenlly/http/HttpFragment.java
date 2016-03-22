package zj.zfenlly.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.util.PreferencesCookieStore;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

//import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.File;

import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;


@SuppressLint("ValidFragment")
public class HttpFragment extends Fragment implements Name {

    // private HttpHandler handler;

    private Context mAppContext;
    private DownloadManager downloadManager;

    private PreferencesCookieStore preferencesCookieStore;

    private int mColorRes = -1;
    public String mName;

    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    public HttpFragment() {
        this(R.color.white, "color");
    }

    public HttpFragment(int colorRes, String name) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("destroy");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_http, null);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);

        mAppContext = inflater.getContext().getApplicationContext();

        downloadManager = DownloadService.getDownloadManager(mAppContext);

        preferencesCookieStore = new PreferencesCookieStore(mAppContext);
//        BasicClientCookie cookie = new BasicClientCookie("test", "hello");
//
//        String ip = getIPFromPath(downloadAddrEdit.getText().toString());
//        print("ip:" + ip);
//        cookie.setDomain(ip);
//        cookie.setPath("/");
//        preferencesCookieStore.addCookie(cookie);

        // test();

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

    @SuppressLint("SdCardPath")
    private void startDownload() {
        String downloadUrl = downloadAddrEdit.getText().toString();
        String filename = getNameFromPath(downloadUrl);
        String target;

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // File savDir =
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            File savDir = Environment.getExternalStorageDirectory();
            target = savDir + "/" + filename;
            // print("path: " + path + " savdir: " + savDir);
        } else {
            target = "/sdcard/xUtils/" + filename;
        }
        print("" + target);

        DownloadInfo downloadInfo = downloadManager.isDownloadExist(
                downloadUrl, filename, target);
        if (downloadInfo != null) {
            DownloadManager.DState ds = downloadManager.checkForDownload(downloadInfo);

            switch (ds) {
                case REDOWNLOAD:
                    break;
                case DOWNLOADED:
                    return;
                case DOWNLOADING:
                    try {
                        downloadManager.resumeDownload(downloadInfo, null);
                    } catch (DbException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                default:
                    return;
            }
        }
        try {
            downloadManager.addNewDownload(downloadUrl, filename, target, true, true, null);
        } catch (DbException e) {
            LogUtils.e(e.getMessage(), e);
        }
    }

    @ViewInject(R.id.download_addr_edit)
    private EditText downloadAddrEdit;

    @ViewInject(R.id.download_btn)
    private Button downloadBtn;

    @ViewInject(R.id.download_page_btn)
    private Button downloadPageBtn;

    @ViewInject(R.id.result_txt)
    private TextView resultText;

    @ResInject(id = R.string.download_label, type = ResType.String)
    private String label;

    private String getNameFromPath(String path) {
        String name;

        name = path.substring(path.lastIndexOf('/') + 1);

        return name;
    }

    private String getIPFromPath(String path) {
        String ip;
        print("path" + path);

        ip = path.substring(path.indexOf('/') + 2, path.lastIndexOf('/'));

        return ip;
    }

    @OnClick(R.id.download_btn)
    public void download(View view) {
        startDownload();
    }

    @OnClick(R.id.download_page_btn)
    public void downloadPage(View view) {
        Intent intent = new Intent(this.getActivity(),
                DownloadListActivity.class);
        this.getActivity().startActivity(intent);
    }
}
