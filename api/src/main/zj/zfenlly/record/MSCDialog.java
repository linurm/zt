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


import zj.zfenlly.main.MainActivity;
import zj.zfenlly.daodb.MSC;
import zj.zfenlly.tools.R;


public class MSCDialog extends Dialog {
    public Context mContext;
    public View contentView;
    //void String

    public MSCDialog(Context context) {
        super(context);
        mContext = context;
    }

    public MSCDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }


    public void initDatabase(MSC mMsc) {
        DataBaseImpl.MSCDataBaseOp.insert(mContext, mMsc);
    }


    public boolean openDialogDate() {
        ((MainActivity) mContext).showDialog(contentView);
        Log.e("22222222", "" + mContext);
        return true;
    }
    public boolean addMSCDate() {

        if (contentView == null) {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
            return false;
        }
        EditText et = (EditText) (contentView.findViewById(R.id.last7minites));
        String l = et.getText().toString();


        if (!l.equals("") || l.length() != 0) {
            //d = NowDataTime.getDataTime();
            String d = null;
            TextView mTv = (TextView) contentView.findViewById(R.id.date_value);
            String s = mTv.getText().toString();
            if (s.equals("") || s == null) {
                d = NowDataTime.getDataTime();
            } else {
                d = s + " " + NowDataTime.getTime();
            }
            MSC mMsc = new MSC(null, l, "", d);
            initDatabase(mMsc);
            Toast.makeText(mContext, "add successed!!!", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(mContext, "data error!!!", Toast.LENGTH_SHORT).show();
        return false;
    }


    public static class Builder {

        private final String TAG = this.getClass().getName();
        private Context context;
        private String title;
        private String positiveButtonText;
        private String negativeButtonText;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private DialogInterface.OnClickListener dataPickClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        private void print(String msg) {
            Log.i(TAG, msg);
        }


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(MSCDialog dialog, View v) {
            dialog.contentView = v;
            return this;
        }


        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        public Builder setDatePickerButton(String DataPickerText, DialogInterface.OnClickListener listener) {
            this.dataPickClickListener = listener;
            return this;
        }
        public MSCDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MSCDialog dialog = new MSCDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.mscdialog_setting, null);
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
            setContentView(dialog, layout);
            return dialog;
        }
    }
}
