package com.example.shareholders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Messages extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,messageSendToLable;
	Button BtnSendMessage,BtnSendEnteghadVaPishnahad,BtnSendLawyerMess;
	Spinner lstLawyers,lstAllLawyers;
	EditText txtTitlem,txtBodym,txtTitlemEnteghadVaPishnahad,txtBodymEnteghadVaPishnahad,txtBodymLawyerMess;
	
	ListView lst_dataInbox,lst_dataSendbox;
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ListAdapter adapter;
	
	int SelectLawyer = 0 , SMSSelectLawyer = 0;
	String PersonGuid;
	
	DatabaseHelper dbhPersonalInfo;
	SQLiteDatabase dbPersonalInfo;
	
	DatabaseHelper dbhLawyerMobile;
	SQLiteDatabase dbLawyerMobile;
	
//	DatabaseHelper dbhUpdateMessage;
//	SQLiteDatabase dbUpdateMessage;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);
        
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
        
        btnPageTitle = (TextView)findViewById(R.id.messagespagetitle);
        messageSendToLable = (TextView)findViewById(R.id.messageSendToLable);
        lstLawyers = (Spinner)findViewById(R.id.lstLawyers);
        lstAllLawyers = (Spinner)findViewById(R.id.lstAllLawyers);
        txtTitlem = (EditText)findViewById(R.id.txtTitlem);
        txtBodym = (EditText)findViewById(R.id.txtBodym);
        txtBodymLawyerMess = (EditText)findViewById(R.id.txtBodymLawyerMess);
        txtTitlemEnteghadVaPishnahad= (EditText)findViewById(R.id.txtTitlemEnteghadVaPishnahad);
        txtBodymEnteghadVaPishnahad= (EditText)findViewById(R.id.txtBodymEnteghadVaPishnahad);
        BtnSendMessage = (Button)findViewById(R.id.BtnSendMessage);
        BtnSendEnteghadVaPishnahad= (Button)findViewById(R.id.BtnSendEnteghadVaPishnahad);
        BtnSendLawyerMess= (Button)findViewById(R.id.BtnSendLawyerMess);
        
        BtnSendLawyerMess.setTypeface(FontMitra);
        txtBodymLawyerMess.setTypeface(FontMitra);
        btnPageTitle.setTypeface(FontMitra);
        messageSendToLable.setTypeface(FontMitra);
        txtTitlem.setTypeface(FontMitra);
        txtBodym.setTypeface(FontMitra);
        BtnSendMessage.setTypeface(FontMitra);
        BtnSendEnteghadVaPishnahad.setTypeface(FontMitra);
        txtBodymEnteghadVaPishnahad.setTypeface(FontMitra);
        txtTitlemEnteghadVaPishnahad.setTypeface(FontMitra);
        
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec=tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(PersianReshape.reshape("جدید"),getResources().getDrawable(R.drawable.tabnewmessage));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(PersianReshape.reshape("ارسالی"),getResources().getDrawable(R.drawable.tabsendbox));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator(PersianReshape.reshape("دریافتی"),getResources().getDrawable(R.drawable.tabinbox));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag4");
        spec.setContent(R.id.tab4);
        spec.setIndicator(PersianReshape.reshape("انتقاد و پیشنهاد"),getResources().getDrawable(R.drawable.tabhistory));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag5");
        spec.setContent(R.id.tab5);
        spec.setIndicator(PersianReshape.reshape("ارتباط با وکیل"),getResources().getDrawable(R.drawable.tabperonalinfo));
        tabs.addTab(spec);
        tabs.setCurrentTab(2);
        
        tabs.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#4cc131"));
        tabs.getTabWidget().getChildAt(2).setBackgroundColor(Color.RED);
        
        
        lst_dataInbox=(ListView)findViewById(R.id.listViewInBoxN);
        lst_dataSendbox=(ListView)findViewById(R.id.listViewSendBox);
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
   		
   		dbhLawyerMobile=new DatabaseHelper(getApplicationContext());
		try {

			dbhLawyerMobile.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhLawyerMobile.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}	
   		
   		
