package com.example.shareholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
@SuppressLint("NewApi")
public class PicGalleryLvl1Adapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;

	public PicGalleryLvl1Adapter(Activity activity,
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
		TextView txtdate;
		ImageView NewsImageView;
	}

	// @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {
			Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
			convertView = inflater.inflate(R.layout.news_list_row, null);
			holder = new ViewHolder();
			holder.txtdate = (TextView) convertView.findViewById(R.id.title);
			holder.txtdate.setTypeface(faceh);
			holder.txtdate.setTextSize(18);
			holder.NewsImageView = (ImageView)convertView.findViewById(R.id.imageViewCarListRow);
			//holder.NewsImageView.setOnClickListener(ImageItemOnclick);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> map = list.get(position);
		
		holder.txtdate.setText(Html.fromHtml("<div style='width:100%;height:auto;text-align:right;direction:rtl;color:white'>"+map.get("title")+"</div>"));
		//map.get("title")
		holder.txtdate.setTag(map.get("id")+"*"+map.get("PGuid"));
		holder.txtdate.setOnClickListener(TextViewItemOnclick);
		
		try
		{
			byte[] decodedByte = Base64.decode(map.get("pic"), Base64.DEFAULT);
			Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
			if(Bmp!=null)
			{
				holder.NewsImageView.setImageBitmap(Bmp);
			}
			else
			{
				holder.NewsImageView.setImageResource(R.drawable.logojpg);
			}
			holder.NewsImageView.setTag(map.get("id")+"*"+map.get("PGuid"));
			holder.NewsImageView.setOnClickListener(ImageItemOnclick);
		}
		catch (Exception e) {
			holder.NewsImageView.setImageResource(R.drawable.logojpg);
			holder.NewsImageView.setTag(map.get("id")+"*"+map.get("PGuid"));
			holder.NewsImageView.setOnClickListener(ImageItemOnclick);
			e.printStackTrace();
		}
		return convertView;
	}

	
	private OnClickListener TextViewItemOnclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 String item = ((TextView)v).getTag().toString();
			 item = item.split(Pattern.quote("*"))[0].toString();
			 String Guid="";
			 Guid = ((TextView)v).getTag().toString().split(Pattern.quote("*"))[1].toString();
			 Intent intent = new Intent(activity.getApplicationContext(),PicGalleryGuest.class);
			 intent.putExtra("LvlId",item);
			 if(Guid.compareTo("0000-0000-0000-0000") == 0)
			 { 
				 intent.putExtra("BackActivity","Guest");
			 }
			 else{intent.putExtra("PGuid",Guid);}
			 activity.startActivity(intent);
		}
	};
	
	private OnClickListener ImageItemOnclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 String item = ((ImageView)v).getTag().toString();
			 item = item.split(Pattern.quote("*"))[0].toString();
			 String Guid="";
			 Guid = ((ImageView)v).getTag().toString().split(Pattern.quote("*"))[1].toString();
			 Intent intent = new Intent(activity.getApplicationContext(),PicGalleryGuest.class);
			 intent.putExtra("LvlId",item);
			 if(Guid.compareTo("0000-0000-0000-0000") == 0)
			 { 
				 intent.putExtra("BackActivity","Guest");
			 }
			 else{intent.putExtra("PGuid",Guid);}
			 activity.startActivity(intent);
		}
	};
	
}
