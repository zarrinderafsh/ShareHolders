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
public class GroundAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;
	InternetConnection IC;

	public GroundAdapter(Activity activity,
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
		holder.txtrequesttype.setText("زمین به متراژ"+" "+map.get("Metraj"));
		holder.txtalldate.setText(Html.fromHtml("ابعاد : "+map.get("Abad")+" - "+"موقعیت جغرافیایی : "+map.get("MogheeyatJoghrafi")+" <br /><br /> "+" موقعیت مکانی : "+map.get("MogheeyatMakani")+" - "+" آدرس : "+map.get("Address")+" <br /><br /> "+" سند : "+map.get("Sanad")+" - "+" تعداد شرکا : "+map.get("TedadShoraka")+" <br /><br /> "+" توضیحات : "+map.get("Tozihat")+" <br /><br /> "+" قیمت : "+map.get("Gheymat")));
		holder.txtrequesttype.setTag(map.get("Code")+"##"+map.get("PGuid"));
		holder.txtalldate.setTag(map.get("Code")+"##"+map.get("PGuid"));
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
		       alertbox.setMessage("آیا می خواهید برای این زمین درخواست خرید ثبت کنید؟");
		       
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
		        	   SendCancelRequestToWs("2",item.split("##")[0].toString(),item.split("##")[1].toString());
		           }
		       });
		      
		       alertbox.show();
			return false;
		}
	};
	
	public void SendCancelRequestToWs(String ReqType,String ReqId,String PGuid)
	{
		if(IC.isConnectingToInternet()==true)
		{
			try
			{
			 AsyncCallWSSendCancelRequest task = new AsyncCallWSSendCancelRequest(activity,ReqType,ReqId,PGuid);
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
	private class AsyncCallWSSendCancelRequest extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		private String cuReqType,cuReqId,cuPGuid;
		
		public AsyncCallWSSendCancelRequest(Activity activity,String ReqType,String ReqId,String PGuid) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		    cuReqType = ReqType;
		    cuReqId = ReqId;
		    cuPGuid = PGuid;
		}
		
        @Override
        protected Void doInBackground(String... params) {
        		CallWsMethodLogin("InsertApartemanAndGroundRequest",cuReqType,cuReqId,cuPGuid);
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if(WsResault.toLowerCase().compareTo("1")==0)
            {
            	Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("درخواست شما با موفقیت ثبت شد"), Toast.LENGTH_LONG).show();
            	//update Buy And Sell Requests
				SyncGroundRequest SBASR = new SyncGroundRequest(activity, cuPGuid,true,true);
				SBASR.AsyncExecute();
				//update Buy And Sell Requests
            }
            else if(WsResault.toLowerCase().compareTo("2")==0)
            {
            	Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("درخواست این زمین قبلا برای شما ثبت شده است"), Toast.LENGTH_LONG).show();
            }
            else
            {
            	Toast.makeText(activity.getApplicationContext(), PersianReshape.reshape("خطا در ثبت درخواست"), Toast.LENGTH_LONG).show();
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
	
	
	public void CallWsMethodLogin(String METHOD_NAME,String ReqType,String ReqId,String PGuid) {
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
	    PassPI.setName("RequestType");
	    //Set Value
	    PassPI.setValue(ReqType);
	    //Set dataType
	    PassPI.setType(Integer.class);
	    //Add the property to request object
	    request.addProperty(PassPI);
	    PropertyInfo ObjectCodePI = new PropertyInfo();
	    //Set Name
	    ObjectCodePI.setName("ObjectCode");
	    //Set Value
	    ObjectCodePI.setValue(ReqId);
	    //Set dataType
	    ObjectCodePI.setType(Integer.class);
	    //Add the property to request object
	    request.addProperty(ObjectCodePI);
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
