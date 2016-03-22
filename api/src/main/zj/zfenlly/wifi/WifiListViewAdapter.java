package zj.zfenlly.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import zj.zfenlly.tools.R;

public class WifiListViewAdapter extends BaseAdapter {

	private Context context = null; // 运行上下文
	private List<Map<String, Object>> listItems; // 商品信息集合
	private LayoutInflater listContainer;// = LayoutInflater.from(context); //
											// 视图容器

	public final class ListItemView { // 自定义控件集合
		// public ImageView image;
		public TextView title;
		public TextView info;
	}

	public WifiListViewAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		this.listItems = listItems;
		listContainer = LayoutInflater.from(this.context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// Log.e("method", "getView");
		// 自定义视图
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			convertView = listContainer
					.inflate(R.layout.find_device_item, null);
			// 获取控件对象
			// listItemView.image =
			// (ImageView)convertView.findViewById(android.R.id.text1);
			listItemView.title = (TextView) convertView
					.findViewById(android.R.id.text1);
			listItemView.info = (TextView) convertView
					.findViewById(android.R.id.text2);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		// // Log.e("image", (String) listItems.get(position).get("title"));
		// //测试
		// // Log.e("image", (String) listItems.get(position).get("info"));
		//
		// //设置文字和图片
		// listItemView.image.setBackgroundResource((Integer) listItems.get(
		// position).get("image"));

		// List<String> lists =
		// ((DeviceList)this.context).mWifiApplication.getFinds();

		listItems.get(position).get("title");

		listItemView.info.setText(listItems.get(position).get("title")
				.toString());
		listItemView.title.setText(listItems.get(position).get("info")
				.toString());
//		Log.i("listviewadapter", listItems.get(position).get("title")
//				.toString()
//				+ position);
		// listItemView.info.setText((String)
		// listItems.get(position).get("info"));

		return convertView;
	}
}
