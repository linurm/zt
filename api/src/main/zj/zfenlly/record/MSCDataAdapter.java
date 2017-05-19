package zj.zfenlly.record;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.List;

import zj.zfenlly.daodb.MSC;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/7/22.
 */
public class MSCDataAdapter extends BaseAdapter {
    List<MSC> items;
    private LayoutInflater layoutInflater;
    private Context context;
    private int mResourceId;

    public MSCDataAdapter(Context context, int textViewResourceId) {
        //super(context, textViewResourceId);
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mResourceId = textViewResourceId;
    }

    public void setAliases(List<MSC> items) {
//        clear();
//
//        List<String> list = new ArrayList<String>();
//        Iterator it1 = items.iterator();
//        while (it1.hasNext()) {
//            list.add(((MSC) it1.next()).toString());
//        }
        this.items = items;
        //addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
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
            cp.id = (TextView) convertView.findViewById(R.id.v_date);
            cp.ctv = (CheckedTextView) convertView.findViewById(R.id.v_content);
            convertView.setTag(cp);
        } else {
            cp = (Compan) convertView.getTag();
        }
        cp.id.setText((CharSequence) items.get(position).getDate());
        cp.ctv.setText((CharSequence) items.get(position).getLast7minites() + " " + (CharSequence) items.get(position).getTodayminites());

        Log.e("TAG", "--------------------------------------");
        return convertView;

    }

    class Compan {
        TextView id;
        CheckedTextView ctv;
    }
}