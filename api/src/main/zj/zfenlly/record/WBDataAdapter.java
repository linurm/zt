package zj.zfenlly.record;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.List;

import zj.zfenlly.daodb.WB;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/7/22.
 */
public class WBDataAdapter extends BaseAdapter {
    List<WB> items;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mResourceId;

    public WBDataAdapter(Context context, int textViewResourceId) {
        //super(context, textViewResourceId);
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mResourceId = textViewResourceId;
    }

    public void setAliases(List<WB> items) {
        this.items = items;
        //addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (items != null)
            return items.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Compan cp;
        if (convertView == null) {
            cp = new Compan();
            convertView = layoutInflater.inflate(mResourceId, null);
            cp.ts = (TextView) convertView.findViewById(R.id.t_score);
            cp.ns = (TextView) convertView.findViewById(R.id.n_score);
            cp.id = (CheckedTextView) convertView.findViewById(R.id.wb2_date);
            convertView.setTag(cp);
        } else {
            cp = (Compan) convertView.getTag();
        }
        cp.id.setText((CharSequence) items.get(position).getDate());
        cp.ts.setText((CharSequence) items.get(position).getTotalscore());
        cp.ns.setText((CharSequence) items.get(position).getNextscore());

//        Log.e("TAG", "--------------------------------------");
        return convertView;

    }

    class Compan {
        TextView ts;
        TextView ns;
        CheckedTextView id;
    }
}
