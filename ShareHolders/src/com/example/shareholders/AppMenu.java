package com.example.shareholders;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppMenu extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	LinearLayout AppMenuMailLayout;
	
	Button btnPersonalInfo,btnLawyerInfo,btnSharesInfo,
	btnFinancialInfo,btnNews,btnMessages,btnSync,btnExit,btnAboutUs,btnPicGallery,btnAparteman,btnGround
	,btnExitOrg,btnProjects;
	TextView textVSoftTitle,textVSoftVersion;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	DatabaseHelper dbhGetMessageUnread;
	SQLiteDatabase dbGetMessageUnread;
	
	String PersonGuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appm);
        
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
        btnPersonalInfo=(Button)findViewById(R.id.btnPersonalInfo);
        btnLawyerInfo=(Button)findViewById(R.id.btnLawyerInfo);
        btnSharesInfo=(Button)findViewById(R.id.btnSharesInfo);
        btnFinancialInfo=(Button)findViewById(R.id.btnFinancialInfo);
        btnNews=(Button)findViewById(R.id.btnNews);
        btnMessages=(Button)findViewById(R.id.btnMessages);
        btnSync=(Button)findViewById(R.id.btnSync);
        btnExit=(Button)findViewById(R.id.btnExit);
        btnAboutUs=(Button)findViewById(R.id.btnAboutUs);
        btnPicGallery=(Button)findViewById(R.id.btnPicGallery);
        btnAparteman=(Button)findViewById(R.id.btnAparteman);
        btnGround=(Button)findViewById(R.id.btnGround);
        btnExitOrg=(Button)findViewById(R.id.btnExitOrg);
        btnProjects=(Button)findViewById(R.id.btnProjects);
        
        btnProjects.setTypeface(FontMitra);
        btnExitOrg.setTypeface(FontMitra);
        btnPersonalInfo.setTypeface(FontMitra);
        btnLawyerInfo.setTypeface(FontMitra);
        btnSharesInfo.setTypeface(FontMitra);
        btnFinancialInfo.setTypeface(FontMitra);
        btnNews.setTypeface(FontMitra);
        btnMessages.setTypeface(FontMitra);
        btnSync.setTypeface(FontMitra);
        btnExit.setTypeface(FontMitra);
        btnAboutUs.setTypeface(FontMitra);
        btnPicGallery.setTypeface(FontMitra);
        textVSoftTitle.setTypeface(FontMitra);
        btnGround.setTypeface(FontMitra);
        btnAparteman.setTypeface(FontMitra);
        textVSoftVersion.setTypeface(FontMitra);
        
       // LoadAnimationMenuItem();
        
       // btnSharesInfo.setTextSize(18);
        
        try
        {
        	GetScreenResolutionAndChangeMainLayout();
        }
        catch (Exception e) {}
        
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
   		
   		dbhGetMessageUnread=new DatabaseHelper(getApplicationContext());
		try {

			dbhGetMessageUnread.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhGetMessageUnread.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
   		
   		try
   		{
	   		SyncMessageUnReadIsNotificationAll SMUINA = new SyncMessageUnReadIsNotificationAll(getApplicationContext());
	   		SMUINA.AsyncExecute();
   		}
   		catch (Exception e) {
			e.printStackTrace();
		}
        
   		try
   		{
	        //update Last Share Price
			SyncLastSharePrices SLSP1 = new SyncLastSharePrices(AppMenu.this, PersonGuid,false);
			SLSP1.AsyncExecute();
			//update Last Share Price
   		}
   		catch (Exception e) {
			e.printStackTrace();
		}
   		
   		if(GetAppMenuItemClicked("FlagAboutUs").compareTo("0")==0)
		{
   			
   			try
			{
				AllSync();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
   			
   			UpdateAppMenuItemClicked("FlagAboutUs");
			try
			{
				//update Lawyers
				SyncLawyers SLW = new SyncLawyers(AppMenu.this, PersonGuid,false,false);
				SLW.AsyncExecute();
				//update Lawyers
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			try
			{
				//update Message InBox
				SyncMessageInbox SMI = new SyncMessageInbox(AppMenu.this, PersonGuid,false,false);
				SMI.AsyncExecute();
				//update Message InBox
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
   		
   		GetMessageUnreadFunc();
   		
   		final Handler mHandler = new Handler();
   		new Thread(new Runnable() {
   	        @Override
   	        public void run() {
   	            // TODO Auto-generated method stub
   	            while (true) {
   	                try {
   	                    Thread.sleep(3000);
   	                    mHandler.post(new Runnable() {

   	                        @Override
   	                        public void run() {
   	                            // TODO Auto-generated method stub
   	                            // Write your code here to update the UI.
   	                        	GetMessageUnreadFunc();
   	                        }
   	                    });
   	                } catch (Exception e) {
   	                    // TODO: handle exception
   	                }
   	            }
   	        }
   	    }).start();
   		
        
    }
    
    
    public void GetMessageUnreadFunc()
    {
    	dbGetMessageUnread = dbhGetMessageUnread.getReadableDatabase();
   		Cursor cursors = dbGetMessageUnread.rawQuery("select * from messages where openned = '0'", null);

   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			//Toast.makeText(getApplicationContext(), "شما "+cursors.getString(cursors.getColumnIndex("title"))+" پیام خوانده نشده دارید", Toast.LENGTH_LONG).show();
   			//NotificationClass.Notificationm(getApplicationContext(), "پیام جدید از طرف الهیه", "سهامدار عزیز شما "+cursors.getString(cursors.getColumnIndex("title"))+" پیام جدید در نرم افزار سهامداری شرکت الهیه خراسان دارید", "a");
   			btnMessages.setText(Html.fromHtml("<b>"+String.valueOf(cursors.getCount() - 1) + " " + "پیام جدید"+"</b>"));
   			btnMessages.setTextColor(Color.RED);
   			
   			//db = dbh.getWritableDatabase();
        	//db.execSQL("delete from messages where type = '100'");
   		}
    }
    
    public void LoadAnimationMenuItem()
    {
    	TranslateAnimation LeftToRightAnimation = new TranslateAnimation(-400.0f, 0.0f,
	            0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
    	LeftToRightAnimation.setDuration(2000);  // animation duration 
    	LeftToRightAnimation.setRepeatCount(0);  // animation repeat count
    	LeftToRightAnimation.setRepeatMode(1);
    	
    	btnPersonalInfo.startAnimation(LeftToRightAnimation);
    	btnSharesInfo.startAnimation(LeftToRightAnimation);
    	btnAparteman.startAnimation(LeftToRightAnimation);
    	btnNews.startAnimation(LeftToRightAnimation);
    	btnAboutUs.startAnimation(LeftToRightAnimation);
    	btnSync.startAnimation(LeftToRightAnimation);
    	
    	btnLawyerInfo.startAnimation(LeftToRightAnimation);
    	btnFinancialInfo.startAnimation(LeftToRightAnimation);
    	btnGround.startAnimation(LeftToRightAnimation);
    	btnMessages.startAnimation(LeftToRightAnimation);
    	btnPicGallery.startAnimation(LeftToRightAnimation);
    	btnExit.startAnimation(LeftToRightAnimation);
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
    
    public int FlagPersonalInfo = 0,FlagSharesInfo=0,FlagAboutUs=0;
    public void clickHandler(View v){
		try {
			switch (v.getId()) {
			case R.id.btnPersonalInfo:
				//if(GetAppMenuItemClicked("FlagPersonalInfo").compareTo("0")==0)
				//{
					//update Personal Info
					//SyncPerosnalInfo SPI = new SyncPerosnalInfo(AppMenu.this, PersonGuid,true,true);
					//SPI.AsyncExecute();
					//update Personal Info
					
					//update Personal Image
					//SyncPersonalPic SPPI = new SyncPersonalPic(AppMenu.this, PersonGuid,true);
					//SPPI.AsyncExecute();
					//update Personal Image
					//LoadActivity(PersonalInfo.class, "PGuid", PersonGuid);
					//UpdateAppMenuItemClicked("FlagPersonalInfo");
				//}
				//else
				//{
					LoadActivity(PersonalInfo.class, "PGuid", PersonGuid);
				//}
				break;
			case R.id.btnSharesInfo:
				if(GetAppMenuItemClicked("FlagSharesInfo").compareTo("0")==0)
				{
					try
					{
						//update Buy And Sell Requests
						SyncBuyAndSellRequests SBASR = new SyncBuyAndSellRequests(AppMenu.this, PersonGuid,false,false);
						SBASR.AsyncExecute();
						//update Buy And Sell Requests
						UpdateAppMenuItemClicked("FlagSharesInfo");
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					LoadActivity(SharesSalesAndBuy.class, "PGuid", PersonGuid);
				}
				else
				{
					LoadActivity(SharesSalesAndBuy.class, "PGuid", PersonGuid);
				}
				break;
			case R.id.btnFinancialInfo:
				//update Financialls
				//SyncFinancialls SFS = new SyncFinancialls(AppMenu.this, PersonGuid);
				//SFS.AsyncExecute();
				//update Financialls
				LoadActivity(FinancialInfo.class, "PGuid", PersonGuid);
				break;
			case R.id.btnLawyerInfo:
				//update Last Share Price
				//SyncLastSharePrices SLSP = new SyncLastSharePrices(AppMenu.this, PersonGuid,false);
				//SLSP.AsyncExecute();
				//update Last Share Price
				LoadActivity(LastPeices.class, "PGuid", PersonGuid);
				break;
			case R.id.btnMessages:
				LoadActivity(Messages.class, "PGuid", PersonGuid);
				break;
			case R.id.btnAboutUs:
				//LoadActivity(AboutUsFromUrl.class, "PGuid", PersonGuid);
				LoadActivity(Projects.class, "PGuid", PersonGuid);
				break;
			case R.id.btnPicGallery:
				//update Picture Gallery
				//SyncPicGallery SPG = new SyncPicGallery(AppMenu.this, PersonGuid,false);
				//SPG.AsyncExecute();
			    //update Picture Gallery
				//LoadActivity(ChangePass.class, "PGuid", PersonGuid);
				LoadActivity(PicGallery.class, "PGuid", PersonGuid);
				break;
			case R.id.btnAparteman:
				LoadActivity(Aparteman.class, "PGuid", PersonGuid);
				break;
			case R.id.btnGround:
				LoadActivity(Ground.class, "PGuid", PersonGuid);
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
				LoadActivity(News.class, "PGuid", PersonGuid);
				break;
			case R.id.btnSync:
				try
				{
					AllSync();
				}
				catch (Exception e) {
					Toast.makeText(getApplicationContext(), "خطا در بروزرسانی", Toast.LENGTH_SHORT).show();
				}
				return;
			case R.id.btnExit:
				//db = dbh.getWritableDatabase();
				//db.execSQL("update settings set value=datetime('now', '-3 Minute') where name='DateOfLastLogin'");
				//ExitApplication();
				//LoadActivity(PicGallery.class, "PGuid", PersonGuid);
				LoadActivity(ChangePass.class, "PGuid", PersonGuid);
				break;
			case R.id.btnExitOrg:
				db = dbh.getWritableDatabase();
				db.execSQL("update settings set value=datetime('now', '-3 Minute') where name='DateOfLastLogin'");
				ExitApplication();
				//LoadActivity(PicGallery.class, "PGuid", PersonGuid);
				break;
			case R.id.btnProjects:
				//db = dbh.getWritableDatabase();
				//db.execSQL("update settings set value=datetime('now', '-3 Minute') where name='DateOfLastLogin'");
				//ExitApplication();
				//LoadActivity(Projects.class, "PGuid", PersonGuid);
				LoadActivity(AboutUsFromUrl.class, "PGuid", PersonGuid);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			
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
    
    public void AllSync()
    {
    	
    	try{
			SyncPicGalleryLvl1 SPGL1 = new SyncPicGalleryLvl1(AppMenu.this, PersonGuid, false, false);
			SPGL1.AsyncExecute();
			
			SyncPicGalleryLvl2 SPGL2 = new SyncPicGalleryLvl2(AppMenu.this, PersonGuid, false, false);
			SPGL2.AsyncExecute();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
    	
    	try
		{
			//update Message InBox
			SyncMessageInbox SMI = new SyncMessageInbox(AppMenu.this, PersonGuid,false,false);
			SMI.AsyncExecute();
			//update Message InBox
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	try
    	{
	    	//update Lawyers
			SyncLawyers SLW = new SyncLawyers(AppMenu.this, PersonGuid,false,false);
			SLW.AsyncExecute();
			//update Lawyers
    	}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	try
		{
			//update About Us
			SyncShareText SST = new SyncShareText(AppMenu.this, PersonGuid,false,false);
			SST.AsyncExecute();
		    //update About Us
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	try
    	{
	    	//update Buy And Sell Requests
			SyncBuyAndSellRequests SBASR = new SyncBuyAndSellRequests(AppMenu.this, PersonGuid,false,false);
			SBASR.AsyncExecute();
			//update Buy And Sell Requests
    	}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//update Cities
		//SyncCityes SCI = new SyncCityes(AppMenu.this, PersonGuid,false);
		//SCI.AsyncExecute();
		//update Cities
		
    	try
    	{
			//update Personal Info
			SyncPerosnalInfo SPI = new SyncPerosnalInfo(AppMenu.this, PersonGuid,false,false);
			SPI.AsyncExecute();
			//update Personal Info
    	}
		catch (Exception e) {
			e.printStackTrace();
		}
		
    	try
    	{
			//update Personal Image
			SyncPersonalPic SPPI = new SyncPersonalPic(AppMenu.this, PersonGuid,false,false);
			SPPI.AsyncExecute();
			//update Personal Image
    	}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
		//update Last Share Price
		//SyncLastSharePrices SLSP = new SyncLastSharePrices(AppMenu.this, PersonGuid,false);
		//SLSP.AsyncExecute();
		//update Last Share Price
		
		//update Financialls
		SyncFinancialls SFS = new SyncFinancialls(AppMenu.this, PersonGuid,false);
		SFS.AsyncExecute();
		//update Financialls
		
		try
		{
			//update Benefits
			SyncBenefits SBF = new SyncBenefits(AppMenu.this, PersonGuid,false);
			SBF.AsyncExecute();
			//update Benefits
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			//update News
			SyncNews SNE = new SyncNews(AppMenu.this, PersonGuid,true,false);
			SNE.AsyncExecute();
			//update News
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		try
		{
			//update Message OutBox
			SyncMessageSendbox SMO = new SyncMessageSendbox(AppMenu.this, PersonGuid,false,false);
			SMO.AsyncExecute();
			//update Message OutBox
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
//		//update Lawyers
//		try
//		{
//			SyncLawyers SLW = new SyncLawyers(AppMenu.this, PersonGuid,false,false);
//			SLW.AsyncExecute();
//		//update Lawyers
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
		
		try
		{
			//update About Us
			SyncAboutUs SAUS = new SyncAboutUs(AppMenu.this, PersonGuid,false);
			SAUS.AsyncExecute();
		    //update About Us
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			//update About Us
			SyncProjects SAUSP = new SyncProjects(AppMenu.this, PersonGuid,false,false);
			SAUSP.AsyncExecute();
		    //update About Us
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try
		{
			
			SyncAparteman SAP = new SyncAparteman(AppMenu.this, PersonGuid,false,false);
			SAP.AsyncExecute();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			
			SyncApartemanRequest SAPR = new SyncApartemanRequest(AppMenu.this, PersonGuid,false,false);
			SAPR.AsyncExecute();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			
			SyncGround SAG = new SyncGround(AppMenu.this, PersonGuid,false,false);
			SAG.AsyncExecute();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			
			SyncGroundRequest SAGR = new SyncGroundRequest(AppMenu.this, PersonGuid,false,false);
			SAGR.AsyncExecute();
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//update Picture Gallery
		//SyncPicGallery SPG = new SyncPicGallery(AppMenu.this, PersonGuid,true);
		//SPG.AsyncExecute();
	    //update Picture Gallery
		
		try
		{
		GetMessageUnreadFunc();
		}
		catch (Exception e) {
			e.printStackTrace();
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
		 Builder alertbox = new AlertDialog.Builder(AppMenu.this);
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
	    	ExitApplication();
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}

}
