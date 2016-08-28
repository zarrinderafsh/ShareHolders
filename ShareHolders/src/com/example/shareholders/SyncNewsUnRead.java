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

import android.R.integer;
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

public class SyncNewsUnRead {

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
	private String LastNewsId;
	//Contractor
	public SyncNewsUnRead(Context context) {
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
	
	
	public void LoadMaxNewId()
	{
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select IFNULL(max(id),0)MID from news", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			LastNewsId = cursors.getString(cursors.getColumnIndex("MID"));
		}
	}
	
	public void AsyncExecute()
	{
		
			
				LoadMaxNewId();
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
        		CallWsMethod("GetCountNewsUnread");
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
        		String[] CCountMaxCode = WsResponse.toString().split(Pattern.quote(PV.FIELD_SPLITTER));
	            if(WsResponse.toString().compareTo("ER") == 0)
	            {
					
	            }
	            else
	            {
	            	//Toast.makeText(CuContext, "Repose - Get Message Unread With Guid" + WsResponse.toString(), Toast.LENGTH_SHORT).show();
	            	if(Integer.valueOf(CCountMaxCode[0].toString())>0)
	            	{		
	            		InsertMaxNewId(CCountMaxCode[1].toString());
	            		NotificationClass.Notificationm(CuContext, "خبر جدید در نرم افزار الهیه", "شما "+ CCountMaxCode[0].toString() + " خبر جدید دارید", "a");
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
	
	public void InsertMaxNewId(String NewsId)
	{
		db = dbh.getWritableDatabase();
    	db.execSQL("insert into news(id,title,description,sDate,pic)"+
    	" values("+NewsId+",'0','0','0','0')");
	}
	
	public void CallWsMethod(String METHOD_NAME) {
		//Toast.makeText(CuContext, "In Method - Get Message Unread With Guid", Toast.LENGTH_SHORT).show();
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("LastNewsId");
	    //Set Value
	    UserPI.setValue(this.LastNewsId);
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
