package zj.zfenlly.coloradjust;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;
import com.lidroid.xutils.view.annotation.event.OnProgressChanged;
import com.lidroid.xutils.view.annotation.event.OnStopTrackingTouch;

import zj.zfenlly.main.BaseFragment;
import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class ColorAdjustFragment extends BaseFragment {

	private int value_temp;

	private String sys_file = "/sys/class/graphics/fb0/bcsh";

	private final static int BRIGHTNESS_DEFAULT_VALUE = 128;
	private final static int CONTRAST_DEFAULT_VALUE = 256;
	private final static int SAT_CON_DEFAULT_VALUE = 256;
	private final static int SIN_HUE_DEFAULT_VALUE = 0;
	private final static int COS_HUE_DEFAULT_VALUE = 256;

	private int brightness_value = BRIGHTNESS_DEFAULT_VALUE;
	private int contrast_value = CONTRAST_DEFAULT_VALUE;
	private int sat_con_value = SAT_CON_DEFAULT_VALUE;
	private int sin_hue_value = SIN_HUE_DEFAULT_VALUE;
	private int cos_hue_value = COS_HUE_DEFAULT_VALUE;
	private int opened;

	private int mColorRes = -1;


	public ColorAdjustFragment() {
		this(R.color.white, "color adjust");
	}

	public ColorAdjustFragment(int colorRes, String name) {
		super(name, false);
		mColorRes = colorRes;
	}



	private void init_view_value() {
		String s = FileOpration.read_bcsh(sys_file);
		if (s == null) {
			return;
		}
		parse_bcsh(s);
		update_text();
		update_seekbar_view();

		open_bcsh();
		set_brightness();
		set_contrast();
		set_sat_con();
		set_hue();
	}

	private void open_bcsh() {
		opened = FileOpration.open_bcsh(sys_file);
	}

	private void reset_view_value() {

		brightness_value = BRIGHTNESS_DEFAULT_VALUE;
		contrast_value = CONTRAST_DEFAULT_VALUE;
		sat_con_value = SAT_CON_DEFAULT_VALUE;
		sin_hue_value = SIN_HUE_DEFAULT_VALUE;
		cos_hue_value = COS_HUE_DEFAULT_VALUE;

		update_text();
		update_seekbar_view();
		open_bcsh();
		set_brightness();
		set_contrast();
		set_sat_con();
		set_hue();
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

	private final String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	@OnClick(R.id.reset)
	public void reset(View v) {
		FileOpration.reset_bcsh(sys_file);
		reset_view_value();
	}

	private void parse_bcsh(String s) {
		String ns = s;
		int b = 0;

		String[] sn = ns.split(",");
		// print("" + sn[0].split(":")[1].trim());
		try {
			b = Integer.valueOf(sn[0].split(":")[1].trim()).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		brightness_value = b;
		try {
			b = Integer.valueOf(sn[1].split(":")[1].trim()).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		contrast_value = b;
		try {
			b = Integer.valueOf(sn[2].split(":")[1].trim()).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		sat_con_value = b;
		try {
			b = Integer.valueOf(sn[3].split(":")[1].trim()).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		sin_hue_value = b;
		try {
			b = Integer.valueOf(sn[4].split(":")[1].trim()).intValue();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		cos_hue_value = b;
	}

	private void update_seekbar_view() {
		brightness_seek.setProgress(brightness_value);
		contrast_seek.setProgress(contrast_value);
		sat_con_seek.setProgress(sat_con_value);
		sin_hue_seek.setProgress(sin_hue_value);
		cos_hue_seek.setProgress(cos_hue_value);
	}

	private void update_text() {
		String text;
		text = "brightness:" + brightness_value + "  " + "contrast: "
				+ contrast_value + "  " + "sat_con: " + sat_con_value + "  "
				+ "sin_hue:" + sin_hue_value + "  " + "cos_hue:"
				+ cos_hue_value;
		print(text);
		textView.setText(text);

	}

	private void set_brightness() {
		FileOpration.write_bcsh(sys_file, "brightness " + brightness_value);
	}

	private void set_contrast() {
		FileOpration.write_bcsh(sys_file, "contrast " + contrast_value);
	}

	private void set_sat_con() {
		FileOpration.write_bcsh(sys_file, "sat_con " + sat_con_value);
	}

	private void set_hue() {
		FileOpration.write_bcsh(sys_file, "hue " + sin_hue_value + " "
				+ cos_hue_value);
	}

	@ViewInject(R.id.value)
	public TextView textView;

	@ViewInject(R.id.fb_switch)
	public Spinner switchsp;

	@OnItemSelected(R.id.fb_switch)
	public void fb_switch(AdapterView parent, View view, int position, long id) {
		String str = parent.getItemAtPosition(position).toString();
		print(str);
		if (str.equals("LVDS")) {
			sys_file = "/sys/class/graphics/fb0/bcsh";
		} else {
			sys_file = "/sys/class/graphics/fb4/bcsh";
		}
		init_view_value();
		// Toast.makeText(SpinnerActivity.this, "��������:"+str, 2000).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActivity().setContentView(R.layout.fragment_color_adjust);

		print("onCreate");

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int color = getResources().getColor(mColorRes);
		View view = inflater.inflate(R.layout.fragment_color_adjust, null);
		view.setBackgroundColor(color);
		ViewUtils.inject(this, view);

		String[] mItems = getResources().getStringArray(R.array.spinnername);

		ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, mItems);

		switchsp.setAdapter(_Adapter);
		init_view_value();

		// test();

		return view;
	}

	// ////////////////////////////////////////////////////////////
	@ViewInject(R.id.brightness_seek)
	public SeekBar brightness_seek;

	@OnClick(R.id.brightness_add)
	public void brightnessAddClick(View v) {
		int v1 = brightness_seek.getProgress() + 1;
		if (v1 > 255)
			v1 = 255;
		brightness_seek.setProgress(v1);
		brightness_value = v1;
		update_text();
		set_brightness();
	}

	@OnStopTrackingTouch(R.id.brightness_seek)
	public void brightness_seek_StopTrackingTouch(SeekBar seekBar) {
		brightness_seek.setProgress(value_temp);
		brightness_value = value_temp;
		update_text();
		set_brightness();
	}

	@OnProgressChanged(R.id.brightness_seek)
	public void brightness_seek_Changed(SeekBar seekBar, int progress,
			boolean fromUser) {
		value_temp = progress;
	}

	@OnClick(R.id.brightness_sub)
	public void brightnessSubClick(View v) {
		int v1 = brightness_seek.getProgress() - 1;
		if (v1 < 0)
			v1 = 0;
		brightness_seek.setProgress(v1);
		brightness_value = v1;
		update_text();
		set_brightness();
	}

	// /////////////////////////////////////////////////////////////////
	@ViewInject(R.id.contrast_seek)
	public SeekBar contrast_seek;

	@OnClick(R.id.contrast_add)
	public void contrast_add(View v) {
		int v1 = contrast_seek.getProgress() + 1;
		if (v1 > 510)
			v1 = 510;
		contrast_seek.setProgress(v1);
		contrast_value = v1;
		update_text();
		set_contrast();
	}

	@OnStopTrackingTouch(R.id.contrast_seek)
	public void contrast_seek_StopTrackingTouch(SeekBar seekBar) {
		contrast_seek.setProgress(value_temp);
		contrast_value = value_temp;
		update_text();
		set_contrast();
	}

	@OnProgressChanged(R.id.contrast_seek)
	public void contrast_seek_Changed(SeekBar seekBar, int progress,
			boolean fromUser) {
		value_temp = progress;
	}

	@OnClick(R.id.contrast_sub)
	public void contrast_sub(View v) {
		int v1 = contrast_seek.getProgress() - 1;
		if (v1 < 0)
			v1 = 0;
		contrast_seek.setProgress(v1);
		contrast_value = v1;
		update_text();
		set_contrast();
	}

	// ///////////////////////////////////////////////////////////////////
	@ViewInject(R.id.sat_con_seek)
	public SeekBar sat_con_seek;

	@OnClick(R.id.sat_con_add)
	public void sat_con_add(View v) {
		int v1 = sat_con_seek.getProgress() + 1;
		if (v1 > 1015)
			v1 = 1015;
		sat_con_seek.setProgress(v1);
		sat_con_value = v1;
		update_text();
		set_sat_con();
	}

	@OnStopTrackingTouch(R.id.sat_con_seek)
	public void sat_con_seek_StopTrackingTouch(SeekBar seekBar) {
		sat_con_seek.setProgress(value_temp);
		sat_con_value = value_temp;
		update_text();
		set_sat_con();
	}

	@OnProgressChanged(R.id.sat_con_seek)
	public void sat_con_seek_Changed(SeekBar seekBar, int progress,
			boolean fromUser) {
		value_temp = progress;
	}

	@OnClick(R.id.sat_con_sub)
	public void sat_con_sub(View v) {
		int v1 = sat_con_seek.getProgress() - 1;
		if (v1 < 0)
			v1 = 0;
		sat_con_seek.setProgress(v1);
		sat_con_value = v1;
		update_text();
		set_sat_con();
	}

	// ///////////////////////////////////////////////////////////////////
	@ViewInject(R.id.sin_hue_seek)
	public SeekBar sin_hue_seek;

	@OnClick(R.id.sin_hue_add)
	public void sin_hue_add(View v) {
		int v1 = sin_hue_seek.getProgress() + 1;
		if (v1 > 511)
			v1 = 511;
		sin_hue_seek.setProgress(v1);
		sin_hue_value = v1;
		update_text();
		set_hue();
	}

	@OnStopTrackingTouch(R.id.sin_hue_seek)
	public void sin_hue_seek_StopTrackingTouch(SeekBar seekBar) {
		sin_hue_seek.setProgress(value_temp);
		sin_hue_value = value_temp;
		update_text();
		set_hue();
	}

	@OnProgressChanged(R.id.sin_hue_seek)
	public void sin_hue_seek_Changed(SeekBar seekBar, int progress,
			boolean fromUser) {
		value_temp = progress;
	}

	@OnClick(R.id.sin_hue_sub)
	public void sin_hue_sub(View v) {
		int v1 = sin_hue_seek.getProgress() - 1;
		if (v1 < 0)
			v1 = 0;
		sin_hue_seek.setProgress(v1);
		sin_hue_value = v1;
		update_text();
		set_hue();
	}

	// ///////////////////////////////////////////////////////////////////////
	@ViewInject(R.id.cos_hue_seek)
	public SeekBar cos_hue_seek;

	@OnClick(R.id.cos_hue_add)
	public void cos_hue_add(View v) {
		int v1 = cos_hue_seek.getProgress() + 1;
		if (v1 > 511)
			v1 = 511;
		cos_hue_seek.setProgress(v1);
		cos_hue_value = v1;
		update_text();
		set_hue();
	}

	@OnStopTrackingTouch(R.id.cos_hue_seek)
	public void cos_hue_seek_StopTrackingTouch(SeekBar seekBar) {
		cos_hue_seek.setProgress(value_temp);
		cos_hue_value = value_temp;
		update_text();
		set_hue();
	}

	@OnProgressChanged(R.id.cos_hue_seek)
	public void cos_hue_seek_Changed(SeekBar seekBar, int progress,
			boolean fromUser) {
		value_temp = progress;
	}

	@OnClick(R.id.cos_hue_sub)
	public void cos_hue_sub(View v) {
		int v1 = cos_hue_seek.getProgress() - 1;
		if (v1 < 0)
			v1 = 0;
		cos_hue_seek.setProgress(v1);
		cos_hue_value = v1;
		update_text();
		set_hue();
	}

}