//   		dbhUpdateMessage=new DatabaseHelper(getApplicationContext());
//		try {
//
//			dbhUpdateMessage.createDataBase();
//
//   		} catch (IOException ioe) {
//
//   			throw new Error("Unable to create database");
//
//   		}
//
//   		try {
//
//   			dbhUpdateMessage.openDataBase();
//
//   		} catch (SQLException sqle) {
//
//   			throw sqle;
//
//   		}
   		
   		
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
        
   		FillDataInbox();
   		FillDataSendbox();
   		FillLawyerSpinner();
   		FillAllLawyerSpinner();
   		
   		//GetMessageUnRead();
   		
   		lstLawyers.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				SelectLawyer = Integer.valueOf(lstLawyers.getSelectedItem().toString().split("-")[0].toString());
			}
		});
   		
   		lstAllLawyers.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				SMSSelectLawyer = Integer.valueOf(lstAllLawyers.getSelectedItem().toString().split("-")[0].toString());
				FillLawyerMobile(String.valueOf(SMSSelectLawyer));
			}
		});
   		
    }
    
    
//    public void GetMessageUnRead()
//    {
//   		dbUpdateMessage = dbhUpdateMessage.getWritableDatabase();
//   		dbUpdateMessage.execSQL("delete from messages where type = '100'");
//    }
    
    public String StLawyerMobile = null;
    private void FillLawyerMobile(String Id)
   	{
    	dbLawyerMobile = dbhLawyerMobile.getReadableDatabase();
   		Cursor cursors = dbLawyerMobile.rawQuery("select * from lawyers where id="+Id, null);

   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			StLawyerMobile = cursors.getString(cursors.getColumnIndex("mobile"));
   		}
