package com.example.shareholders;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppMenuGuest extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	LinearLayout AppMenuMailLayout;
	
	Button btnExit,btnAboutUs,btnPicGallery,btnNews,btnLawyerInfo,btnProjects;
	TextView textVSoftTitle,textVSoftVersion;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	String PersonGuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appmguest);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		AppMenuMailLayout = (LinearLayout)findViewById(R.id.AppMenuMailLayout);
		
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        textVSoftVersion= (TextView)findViewById(R.id.textVSoftVersion);
        textVSoftTitle = (TextView)findViewById(R.id.textVSoftTitle);
        btnExit=(Button)findViewById(R.id.btnExit);
        btnAboutUs=(Button)findViewById(R.id.btnAboutUs);
        btnPicGallery=(Button)findViewById(R.id.btnPicGallery);
        btnNews=(Button)findViewById(R.id.btnNews);
        btnLawyerInfo=(Button)findViewById(R.id.btnLawyerInfo);
        btnProjects  =(Button)findViewById(R.id.btnProjects);
        
        btnNews.setTypeface(FontMitra);
        btnLawyerInfo.setTypeface(FontMitra);
        btnExit.setTypeface(FontMitra);
        btnAboutUs.setTypeface(FontMitra);
        btnPicGallery.setTypeface(FontMitra);
        textVSoftTitle.setTypeface(FontMitra);
        btnProjects.setTypeface(FontMitra);
        textVSoftVersion.setTypeface(FontMitra);
        
       // btnSharesInfo.setTextSize(18);
        
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
        
        if(GetAppMenuItemClicked("FlagAboutUs").compareTo("0")==0)
		{
		        try{
		        	
		        	SyncPicGalleryLvl1 SPGL1 = new SyncPicGalleryLvl1(AppMenuGuest.this, PersonGuid, false, false);
					SPGL1.AsyncExecute();
					
					SyncPicGalleryLvl2 SPGL2 = new SyncPicGalleryLvl2(AppMenuGuest.this, PersonGuid, false, false);
					SPGL2.AsyncExecute();
		        	
		        	SyncLastSharePrices SLSP = new SyncLastSharePrices(AppMenuGuest.this, PersonGuid,false);
		        	SLSP.AsyncExecute();
		        	
		        	SyncNews SNE = new SyncNews(AppMenuGuest.this, PersonGuid,false,false);
					SNE.AsyncExecute();
					
					SyncAboutUs SAUS = new SyncAboutUs(AppMenuGuest.this, PersonGuid,false);
					SAUS.AsyncExecute();
					
					SyncProjects SAUSP = new SyncProjects(AppMenuGuest.this, PersonGuid,false,false);
					SAUSP.AsyncExecute();
					
					//update Benefits
					SyncBenefits SBF = new SyncBenefits(AppMenuGuest.this, PersonGuid,false);
					SBF.AsyncExecute();
					//update Benefits
					
					UpdateAppMenuItemClicked("FlagAboutUs");
				}
		        catch (Exception e) {
					e.printStackTrace();
				}
		        
		        
		}
        
        try
        {
        	GetScreenResolutionAndChangeMainLayout();
        }
        catch (Exception e) {}
        
        
        try{
        	SyncProjects SAUSP = new SyncProjects(AppMenuGuest.this, PersonGuid,false,false);
        	SAUSP.AsyncExecute();
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
    public void UpdateAppMenuItemClicked(String Name)
	{
		db = dbh.getWritableDatabase();
		db.execSQL("update settings set value='1' where name = '"+Name+"'");
	}
    
    public String GetAppMenuItemClicked(String Name)
	{
    	String Res="";
    	db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select value from settings where name = '"+Name+"'", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			Res = cursors.getString(cursors.getColumnIndex("value"));
		}
		return Res;
	}
    
    
    
   
    public void clickHandler(View v){
		try {
			switch (v.getId()) {
			case R.id.btnAboutUs:
				LoadActivity(AboutUsFromUrl.class, "BackActivity", "Guest");
				break;
			case R.id.btnPicGallery:
				//update Picture Gallery
				//SyncPicGallery SPG = new SyncPicGallery(AppMenu.this, PersonGuid,false);
				//SPG.AsyncExecute();
			    //update Picture Gallery
				LoadActivity(PicGallery.class, "BackActivity", "Guest");
				break;
			case R.id.btnLawyerInfo:
				//update Last Share Price
//				SyncLastSharePrices SLSP = new SyncLastSharePrices(AppMenuGuest.this, PersonGuid,false);
	//			SLSP.AsyncExecute();
				//update Last Share Price
				LoadActivity(LastPeices.class, "BackActivity", "Guest");
				break;
			case R.id.btnNews:
//				try
//				{
//					//update News
//					SyncNews SNE = new SyncNews(AppMenu.this, PersonGuid,false,true);
//					SNE.AsyncExecute();
//					//update News
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
				LoadActivity(News.class, "BackActivity", "Guest");
				break;
			case R.id.btnProjects:
//				try
//				{
//					//update News
//					SyncNews SNE = new SyncNews(AppMenu.this, PersonGuid,false,true);
//					SNE.AsyncExecute();
//					//update News
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
				LoadActivity(Projects.class, "BackActivity", "Guest");
				break;
			case R.id.btnExit:
				//ExitApplication();
				try{
				AllSync();
				}catch(Exception ex){}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			
		}
	}
    
    public void AllSync()
    {
    	
    	try
		{
	    	SyncPicGalleryLvl1 SPGL1 = new SyncPicGalleryLvl1(AppMenuGuest.this, PersonGuid, true, false);
			SPGL1.AsyncExecute();
			
			SyncPicGalleryLvl2 SPGL2 = new SyncPicGalleryLvl2(AppMenuGuest.this, PersonGuid, true, false);
			SPGL2.AsyncExecute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
		try
		{
			//update News
			SyncNews SNE = new SyncNews(AppMenuGuest.this, PersonGuid,true,false);
			SNE.AsyncExecute();
			//update News
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			//update About Us
			SyncAboutUs SAUS = new SyncAboutUs(AppMenuGuest.this, PersonGuid,false);
			SAUS.AsyncExecute();
		    //update About Us
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			//update About Us
			SyncProjects SAUSP = new SyncProjects(AppMenuGuest.this, PersonGuid,false,false);
			SAUSP.AsyncExecute();
		    //update About Us
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try
		{
			//update Benefits
			SyncBenefits SBF = new SyncBenefits(AppMenuGuest.this, PersonGuid,false);
			SBF.AsyncExecute();
			//update Benefits
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
    
    public void GetScreenResolutionAndChangeMainLayout()
    {
    	DisplayMetrics displayMetrics = new DisplayMetrics();
    	WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
    	wm.getDefaultDisplay().getMetrics(displayMetrics);
    	int screenWidth = displayMetrics.widthPixels;
    	int screenHeight = displayMetrics.heightPixels;
    	if(screenHeight < 500)
    	{
    		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
    		lp.topMargin = 30;
    		AppMenuMailLayout.setLayoutParams(lp);
    	}
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
	
	public void LoadActivityPublic(Class<?> Cls,String VariableName,String VariableValue)
	{
		Intent intent = new Intent(this,Cls);
		intent.putExtra(VariableName, VariableValue);
		startActivity(intent);
	}
	
	private void ExitApplication()
	{
		//Exit All Activity And Kill Application
		 Builder alertbox = new AlertDialog.Builder(AppMenuGuest.this);
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
	       alertbox.setNegativeButton("بلی", new DialogInterface.OnClickListener() {
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
	    	//LoadActivity(Login.class, "", "");
	    	ExitApplication();
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}

}
