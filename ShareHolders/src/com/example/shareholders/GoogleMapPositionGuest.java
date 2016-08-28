package com.example.shareholders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class GoogleMapPositionGuest extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle;
	
	WebView GoogleMapView;
	ProgressDialog progressBar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_position_guest);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
//        try
//        {
//        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
//        }
//        catch (Exception e) {
//        	PersonGuid = "0000-0000-0000-0000";
//		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        GoogleMapView = (WebView)findViewById(R.id.webViewGoogleMap);
        
        GoogleMapView.getSettings().setJavaScriptEnabled(true);
		GoogleMapView.getSettings().setUseWideViewPort(true);
		GoogleMapView.getSettings().setLoadWithOverviewMode(true);
		GoogleMapView.getSettings().setBuiltInZoomControls(true);
		GoogleMapView.getSettings().setSupportZoom(true);
        
        btnPageTitle = (TextView)findViewById(R.id.googlemappagetitle);
        
        btnPageTitle.setTypeface(FontMitra);
        
        if(IC.isConnectingToInternet()==true)
		{
        	try
			{
        		AsyncCallLoadGoogleMap task = new AsyncCallLoadGoogleMap(GoogleMapPositionGuest.this);
             task.execute();
			}	
			 catch (Exception e) {
				Toast.makeText(getApplicationContext(), PersianReshape.reshape("عدم دسترسی به سرور"), Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
			 }
			//GoogleMapView.loadUrl("https://maps.google.com/maps?q=36.369725,59.484411&hl=en&ll=36.369656,59.484873&spn=0.011127,0.013797&sll=44.225824,-77.872719&sspn=0.63376,0.883026&t=m&z=16");
		}
		else if(IC.isConnectingToInternet()==false)
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_LONG).show();
		}
        
    }
    
    private class AsyncCallLoadGoogleMap extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog;
		private Activity activity;
		
		public AsyncCallLoadGoogleMap(Activity activity) {
		    this.activity = activity;
		    this.dialog = new ProgressDialog(activity);
		}
		
        @Override
        protected Void doInBackground(String... params) {
        	//GoogleMapView.loadUrl("https://maps.google.com/maps?q=36.369725,59.484411&hl=en&ll=36.369656,59.484873&spn=0.011127,0.013797&sll=44.225824,-77.872719&sspn=0.63376,0.883026&t=m&z=16");
        	GoogleMapView.loadUrl("https://maps.google.com/maps/ms?msid=204599517453738557161.0004f7c4493ca19461db3&msa=0&ll=36.373956,59.477172&spn=0.001391,0.001725&iwloc=lyrftr:starred_items:101914281361635798895:,2078351405130951119,36.373942,59.477046");
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }
 
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(PersianReshape.reshape("در حال بارگذاری نقشه"));
            this.dialog.show();
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
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
		 Builder alertbox = new AlertDialog.Builder(GoogleMapPositionGuest.this);
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
	    	LoadActivity(AboutUs.class, "BackActivity", "Guest");
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}


}
