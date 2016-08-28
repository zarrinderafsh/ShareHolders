package com.example.shareholders;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class LastPriceAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;

	public LastPriceAdapter(Activity activity,
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
		TextView txtdate,txtPrice;
	}

	// @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {
			Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
			convertView = inflater.inflate(R.layout.price_list_row, null);
			holder = new ViewHolder();
//			holder.txtdate = (TextView) convertView.findViewById(R.id.txtdata);
//			holder.txtdate.setTypeface(faceh);
//			holder.txtdate.setTextSize(22);
			holder.txtPrice = (TextView) convertView.findViewById(R.id.txtprice);
			holder.txtPrice.setTypeface(faceh);
			holder.txtPrice.setTextSize(22);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> map = list.get(position);
		//holder.txtdate.setText(map.get("date"));
		String LastPrice = map.get("price");
		NumberFormat baseFormat = NumberFormat.getCurrencyInstance();
		String moneyStringMin = baseFormat.format(Long.valueOf(LastPrice));
		String FinalPriceMin = moneyStringMin.replace('$',' ');
		FinalPriceMin = FinalPriceMin.replace('£',' ');
		holder.txtPrice.setText("قیمت هر سهم شرکت الهیه خراسان از تاریخ " + map.get("date") + " "+"مبلغ " + FinalPriceMin.substring(0, FinalPriceMin.length()-3) + " "+"ریال میباشد");
		return convertView;
	}
	
}
