package com.example.shareholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class MessageInboxAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;
	InternetConnection IC;

	public MessageInboxAdapter(Activity activity,
			ArrayList<HashMap<String, String>> list) {
		super();
		this.activity = activity;
		this.list = list;
		IC = new InternetConnection(activity.getApplicationContext());
	}

	// @Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	// @Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	// @Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private class ViewHolder {
		TextView txtdate,txtPrice,txtdiscription;
	}

	// @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		HashMap<String, String> map = list.get(position);
		
		if (convertView == null) {
			Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
//			if(map.get("openned").toString().compareTo("0")==0)
//			{
				convertView = inflater.inflate(R.layout.messagelist_row, null);
//			}
//			else
//			{
//				convertView = inflater.inflate(R.layout.messagelist_row_read, null);
//			}
			holder = new ViewHolder();
			holder.txtdate = (TextView) convertView.findViewById(R.id.txtdata);
			holder.txtdate.setTypeface(faceh);
			holder.txtdate.setTextSize(22);
			holder.txtPrice = (TextView) convertView.findViewById(R.id.txtprice);
			holder.txtPrice.setTypeface(faceh);
			holder.txtPrice.setTextSize(20);
			holder.txtdiscription = (TextView) convertView.findViewById(R.id.txtdiscription);
			holder.txtdiscription.setTypeface(faceh);
			holder.txtdiscription.setTextSize(20);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtdate.setText("فرستنده : "+map.get("SenderName"));
		holder.txtPrice.setText(map.get("sDate"));
		holder.txtdiscription.setText(map.get("title"));
		holder.txtdate.setTag(map.get("id")+"*"+map.get("PGuid"));
		holder.txtPrice.setTag(map.get("id")+"*"+map.get("PGuid"));
		holder.txtdiscription.setTag(map.get("id")+"*"+map.get("PGuid"));
		holder.txtdate.setOnClickListener(ImageItemOnclick);
		holder.txtPrice.setOnClickListener(ImageItemOnclick);
		holder.txtdiscription.setOnClickListener(ImageItemOnclick);
		holder.txtdate.setOnLongClickListener(ImageItemOnclickLongClick);
		holder.txtPrice.setOnLongClickListener(ImageItemOnclickLongClick);
		holder.txtdiscription.setOnLongClickListener(ImageItemOnclickLongClick);
		
		if(map.get("openned").toString().compareTo("0")==0)
		{
			holder.txtdate.setCompoundDrawablesWithIntrinsicBounds( R.drawable.messagenew, 0, 0, 0);
		}
		else
		{
			holder.txtdate.setCompoundDrawablesWithIntrinsicBounds( R.drawable.messageread, 0, 0, 0);
		}
		
		return convertView;
	}
	
	private OnClickListener ImageItemOnclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 String item = ((TextView)v).getTag().toString();
			 item = item.split(Pattern.quote("*"))[0].toString();
			 String Guid="";
			 Guid = ((TextView)v).getTag().toString().split(Pattern.quote("*"))[1].toString();
			 Intent intent = new Intent(activity.getApplicationContext(),ShowOneMessage.class);
			 intent.putExtra("MessageId",item);
			 intent.putExtra("PGuid",Guid);
			 activity.startActivity(intent);
		}
	};
	
	
	
	private OnLongClickListener ImageItemOnclickLongClick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			 final String item = ((TextView)v).getTag().toString();
			 Builder alertbox = new AlertDialog.Builder(activity);
		       // set the message to display
		       alertbox.setMessage("آیا می خواهید این پیام را حذف کنید ؟");
		       
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
		        	   SendCancelRequestToWs(item.split(Pattern.quote("*"))[0].toString(),item.split(Pattern.quote("*"))[1].toString());
		           }
		       });
		      
		       alertbox.show();
			return false;
		}
	};
	
	public void SendCancelRequestToWs(String ReqId,String PGuid)
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
				AsyncCallWSSendCancelRequestN task = new AsyncCallWSSendCancelRequestN(activity,ReqId,PGuid);
             task.execute();
			}	
			 catch (Exception e) {
				//Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
			 }
		}
		else
		{
			Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_SHORT).show();
		}
	}
	
	String WsResault;
	private class AsyncCallWSSendCancelRequestN extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;
		private Activity activity;
		private String cuReqId,cuPGuid;
		
		public AsyncCallWSSendCancelRequestN(Activity activity,String ReqId,String PGuid) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		    cuReqId = ReqId;
		    cuPGuid = PGuid;
		}
		
        @Override
        protected String doInBackground(String... params) {
        		String result = null;
            	try
            	{
            		CallWsMethodLogin("DeleteMessageInbox",cuReqId,cuPGuid);
            	}
    	    	catch (Exception e) {
    	    		result = e.getMessage().toString();
    			}
            return result;
        }
 
        @Override
        protected void onPostExecute(String result) {
            if(WsResault.toLowerCase().compareTo("1")==0)
            {
            	Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("پیام شما با موفقیت حذف شد"), Toast.LENGTH_LONG).show();
            	//update Buy And Sell Requests
            	
					SyncMessageInbox SBASR2 = new SyncMessageInbox(activity, cuPGuid,true,true);
					SBASR2.AsyncExecute();
            	
            }
            else
            {
            	//Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("خطا در حذف درخواست"), Toast.LENGTH_LONG).show();
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
	
	
	public void CallWsMethodLogin(String METHOD_NAME,String ReqId,String PGuid) {
		PublicVariable PV = new PublicVariable();
	    //Create request
	    SoapObject request = new SoapObject(PV.NAMESPACE, METHOD_NAME);
	    PropertyInfo UserPI = new PropertyInfo();
	    //Set Name
	    UserPI.setName("pGuid");
	    //Set Value
	    UserPI.setValue(PGuid);
	    //Set dataType
	    UserPI.setType(String.class);
	    //Add the property to request object
	    request.addProperty(UserPI);
	    PropertyInfo PassPI = new PropertyInfo();
	    //Set Name
	    PassPI.setName("MessageId");
	    //Set Value
	    PassPI.setValue(ReqId);
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
	        WsResault = response.toString();	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	
}
