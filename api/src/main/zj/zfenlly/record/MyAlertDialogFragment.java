package zj.zfenlly.record;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/7/21.
 */
public class MyAlertDialogFragment extends DialogFragment {
    static private View mView;
    static private TextView mtw;
    private int mYear;
    private int mMonth;
    private int mDay;
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    String m = null;
                    String d = null;
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;
                    if (mMonth < 10)
                        m = "0" + mMonth;
                    else
                        m = "" + mMonth;
                    if (mDay < 10)
                        d = "0" + mDay;
                    else
                        d = "" + mDay;
                    mtw.setText("" + mYear + "-" + m + "-" + d);
                }
            };

    public static MyAlertDialogFragment newInstance(int title, View v) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        mView = v;
        mtw = (TextView) mView.findViewById(R.id.date_value);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);

        return new DatePickerDialog(getActivity(),
                mDateSetListener,
                mYear, mMonth, mDay);
//        return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.alert_dialog_icon)
//                .setTitle(title)
//                .setPositiveButton(R.string.alert_dialog_ok,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                ;//((FragmentAlertDialog) getActivity()).doPositiveClick();
//                                dialog.dismiss();
//                            }
//                        }
//                )
//                .setNegativeButton(R.string.alert_dialog_cancel,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                                ;//((FragmentAlertDialog) getActivity()).doNegativeClick();
//                            }
//                        }
//                )
//                .create();
    }
}
