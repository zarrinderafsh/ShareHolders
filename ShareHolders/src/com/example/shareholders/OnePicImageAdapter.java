package com.example.shareholders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class OnePicImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
 
    private int[] CuPicId ;
 
    public OnePicImageAdapter(Context c,int[] PicId) {
        mContext = c;
        CuPicId = new int[PicId.length];
        int ThisPicId;
        int ZeroPosition=-1;
        for(int i = 0 ; i < PicId.length ; i++)
        {
        	if(PicId[i] != 0)
        	{
        		if(ZeroPosition < 0)
        		{
        			ThisPicId = PicId[i];
        			CuPicId[i] = ThisPicId;
        		}
        		else
        		{
        			ThisPicId = PicId[i];
        			CuPicId[ZeroPosition] = ThisPicId;
        			ZeroPosition++;
        		}
        	}
        	else
        	{
        		ZeroPosition = i;
        	}
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
    	
    	ImageView Oldimg = new ImageView(mContext);
    	Oldimg.setImageResource(CuPicId[position]);
    	OldBitmap = ((BitmapDrawable)Oldimg.getDrawable()).getBitmap();
    	NewBitmap = Bitmap.createScaledBitmap(OldBitmap, MyDisplay.getWidth(), MyDisplay.getHeight(), false);
    	
        ImageView i = new ImageView(mContext);
        i.setImageBitmap(NewBitmap);
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setBackgroundResource(mGalleryItemBackground);
        return i;
        
    }
    
}
