package com.example.shareholders;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.bool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.telephony.TelephonyManager;
import android.text.method.DateTimeKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	EditText EdUserName,EdPassword;
	Button BtnLogin,BtnGuestLogin;
	TextView textVSoftTitle,textVSoftVersion;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	DatabaseHelper dbhPersonalInfo;
	SQLiteDatabase dbPersonalInfo;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        EdUserName = (EditText)findViewById(R.id.EditTextUserName);
        EdPassword = (EditText)findViewById(R.id.EditTextPassword);
        BtnLogin = (Button)findViewById(R.id.BtnLogin);
        BtnGuestLogin = (Button)findViewById(R.id.BtnGuestLogin);
        textVSoftTitle = (TextView)findViewById(R.id.textVSoftTitle);
        textVSoftVersion= (TextView)findViewById(R.id.textVSoftVersion);
        
        EdUserName.setTypeface(FontMitra);
        EdPassword.setTypeface(FontMitra);
        BtnLogin.setTypeface(FontMitra);
        textVSoftTitle.setTypeface(FontMitra);
        textVSoftVersion.setTypeface(FontMitra);
        BtnGuestLogin.setTypeface(FontMitra);
        BtnLogin.setOnClickListener(BtnLoginOnClick);
        BtnGuestLogin.setOnClickListener(BtnGuestLoginOnClick);
        
        
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
   		
   		try {
			if(GetDateOfLastLogin()==1)
			{
				LoadActivity(AppMenu.class, "PGuid", GetPersonGuid());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
   		
   		
   		dbhPersonalInfo=new DatabaseHelper(getApplicationContext());
		try {

			dbhPersonalInfo.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhPersonalInfo.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
   		
   		
   		
   		
   		FillPersonalData();
   		
   		
//   		TelephonyManager t = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
//		String number = t.getLine1Number();
		//Toast.makeText(getApplicationContext(),"Phone Number : " + displaySIMContacts(), Toast.LENGTH_LONG).show();
        
    }
    
    
    
    private String displaySIMContacts() {
    	TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
    	String number = tm.getSimSerialNumber();
    	return number;
   }
    
    
    
    public String ShareHolderCode;
    private void FillPersonalData()
   	{
    	dbPersonalInfo = dbhPersonalInfo.getReadableDatabase();
   		Cursor cursors = dbPersonalInfo.rawQuery("select * from persons order by id desc", null);

   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			ShareHolderCode = cursors.getString(cursors.getColumnIndex("code"));
   		}
   		else
   		{
   			ShareHolderCode = "0";
   		}
   	}
    
    private OnClickListener BtnGuestLoginOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			if(EdUserName.getText().length()>0)
//			{
//				if(EdPassword.getText().length()>0)
//				{
//					try
//					{
//						WSLogin(true);
//					}
//					catch (Exception e) {
//						Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
//					}
//				}
//				else
//				{
//					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا رمز عبور را وارد کنید"), Toast.LENGTH_SHORT).show();
//				}
//			}
//			else
//			{
//				Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا نام کاربری را وارد کنید"), Toast.LENGTH_SHORT).show();
//			}
			LoadActivity(AppMenuGuest.class, "PGuid", "0000-0000-0000-0000");
		}
    };
    
    private OnClickListener BtnLoginOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(EdUserName.getText().length()>0)
			{
				if(EdPassword.getText().length()>0)
				{
					try
					{
						WSLogin(false);
					}
					catch (Exception e) {
						Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا رمز عبور را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا نام کاربری را وارد کنید"), Toast.LENGTH_SHORT).show();
			}
			//LoadActivity(AppMenu.class, "PGuid", "1");
		}
	};
	
	public void WSLogin(Boolean IsGuest)
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSLogin task = new AsyncCallWSLogin(Login.this,IsGuest);
             task.execute();
			}	
			 catch (Exception e) {
				Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
			 }
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_SHORT).show();
		}
	}
	
	String PersonGuid; 
	private class AsyncCallWSLogin extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Activity activity;
		private Boolean IsGuest;
		
		public AsyncCallWSLogin(Activity activity,Boolean InIsGuest) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		    this.IsGuest = InIsGuest;
		}
		
        @Override
        protected String doInBackground(String... params) {
        	String result = null;
        	try
        	{
        		CallWsMethodLogin("Login");
        	}
        	catch (Exception e) {
        		result = e.getMessage().toString();
			}
            return result;
        }
 
        protected void onPostExecute(String result) {
        	if(result == null)
        	{
	            if(PersonGuid!= null && PersonGuid.length() > 12)
	            {
	            	try
	           		{
	           		}
	           		catch (Exception e) {
	        			e.printStackTrace();
	        		}
	            	
	            	InsertUserNameAndPassWord();

	            	if(IsGuest==false)
	        		{
	            	if(EdUserName.getText().toString().compareTo(ShareHolderCode)!=0)
	            	{
	            		DeleteFirstLogin();
	            	}
	            	
	            	PV.GuidCode = PersonGuid;
	            	
	            	UpdateAppMenuItemClicked();
	            	
	            	try {
						InsertDateOfLastLogin();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            	
	            	InsertPersonGuid();
	            	
	    			if(IsFirstLogin()==1)
	    			{
	    				
//	    				try
//	    				{
//		    				//update Lawyers
//		    				SyncLawyers SLW = new SyncLawyers(Login.this, PersonGuid,false,false);
//		    				SLW.AsyncExecute();
//		    				//update Lawyers
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				try
//	    				{
//		    				//update About Us
//		    				SyncShareText SST = new SyncShareText(Login.this, PersonGuid,false,false);
//		    				SST.AsyncExecute();
//		    			    //update About Us
//	    				}
//	    				catch (Exception e) {
//							e.printStackTrace();
//						}
//	    				
//	    				try
//	    				{
//		    				//update Buy And Sell Requests
//		    				SyncBuyAndSellRequests SBASR = new SyncBuyAndSellRequests(Login.this, PersonGuid,false,false);
//		    				SBASR.AsyncExecute();
//		    				//update Buy And Sell Requests
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				//update Cities
//	    				//SyncCityes SCI = new SyncCityes(Login.this, PersonGuid,false);
//	    				//SCI.AsyncExecute();
//	    				//update Cities
//	    				
//	    				try
//	    				{
//		    				//update Personal Info
//		    				SyncPerosnalInfo SPI = new SyncPerosnalInfo(Login.this, PersonGuid,true,false);
//		    				SPI.AsyncExecute();
//		    				//update Personal Info
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				try
//	    				{
//		    				//update Personal Image
//		    				SyncPersonalPic SPPI = new SyncPersonalPic(Login.this, PersonGuid,true,false);
//		    				SPPI.AsyncExecute();
//		    				//update Personal Image
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				//update Last Share Price
//	    				//SyncLastSharePrices SLSP = new SyncLastSharePrices(Login.this, PersonGuid,false);
//	    				//SLSP.AsyncExecute();
//	    				//update Last Share Price
//	    				
//	    				//update Financialls
//	    				SyncFinancialls SFS = new SyncFinancialls(Login.this, PersonGuid,false);
//	    				SFS.AsyncExecute();
//	    				//update Financialls
//	    				
//	    				try
//	    				{
//		    				//update Benefits
//		    				SyncBenefits SBF = new SyncBenefits(Login.this, PersonGuid,false);
//		    				SBF.AsyncExecute();
//		    				//update Benefits
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				try
//	    				{
//		    				//update News
//		    				SyncNews SNE = new SyncNews(Login.this, PersonGuid,false,false);
//		    				SNE.AsyncExecute();
//		    				//update News
//	    				}
//	    				catch (Exception e) {
//							e.printStackTrace();
//						}
//	    				
//	    				try
//	    				{
//		    				//update Message InBox
//		    				SyncMessageInbox SMI = new SyncMessageInbox(Login.this, PersonGuid,false,false);
//		    				SMI.AsyncExecute();
//		    				//update Message InBox
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				try
//	    				{
//		    				//update Message OutBox
//		    				SyncMessageSendbox SMO = new SyncMessageSendbox(Login.this, PersonGuid,false,false);
//		    				SMO.AsyncExecute();
//		    				//update Message OutBox
//	    				}
//	    				catch (Exception e) {
//	    					e.printStackTrace();
//	    				}
//	    				
//	    				try
//	    				{
//		    				//update About Us
//		    				SyncAboutUs SAUS = new SyncAboutUs(Login.this, PersonGuid,false);
//		    				SAUS.AsyncExecute();
//		    			    //update About Us
//	    				}
//	    				catch (Exception e) {
//							e.printStackTrace();
//						}
//	    				
//	    				
//	    				try{
//	    					SyncPicGalleryLvl1 SPGL1 = new SyncPicGalleryLvl1(Login.this, PersonGuid, false, false);
//	    					SPGL1.AsyncExecute();
//	    					
//	    					SyncPicGalleryLvl2 SPGL2 = new SyncPicGalleryLvl2(Login.this, PersonGuid, false, false);
//	    					SPGL2.AsyncExecute();
//	    				}
//	    				catch (Exception e) {
//							// TODO: handle exception
//						}
//	    				
//	    				//update Picture Gallery
//	    				//SyncPicGallery SPG = new SyncPicGallery(Login.this, PersonGuid,false);
//	    				//SPG.AsyncExecute();
//	    			    //update Picture Gallery
//	    				
//	    				//InsertIsFirstLogin();
	    				LoadActivity(AppMenu.class, "PGuid", PersonGuid);
	    			}
	    			else
	    			{
	    				LoadActivity(AppMenu.class, "PGuid", PersonGuid);
	    			}
	        		}
	        		else{
		            	LoadActivity(AppMenuGuest.class, "PGuid", "0000-0000-0000-0000");
		            	}
	            
	            }
	            else
	            {
	            	Toast.makeText(getApplicationContext(), "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
	            }
        		
        	}
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
 
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(PersianReshape.reshape("در حال دریافت اطلاعات"));
            this.dialog.show();
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
	
	public int IsFirstLogin()
	{
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from settings where name='IsFirstLogin'", null);
		if(cursors.getCount() > 0)
		{
			return 0;
		}
		return 1;
	}
	
//	public void InsertCuUserName(String UserName)
//	{
//		db = dbh.getWritableDatabase();
//		db.execSQL("insert into settings(name,value) values('CuUserName','" + UserName + "')");
//	}
//	
//	public void UpdateCuUserName(String UserName)
//	{
//		db = dbh.getWritableDatabase();
//		db.execSQL("update settings set value='"+UserName+"' where name = 'CuUserName'");
//	}
	
	public void UpdateAppMenuItemClicked()
	{
		db = dbh.getWritableDatabase();
		db.execSQL("update settings set value='0' where name in('FlagPersonalInfo','FlagSharesInfo','FlagAboutUs')");
	}
	
	public void InsertIsFirstLogin()
	{
		db = dbh.getWritableDatabase();
		db.execSQL("insert into settings(name,value) values('IsFirstLogin','Do It')");
	}
	
	public void DeleteFirstLogin()
	{
		db = dbh.getWritableDatabase();
		db.execSQL("delete from settings where name = 'IsFirstLogin'");
	}
	
	public void InsertDateOfLastLogin() throws ParseException
	{
		db = dbh.getWritableDatabase();
		db.execSQL("delete from settings where name='DateOfLastLogin'");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    Calendar cal = Calendar.getInstance();
	    String time_str = dateFormat.format(cal.getTime());
	    java.util.Date CuTime;
		CuTime = dateFormat.parse(time_str);
		db = dbh.getWritableDatabase();
		db.execSQL("insert into settings(name,value) values('DateOfLastLogin',datetime('now'))");
//		if(CuTime.getMonth() > 4 & CuTime.getMonth() < 10)
//		{
//			db = dbh.getWritableDatabase();
//			db.execSQL("insert into settings(name,value) values('DateOfLastLogin',datetime('now', '+270 Minute'))");
//		}
//		else
//		{
//			db = dbh.getWritableDatabase();
//			db.execSQL("insert into settings(name,value) values('DateOfLastLogin',datetime('now', '+210 Minute'))");
//		}
	}
	
	
	public void InsertUserNameAndPassWord()
	{
		db = dbh.getWritableDatabase();
		db.execSQL("delete from settings where name='UserName' and name='PassWord'");
		db = dbh.getWritableDatabase();
		db.execSQL("insert into settings(name,value) values('UserName','"+EdUserName.getText().toString()+"')");
		db = dbh.getWritableDatabase();
		db.execSQL("insert into settings(name,value) values('PassWord','"+EdPassword.getText().toString()+"')");
	}
	
	public void InsertPersonGuid()
	{
		db = dbh.getWritableDatabase();
		db.execSQL("delete from settings where name='PersonGuid'");
		db = dbh.getWritableDatabase();
		db.execSQL("insert into settings(name,value) values('PersonGuid','"+PersonGuid+"')");
		
		try
		{
			AlarmManager mgr=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			Intent i=new Intent(getApplicationContext(), MyAlarmManager.class);
			//i.putExtra("PGuid", PersonGuid);
			PendingIntent pi=PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 61000, pi);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public String GetPersonGuid()
	{
		String PGuid = null;
		Cursor cursors = db.rawQuery("select * from settings where name='PersonGuid'", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			PGuid = cursors.getString(cursors.getColumnIndex("value"));
		}
		return PGuid;
	}
	
	public int GetDateOfLastLogin() throws ParseException
	{
		String LastDateLogin,time_str;
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat dateFormatFromDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    Calendar cal = Calendar.getInstance();
//	    String time_str = dateFormat.format(cal.getTime());
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select name,value,datetime('now')DateTimeNow from settings where name='DateOfLastLogin' order by value desc", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			LastDateLogin = cursors.getString(cursors.getColumnIndex("value"));
			time_str = cursors.getString(cursors.getColumnIndex("DateTimeNow"));
			
			java.util.Date CuTime,CuLastLogin;
			CuTime = dateFormatFromDb.parse(time_str);
			CuLastLogin = dateFormatFromDb.parse(LastDateLogin);
			
			//Toast.makeText(getApplicationContext(), CuTime.toString(), Toast.LENGTH_LONG).show();
			//Toast.makeText(getApplicationContext(), CuLastLogin.toString(), Toast.LENGTH_LONG).show();
			
			long diffInMs = CuTime.getTime() - CuLastLogin.getTime();

			long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
			
			if (diffInSec > 0 && diffInSec < 60)
			{
				return 1;
			}
			return 0;
		}
		return 0;
	}
	
	public void CallWsMethodLogin(String METHOD_NAME) {

		String UserName = "";
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from settings where name='UserName'", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			UserName = cursors.getString(cursors.getColumnIndex("value"));
		}
		String OldUserName = EdUserName.getText().toString();
		Integer i1,i2;
		i1 = Integer.valueOf(OldUserName);
		i2 = Integer.valueOf(UserName);

		if(UserName.length() > 0 && (i1+1) != (i2+1)) {
			Toast.makeText(getApplicationContext(), "شما نمی توانید با این نام کاربری وارد شوید. در صورت نیاز نرم افزار را پاک و دوباره نصب کنید", Toast.LENGTH_SHORT).show();
			return;
		}
		PublicVariable PV = new PublicVariable();
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("UserName");
	    //Set Value
	    UserPI.setValue(EdUserName.getText().toString());
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
	    PropertyInfo PassPI = new PropertyInfo();
	    //Set Name
	    PassPI.setName("Password");
	    //Set Value
	    PassPI.setValue(EdPassword.getText().toString());
	    //Set dataType
	    PassPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PassPI);
//	    PropertyInfo MobilePI = new PropertyInfo();
//	    //Set Name
//	    MobilePI.setName("mobile");
//	    //Set Value
//	    MobilePI.setValue(displaySIMContacts());
//	    //Set dataType
//	    MobilePI.setType(String.class);
//	    //Add the property to request object
//	    request.addProperty(MobilePI);
	    //Create envelope
	    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	            SoapEnvelope.VER11);
	    envelope.dotNet = true;
	    //Set output SOAP object
	    envelope.setOutputSoapObject(request);
	    //Create HTTP call object
	    HttpTransportSE androidHttpTransport = new HttpTransportSE(PV.URL);
	    try {
	        //Invoke web service
	        androidHttpTransport.call("http://tempuri.org/"+METHOD_NAME, envelope);
	        //Get the response
	        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
	        //Assign it to FinalResultForCheck static variable
	        PersonGuid = response.toString();	
	        if(PersonGuid != null && PersonGuid.toString().length() < 12)
	        {
	        	runOnUiThread(new Runnable() 
				{
				   public void run() 
				   {
					   Toast.makeText(getApplicationContext(), "اطلاعات وارد شده صحیح نمی باشد", Toast.LENGTH_SHORT).show();
				   }
				}
	            );
	        }
	    } catch (Exception e) {
	    	runOnUiThread(new Runnable() 
				{
				   public void run() 
				   {
					   Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
				   }
				});
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
	
	private void ExitApplication()
	{
		//Exit All Activity And Kill Application
		 Builder alertbox = new AlertDialog.Builder(Login.this);
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
