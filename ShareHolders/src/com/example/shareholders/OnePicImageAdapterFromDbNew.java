package com.example.shareholders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.util.Base64;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class OnePicImageAdapterFromDbNew extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
 
    private String[] CuPicId ;
 
    public OnePicImageAdapterFromDbNew(Context c,String[] PicId) {
        mContext = c;
        CuPicId = new String[PicId.length];
        for(int i = 0 ; i < PicId.length ; i++)
        {
        	CuPicId[i] = PicId[i];
        }
    }
 
    public int getCount() {
        return CuPicId.length;
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
    	Display MyDisplay = wm.getDefaultDisplay();
    	Bitmap OldBitmap,NewBitmap;
    	
    	byte[] decodedByte = Base64.decode(CuPicId[position], Base64.DEFAULT);
		Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    	
    	ImageView Oldimg = new ImageView(mContext);
    	Oldimg.setImageBitmap(Bmp);
    	OldBitmap = ((BitmapDrawable)Oldimg.getDrawable()).getBitmap();
    	//NewBitmap = Bitmap.createScaledBitmap(OldBitmap, MyDisplay.getWidth(), MyDisplay.getHeight(), false);
    	NewBitmap = OldBitmap;
    	
        ImageView i = new ImageView(mContext);
        i.setImageBitmap(NewBitmap);
        //i.setPadding(0, 0, 5, 0);
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(mGalleryItemBackground);
        
        return i;
        
    }
    
}
