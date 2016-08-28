package com.example.shareholders;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ShowOneMessage extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,txtNewsTitle,txtNewsDate;
	TextViewEx txtNewsDiscription;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	DatabaseHelper dbhUpdateMessage;
	SQLiteDatabase dbUpdateMessage;
	
	String PersonGuid;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showonenews);
        
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
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        btnPageTitle = (TextView)findViewById(R.id.newspagetitle);
        txtNewsTitle = (TextView)findViewById(R.id.showonenewstitle);
        txtNewsDate = (TextView)findViewById(R.id.showonenewsdate);
        txtNewsDiscription= (TextViewEx)findViewById(R.id.textViewEx);
        
        btnPageTitle.setText(PersianReshape.reshape("پیام ها"));
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
   		
   		dbhUpdateMessage=new DatabaseHelper(getApplicationContext());
		try {

			dbhUpdateMessage.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhUpdateMessage.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
   		
   		
   		MessageId = getIntent().getStringExtra("MessageId").toString();
   		try{SendPersonalPicToWs();}catch (Exception e){}
        
   		FillData(getIntent().getStringExtra("MessageId").toString());
   		
   		try{
   		UpdateMessageUnRead(getIntent().getStringExtra("MessageId").toString());
   		} catch (Exception e){}
   		
   		
        
    }
	
	public void UpdateMessageUnRead(String Id)
    {
		dbUpdateMessage = dbhUpdateMessage.getReadableDatabase();
   		Cursor cursors = dbUpdateMessage.rawQuery("select openned,title from messages where id="+Id, null);
   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			String OpenStatus = "";
   			OpenStatus = cursors.getString(cursors.getColumnIndex("openned"));
   			if(OpenStatus.compareTo("0")==0)
   			{
   				dbUpdateMessage = dbhUpdateMessage.getWritableDatabase();
   		   		dbUpdateMessage.execSQL("update messages set title=title-1 where type='100'");
   			}
   		}
   		dbUpdateMessage = dbhUpdateMessage.getWritableDatabase();
	    dbUpdateMessage.execSQL("update messages set openned='1' where id="+Id);
    }
    
    
    private void FillData(String Id)
	{
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select id,senderId,receiverId,SenderName,sDate,[type],title,body from messages where id="+Id, null);

		if(cursors.getCount() > 0)
		{
			String title,sDate,discription;
			cursors.moveToNext();
			discription = cursors.getString(cursors.getColumnIndex("body"));
			title = cursors.getString(cursors.getColumnIndex("title"));
			sDate = cursors.getString(cursors.getColumnIndex("sDate"));
			txtNewsDiscription.setText(discription);
			txtNewsDate.setText(sDate);
			txtNewsTitle.setText(title);
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید"), Toast.LENGTH_LONG).show();
		}

	}
    
    
    
    
    
    String MessageId="";
    public void SendPersonalPicToWs()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSPersonalPic task = new AsyncCallWSPersonalPic(ShowOneMessage.this);
             task.execute();
			}	
			 catch (Exception e) {
				//Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
			 }
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_SHORT).show();
		}
	}
	
	private class AsyncCallWSPersonalPic extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWSPersonalPic(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	CallWsMethodSendPersonalPic("UpdateMessageIsRead");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            //if(WsResault.compareTo("1")==0)
            //{
            	//Toast.makeText(getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
				//update Personal Image
				//SyncPersonalPic SPPI = new SyncPersonalPic(ShowOneMessage.this, PersonGuid,false,false);
				//SPPI.AsyncExecute();
				//update Personal Image
           // }
            //if (this.dialog.isShowing()) {
             //   this.dialog.dismiss();
           // }
        }
 
        @Override
        protected void onPreExecute() {
            //this.dialog.setMessage(PersianReshape.reshape("در حال ارسال اطلاعات"));
            //this.dialog.show();
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
    
	String WsResault;
	public void CallWsMethodSendPersonalPic(String METHOD_NAME) {
		PublicVariable PV = new PublicVariable();
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("pGuid");
	    //Set Value
	    UserPI.setValue(PersonGuid);
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
	    
	    PropertyInfo PassPI = new PropertyInfo();
	    //Set Name
	    PassPI.setName("MessageId");
	    //Set Value
	    PassPI.setValue(MessageId);
	    //Set dataType
	    PassPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PassPI);
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
	        String CuWsResault;
	        CuWsResault = response.toString();	
	        WsResault = CuWsResault;
//	        if(Integer.valueOf(CuWsResault) < 1)
//	        {
//	        	runOnUiThread(new Runnable() 
//				{
//				   public void run() 
//				   {
//					   Toast.makeText(getApplicationContext(), "خطا در ثبت عکس پرسنلی", Toast.LENGTH_SHORT).show();
//				   }
//				}
//	            );
//	        }
	    } catch (Exception e) {
//	    	runOnUiThread(new Runnable() 
//				{
//				   public void run() 
//				   {
//					   Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
//				   }
//				});
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
		 Builder alertbox = new AlertDialog.Builder(ShowOneMessage.this);
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
	    	LoadActivity(Messages.class, "PGuid", PersonGuid);
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
	        	LoadActivity(AppMenu.class,"PGuid",PersonGuid);
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
