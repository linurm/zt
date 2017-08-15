package zj.zfenlly.gua.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/2/16.
 */

public class AppListAdapter extends BaseAdapter {

    private Context mContext;

    private String[] mName;
    private List<Drawable> mIcon;

    public AppListAdapter(Context context, String[] names, List<Drawable> icons) {
        mContext = context;
        mName = names;
        mIcon = icons;
    }

    @Override
    public int getCount() {
        return mName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_app, null);

        ImageView iconIv = (ImageView) view.findViewById(R.id.list_item_icon);
        TextView tv = (TextView) view.findViewById(R.id.list_item_info);

//        iconIv.seti
        //mIcon.get(position);
        iconIv.setImageDrawable(mIcon.get(position));
        tv.setText(mName[position]);
        return view;
    }

}
