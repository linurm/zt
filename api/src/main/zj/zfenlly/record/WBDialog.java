package zj.zfenlly.record;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zfenlly.wb.WB;

import zj.zfenlly.main.MainActivity;
import zj.zfenlly.tools.R;


public class WBDialog extends Dialog {
    public Context mContext;
    public View contentView;

    public WBDialog(Context context) {
        super(context);
        mContext = context;
    }

    public WBDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void initDatabase(WB mWb) {
        DataBaseImpl.WBDataBaseOp mWBOp = new DataBaseImpl.WBDataBaseOp();
        mWBOp.insert(mContext, mWb);
    }

    public boolean openDialogDate() {
        ((MainActivity) mContext).showDialog(contentView);
        Log.e("11111111", "" + mContext);
        return true;
    }

    public boolean addWBDate() {

        if (contentView == null) {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
            return false;
        }
        EditText ts = (EditText) (contentView.findViewById(R.id.totalscore));
        //EditText ns = (EditText) (contentView.findViewById(R.id.nextscore));
        String t = ts.getText().toString();
        //String n = ns.getText().toString();
        String n = CaculateLevel.getNextLevelScore(t);
        if (t.equals("") || t.length() == 0 || n.equals("") || n.length() == 0) {
            Toast.makeText(mContext, t + ":" + n, Toast.LENGTH_SHORT).show();
            return false;

        }

        TextView mTv = (TextView) contentView.findViewById(R.id.date_value);
        String s = mTv.getText().toString();
        String d = null;
        if (s.equals("") || s == null) {
            d = NowDataTime.getDataTime();
        } else {
            d = s + " " + NowDataTime.getTime();
        }

        WB mWb = new WB(null, t, n, d);
        initDatabase(mWb);
        Toast.makeText(mContext, t + ":" + n + " add successed!!!", Toast.LENGTH_SHORT).show();
        return true;
    }


    public static class Builder {
        private Context context;
        private String title;
        private String positiveButtonText;
        private String negativeButtonText;

        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;
        private DialogInterface.OnClickListener dataPickClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(WBDialog dialog, View v) {
            dialog.contentView = v;
            return this;
        }


        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setDatePickerButton(String DataPickerText, DialogInterface.OnClickListener listener) {
            this.dataPickClickListener = listener;
            return this;
        }

        public WBDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final WBDialog dialog = new WBDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.wbdialog_setting, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }

            if (dataPickClickListener != null) {
                ((Button) layout.findViewById(R.id.datepick)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataPickClickListener.onClick(dialog, 0);
                    }
                });
            } else {
                layout.findViewById(R.id.datepick).setVisibility(View.GONE);
            }

            layout.findViewById(R.id.date_value);
            // set the content message
//            if (message != null) {
//                ((TextView) layout.findViewById(R.id.nextscore)).setText(message);
//            } else if (contentView != null) {
//                // if no message set
//                // add the contentView to the dialog body
//                ((LinearLayout) layout.findViewById(R.id.content))
//                        .removeAllViews();
//                ((LinearLayout) layout.findViewById(R.id.content))
//                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//            }
            setContentView(dialog, layout);
            return dialog;
        }
    }
}
