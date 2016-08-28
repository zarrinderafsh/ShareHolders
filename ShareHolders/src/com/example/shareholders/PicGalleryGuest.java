package com.example.shareholders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

public class PicGalleryGuest extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	Gallery OneImageView;
	
	String PersonGuid;
	String BackActivity;
	String LvlId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picgallery);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //LvlId
        try
        {
        	BackActivity = "Empty";
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        	LvlId = getIntent().getStringExtra("LvlId").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
        	try{BackActivity = getIntent().getStringExtra("BackActivity").toString();
        	LvlId = getIntent().getStringExtra("LvlId").toString();}catch (Exception e1) {e1.printStackTrace();}
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        OneImageView = (Gallery)findViewById(R.id.imageViewOnePicShop);
        
        btnPageTitle = (TextView)findViewById(R.id.picgallerypagetitle);
        
        btnPageTitle.setTypeface(FontMitra);
        
        dbh=new DatabaseHelper(getApplicationContext());
		try {

			dbh.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbh.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
//        
//   		FillData();
   		
        LoadPic();
    }
    
//    private void FillData()
//	{
//		
//		db = dbh.getReadableDatabase();
//		Cursor cursors = db.rawQuery("select id,pic from picgallery order by id desc", null);
//
//		boolean HasPicFlag = false;
//		if(cursors.getCount() > 0)
//		{
//			String pic;
//			try
//			{
//				Bitmap[] PicIdArray = new Bitmap[cursors.getCount()];
//				for (int i = 0; i < cursors.getCount(); i++) {
//					cursors.moveToNext();
//					pic = cursors.getString(cursors.getColumnIndex("pic"));
//					if(pic.length() > 20)
//					{
//						try
//						{
//							byte[] decodedByte = Base64.decode(pic, Base64.DEFAULT);
//							Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//							PicIdArray[i] = Bmp;
//						}
//						catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//					else
//					{
//						HasPicFlag = true;
//						break;
//					}
//				}
//				if(HasPicFlag==false)
//				{
//					OneImageView.setAdapter(new OnePicImageAdapterFromDb(this, PicIdArray));
//				}
//				else
//				{
//					LoadPic();
//				}
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		else
//		{
//			LoadPic();
//		}
//
//	}
    
    String [] PicIdArray;
	public void LoadPic()
	{
//			PicIdArray = new int[14];
//			PicIdArray[0]=R.drawable.picgallery14;
		
		String title,id,pic;
		
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select Code,Name,pic,Lvl1Code from picgallerylvl2 where Lvl1Code = "+LvlId+" order by Code", null);

		if(cursors.getCount() > 0)
		{
			PicIdArray = new String[cursors.getCount()];
			for (int i = 0; i < cursors.getCount(); i++) {
				cursors.moveToNext();
				
				id = cursors.getString(cursors.getColumnIndex("Code"));
				title = cursors.getString(cursors.getColumnIndex("Name"));
				pic = cursors.getString(cursors.getColumnIndex("pic"));
				String FinalPic = "";
				PublicFunction PF = new PublicFunction();
				if(GetNewsPicFromTempTbl(id).compareTo("0")==0)
				{
					Drawable myDrawable = getResources().getDrawable(R.drawable.logojpg);
					BitmapDrawable myLogo = (BitmapDrawable) myDrawable;
					FinalPic = PF.ConvertImageViewToBase64String(myLogo);
				}
				else
				{
					FinalPic=GetNewsPicFromTempTbl(id);
				}
				
				PicIdArray[i] = FinalPic;
			}
			
			OneImageView.setAdapter(new OnePicImageAdapterFromDbNew(this, PicIdArray));
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید"), Toast.LENGTH_LONG).show();
		}

		
		//	OneImageView.setAdapter(new OnePicImageAdapterFromDbNew(this, PicIdArray));
	}
	
	
	 public String GetNewsPicFromTempTbl(String NewsId)
		{
	    	String Res = "0";
			db = dbh.getReadableDatabase();
			Cursor cursors = db.rawQuery("select * from picgallerylvl2pic where id = "+NewsId, null);
			if(cursors.getCount() > 0)
			{
				cursors.moveToNext();
				Res = cursors.getString(cursors.getColumnIndex("pic"));
			}
			return Res;
		}
    
    
    @Override
	  protected void onPause()
	  {
	    super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.drawable.activity_open_scale,R.drawable.activity_close_translate);
	  }

	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
	{
		Intent intent = new Intent(this,Cls);
		intent.putExtra(VariableName, VariableValue);
		startActivity(intent);
		this.finish();
	}
	
	private void ExitApplication()
	{
		//Exit All Activity And Kill Application
		 Builder alertbox = new AlertDialog.Builder(PicGalleryGuest.this);
	       // set the message to display
	       alertbox.setMessage("آیا می خواهید از برنامه خارج شوید ؟");
	       
	       // set a negative/no button and create a listener
	       alertbox.setPositiveButton("خیر", new DialogInterface.OnClickListener() {
	           // do something when the button is clicked
	           public void onClick(DialogInterface arg0, int arg1) {
	               arg0.dismiss();
	           }
	       });
	       
	       // set a positive/yes button and create a listener
	       alertbox.setNegativeButton("بله", new DialogInterface.OnClickListener() {
	           // do something when the button is clicked
	           public void onClick(DialogInterface arg0, int arg1) {
	        	   //Declare Object From Get Internet Connection Status For Check Internet Status
	        	  System.exit(0);
	   			  arg0.dismiss();
	   			  
	           }
	       });
	      
	       alertbox.show();
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )  {
	    if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
	    	if(BackActivity.compareTo("Guest")==0)
	    	{
	    		LoadActivity(PicGallery.class, "BackActivity", "Guest");
	    	}
	    	else
	    	{
	    		LoadActivity(PicGallery.class, "PGuid", PersonGuid);
	    	}
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}

}
