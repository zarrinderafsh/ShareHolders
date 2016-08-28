package com.example.shareholders;

import java.io.IOException;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowOneProjects extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,txtNewsTitle,txtNewsDate;
	TextViewEx txtNewsDiscription;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ImageView OneNewsImg;
	
	String PersonGuid;
	String BackActivity;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showonenews);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	BackActivity = "Empty";
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
        	BackActivity = "Guest";
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        btnPageTitle = (TextView)findViewById(R.id.newspagetitle);
        txtNewsTitle = (TextView)findViewById(R.id.showonenewstitle);
        txtNewsDate = (TextView)findViewById(R.id.showonenewsdate);
        txtNewsDiscription= (TextViewEx)findViewById(R.id.textViewEx);
        OneNewsImg = (ImageView)findViewById(R.id.onenewsimageView1);
        
        btnPageTitle.setText("پروژه ها");
        
        btnPageTitle.setTypeface(FontMitra);
        txtNewsTitle.setTypeface(FontMitra);
        txtNewsDate.setTypeface(FontMitra);
        txtNewsDiscription.setTypeface(FontMitra);
        
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
        
   		FillData(getIntent().getStringExtra("NewsId").toString());
        
    }
    
    
    private void FillData(String Id)
	{
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from projects where id = "+Id, null);

		if(cursors.getCount() > 0)
		{
			String title,sDate,discription,pic;
			cursors.moveToNext();
			discription = cursors.getString(cursors.getColumnIndex("description"));
			title = cursors.getString(cursors.getColumnIndex("title"));
			sDate = cursors.getString(cursors.getColumnIndex("sDate"));
			pic = cursors.getString(cursors.getColumnIndex("pic"));
			txtNewsDiscription.setText(discription);
			txtNewsDate.setText(sDate);
			txtNewsTitle.setText(title);
			
			OneNewsImg.setImageResource(R.drawable.logojpg);
			
//			if(pic.length() > 20)
//			{
//				try
//				{
//					byte[] decodedByte = Base64.decode(pic, Base64.DEFAULT);
//					Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//					OneNewsImg.setImageBitmap(Bmp);
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
			
//			if(pic.length() > 20)
//			{
//				try
//				{
//					byte[] decodedByte = Base64.decode(pic, Base64.DEFAULT);
//					Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//					OneNewsImg.setImageBitmap(Bmp);
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			else
//			{
				try
				{
					byte[] decodedByte = Base64.decode(GetNewsPicFromTempTbl(pic), Base64.DEFAULT);
					Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
					OneNewsImg.setImageBitmap(Bmp);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			//}
			
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
		Cursor cursors = db.rawQuery("select * from picgallery where picCode = "+NewsId, null);
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
		 Builder alertbox = new AlertDialog.Builder(ShowOneProjects.this);
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
	    		LoadActivity(Projects.class, "BackActivity", "Guest");
	    	}
	    	else {
	    		LoadActivity(Projects.class,"PGuid",PersonGuid);
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
		    		LoadActivity(AppMenuGuest.class,"PGuid",PersonGuid);
		    	}
		    	else {
		    		LoadActivity(AppMenu.class,"PGuid",PersonGuid);
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
