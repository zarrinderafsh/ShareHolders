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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePass extends Activity {

	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView changepasspagetitle,oldPassword,NewPassword,RepNewPassword;
	Button BtnSave;
	EditText txtoldPassword,txtNewPassword,txtRepNewPassword;
	
	String PersonGuid;
	
	String WsResault;
	
	DatabaseHelper dbhPersonalInfo;
	SQLiteDatabase dbPersonalInfo;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepass);
        
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
        
        changepasspagetitle = (TextView)findViewById(R.id.changepasspagetitle);
        oldPassword = (TextView)findViewById(R.id.oldPassword);
        NewPassword = (TextView)findViewById(R.id.NewPassword);
        RepNewPassword = (TextView)findViewById(R.id.RepNewPassword);
        
        BtnSave = (Button)findViewById(R.id.ChangePassBtnSave);
        
        txtoldPassword = (EditText)findViewById(R.id.txtoldPassword);
        txtNewPassword = (EditText)findViewById(R.id.txtNewPassword);
        txtRepNewPassword = (EditText)findViewById(R.id.txtRepNewPassword);
        
        changepasspagetitle.setTypeface(FontMitra);
        oldPassword.setTypeface(FontMitra);
        NewPassword.setTypeface(FontMitra);
        RepNewPassword.setTypeface(FontMitra);
        BtnSave.setTypeface(FontMitra);
        txtoldPassword.setTypeface(FontMitra);
        txtNewPassword.setTypeface(FontMitra);
        txtRepNewPassword.setTypeface(FontMitra);
        
        
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
        
        BtnSave.setOnClickListener(BtnOnClick);
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
	
	
	private OnClickListener BtnOnClick = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(txtoldPassword.getText().toString().length() > 0)
			{
				if(txtNewPassword.getText().toString().length() > 0)
				{
					if(txtRepNewPassword.getText().toString().length() > 0)
					{
						if(txtRepNewPassword.getText().toString().toLowerCase().compareTo(txtNewPassword.getText().toString().toLowerCase())==0)
						{
							SendChangePassToWs();
						}
						else
						{
							Toast.makeText(getApplicationContext(), PersianReshape.reshape("رمز عبور جدید با تکرار آن برابر نیست"), Toast.LENGTH_SHORT).show();
						}
					}
					else
					{
						Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا تکرار رمز عبور جدید را وارد کنید"), Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا رمز عبور جدید را وارد کنید"), Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), PersianReshape.reshape("لطفا رمز عبور فعلی را وارد کنید"), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void SendChangePassToWs()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
				AsyncCallWSendChangePassToWs task = new AsyncCallWSendChangePassToWs(ChangePass.this);
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
	
	private class AsyncCallWSendChangePassToWs extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWSendChangePassToWs(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	CallWsMethodLogin("ChangePass");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(WsResault.toString().length() > 10)
            {
            	PersonGuid = WsResault;
            	Toast.makeText(getApplicationContext(), PersianReshape.reshape("رمز عبور شما با موفقیت تغییر یافت"), Toast.LENGTH_LONG).show();
            }
            else 
            {
            	Toast.makeText(getApplicationContext(), PersianReshape.reshape("خطا در تغییر رمز عبور"), Toast.LENGTH_LONG).show();
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
	    UserPI.setName("username");
	    //Set Value
	    UserPI.setValue(ShareHolderCode);
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
	    PropertyInfo PassPI = new PropertyInfo();
	    //Set Name
	    PassPI.setName("oldPassword");
	    //Set Value
	    PassPI.setValue(txtoldPassword.getText().toString());
	    //Set dataType
	    PassPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PassPI);
	    PropertyInfo PostalCodePI = new PropertyInfo();
	    //Set Name
	    PostalCodePI.setName("newPassword");
	    //Set Value
	    PostalCodePI.setValue(txtNewPassword.getText().toString());
	    //Set dataType
	    PostalCodePI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(PostalCodePI);
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
	        if(WsResault.compareTo("Error Authenticate")==0) WsResault = "0";
	        if(WsResault.toString().length() < 10)
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
		 Builder alertbox = new AlertDialog.Builder(ChangePass.this);
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
