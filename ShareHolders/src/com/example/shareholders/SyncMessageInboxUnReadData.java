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

public class SyncMessageInboxUnReadData {

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
	//Contractor
	public SyncMessageInboxUnReadData(Context context) {
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
	
	
	public void LoadUserNameAndPassWord()
	{
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from settings where name='UserName'", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			UserName = cursors.getString(cursors.getColumnIndex("value"));
		}
		
		db = dbh.getReadableDatabase();
		Cursor cursors2 = db.rawQuery("select * from settings where name='PassWord'", null);
		if(cursors2.getCount() > 0)
		{
			cursors2.moveToNext();
			PassWord = cursors2.getString(cursors2.getColumnIndex("value"));
		}
	}
	
	public void LoadPersonGuid()
	{
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from settings where name='PersonGuid'", null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			PGuid = cursors.getString(cursors.getColumnIndex("value"));
		}
	}
	
	public void AsyncExecute()
	{
		
			
				LoadUserNameAndPassWord();
//				
//				if(IC.isConnectingToInternet()==true)
//				{
//					try
//					{
//						WSLogin();
//					}
//					catch (Exception e) {
//						// TODO: handle exception
//					}
//				}
			
				//Toast.makeText(CuContext, "Get Message Unread With Guid", Toast.LENGTH_SHORT).show();
				 //LoadPersonGuid();
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
        		CallWsMethod("GetMessagesInboxWithUserName");
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
	            	db = dbh.getWritableDatabase();
	                db.execSQL("delete from messages  where type = '1'");
	            	WsResponse = "Nothing";
	            }
	            else
	            {
	            	InsertDataFromWsToDb(WsResponse);
	            }
        	}
        }
 
        @Override
        protected void onPreExecute() {
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        
    }
	
	public void CallWsMethod(String METHOD_NAME) {
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("UserName");
	    //Set Value
	    UserPI.setValue(this.UserName);
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
	
	
	public void InsertDataFromWsToDb(String AllRecord)
    {
        String[] CuAllRecord = AllRecord.split(Pattern.quote(PV.RECORD_SPLITTER));
        int[] MessageId = new int[CuAllRecord.length + 100];
        String[] AllFields;   
        int NewNewMessCount=0;
        int BitTypeForInsert = 0;
        for(int i = 0 ; i < CuAllRecord.length;i++)
        {
            AllFields = CuAllRecord[i].split(Pattern.quote(PV.FIELD_SPLITTER));
            if(AllFields.length > 0)
            {
            	db = dbh.getWritableDatabase();
            	
            	if(AllFields[8].toString().compareTo("False")==0)
            	{
            		NewNewMessCount=NewNewMessCount+1;
            		BitTypeForInsert = 0;
            	}
            	else
            	{
            		BitTypeForInsert = 1;
            	}
            	
            	db.execSQL("insert into messages(id,senderId,receiverId,SenderName,sDate,type,title,body,openned,sent)"+
            	" values("+AllFields[0].toString()+","+AllFields[1].toString()+","+AllFields[2].toString()+
            	",'"+AllFields[3].toString()+"','"+AllFields[4].toString()+
            	"','1','"+AllFields[6].toString()+"','"+AllFields[7].toString()+"','"+String.valueOf(BitTypeForInsert)+"',0)");
            	
            	
            	
            	MessageId[i] = Integer.valueOf(AllFields[0].toString());
            }
        }
        
        
        db.close();
        dbh.close();
    }
	

//	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
//	{
//		Intent intent = new Intent(activity,Cls);
//		intent.putExtra(VariableName, VariableValue);
//		activity.startActivity(intent);
//	}
	
}
