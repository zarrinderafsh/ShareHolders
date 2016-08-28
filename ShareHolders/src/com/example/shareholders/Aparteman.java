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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Aparteman extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,gText;	
	ListView lst_data;
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ListAdapter adapter;
	
	ListView lst_dataRequest;
	DatabaseHelper dbhRequest;
	SQLiteDatabase dbRequest;
	ListAdapter adapterRequest;
	
	String PersonGuid,WsResault;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aparteman);
        
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
        gText= (TextView)findViewById(R.id.gText);
        
        btnPageTitle.setTypeface(FontMitra);
        gText.setTypeface(FontMitra);
        
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec=tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(PersianReshape.reshape("آپارتمان ها"),getResources().getDrawable(R.drawable.tabhistory));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(PersianReshape.reshape("درخواست خرید"),getResources().getDrawable(R.drawable.tabbuy));
        tabs.addTab(spec);


        try{
        	String RequestTab = getIntent().getStringExtra("RequestTab").toString();
        	if(RequestTab.compareTo("Request")==0)
        	{
        		tabs.setCurrentTab(1);
        	}
        	else{tabs.setCurrentTab(0);}
        }
        catch (Exception e) {
			// TODO: handle exception
        	tabs.setCurrentTab(0);
		}
        
      //  tabs.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#4cc131"));
//        tabs.getTabWidget().getChildAt(1).setBackgroundColor(Color.RED);
        
        
        lst_data=(ListView)findViewById(R.id.apartemanlistView1);
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
   		
   		lst_dataRequest=(ListView)findViewById(R.id.apartemanRequestlistView1);
        dbhRequest=new DatabaseHelper(getApplicationContext());
		try {

			dbhRequest.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhRequest.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
   		
   		FillData();
   		FillDataRequest();
        
	}
	
	public void FillDataRequest()
   	{
   		
   		dbRequest = dbhRequest.getReadableDatabase();
   		Cursor cursors = dbRequest.rawQuery("select Code,RequestType,ObjectCode,GroundAparteman,Status,InsertDate from AptGroRequest where RequestType = 1 order by Code desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String Code,RequestType,ObjectCode,GroundAparteman,Status,InsertDate;
   			
   			ArrayList<HashMap<String, String>> DataLList;
   			DataLList = new ArrayList<HashMap<String, String>>();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				HashMap<String, String> map = new HashMap<String, String>();
   				
   				Code = cursors.getString(cursors.getColumnIndex("Code"));
   				RequestType = cursors.getString(cursors.getColumnIndex("RequestType"));
   				ObjectCode = cursors.getString(cursors.getColumnIndex("ObjectCode"));
   				GroundAparteman = cursors.getString(cursors.getColumnIndex("GroundAparteman"));
   				Status = cursors.getString(cursors.getColumnIndex("Status"));
   				InsertDate = cursors.getString(cursors.getColumnIndex("InsertDate"));
   				map.put("PGuid", getIntent().getStringExtra("PGuid").toString());
   				map.put("Code", Code);
   				map.put("RequestType", RequestType);
   				map.put("ObjectCode", ObjectCode);
   				map.put("GroundAparteman", GroundAparteman);
   				map.put("Status", Status);
   				map.put("InsertDate", InsertDate);
   				DataLList.add(map);
   			}
   			adapterRequest = new AprGroRequestAdapter(this, DataLList);
   			lst_dataRequest.setAdapter(adapterRequest);
   		}
   		else
   		{
   			//Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعات سوابق موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}

   	}
	
	
	public void FillData()
   	{
   		
   		db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select Code,ProjectName,Metraj,TedadeVahed,ShomareVahed,Tabaghe,Emkanat,Gheymat from aparteman order by Code desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String Code,ProjectName,Metraj,TedadeVahed,ShomareVahed,Tabaghe,Emkanat,Gheymat;
   			
   			ArrayList<HashMap<String, String>> DataLList;
   			DataLList = new ArrayList<HashMap<String, String>>();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				HashMap<String, String> map = new HashMap<String, String>();
   				
   				Code = cursors.getString(cursors.getColumnIndex("Code"));
   				ProjectName = cursors.getString(cursors.getColumnIndex("ProjectName"));
   				Metraj = cursors.getString(cursors.getColumnIndex("Metraj"));
   				TedadeVahed = cursors.getString(cursors.getColumnIndex("TedadeVahed"));
   				ShomareVahed = cursors.getString(cursors.getColumnIndex("ShomareVahed"));
   				Tabaghe = cursors.getString(cursors.getColumnIndex("Tabaghe"));
   				Emkanat = cursors.getString(cursors.getColumnIndex("Emkanat"));
   				Gheymat = cursors.getString(cursors.getColumnIndex("Gheymat"));
   				map.put("PGuid", getIntent().getStringExtra("PGuid").toString());
   				map.put("Code", Code);
   				map.put("ProjectName", ProjectName);
   				map.put("Metraj", Metraj);
   				map.put("TedadeVahed", TedadeVahed);
   				map.put("ShomareVahed", ShomareVahed);
   				map.put("Tabaghe", Tabaghe);
   				map.put("Emkanat", Emkanat);
   				map.put("Gheymat", Gheymat);
   				DataLList.add(map);
   			}
   			adapter = new ApartemanAdapter(this, DataLList);
   			lst_data.setAdapter(adapter);
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعات آپارتمان ها موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
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
		 Builder alertbox = new AlertDialog.Builder(Aparteman.this);
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
