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
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PicGallery extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ListAdapter adapter;
	ListView lst_data;
	
	Gallery OneImageView;
	
	String PersonGuid;
	String BackActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	BackActivity = "Empty";
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
        	try{BackActivity = getIntent().getStringExtra("BackActivity").toString();}catch (Exception e1) {e1.printStackTrace();}
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
      //  OneImageView = (Gallery)findViewById(R.id.imageViewOnePicShop);
        
        btnPageTitle = (TextView)findViewById(R.id.newspagetitle);
        btnPageTitle.setText("گالری تصاویر");
        
        btnPageTitle.setTypeface(FontMitra);
        
        lst_data=(ListView)findViewById(R.id.listView1);
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
        
   		FillData();
   		
        //LoadPic();
    }
    
    private void FillData()
	{
		
    	String title,id,pic;
		
		ArrayList<HashMap<String, String>> DataLList;
		DataLList = new ArrayList<HashMap<String, String>>();
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select Code,Name,pic from picgallerylvl1 order by Code desc", null);

		if(cursors.getCount() > 0)
		{
			
			for (int i = 0; i < cursors.getCount(); i++) {
				cursors.moveToNext();
				HashMap<String, String> map = new HashMap<String, String>();
				
				id = cursors.getString(cursors.getColumnIndex("Code"));
				title = cursors.getString(cursors.getColumnIndex("Name"));
				pic = cursors.getString(cursors.getColumnIndex("pic"));
				map.put("id", id);
				map.put("title", title);
				map.put("pic", GetNewsPicFromTempTbl(id));
				map.put("PGuid", PersonGuid);
				DataLList.add(map);
	
			}
			
			adapter = new PicGalleryLvl1Adapter(this, DataLList);
			lst_data.setAdapter(adapter);
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید"), Toast.LENGTH_LONG).show();
		}

	}
    
    
    public String GetNewsPicFromTempTbl(String NewsId)
	{
    	String Res = "0";
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from picgallerylvl1pic where id = "+NewsId, null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			Res = cursors.getString(cursors.getColumnIndex("pic"));
		}
		return Res;
	}
    
    int [] PicIdArray;
	public void LoadPic()
	{
			PicIdArray = new int[14];
			PicIdArray[0]=R.drawable.picgallery14;
			PicIdArray[1]=R.drawable.picgallery1;
			PicIdArray[2]=R.drawable.picgallery2;
			PicIdArray[3]=R.drawable.picgallery3;
			PicIdArray[4]=R.drawable.picgallery4;
			PicIdArray[5]=R.drawable.picgallery5;
			PicIdArray[6]=R.drawable.picgallery6;
			PicIdArray[7]=R.drawable.picgallery7;
			PicIdArray[8]=R.drawable.picgallery8;
			PicIdArray[9]=R.drawable.picgallery9;
			PicIdArray[10]=R.drawable.picgallery10;
			PicIdArray[11]=R.drawable.picgallery11;
			PicIdArray[12]=R.drawable.picgallery12;
			PicIdArray[13]=R.drawable.picgallery13;
			OneImageView.setAdapter(new OnePicImageAdapter(this, PicIdArray));
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
		 Builder alertbox = new AlertDialog.Builder(PicGallery.this);
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
	    		LoadActivity(AppMenuGuest.class, "PGuid", PersonGuid);
	    	}
	    	else
	    	{
	    		LoadActivity(AppMenu.class, "PGuid", PersonGuid);
	    	}
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.MnuAppMenu:
	        	if(BackActivity.compareTo("Guest")==0)
		    	{
		    		LoadActivity(AppMenuGuest.class, "PGuid", PersonGuid);
		    	}
		    	else
		    	{
		    		LoadActivity(AppMenu.class, "PGuid", PersonGuid);
		    	}
	            return true;
	        case R.id.MnuAboutOwner:
	        	LoadActivity(AboutOwner.class,"PGuid",PersonGuid);
	            return true;
	        case R.id.MnuExitApp:
	        	ExitApplication();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
