package com.example.shareholders;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class AprGroRequestAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;
	InternetConnection IC;

	public AprGroRequestAdapter(Activity activity,
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
		TextView txtalldate,txtrequesttype;
	}

	// @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();

		if (convertView == null) {
			Typeface faceh = Typeface.createFromAsset(activity.getAssets(), "font/BMitra.ttf");
			convertView = inflater.inflate(R.layout.share_sale_and_buy_list_row, null);
			holder = new ViewHolder();
			holder.txtalldate = (TextView) convertView.findViewById(R.id.txtalldata);
			holder.txtalldate.setTypeface(faceh);
			holder.txtalldate.setTextSize(20);
			holder.txtrequesttype = (TextView) convertView.findViewById(R.id.txtrequesttype);
			holder.txtrequesttype.setTypeface(faceh);
			holder.txtrequesttype.setTextSize(22);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> map = list.get(position);
		if(map.get("RequestType").toString().compareTo("1")==0)
		{
			holder.txtrequesttype.setText("درخواست خرید آپارتمان");
		}
		else
		{
			holder.txtrequesttype.setText("درخواست خرید زمین");
		}
		holder.txtalldate.setText(Html.fromHtml("کد : "+map.get("ObjectCode")+"<br /><br /> "+" موضوع : "+map.get("GroundAparteman")+" <br /><br /> "+" وضعیت : "+map.get("Status")+" <br /><br /> "+" تاریخ درج : "+map.get("InsertDate")));
		holder.txtrequesttype.setTag(map.get("Code")+"##"+map.get("PGuid")+"##"+holder.txtrequesttype.getText());
		holder.txtalldate.setTag(map.get("Code")+"##"+map.get("PGuid")+"##"+holder.txtrequesttype.getText());
		holder.txtalldate.setOnLongClickListener(ImageItemOnclick);
		holder.txtrequesttype.setOnLongClickListener(ImageItemOnclick);
		return convertView;
	}
	
	
	private OnLongClickListener ImageItemOnclick = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			 final String item = ((TextView)v).getTag().toString();
			 Builder alertbox = new AlertDialog.Builder(activity);
		       // set the message to display
		       alertbox.setMessage("آیا می خواهید این درخواست را لغو کنید ؟");
		       
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
		        	   SendCancelRequestToWs(item.split("##")[0].toString(),item.split("##")[1].toString(),item.split("##")[2].toString());
		           }
		       });
		      
		       alertbox.show();
			return false;
		}
	};
	
	public void SendCancelRequestToWs(String ReqId,String PGuid,String ReqType)
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
				AsyncCallWSSendCancelRequestN task = new AsyncCallWSSendCancelRequestN(activity,ReqId,PGuid,ReqType);
             task.execute();
			}	
			 catch (Exception e) {
				Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
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
		private String cuReqId,cuPGuid,CuReqType;
		
		public AsyncCallWSSendCancelRequestN(Activity activity,String ReqId,String PGuid,String ReqType) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		    cuReqId = ReqId;
		    cuPGuid = PGuid;
		    CuReqType = ReqType;
		}
		
        @Override
        protected String doInBackground(String... params) {
        		String result = null;
            	try
            	{
            		CallWsMethodLogin("DeleteApartemanAndGroundRequest",cuReqId,cuPGuid);
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
            	Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
            	//update Buy And Sell Requests
            	if(CuReqType.compareTo("درخواست خرید آپارتمان")==0)
            	{
					SyncApartemanRequest SBASR = new SyncApartemanRequest(activity, cuPGuid,true,true);
					SBASR.AsyncExecute();
					//update Buy And Sell Requests
            	}
            	else
            	{
					SyncGroundRequest SBASR2 = new SyncGroundRequest(activity, cuPGuid,true,true);
					SBASR2.AsyncExecute();
            	}
            }
            else
            {
            	Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("خطا در حذف درخواست"), Toast.LENGTH_LONG).show();
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
	    PassPI.setName("ObjectCode");
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
