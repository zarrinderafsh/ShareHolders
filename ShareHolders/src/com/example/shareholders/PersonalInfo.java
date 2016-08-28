package com.example.shareholders;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.integer;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class PersonalInfo extends Activity {

	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,postal_code,email,home_tel,work_tel,home_address,work_address,mobile,
	share_code,full_name,father,birth_date,shsh,city,national_code,txtvsharecount,cityhome;
	Button BtnSave,BtnTakeAPersonalPhoto,BtnSelectPhotoFromGallery,personelPicSave;
	ImageView imgPerson;
	EditText lstCities;
	EditText txtPostalCode,textEmailAddress,txtHomeTel,txtWorkTel,txtHomeAddress,txtWorkAddress,
	txtMobile,txtCode,txtFullName,txtFather,txtBirthdate,txtShSh,txtNationalCode,edtextsharecount;
	
	Spinner lstCitys;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	DatabaseHelper dbhPersonalInfo;
	SQLiteDatabase dbPersonalInfo;
	
	DatabaseHelper dbhPersonImage;
	SQLiteDatabase dbPersonImage;
	
	DatabaseHelper dbhCityHome;
	SQLiteDatabase dbCityHome;
	
	DatabaseHelper dbhCityHomeGetId;
	SQLiteDatabase dbCityHomeGetId;
	
	String PersonGuid;
	int SelectCity = 0;
	
	String WsResault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalinfo);
        
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
        
        imgPerson = (ImageView)findViewById(R.id.imgPerson);
        
        lstCitys = (Spinner)findViewById(R.id.lstCitys);
        
        btnPageTitle = (TextView)findViewById(R.id.personalinfopagetitle);
        postal_code = (TextView)findViewById(R.id.postal_code);
        email = (TextView)findViewById(R.id.email);
        home_tel = (TextView)findViewById(R.id.home_tel);
        work_tel = (TextView)findViewById(R.id.work_tel);
        home_address = (TextView)findViewById(R.id.home_address);
        work_address = (TextView)findViewById(R.id.work_address);
        mobile = (TextView)findViewById(R.id.mobile);
        share_code = (TextView)findViewById(R.id.share_code);
        full_name = (TextView)findViewById(R.id.full_name);
        father = (TextView)findViewById(R.id.father);
        birth_date = (TextView)findViewById(R.id.birth_date);
        shsh = (TextView)findViewById(R.id.shsh);
        city = (TextView)findViewById(R.id.city);
        national_code = (TextView)findViewById(R.id.national_code);
        cityhome = (TextView)findViewById(R.id.cityhome);
        
        txtvsharecount = (TextView)findViewById(R.id.txtvsharecount);
        
        BtnSave = (Button)findViewById(R.id.personelinfosave);
        BtnTakeAPersonalPhoto = (Button)findViewById(R.id.BtnTakeAPersonalPhoto);
        BtnSelectPhotoFromGallery = (Button)findViewById(R.id.BtnSelectPhotoFromGallery);
        personelPicSave= (Button)findViewById(R.id.personelPicSave);
        
        txtPostalCode = (EditText)findViewById(R.id.txtPostalCode);
        textEmailAddress = (EditText)findViewById(R.id.txtEmail);
        txtHomeTel = (EditText)findViewById(R.id.txtHomeTel);
        txtWorkTel = (EditText)findViewById(R.id.txtWorkTel);
        txtHomeAddress = (EditText)findViewById(R.id.txtHomeAddress);
        txtWorkAddress = (EditText)findViewById(R.id.txtWorkAddress);
        txtMobile = (EditText)findViewById(R.id.txtMobile);
        txtCode = (EditText)findViewById(R.id.txtCode);
        txtFullName = (EditText)findViewById(R.id.txtFullNamePersonalInfo);
        txtFather = (EditText)findViewById(R.id.txtFather);
        txtBirthdate = (EditText)findViewById(R.id.txtBirthdate);
        txtShSh = (EditText)findViewById(R.id.txtShSh);
        txtNationalCode = (EditText)findViewById(R.id.txtNationalCode);
        
        lstCities = (EditText)findViewById(R.id.lstCities);
        
        edtextsharecount= (EditText)findViewById(R.id.edtextsharecount);
        
        edtextsharecount.setTypeface(FontMitra);
        cityhome.setTypeface(FontMitra);
        txtvsharecount.setTypeface(FontMitra);
        personelPicSave.setTypeface(FontMitra);
        BtnSelectPhotoFromGallery.setTypeface(FontMitra);
        lstCities.setTypeface(FontMitra);
        city.setTypeface(FontMitra);
        shsh.setTypeface(FontMitra);
        national_code.setTypeface(FontMitra);
        birth_date.setTypeface(FontMitra);
        father.setTypeface(FontMitra);
        full_name.setTypeface(FontMitra);
        share_code.setTypeface(FontMitra);
        mobile.setTypeface(FontMitra);
        work_address.setTypeface(FontMitra);
        home_address.setTypeface(FontMitra);
        work_tel.setTypeface(FontMitra);
        home_tel.setTypeface(FontMitra);
        email.setTypeface(FontMitra);
        postal_code.setTypeface(FontMitra);
        btnPageTitle.setTypeface(FontMitra);
        BtnTakeAPersonalPhoto.setTypeface(FontMitra);
        txtPostalCode.setTypeface(FontMitra);
        textEmailAddress.setTypeface(FontMitra);
        txtHomeTel.setTypeface(FontMitra);
        txtWorkTel.setTypeface(FontMitra);
        txtHomeAddress.setTypeface(FontMitra);
        txtWorkAddress.setTypeface(FontMitra);
    	txtMobile.setTypeface(FontMitra);
    	txtCode.setTypeface(FontMitra);
    	txtFullName.setTypeface(FontMitra);
    	txtFather.setTypeface(FontMitra);
    	txtBirthdate.setTypeface(FontMitra);
    	txtShSh.setTypeface(FontMitra);
    	txtNationalCode.setTypeface(FontMitra);
    	BtnSave.setTypeface(FontMitra);
        
        final TabHost tabs = (TabHost)findViewById(R.id.tabhost);
        tabs.setup();
        TabHost.TabSpec spec=tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(PersianReshape.reshape("اطلاعات شخصی"),getResources().getDrawable(R.drawable.tabperonalinfo));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(PersianReshape.reshape("اطلاعات پستی"),getResources().getDrawable(R.drawable.tabnewmessage));
        tabs.addTab(spec);
        spec=tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator(PersianReshape.reshape("عکس"),getResources().getDrawable(R.drawable.tabpic));
        tabs.addTab(spec);
        tabs.setCurrentTab(0);
        
        dbhCityHomeGetId=new DatabaseHelper(getApplicationContext());
		try {

			dbhCityHomeGetId.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhCityHomeGetId.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
        
        dbhCityHome=new DatabaseHelper(getApplicationContext());
		try {

			dbhCityHome.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhCityHome.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

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
   		
   		FillPersonalData();
        
        
   		
   		//FillCitySpinner(CuCityId);

   		
   		
   		dbhPersonImage=new DatabaseHelper(getApplicationContext());
		try {

			dbhPersonImage.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbhPersonImage.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
   		
   		FillImgPerosnFromDb();
   		
   		lstCitys.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
//			String Id = 	arg0.getTag().toString();
				//int Id = lstCitys.getSelectedItemPosition();
				SelectCityHomeAndWork = GetCityIdFromSort(Integer.valueOf(lstCitys.getSelectedItem().toString().split("-")[0].toString()));
			}
		});
   		
   		BtnSave.setOnClickListener(BtnOnClick);
   		personelPicSave.setOnClickListener(BtnPicOnClick);
   		
   		
    }
    
    private int GetCityIdFromSort(int Sort)
    {
    	int Res=0;
    	dbCityHomeGetId = dbhCityHomeGetId.getReadableDatabase();
   		Cursor cursors = dbCityHomeGetId.rawQuery("select id from cities where show = 1 and Sort = "+Sort+"  limit 1", null);
   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			Res = Integer.valueOf(cursors.getString(cursors.getColumnIndex("id")));
   		}
    	return Res;
    }
    int SelectCityHomeAndWork;
    private void FillCityWorkAndHome(String CityId)
   	{
    	int SelectedPosition=0;
    	dbCityHome = dbhCityHome.getReadableDatabase();
   		Cursor cursors = dbCityHome.rawQuery("select id,name,sort from cities where show = 1 order by sort,name", null);

   		String Name = "نامشخص";
   		if(cursors.getCount() > 0)
   		{
   			String id,title,sort;
   			
   			ArrayList< String > DataLList;
   			DataLList = new ArrayList< String >();
   			for (int i = 0; i < cursors.getCount(); i++) {
   				cursors.moveToNext();
   				id = cursors.getString(cursors.getColumnIndex("id"));
   				sort = cursors.getString(cursors.getColumnIndex("sort"));
   				title = cursors.getString(cursors.getColumnIndex("name"));
   				DataLList.add(sort+"-"+title);
   				if(i==0)
   				{
   					SelectCityHomeAndWork = Integer.valueOf(id);
   				}
   				if(id.compareTo(CityId)==0)
   				{
   					
   					SelectedPosition = i;
   				}
   			}
   			lstCitys.setAdapter(new ArrayAdapter<String>(this,R.layout.spinnerlayout,DataLList));
   			//lstCitys.setSelection(Integer.valueOf(CityId)-1);
   			lstCitys.setSelection(SelectedPosition);
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("گیرنده پیامی موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}
   	}
    
    private void FillImgPerosnFromDb()
	{
		dbPersonImage = dbhPersonImage.getReadableDatabase();
		Cursor cursors = dbPersonImage.rawQuery("select * from personImage order by id desc", null);

		if(cursors.getCount() > 0)
		{
			try
			{
				cursors.moveToNext();
				
				byte[] decodedByte = Base64.decode(cursors.getString(cursors.getColumnIndex("personimage")), Base64.DEFAULT);
				Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
				
				ImageView img = (ImageView)findViewById(R.id.imgPerson);
				img.setImageBitmap(Bmp);
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("عکس پرسنلی موجود نمی باشد"), Toast.LENGTH_LONG).show();
		}

	}
    
    private OnClickListener BtnPicOnClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			SendPersonalPicToWs();
		}
	};
	
	public void SendPersonalPicToWs()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSPersonalPic task = new AsyncCallWSPersonalPic(PersonalInfo.this);
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
        	CallWsMethodSendPersonalPic("SetPersonImage");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(WsResault.compareTo("1")==0)
            {
            	Toast.makeText(getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
				//update Personal Image
				SyncPersonalPic SPPI = new SyncPersonalPic(PersonalInfo.this, PersonGuid,false,false);
				SPPI.AsyncExecute();
				//update Personal Image
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
    
    private OnClickListener BtnOnClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			SendEditPersonalInfoToWs();
		}
	};
	
	public void SendEditPersonalInfoToWs()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSEditPersonalInfo task = new AsyncCallWSEditPersonalInfo(PersonalInfo.this);
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
	
	private class AsyncCallWSEditPersonalInfo extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWSEditPersonalInfo(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	CallWsMethodLogin("UpdatePersonInfo");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(Integer.valueOf(WsResault) > 0)
            {
            	Toast.makeText(getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
            	
            	//update Personal Info
				SyncPerosnalInfo SPI = new SyncPerosnalInfo(PersonalInfo.this, PersonGuid,false,false);
				SPI.AsyncExecute();
				//update Personal Info
				
				//update Personal Image
				//SyncPersonalPic SPPI = new SyncPersonalPic(PersonalInfo.this, PersonGuid);
				//SPPI.AsyncExecute();
				//update Personal Image
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
	
	public void CallWsMethodLogin(String METHOD_NAME) {
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
	    PassPI.setName("City");
	    //Set Value
	    PassPI.setValue(String.valueOf(SelectCityHomeAndWork));
	    //Set dataType
	    PassPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PassPI);
	    PropertyInfo PostalCodePI = new PropertyInfo();
	    //Set Name
	    PostalCodePI.setName("PostalCode");
	    //Set Value
	    PostalCodePI.setValue(txtPostalCode.getText().toString());
	    //Set dataType
	    PostalCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PostalCodePI);
	    PropertyInfo EmailPI = new PropertyInfo();
	    //Set Name
	    EmailPI.setName("Email");
	    //Set Value
	    EmailPI.setValue(textEmailAddress.getText().toString());
	    //Set dataType
	    EmailPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(EmailPI);
	    PropertyInfo HomeTelPI = new PropertyInfo();
	    //Set Name
	    HomeTelPI.setName("HomeTel");
	    //Set Value
	    HomeTelPI.setValue(txtHomeTel.getText().toString());
	    //Set dataType
	    HomeTelPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(HomeTelPI);
	    PropertyInfo WorkTelPI = new PropertyInfo();
	    //Set Name
	    WorkTelPI.setName("WorkTel");
	    //Set Value
	    WorkTelPI.setValue(txtWorkTel.getText().toString());
	    //Set dataType
	    WorkTelPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(WorkTelPI);
	    PropertyInfo MobilePI = new PropertyInfo();
	    //Set Name
	    MobilePI.setName("Mobile");
	    //Set Value
	    MobilePI.setValue(txtMobile.getText().toString());
	    //Set dataType
	    MobilePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(MobilePI);
	    
	    
//	    String[]HomeAddArr = txtHomeAddress.getText().toString().split("-");
//	    String FHomeAdd = "";
//	    if(HomeAddArr.length == 1)
//	    {
//	    	FHomeAdd = HomeAddArr[0].toString();
//	    }
//	    else
//	    {
//		    for(int i = 1 ; i < HomeAddArr.length ; i++)
//		    {
//		    	FHomeAdd += HomeAddArr[i].toString() + " - ";
//		    }
//		    FHomeAdd = FHomeAdd.substring(0, FHomeAdd.length() - 3);
//	    }
	    PropertyInfo HomeAddressPI = new PropertyInfo();
	    //Set Name
	    HomeAddressPI.setName("HomeAddress");
	    //Set Value
	    HomeAddressPI.setValue(txtHomeAddress.getText().toString());
	    //Set dataType
	    HomeAddressPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(HomeAddressPI);
	    
	    String[]WorkAddArr = txtWorkAddress.getText().toString().split("-");
	    String FWorkAdd = "";
	    if(WorkAddArr.length == 1)
	    {
	    	FWorkAdd = WorkAddArr[0].toString();
	    }
	    else
	    {
		    for(int i = 1 ; i < WorkAddArr.length ; i++)
		    {
		    	FWorkAdd += WorkAddArr[i].toString() + " - ";
		    }
		    FWorkAdd = FWorkAdd.substring(0, FWorkAdd.length() - 3);
	    }
	    PropertyInfo WorkAddressPI = new PropertyInfo();
	    //Set Name
	    WorkAddressPI.setName("WorkAddress");
	    //Set Value
	    WorkAddressPI.setValue(FWorkAdd);
	    //Set dataType
	    WorkAddressPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(WorkAddressPI);
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
	    
	    PublicFunction PF = new PublicFunction();
	    BitmapDrawable drawable = (BitmapDrawable) imgPerson.getDrawable();
	    
	    PropertyInfo PassPI = new PropertyInfo();
	    //Set Name
	    PassPI.setName("strImage");
	    //Set Value
	    PassPI.setValue(PF.ConvertImageViewToBase64String(drawable));
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
	
	
//	public String ConvertImageViewToBase64String()
//	{
//		byte[] image;
//		String StrImageProfile="";
//		try
//		{
//			BitmapDrawable drawable = (BitmapDrawable) imgPerson.getDrawable();
//			Bitmap bitmap = drawable.getBitmap();
//		    ByteArrayOutputStream stream=new ByteArrayOutputStream();
//		    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//		    image = stream.toByteArray();
//		    StrImageProfile = Base64.encode(image);
//		}
//		catch(Exception ex){}
//	    return StrImageProfile;
//	}
	
    
    public void clickHandler(View v){
		try {
			switch (v.getId()) {
			case R.id.BtnSelectPhotoFromGallery:
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				break;
			case R.id.BtnTakeAPersonalPhoto:
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
                startActivityForResult(cameraIntent, CAMERA_REQUEST); 
				break;
			default:
				break;
			}
		} catch (Exception e) {
			
		}
	}
    
    private static final int CAMERA_REQUEST = 1888; 
    private static final int SELECT_PHOTO = 100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
            if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK){  
                Uri selectedImage = imageReturnedIntent.getData();
                InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                imgPerson.setImageBitmap(yourSelectedImage);
            }
            else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
        		Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data"); 
                imgPerson.setImageBitmap(photo);
        	}     
    }
    
    public String CuCityId="0";
    private void FillPersonalData()
   	{
    	dbPersonalInfo = dbhPersonalInfo.getReadableDatabase();
   		Cursor cursors = dbPersonalInfo.rawQuery("select * from persons order by id desc", null);

   		if(cursors.getCount() > 0)
   		{
   			String code,fullname,father,birthdate,shsh,nationalcode,city,postalcode,email,hometel,worktel,mobile,homeaddress,workaddress,sharecount;
   			cursors.moveToNext();
   			code = cursors.getString(cursors.getColumnIndex("code"));
   			fullname = cursors.getString(cursors.getColumnIndex("fullName"));
   			father = cursors.getString(cursors.getColumnIndex("father"));
   			birthdate = cursors.getString(cursors.getColumnIndex("birthDate"));
   			shsh = cursors.getString(cursors.getColumnIndex("shsh"));
   			nationalcode = cursors.getString(cursors.getColumnIndex("nationalCode"));
   			city = cursors.getString(cursors.getColumnIndex("city"));
   			postalcode = cursors.getString(cursors.getColumnIndex("postalCode"));
   			email = cursors.getString(cursors.getColumnIndex("email"));
   			hometel = cursors.getString(cursors.getColumnIndex("homeTel"));
   			worktel = cursors.getString(cursors.getColumnIndex("workTel"));
   			mobile = cursors.getString(cursors.getColumnIndex("mobile"));
   			homeaddress = cursors.getString(cursors.getColumnIndex("homeAddress"));
   			workaddress = cursors.getString(cursors.getColumnIndex("workAddress"));
   			sharecount = cursors.getString(cursors.getColumnIndex("sharecount"));
   			txtCode.setText(code);
   			txtFullName.setText(fullname);
   			txtFather.setText(father);
   			txtBirthdate.setText(birthdate);
   			txtShSh.setText(shsh);
   			txtNationalCode.setText(nationalcode);
   			txtPostalCode.setText(postalcode);
   			textEmailAddress.setText(email);
   			txtHomeTel.setText(hometel);
   			txtWorkTel.setText(worktel);
   			txtMobile.setText(mobile);
   			txtHomeAddress.setText(homeaddress);
   			txtWorkAddress.setText(workaddress);
   			edtextsharecount.setText(sharecount);
   			
   			String[] strcity = city.split(Pattern.quote("-"));
   			
   			CuCityId = strcity[0].toString();
   			FillCitySpinner(CuCityId);
   			
   			FillCityWorkAndHome(strcity[1].toString());
   		}
   		else
   		{
   			Toast.makeText(getApplicationContext(), PersianReshape.reshape("شهری موجود نمی باشد ، لطفا بروزرسانی کنید"), Toast.LENGTH_LONG).show();
   		}
   	}
    
    private void FillCitySpinner(String CityId)
   	{
    	db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select id,name from cities where id="+CityId, null);

   		String Name = "نامشخص";
   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			Name = cursors.getString(cursors.getColumnIndex("name"));
   			lstCities.setText(Name);
   		}
   		else
   		{
   			lstCities.setText("نامشخص");
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
		 Builder alertbox = new AlertDialog.Builder(PersonalInfo.this);
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
