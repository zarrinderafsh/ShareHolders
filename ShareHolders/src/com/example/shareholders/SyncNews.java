package com.example.shareholders;

import java.io.IOException;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

public class SyncNews {

	//Primary Variable
	DatabaseHelper dbh;
	SQLiteDatabase db;
	PublicVariable PV;
    InternetConnection IC;
	private Activity activity;
	private String PGuid;
	private String WsResponse; 
	private boolean CuShowDialog;
	private boolean CuLoadActivityAfterExecute;
	//Contractor
	public SyncNews(Activity activity,String PGuid,boolean ShowDialog,boolean LoadActivityAfterExecute) {
		this.activity = activity;
		this.PGuid = PGuid;
		this.CuShowDialog = ShowDialog;
		this.CuLoadActivityAfterExecute = LoadActivityAfterExecute;
		IC = new InternetConnection(this.activity.getApplicationContext());
		PV = new PublicVariable();
		
		dbh=new DatabaseHelper(this.activity.getApplicationContext());
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
	
	public void AsyncExecute()
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
				AsyncCallWS task = new AsyncCallWS(this.activity);
				task.execute();
			}	
			 catch (Exception e) {
	            e.printStackTrace();
			 }
		}
		else
		{
			Toast.makeText(this.activity.getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_SHORT).show();
		}
	}
	
	//Async Method
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallWS(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected String doInBackground(String... params) {
        	String result = null;
        	try
        	{
        		CallWsMethod("GetNews");
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
	            else if(WsResponse.toString().compareTo("Nothing") == 0)
	            {
//	            	db = dbh.getWritableDatabase();
//	                db.execSQL("delete from news");
//	            	WsResponse = "Nothing";
	            }
	            else
	            {
	            	InsertDataFromWsToDb(WsResponse);
	            }
        	}
        	else
        	{
        	}
            try
            {
            	if (this.dialog.isShowing()) {
            		this.dialog.dismiss();
            	}
            }
            catch (Exception e) {}
        }
 
        @Override
        protected void onPreExecute() {
        	if(CuShowDialog)
        	{
        		this.dialog.setMessage(PersianReshape.reshape("در حال دریافت اطلاعات"));
        		this.dialog.show();
        	}
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
	
	public void CallWsMethod(String METHOD_NAME) {
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
		request.addProperty(PublicFunction.GetProperty("pGuid",this.PGuid,String.class));
		request.addProperty(PublicFunction.GetProperty("LastId",0,Integer.class));

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
	
	
	public void InsertDataFromWsToDb(String AllRecord)
    {
        db = dbh.getWritableDatabase();
        db.execSQL("delete from news");
        
        String[] CuAllRecord = AllRecord.split(Pattern.quote(PV.RECORD_SPLITTER));
        
        String[] AllFields;        
        for(int i = 0 ; i < CuAllRecord.length;i++)
        {
            AllFields = CuAllRecord[i].split(Pattern.quote(PV.FIELD_SPLITTER));
            if(AllFields.length > 0)
            {
				Integer PicCode = 0;
				if(AllFields[4].toString().compareTo("NoPic")==0)
				{
					PicCode = 0;
				}
				else {
					PicCode = Integer.parseInt(AllFields[4].toString());
				}
            	db = dbh.getWritableDatabase();
            	db.execSQL("insert into news(id,title,description,sDate,pic)"+
            	" values("+AllFields[0].toString()+",'"+AllFields[1].toString()+"','"+AllFields[2].toString()+"','"+AllFields[3].toString()+"','"+PicCode+"')");
            	if(PicCode > 0)
            	{
            		SyncNewsPicGetImage SPGGI = new SyncNewsPicGetImage(activity, this.PGuid,AllFields[4].toString(),false);
        			SPGGI.AsyncExecute();
            	}
            }
        }
        
		if(CuLoadActivityAfterExecute)
		{
			LoadActivity(News.class, "PGuid", PGuid);
		}
        
        db.close();
        dbh.close();
    }
	

	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
	{
		Intent intent = new Intent(activity,Cls);
		intent.putExtra(VariableName, VariableValue);
		activity.startActivity(intent);
	}
	
}
