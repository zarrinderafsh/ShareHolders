package com.example.shareholders;

import java.io.IOException;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.integer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

public class SyncMessageInbox {

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
	public SyncMessageInbox(Activity activity,String PGuid,boolean ShowDialog,boolean LoadActivityAfterExecute) {
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
				//Toast.makeText(this.activity.getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
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
        		CallWsMethod("GetMessagesInbox");
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
	            	//Toast.makeText(this.activity.getApplicationContext(), PersianReshape.reshape("خطا در بروزرسانی اخبار و اطلاعیه ها"), Toast.LENGTH_LONG).show();
	            }
	            else if(WsResponse.toString().compareTo("Nothing") == 0)
	            {
	            	//db = dbh.getWritableDatabase();
	                //db.execSQL("delete from messages  where type = '1'");
	            	WsResponse = "Nothing";
	            }
	            else
	            {
	            	InsertDataFromWsToDb(WsResponse);
	            }
        	}
        	else
        	{
        		//Toast.makeText(this.activity, "خطا در اتصال به سرور", Toast.LENGTH_SHORT).show();
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
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("pGuid");
	    //Set Value
	    UserPI.setValue(this.PGuid);
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
		String UpdateWhere="";
		db = dbh.getReadableDatabase();
   		Cursor cursorsU = db.rawQuery("select * from messages where type='1' and openned='1' ", null);
   		if(cursorsU.getCount()>0)
   		{
   			UpdateWhere+="( ";
   			for(int i = 0 ; i < cursorsU.getCount() ; i++)
   			{
   				cursorsU.moveToNext();
   				UpdateWhere+=cursorsU.getString(cursorsU.getColumnIndex("id"))+",";
   			}
   			UpdateWhere=UpdateWhere.substring(0, UpdateWhere.length()-1);
   			UpdateWhere+=" )";
   		}
		
//		db = dbh.getReadableDatabase();
//   		Cursor cursors = db.rawQuery("select * from messages where type='1'", null);
//   		int MessCount = 0;
//   		MessCount = cursors.getCount();
   		
   		int NewMessCount = 0;
		
        //db = dbh.getWritableDatabase();
        //db.execSQL("delete from messages  where type = '1'");
        
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
            	NewMessCount++;
            	db = dbh.getWritableDatabase();
            	
            	if(AllFields.length>=9 && AllFields[8].toString().compareTo("False")==0)
            	{
            		NewNewMessCount=NewNewMessCount+1;
            		BitTypeForInsert = 0;
            	}
            	else
            	{
            		BitTypeForInsert = 1;
            	}

				try {
					db.execSQL("insert into messages(id,senderId,receiverId,SenderName,sDate,type,title,body,openned,sent)" +
							" values(" + AllFields[0].toString() + "," + AllFields[1].toString() + "," + AllFields[2].toString() +
							",'" + AllFields[3].toString() + "','" + AllFields[4].toString() +
							"','1','" + AllFields[6].toString() + "','" + AllFields[7].toString() + "','" + String.valueOf(BitTypeForInsert) + "',0)");
				}
				catch (Exception ex)
				{

				}
            	
            	
            	MessageId[i] = Integer.valueOf(AllFields[0].toString());
            }
        }
        
        if(UpdateWhere.length() > 1)
		{
        	db = dbh.getWritableDatabase();
        	db.execSQL("update messages set openned='1' where id in"+UpdateWhere);
		}
        
        int Ekhtelaf = 0;
        if(NewNewMessCount>0)
        {
//        	if(NewNewMessCount>1)
//        	{
//        		NewNewMessCount=NewNewMessCount-1;
//        	}
        	//Ekhtelaf = NewMessCount - MessCount;
        	Toast.makeText(activity, "شما "+String.valueOf(NewNewMessCount)+" پیام جدید دارید", Toast.LENGTH_LONG).show();
        	//NotificationClass.Notificationm(activity, "پیام جدید از طرف الهیه", "سهامدار عزیز شما "+String.valueOf(Ekhtelaf)+" پیام جدید در نرم افزار سهامداری شرکت الهیه خراسان دارید", "a");
        }
        
        if(NewNewMessCount > 0)
        {
        	db = dbh.getWritableDatabase();
        	db.execSQL("insert into messages(id,senderId,receiverId,SenderName,sDate,type,title,body,openned,sent)"+
        	" values(10001,0,0,'0','0','100','"+String.valueOf(NewNewMessCount)+"','0','0',0)");
        }
        
        if(CuLoadActivityAfterExecute)
		{
			LoadActivity(Messages.class, "PGuid", PGuid);
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
