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

import android.R.color;
import android.R.string;
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
import android.provider.CalendarContract.Colors;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class SharesSalesAndBuy extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,txtwarning_buy_sell,txtBuytoday_price,txtBuyPrice,textRegisterBuyRequestTitle,textRegisterSellRequestTitle,
	txtwarning_Sale_sell,txtSaletoday_price,txtSalePrice;
	EditText txtBuyCount,txtSaleCount;
	Button BtnSendSalerequests,BtnSendBuyrequests;
	
	ListView lst_data;
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ListAdapter adapter;
	
	
	DatabaseHelper dbhShareText;
	SQLiteDatabase dbShareText;
	
	DatabaseHelper dbhPersonalInfo;
	SQLiteDatabase dbPersonalInfo;
	
	String WsResault,PersonGuid; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharessaleandbuy);
        
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
        
        btnPageTitle = (TextView)findViewById(R.id.sharessaleandbuypagetitle);
        txtwarning_buy_sell = (TextView)findViewById(R.id.txtwarning_buy_sell);
        txtBuytoday_price = (TextView)findViewById(R.id.txtBuytoday_price);
        txtBuyPrice = (TextView)findViewById(R.id.txtBuyPrice);
        txtwarning_Sale_sell = (TextView)findViewById(R.id.txtwarning_Sale_sell);
        txtSaletoday_price = (TextView)findViewById(R.id.txtSaletoday_price);
        txtSalePrice = (TextView)findViewById(R.id.txtSalePrice);
        textRegisterBuyRequestTitle= (TextView)findViewById(R.id.textRegisterBuyRequestTitle);
        textRegisterSellRequestTitle= (TextView)findViewById(R.id.textRegisterSellRequestTitle);
        txtBuyCount = (EditText)findViewById(R.id.txtBuyCount);
        txtSaleCount = (EditText)findViewById(R.id.txtSaleCount);
        BtnSendSalerequests = (Button)findViewById(R.id.BtnSendSalerequests);
        BtnSendBuyrequests = (Button)findViewById(R.id.BtnSendBuyrequests);
        
        textRegisterBuyRequestTitle.setTypeface(FontMitra);
        textRegisterSellRequestTitle.setTypeface(FontMitra);
        
        BtnSendSalerequests.setTypeface(FontMitra);
        BtnSendBuyrequests.setTypeface(FontMitra);
        txtSaletoday_price.setTypeface(FontMitra);
        txtwarning_Sale_sell.setTypeface(FontMitra);
        txtSalePrice.setTypeface(FontMitra);
        txtSaleCount.setTypeface(FontMitra);
        txtBuyPrice.setTypeface(FontMitra);
        btnPageTitle.setTypeface(FontMitra);
        txtwarning_buy_sell.setTypeface(FontMitra);
        txtBuytoday_price.setTypeface(FontMitra);
        txtBuyCount.setTypeface(FontMitra);
        
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec=tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(PersianReshape.reshape("سوابق"),getResources().getDrawable(R.drawable.tabhistory));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(PersianReshape.reshape("خرید"),getResources().getDrawable(R.drawable.tabbuy));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator(PersianReshape.reshape("فروش"),getResources().getDrawable(R.drawable.tabsale));
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
        
        tabs.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#4cc131"));
        tabs.getTabWidget().getChildAt(2).setBackgroundColor(Color.RED);
        
        
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
   		
   		dbhShareText=new DatabaseHelper(getApplicationContext());
		try {

			dbhShareText.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhShareText.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
   		
   		GetLastPrice();
   		FillData();
   		FillShareTextData();
    }
    
    public void FillShareTextData()
   	{
   		dbShareText = dbhShareText.getReadableDatabase();
   		Cursor cursors = dbShareText.rawQuery("select name,value from settings where name='ShareTextBuy' or name='ShareTextSale'", null);

   		if(cursors.getCount() > 0)
   		{
   			String name,value;
   			for(int i = 0 ; i < cursors.getCount() ; i++)
   			{
				cursors.moveToNext();
				name = cursors.getString(cursors.getColumnIndex("name"));
				value = cursors.getString(cursors.getColumnIndex("value"));
				if(name.compareTo("ShareTextBuy")==0)
				{
					txtwarning_buy_sell.setText(value);
				}
				else if(name.compareTo("ShareTextSale")==0)
				{
					txtwarning_Sale_sell.setText(value);
				}
   			}
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
    
    public String GetSharesCount()
    {
    	String Res="0";
    	dbPersonalInfo = dbhPersonalInfo.getReadableDatabase();
   		Cursor cursors = dbPersonalInfo.rawQuery("select * from persons order by id desc", null);
   		String sharecount;
   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			sharecount = cursors.getString(cursors.getColumnIndex("sharecount"));
   		    Res = sharecount;
   		}
   		return Res;
    }
    
    public void clickHandler(View v){
		try {
			switch (v.getId()) {
			case R.id.BtnSendSalerequests:
				if(txtSaleCount.getText().length() > 0)
				{
					if(CheckisNumeric(txtSaleCount.getText().toString())==true)
					{
						if(Integer.valueOf(GetSharesCount().toString()) >= Integer.valueOf(txtSaleCount.getText().toString()))
						{
							SendSaleRequestToWs();
						}
						else
						{
							Toast.makeText(getApplicationContext(), "تعداد سهام مورد نظر جهت فروش از تعداد سهام فعلی شما بیشتر است", Toast.LENGTH_LONG).show();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا مقدار عددی وارد کنید"), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا تعداد مورد نظر را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.BtnSendBuyrequests:
				if(txtBuyCount.getText().length() > 0)
				{
					if(CheckisNumeric(txtBuyCount.getText().toString())==true)
					{
						SendBuyRequestToWs();
					}
					else
					{
						Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا مقدار عددی وارد کنید"), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا تعداد مورد نظر را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			
		}
	}
    
    public void SendBuyRequestToWs()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSSendBuyRequest task = new AsyncCallWSSendBuyRequest(SharesSalesAndBuy.this);
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
    
    private class AsyncCallWSSendBuyRequest extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWSSendBuyRequest(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	CallWsMethodLogin("BuyRequest","buy");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(Integer.valueOf(WsResault) > 0)
            {
            	Toast.makeText(getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
            	//update Buy And Sell Requests
				SyncBuyAndSellRequests SBASR = new SyncBuyAndSellRequests(SharesSalesAndBuy.this, PersonGuid,true,false);
				SBASR.AsyncExecute();
				//update Buy And Sell Requests
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
    
    
    public void SendSaleRequestToWs()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSSendSaleRequest task = new AsyncCallWSSendSaleRequest(SharesSalesAndBuy.this);
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
    
    
	private class AsyncCallWSSendSaleRequest extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWSSendSaleRequest(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	CallWsMethodLogin("SellRequest","sale");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(Integer.valueOf(WsResault) > 0)
            {
            	Toast.makeText(getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
            	//update Buy And Sell Requests
				SyncBuyAndSellRequests SBASR = new SyncBuyAndSellRequests(SharesSalesAndBuy.this, PersonGuid,true,false);
				SBASR.AsyncExecute();
				//update Buy And Sell Requests
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
    
	public void CallWsMethodLogin(String METHOD_NAME,String reqType) {
		PublicVariable PV = new PublicVariable();
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("pGuid");
	    //Set Value
	    UserPI.setValue(getIntent().getStringExtra("PGuid").toString());
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
	    PropertyInfo PassPI = new PropertyInfo();
	    //Set Name
	    PassPI.setName("ShareCount");
	    //Set Value
	    if(reqType.compareTo("sale")==0)
	    	PassPI.setValue(Integer.valueOf(txtSaleCount.getText().toString()));
	    else
	    	PassPI.setValue(Integer.valueOf(txtBuyCount.getText().toString()));
	    //Set dataType
	    PassPI.setType(Integer.class);
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
	        WsResault = response.toString();	
	        if(Integer.valueOf(WsResault) < 1)
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
	
    public void FillData()
   	{
   		
   		db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select id,type,type as nType,sDate,status as nStatus,count from shares order by sDate desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String nType,sDate,nStatus,count,id,type;
   			
   			ArrayList<HashMap<String, String>> DataLList;
   			DataLList = new ArrayList<HashMap<String, String>>();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				HashMap<String, String> map = new HashMap<String, String>();
   				
   				id = cursors.getString(cursors.getColumnIndex("id"));
   				nType = cursors.getString(cursors.getColumnIndex("nType"));
   				type = cursors.getString(cursors.getColumnIndex("type"));
   				sDate = cursors.getString(cursors.getColumnIndex("sDate"));
   				nStatus = cursors.getString(cursors.getColumnIndex("nStatus"));
   				count = cursors.getString(cursors.getColumnIndex("count"));
   				map.put("PGuid", getIntent().getStringExtra("PGuid").toString());
   				map.put("id", id);
   				map.put("nType", nType);
   				map.put("type", type);
   				map.put("sDate", sDate);
   				map.put("nStatus", nStatus);
   				map.put("count", count);
   				DataLList.add(map);
   			}
   			adapter = new ShareSalesAndBuyAdapter(this, DataLList);
   			lst_data.setAdapter(adapter);
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعات سوابق موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}

   	}
    
    public void GetLastPrice()
    {
    	db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from sharePrices order by date desc limit 1", null);
		if(cursors.getCount() > 0)
		{
			String date,price;
			cursors.moveToNext();
			date = cursors.getString(cursors.getColumnIndex("date"));
			price = cursors.getString(cursors.getColumnIndex("price"));
			txtSalePrice.setText(PersianReshape.reshape("تاریخ ")+date+" مبلغ "+price);
			txtBuyPrice.setText(PersianReshape.reshape("تاریخ ")+date+" مبلغ "+price);
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعات آخرین قیمت سهام موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید"), Toast.LENGTH_LONG).show();
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
		 Builder alertbox = new AlertDialog.Builder(SharesSalesAndBuy.this);
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
