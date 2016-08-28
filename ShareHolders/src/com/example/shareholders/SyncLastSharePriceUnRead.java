package com.example.shareholders;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

public class SyncLastSharePriceUnRead {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Context CuContext;
	private String PGuid;
	private String WsResponse; 
	private String UserName;
	private String PassWord;
	private String ConutSharePrice;
	//Contractor
	public SyncLastSharePriceUnRead(Context context) {
		this.CuContext = context;
		IC = new InternetConnection(CuContext);
		PV = new PublicVariable();	
		
		dbh=new DatabaseHelper(CuContext);
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

	}
	
	
	public void LoadMaxSharePriceId()
	{
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select count(*)CCount from shareprices", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			ConutSharePrice = cursors.getString(cursors.getColumnIndex("CCount"));
		}
	}
	
	public void AsyncExecute()
	{
		
			
		LoadMaxSharePriceId();
				 if(IC.isConnectingToInternet()==true)
					{
					 try
		    			{
		    				AsyncCallWS task = new AsyncCallWS();
		    				task.execute();
		    			}	
		    			 catch (Exception e) {
		    				//Toast.makeText(this.activity.getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
		    	            e.printStackTrace();
		    			 }
					}
			
		
	}
	
	

	
	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		//private ProgressDialog dialog;
		//private Activity activity;
		
		public AsyncCallWS() {
		    //this.activity = activity;
		    //this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected String doInBackground(String... params) {
        	String result = null;
        	try
        	{
        		//Toast.makeText(CuContext, "Call Method - Get Message Unread With Guid", Toast.LENGTH_SHORT).show();
        		CallWsMethod("GetCountSharePriceUnread");
        	}
	    	catch (Exception e) {
	    		result = e.getMessage().toString();
			}
	        return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
        	if(result == null)
        	{
	            if(WsResponse.toString().compareTo("ER") == 0)
	            {
					
	            }
	            else
	            {
	            	//Toast.makeText(CuContext, "Repose - Get Message Unread With Guid" + WsResponse.toString(), Toast.LENGTH_SHORT).show();
	            	if(Integer.valueOf(WsResponse.toString())>0)
	            	{
	            		InsertMaxNewId(WsResponse.toString());
	            		NotificationClass.Notificationm(CuContext, "قیمت سهام جدید در نرم افزار الهیه", "شما "+ WsResponse.toString() + " قیمت سهام جدید دارید", "a");
	            	}
	            }
        	}
        	else
        	{
        		//Toast.makeText(this.activity, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
        	}
        }
 
        @Override
        protected void onPreExecute() {
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
	
	public void InsertMaxNewId(String SharePriceCountNew)
	{
		db = dbh.getWritableDatabase();
		for(int i = 0 ; i < Integer.valueOf(SharePriceCountNew);i++)
		{
			db.execSQL("insert into sharePrices(price,date) values('0','0')");
		}
	}
	
	public void CallWsMethod(String METHOD_NAME) {
		//Toast.makeText(CuContext, "In Method - Get Message Unread With Guid", Toast.LENGTH_SHORT).show();
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("CountSharePriceId");
	    //Set Value
	    UserPI.setValue(this.ConutSharePrice);
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
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
	        WsResponse = response.toString();	
	        if(WsResponse == null) WsResponse="ER";
	    } catch (Exception e) {
	    	WsResponse = "ER";
	    	e.printStackTrace();
	    }
	}
	

//	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
//	{
//		Intent intent = new Intent(activity,Cls);
//		intent.putExtra(VariableName, VariableValue);
//		activity.startActivity(intent);
//	}
	
}
