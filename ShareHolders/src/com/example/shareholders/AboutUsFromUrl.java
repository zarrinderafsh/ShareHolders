package com.example.shareholders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsFromUrl extends Activity {
	
	TextView btnPageTitle;
	String PersonGuid;
	String BackActivity;
	//Button BtnShowGoogleMapPosition;
	Typeface FontMitra;
	InternetConnection IC;
	
	private WebView webView;
	private ProgressBar progress;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_from_url);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	BackActivity = "Empty";
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
        	try{BackActivity = getIntent().getStringExtra("BackActivity").toString();}catch (Exception e1) {e1.printStackTrace();}
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
		IC = new InternetConnection(getApplicationContext());
		FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        
		webView = (WebView) findViewById(R.id.webView1);
		webView.setWebChromeClient(new MyWebViewClient());

		progress = (ProgressBar) findViewById(R.id.progressBar);
		progress.setMax(100);
		
		String url = "http://www.elahieh.com/?page_id=2479";
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);

	    AboutUsFromUrl.this.progress.setProgress(0);
		
		
        btnPageTitle = (TextView)findViewById(R.id.aboutuspagetitle);
        
        
      //  BtnShowGoogleMapPosition = (Button)findViewById(R.id.BtnShowGoogleMapPosition);
        
     //   BtnShowGoogleMapPosition.setTypeface(FontMitra);
        btnPageTitle.setTypeface(FontMitra);
        

      //  BtnShowGoogleMapPosition.setOnClickListener(BtnOnClick);
        
    }
	
	
	private class MyWebViewClient extends WebChromeClient {	
		@Override
		public void onProgressChanged(WebView view, int newProgress) {			
			AboutUsFromUrl.this.setValue(newProgress);
			super.onProgressChanged(view, newProgress);
		}
	}
	
	public void setValue(int progress) {
		this.progress.setProgress(progress);		
	}
	
	
//	 private OnClickListener BtnOnClick = new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				
//			    if(arg0.getId()==BtnShowGoogleMapPosition.getId())
//				{
//					if(IC.isConnectingToInternet()==true)
//					{
//						if(BackActivity.compareTo("Guest")==0)
//				    	{
//							LoadActivity(GoogleMapPositionGuest.class, "PGuid", PersonGuid);
//				    	}
//						else
//						{
//							LoadActivity(GoogleMapPosition.class, "PGuid", PersonGuid);
//						}
//					}
//					else
//					{
//						Toast.makeText(getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_SHORT).show();
//					}
//				}
//			}
//		};
	    
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
			 Builder alertbox = new AlertDialog.Builder(AboutUsFromUrl.this);
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
		    	if(BackActivity.compareTo("Guest")==0)
		    	{
		    		LoadActivity(AppMenuGuest.class, "PGuid", PersonGuid);
		    	}
		    	else
		    	{
		    		LoadActivity(AppMenu.class, "PGuid", PersonGuid);
		    	}
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
		        	if(BackActivity.compareTo("Guest")==0)
			    	{
			    		LoadActivity(AppMenuGuest.class, "PGuid", PersonGuid);
			    	}
			    	else
			    	{
			    		LoadActivity(AppMenu.class, "PGuid", PersonGuid);
			    	}
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
