package zj.zfenlly.record;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zfenlly.msc.DaoMaster;
import com.zfenlly.msc.DaoSession;
import com.zfenlly.msc.MSC;
import com.zfenlly.msc.MSCDao;

import zj.zfenlly.tools.R;


public class MSCDialog extends Dialog {
    public Context mContext;
    public View contentView;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private MSCDao mscDao;

    public MSCDialog(Context context) {
        super(context);
        mContext = context;
    }

    public MSCDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void initDatabase() {
        final String DATABASE_PATH = Environment.getExternalStorageDirectory()
                + "/" + "xxx/";
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DATABASE_PATH + "msc-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        mscDao = daoSession.getMSCDao();
    }

    public boolean addMSCDate() {

        if (contentView == null) {
            //print("444444444444444444444444444444444444444444444");
            Toast.makeText(mContext, "444444444444", Toast.LENGTH_SHORT).show();
            return false;
        }
        EditText et = (EditText) (contentView.findViewById(R.id.last7minites));
        String s = et.getText().toString();
        if (!s.equals("") || s.length() != 0) {
            String d = "s";
            initDatabase();
            MSC mMsc = new MSC(null, s, "", d);
            mscDao.insert(mMsc);
            Toast.makeText(mContext, "55555555555555555555555555", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(mContext, "33333333333", Toast.LENGTH_SHORT).show();
        return false;
    }


    public static class Builder {

        private final String TAG = this.getClass().getName();
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        private void print(String msg) {
            Log.i(TAG, msg);
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(MSCDialog dialog, View v) {
            dialog.contentView = v;
            return this;
        }

//        public Builder setPositiveButton(int positiveButtonText,
//                                         OnClickListener listener) {
//            this.positiveButtonText = (String) context
//                    .getText(positiveButtonText);
//            this.positiveButtonClickListener = listener;
//            return this;
//        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

//        public Builder setNegativeButton(int negativeButtonText,
//                                         OnClickListener listener) {
//            this.negativeButtonText = (String) context
//                    .getText(negativeButtonText);
//            this.negativeButtonClickListener = listener;
//            return this;
//        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
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
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
