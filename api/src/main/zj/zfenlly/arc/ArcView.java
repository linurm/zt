package zj.zfenlly.arc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/9/27.
 */

public class ArcView extends FrameLayout {

    private static int num = 0;
    private TextView mTextView;

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.arc_half, this);
        mTextView = (TextView) findViewById(R.id.arc_text);
    }

    public void add_text() {
        num = num + 1;
        if (num > 16)
            num = 16;
        mTextView.setText("" + num);
    }

    public void dec_text() {
        num = num - 1;
        if (num < 0)
            num = 0;
        mTextView.setText("" + num);
    }
}
