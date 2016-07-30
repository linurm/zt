package zj.zfenlly.record;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.zfenlly.db.MSC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22.
 */
public class MSCDataAdapter extends ArrayAdapter<String> {

    public MSCDataAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_single_choice);
    }

    public void setAliases(List<MSC> items) {
        clear();

        List<String> list = new ArrayList<String>();
        Iterator it1 = items.iterator();
        while(it1.hasNext()){
            list.add(((MSC)it1.next()).toStrings());
        }
        addAll(list);
        notifyDataSetChanged();
    }
}
