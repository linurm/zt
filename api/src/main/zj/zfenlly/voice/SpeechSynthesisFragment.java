package zj.zfenlly.voice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;

import zj.zfenlly.tools.R;


/**
 * Sample code that invokes the speech recognition intent API.
 */
@SuppressLint("ValidFragment")
public class SpeechSynthesisFragment extends Fragment {

//	@ViewInject(R.id.btnRecognizer)
//	private Button btnReconizer;

	// 语音合成对象
	private SpeechSynthesizer mTts;

	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	private SharedPreferences mSharedPreferences;

	// 云端/本地单选按钮
	@ViewInject(R.id.tts_rediogroup)
	private RadioGroup mRadioGroup;
	// 默认发音人
	private String voicer = "xiaoyan";
	private String[] mCloudVoicersEntries;
	private String[] mCloudVoicersValue;

	
	private int mColorRes = -1;
	@ViewInject(R.id.tts_text)
	EditText text;
	// 缓冲进度
	private int mPercentForBuffering = 0;
	// 播放进度
	private int mPercentForPlaying = 0;

	// 语记安装助手类
	ApkInstaller mInstaller;

	private Toast mToast;

	private final String TAG = this.getClass().getName();

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	public String mName;

	public SpeechSynthesisFragment(int colorRes, String name) {
		mColorRes = colorRes;
		mName = name;
		setRetainInstance(true);
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

		int color = getResources().getColor(mColorRes);
		View view = inflater.inflate(R.layout.fragment_speechsynthesis, null);
		view.setBackgroundColor(color);
		ViewUtils.inject(this, view);

		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(getActivity(),
				mTtsInitListener);
		mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

		// 云端发音人名称列表
		mCloudVoicersEntries = getResources().getStringArray(
				R.array.voicer_cloud_entries);
		mCloudVoicersValue = getResources().getStringArray(
				R.array.voicer_cloud_values);

		//getActivity();
		mSharedPreferences = getActivity().getSharedPreferences(
				TtsSettings.PREFER_NAME, Context.MODE_PRIVATE);

		mInstaller = new ApkInstaller(getActivity());
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
	}

	@Override
	public void onResume() {
		// 移动数据统计分析
		FlowerCollector.onResume(getActivity());
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	public void onPause() {
		// 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(getActivity());
		super.onPause();
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	private void setParam() {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_CLOUD);
			// 设置在线合成发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
		} else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE,
					SpeechConstant.TYPE_LOCAL);
			// 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
			mTts.setParameter(SpeechConstant.VOICE_NAME, "");
		}
		// 设置合成语速
		mTts.setParameter(SpeechConstant.SPEED,
				mSharedPreferences.getString("speed_preference", "50"));
		// 设置合成音调
		mTts.setParameter(SpeechConstant.PITCH,
				mSharedPreferences.getString("pitch_preference", "50"));
		// 设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME,
				mSharedPreferences.getString("volume_preference", "50"));
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE,
				mSharedPreferences.getString("stream_preference", "3"));

		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/tts.wav");
	}

	private int selectedNum = 0;

	/**
	 * 发音人选择。
	 */
	private void showPresonSelectDialog() {
		switch (mRadioGroup.getCheckedRadioButtonId()) {
		// 选择在线合成
		case R.id.tts_radioCloud:
			new AlertDialog.Builder(getActivity()).setTitle("在线合成发音人选项")
					.setSingleChoiceItems(mCloudVoicersEntries, // 单选框有几项,各是什么名字
							selectedNum, // 默认的选项
							new DialogInterface.OnClickListener() { // 点击单选框后的处理
								public void onClick(DialogInterface dialog,
										int which) { // 点击了哪一项
									voicer = mCloudVoicersValue[which];
									if ("catherine".equals(voicer)
											|| "henry".equals(voicer)
											|| "vimary".equals(voicer)) {
										text.setText(R.string.text_tts_source_en);
									} else {
										text.setText(R.string.text_tts_source);
									}
									selectedNum = which;
									dialog.dismiss();
								}
							}).show();
			break;

		// 选择本地合成
		case R.id.tts_radioLocal:
			if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			} else {
				SpeechUtility.getUtility().openEngineSettings(
						SpeechConstant.ENG_TTS);
			}
			break;
		default:
			break;
		}
	}

	@OnClick(R.id.tts_play)
	public void tts_play(View view) {

		// 设置参数
		setParam();

		int code = mTts.startSpeaking(text.getText().toString(), mTtsListener);

		// /**
		// * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
		// * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
		// */
		// String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
		// int code = mTts.synthesizeToUri(text, path, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			print("tts_play " + code);
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页面
				print("tts_play");
				mInstaller.install();

			} else {
				showTip("语音合成失败,错误码: " + code);
			}
		}
	}

	@OnClick(R.id.tts_cancel)
	public void tts_cancel(View view) {
		mTts.stopSpeaking();
	}

	@OnClick(R.id.tts_pause)
	public void tts_pause(View view) {
		mTts.pauseSpeaking();
	}

	@OnClick(R.id.tts_resume)
	public void tts_resume(View view) {
		mTts.resumeSpeaking();
	}

	@OnClick(R.id.tts_btn_person_select)
	public void tts_btn_person_select(View view) {
		showPresonSelectDialog();
	}

	@OnRadioGroupCheckedChange(R.id.tts_rediogroup)
	public void tts_rediogroup_OnRadioGroupCheckedChange(RadioGroup group,
			int checkedId) {
		switch (checkedId) {
		case R.id.tts_radioCloud:
			mEngineType = SpeechConstant.TYPE_CLOUD;
			break;
		case R.id.tts_radioLocal:
			mEngineType = SpeechConstant.TYPE_LOCAL;
			/**
			 * 选择本地合成 判断是否安装语记,未安装则跳转到提示安装页面
			 */
			if (!SpeechUtility.getUtility().checkServiceInstalled()) {
				mInstaller.install();
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
			showTip("开始播放");
		}

		@Override
		public void onSpeakPaused() {
			showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// 合成进度
			mPercentForBuffering = percent;
			showTip(String.format(getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// 播放进度
			mPercentForPlaying = percent;
			showTip(String.format(getString(R.string.tts_toast_format),
					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (error == null) {
				showTip("播放完成");
			} else if (error != null) {
				showTip(error.getPlainDescription(true));
			}
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

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};

	// @OnClick(R.id.btnRecognizer)
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// mHandler.sendEmptyMessage(START_RECONGIZ);
	//
	// }

	private static final int START_RECONGIZ = 1;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_RECONGIZ:
				// test();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// getActivity().setContentView(R.layout.fragment_recognition);

	}

	// public void test() {
	// try {
	// // 通过Intent传递语音识别的模式，开启语音
	// Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	// // 语言模式和自由模式的语音识别
	// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	// RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	// // 提示语音开始
	// intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "开始语音");
	// // 开始语音识别
	// startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// Toast.makeText(getActivity().getApplicationContext(), "找不到语音设备", 1)
	// .show();
	// Intent browserIntent = new Intent(
	// Intent.ACTION_VIEW,
	// Uri.parse("https://market.android.com/details?id=APP_PACKAGE_NAME"));
	// startActivity(browserIntent);
	// }
	// }
	//
	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// // 回调获取从谷歌得到的数据
	// if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
	// && resultCode == getActivity().RESULT_OK) {
	// // 取得语音的字符
	// ArrayList<String> results = data
	// .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	//
	// String resultString = "";
	// for (int i = 0; i < results.size(); i++) {
	// resultString += results.get(i);
	// }
	// Toast.makeText(getActivity(), resultString, 1).show();
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }

}
