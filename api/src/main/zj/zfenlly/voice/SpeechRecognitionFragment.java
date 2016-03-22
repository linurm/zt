package zj.zfenlly.voice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ContactManager.ContactListener;
import com.iflytek.sunflower.FlowerCollector;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;


@SuppressLint("ValidFragment")
public class SpeechRecognitionFragment extends Fragment implements Name {
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    @ViewInject(R.id.iat_text)
    private EditText mResultText;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    // private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    // 语记安装助手类
    ApkInstaller mInstaller;

    @ViewInject(R.id.iat_recognize)
    Button iat_recognize_btn;
    @ViewInject(R.id.iat_stop)
    Button iat_stop_btn;
    @ViewInject(R.id.iat_cancel)
    Button iat_cancel_btn;
    @ViewInject(R.id.iat_recognize_stream)
    Button iat_recognize_stream_btn;
    @ViewInject(R.id.image_iat_set)
    Button image_iat_set_btn;
    @ViewInject(R.id.iat_upload_contacts)
    Button iat_upload_contacts_btn;
    @ViewInject(R.id.iat_upload_userwords)
    Button iat_upload_userwords_btn;
    @ViewInject(R.id.radioGroup)
    RadioGroup group;

    @OnRadioGroupCheckedChange(R.id.radioGroup)
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.iatRadioCloud:
                mEngineType = SpeechConstant.TYPE_CLOUD;
                iat_upload_contacts_btn.setEnabled(true);
                iat_upload_userwords_btn.setEnabled(true);
                break;
            case R.id.iatRadioLocal:
                mEngineType = SpeechConstant.TYPE_LOCAL;
                iat_upload_contacts_btn.setEnabled(false);
                iat_upload_userwords_btn.setEnabled(false);
                /**
                 * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
                 */
                if (!SpeechUtility.getUtility().checkServiceInstalled()) {
                    mInstaller.install();
                } else {
                    String result = FucUtil.checkLocalResource();
                    if (!TextUtils.isEmpty(result)) {
                        showTip(result);
                    }
                }
                break;
            case R.id.iatRadioMix:
                mEngineType = SpeechConstant.TYPE_MIX;
                iat_upload_contacts_btn.setEnabled(false);
                iat_upload_userwords_btn.setEnabled(false);
                /**
                 * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
                 */
                if (!SpeechUtility.getUtility().checkServiceInstalled()) {
                    mInstaller.install();
                } else {
                    String result = FucUtil.checkLocalResource();
                    if (!TextUtils.isEmpty(result)) {
                        showTip(result);
                    }
                }
                break;
            default:
                break;
        }
    }

    private int mColorRes = -1;

    public String mName;

    public SpeechRecognitionFragment(int colorRes, String name) {
        mColorRes = colorRes;
        mName = name;
        setRetainInstance(true);
    }

    public SpeechRecognitionFragment() {
        this(R.color.white, "color");
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

    @SuppressLint("ShowToast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getActivity(), mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(getActivity(), mInitListener);
        mSharedPreferences = getActivity().getSharedPreferences(
                IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        mInstaller = new ApkInstaller(getActivity());

        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_speechrecognition, null);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);

        print("1111111111111111111111");

        // getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick(R.id.image_iat_set)
    public void image_iat_set_onClick(View view) {
        Intent intents = new Intent(getActivity(), IatSettings.class);
        startActivity(intents);
    }

    @OnClick(R.id.iat_recognize)
    public void iat_recognize_onClick(View view) {
        mResultText.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setParam();
        boolean isShowDialog = mSharedPreferences.getBoolean(
                getString(R.string.pref_key_iat_show), true);
        if (isShowDialog) {
            // 显示听写对话框
            print("display dialog");
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            showTip(getString(R.string.text_begin));
        } else {
            // 不显示听写对话框
            print("no display dialog");
            int ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip(getString(R.string.text_begin));
            }
        }
    }

    @OnClick(R.id.iat_recognize_stream)
    public void iat_recognize_stream_onClick(View view) {
        mResultText.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setParam();
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        // 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
        // mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
        // mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH,
        // "sdcard/XXX/XXX.pcm");
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("识别失败,错误码：" + ret);
        } else {
            byte[] audioData = FucUtil.readAudioFile(getActivity(),
                    "iattest.wav");

            if (null != audioData) {
                showTip(getString(R.string.text_begin_recognizer));
                // 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），位长16bit，单声道的wav或者pcm
                // 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE,
                // "8000")设置正确的采样率
                // 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
                mIat.writeAudio(audioData, 0, audioData.length);
                mIat.stopListening();
            } else {
                mIat.cancel();
                showTip("读取音频流失败");
            }
        }
    }

    @OnClick(R.id.iat_stop)
    public void iat_stop_onClick(View view) {
        mIat.stopListening();
        showTip("停止听写");
    }

    @OnClick(R.id.iat_cancel)
    public void iat_cancel_onClick(View view) {
        mIat.cancel();
        showTip("取消听写");
    }

    @OnClick(R.id.iat_upload_contacts)
    public void iat_upload_contacts_onClick(View view) {
        showTip(getString(R.string.text_upload_contacts));
        ContactManager mgr = ContactManager.createManager(getActivity(),
                mContactListener);
        mgr.asyncQueryAllContactsName();
    }

    @OnClick(R.id.iat_upload_userwords)
    public void iat_upload_userwords_onClick(View view) {
        showTip(getString(R.string.text_upload_userwords));
        String contents = FucUtil.readFile(getActivity(), "userwords", "utf-8");
        mResultText.setText(contents);
        // 指定引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        int ret = mIat.updateLexicon("userword", contents, mLexiconListener);
        if (ret != ErrorCode.SUCCESS)
            showTip("上传热词失败,错误码：" + ret);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 上传联系人/词表监听器。
     */
    private LexiconListener mLexiconListener = new LexiconListener() {

        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                showTip(error.toString());
            } else {
                showTip(getString(R.string.text_upload_success));
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    /**
     * 获取联系人监听器。
     */
    private ContactListener mContactListener = new ContactListener() {

        @Override
        public void onContactQueryFinish(final String contactInfos,
                                         boolean changeFlag) {
            // 注：实际应用中除第一次上传之外，之后应该通过changeFlag判断是否需要上传，否则会造成不必要的流量.
            // 每当联系人发生变化，该接口都将会被回调，可通过ContactManager.destroy()销毁对象，解除回调。
            // if(changeFlag) {
            // 指定引擎类型
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    mResultText.setText(contactInfos);
                }
            });

            mIat.setParameter(SpeechConstant.ENGINE_TYPE,
                    SpeechConstant.TYPE_CLOUD);
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            int ret = mIat.updateLexicon("contact", contactInfos,
                    mLexiconListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("上传联系人失败：" + ret);
            }
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS,
                mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS,
                mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,
                mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/iat.wav");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA,
                mSharedPreferences.getString("iat_dwa_preference", "0"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
    }

    @Override
    public void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(getActivity());
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    public void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(getActivity());
        super.onPause();
    }

}
