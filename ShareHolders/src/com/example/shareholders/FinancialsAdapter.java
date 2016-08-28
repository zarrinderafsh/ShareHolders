package com.example.shareholders;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class FinancialsAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;

	public FinancialsAdapter(Activity activity,
			ArrayList<HashMap<String, String>> list) {
		super();
		this.activity = activity;
		this.list = list;
	}

	// @Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	// @Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	// @Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private class ViewHolder {
		TextView txtdiscription;
	}

	// @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {
			Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
			convertView = inflater.inflate(R.layout.finaciallist_row, null);
			holder = new ViewHolder();
			holder.txtdiscription = (TextView) convertView.findViewById(R.id.txtdiscription);
			holder.txtdiscription.setTypeface(faceh);
			holder.txtdiscription.setTextSize(20);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> map = list.get(position);
		holder.txtdiscription.setText(Html.fromHtml("شما تا این لحظه مبلغ "+"<br /><br />"+map.get("price")+" "+"ریال "+"<br /><br />"+map.get("disc")+" "+"می باشید"));
		return convertView;
	}
	
}
