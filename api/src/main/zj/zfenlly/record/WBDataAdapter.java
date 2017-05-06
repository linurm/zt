package zj.zfenlly.record;

import android.content.Context;
import android.widget.ArrayAdapter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zj.zfenlly.daodb.WB;

/**
 * Created by Administrator on 2016/7/22.
 */
public class WBDataAdapter extends ArrayAdapter<String> {

    public WBDataAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_single_choice);
    }

    public void setAliases(List<WB> items) {
        clear();
        List<String> list = new ArrayList<String>();
        Iterator it1 = items.iterator();
        while (it1.hasNext()) {
            list.add(it1.next().toString());
        }
        addAll(list);
        notifyDataSetChanged();
    }
}