//   		else
//   		{
//   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("شماره موبایل موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
//   		}
   	}
    
    public String ShareHolderCode,ShareHolderName;
    private void FillPersonalData()
   	{
    	dbPersonalInfo = dbhPersonalInfo.getReadableDatabase();
   		Cursor cursors = dbPersonalInfo.rawQuery("select * from persons order by id desc", null);

   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			ShareHolderCode = cursors.getString(cursors.getColumnIndex("code"));
   			ShareHolderName = cursors.getString(cursors.getColumnIndex("fullName"));
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعات شخصی موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}
   	}
    
    
    
    public boolean CheckisNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
    
    public void clickHandler(View v){
		try {
			switch (v.getId()) {
			case R.id.BtnSendMessage:
				if(txtTitlem.getText().length() > 0)
				{
					if(txtBodym.getText().length() > 0)
					{
						try
						{
							SendMessageToWs("BtnSendMessage","2");
						}
						catch (Exception e) {
							Toast.makeText(getApplicationContext(), PersianReshape.reshape("خطا در ارسال پیام"), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا شرح پیام را وارد کنید"), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا عنوان پیام را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.BtnSendEnteghadVaPishnahad:
				if(txtTitlemEnteghadVaPishnahad.getText().length() > 0)
				{
					if(txtBodymEnteghadVaPishnahad.getText().length() > 0)
					{
						try
						{
							SendMessageToWs("BtnSendEnteghadVaPishnahad","3");
						}
						catch (Exception e) {
							Toast.makeText(getApplicationContext(), PersianReshape.reshape("خطا در ارسال پیام"), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا شرح انتقاد یا پیشنهاد را وارد کنید"), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا عنوان انتقاد یا پیشنهاد را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.BtnSendLawyerMess:
				if(txtBodymLawyerMess.getText().length() > 0)
				{
//					if(StLawyerMobile.compareTo("null")==0)
//					{
//						Toast.makeText(getApplicationContext(), PersianReshape.reshape("شماره موبایل وکیل موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_SHORT).show();
//					}
//					else
//					{
						try
						{
							SmsManager sms = SmsManager.getDefault();
							ArrayList<String> parts = sms.divideMessage("پیام از طرف "+ShareHolderName+" سهامدار شرکت الهیه خراسان با کد سهامداری "+ShareHolderCode+" به شرح : "+txtBodymLawyerMess.getText().toString());
							sms.sendMultipartTextMessage(StLawyerMobile, null, parts, null, null);
							txtBodymLawyerMess.setText("");
							Toast.makeText(getApplicationContext(), PersianReshape.reshape("SMS شما به وکیلتان با موفقیت ارسال شد"), Toast.LENGTH_SHORT).show();
						}
						catch (Exception e) {
							Toast.makeText(getApplicationContext(), PersianReshape.reshape("شماره موبایل وکیل موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_SHORT).show();
						}
					//}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا شرح SMS را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void SendMessageToWs(String BtnSender,String Type)
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSSendMessage task = new AsyncCallWSSendMessage(Messages.this,BtnSender,Type);
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
    
    String WsResault;
    private class AsyncCallWSSendMessage extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		private String CuBtnSender,CuMessType;
		
		public AsyncCallWSSendMessage(Activity activity,String BtnSender,String Type) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		    this.CuBtnSender = BtnSender;
		    this.CuMessType = Type;
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	if(CuBtnSender.compareTo("BtnSendMessage")==0)
        	{
        		CallWsMethodLogin("SendMessage",txtTitlem.getText().toString(),txtBodym.getText().toString(),CuMessType);
        	}
        	else
        	{
        		CallWsMethodLogin("SendMessage",txtTitlemEnteghadVaPishnahad.getText().toString(),txtBodymEnteghadVaPishnahad.getText().toString(),CuMessType);
        	}
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(WsResault.compareTo("1") == 0)
            {
            	
            	if(CuBtnSender.compareTo("BtnSendMessage")==0)
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("پیام شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("انتقاد یا پیشنهاد شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
				}
            	
            	//update Message OutBox
				SyncMessageSendbox SMO = new SyncMessageSendbox(Messages.this, PersonGuid,false,true);
				SMO.AsyncExecute();
				//update Message OutBox
				
            }
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
 
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(PersianReshape.reshape("در حال ارسال اطلاعات"));
            this.dialog.show();
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
    
    public void CallWsMethodLogin(String METHOD_NAME,String STitle,String SBody,String MessType) {
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
	    PassPI.setName("receiverCode");
	    //Set Value
	    try
	    {
	    	PassPI.setValue(Integer.valueOf(SelectLawyer));
	    	//PassPI.setValue(10005003);
	    }
	    catch (Exception e) {
			PassPI.setValue(0);
		}
	    //Set dataType
	    PassPI.setType(Integer.class);
	    //Add the property to request object
	    request.addProperty(PassPI);
	    
	    PropertyInfo TitlePI = new PropertyInfo();
	    //Set Name
	    TitlePI.setName("Title");
	    //Set Value
	    TitlePI.setValue(STitle);
	    //Set dataType
	    TitlePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(TitlePI);
	    
	    PropertyInfo BodyPI = new PropertyInfo();
	    //Set Name
	    BodyPI.setName("Body");
	    //Set Value
	    BodyPI.setValue(SBody);
	    //Set dataType
	    BodyPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(BodyPI);
	    
	    PropertyInfo TypePI = new PropertyInfo();
	    //Set Name
	    TypePI.setName("Type");
	    //Set Value
	    TypePI.setValue(MessType);
	    //Set dataType
	    TypePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(TypePI);
	    
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
	        WsResault = response.toString();	
	        if(WsResault.length() > 1)
	        {
	        	runOnUiThread(new Runnable() 
				{
				   public void run() 
				   {
					   Toast.makeText(getApplicationContext(), "خطا در ثبت درخواست", Toast.LENGTH_SHORT).show();
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
    
    
    
    private void FillLawyerSpinner()
   	{
    	db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select id,fullName from lawyers where utype='1' order by id", null);

   		if(cursors.getCount() > 0)
   		{
   			String id,title;
   			
   			ArrayList< String > DataLList;
   			DataLList = new ArrayList< String >();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				id = cursors.getString(cursors.getColumnIndex("id"));
   				title = cursors.getString(cursors.getColumnIndex("fullName"));
   				DataLList.add(id+"-"+title);
   				if(i==0)
   				{
   					SelectLawyer = Integer.valueOf(id);
   				}
   			}
   			lstLawyers.setAdapter(new ArrayAdapter<String>(this,R.layout.spinnerlayout,DataLList));
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("گیرنده پیامی موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}
   	}
    
    private void FillAllLawyerSpinner()
   	{
    	db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select id,fullName from lawyers where utype='0' order by id desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String id,title;
   			
   			ArrayList< String > DataLList;
   			DataLList = new ArrayList< String >();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				id = cursors.getString(cursors.getColumnIndex("id"));
   				title = cursors.getString(cursors.getColumnIndex("fullName"));
   				DataLList.add(id+"-"+title);
   				if(i==0)
   				{
   					SMSSelectLawyer = Integer.valueOf(id);
   				}
   			}
   			FillLawyerMobile(String.valueOf(SMSSelectLawyer));
   			lstAllLawyers.setAdapter(new ArrayAdapter<String>(this,R.layout.spinnerlayout,DataLList));
   		}
   		else
   		{
   		//	Toast.makeText(getApplicationContext(), PersianReshape.reshape("وکیلی موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}
   	}
    
    private void FillDataSendbox()
   	{

   		db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select id,senderId,receiverId,SenderName,sDate,type,title,body,openned,sent from messages where type=2 order by id desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String id,receiverId,sDate,title;
   			
   			ArrayList<HashMap<String, String>> DataLList;
   			DataLList = new ArrayList<HashMap<String, String>>();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				HashMap<String, String> map = new HashMap<String, String>();
   				
   				id = cursors.getString(cursors.getColumnIndex("id"));
   				receiverId = cursors.getString(cursors.getColumnIndex("SenderName"));
   				sDate = cursors.getString(cursors.getColumnIndex("sDate"));
   				title = cursors.getString(cursors.getColumnIndex("title"));
   				map.put("id", id);
   				map.put("receiverId", receiverId);
   				map.put("sDate", sDate);
   				map.put("title", title);
   				map.put("PGuid", PersonGuid);
   				DataLList.add(map);
   	
   			}
   			adapter = new MessageSendboxAdapter(this, DataLList);
   			lst_dataSendbox.setAdapter(adapter);
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("پیام ارسالی موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}

   	}
    
    
    private void FillDataInbox()
   	{
   		
   		db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select id,senderId,receiverId,SenderName,sDate,type,title,body,openned,sent from messages where type=1 order by id desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String id,SenderName,sDate,title,openned;
   			
   			ArrayList<HashMap<String, String>> DataLList;
   			DataLList = new ArrayList<HashMap<String, String>>();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				HashMap<String, String> map = new HashMap<String, String>();
   				
   				id = cursors.getString(cursors.getColumnIndex("id"));
   				SenderName = cursors.getString(cursors.getColumnIndex("SenderName"));
   				sDate = cursors.getString(cursors.getColumnIndex("sDate"));
   				title = cursors.getString(cursors.getColumnIndex("title"));
   				openned = cursors.getString(cursors.getColumnIndex("openned"));
   				map.put("id", id);
   				map.put("SenderName", SenderName);
   				map.put("sDate", sDate);
   				map.put("title", title);
   				map.put("openned", openned);
   				map.put("PGuid", PersonGuid);
   				DataLList.add(map);
   	
   			}
   			adapter = new MessageInboxAdapter(this, DataLList);
   			lst_dataInbox.setAdapter(adapter);
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("پیام دریافتی موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
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
		 Builder alertbox = new AlertDialog.Builder(Messages.this);
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
	    	LoadActivity(AppMenu.class, "PGuid", PersonGuid);
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
