package com.example.shareholders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AboutOwner extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,txtAboutUsTitle,txtAboutUsAddress,txtAboutUsEmail,txtAboutUsTel1,txtAboutUsWebSite;
	
	String PersonGuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_owner);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        btnPageTitle = (TextView)findViewById(R.id.aboutownerpagetitle);
        
        txtAboutUsTitle= (TextView)findViewById(R.id.txtAboutOwnerTitle);
        txtAboutUsAddress= (TextView)findViewById(R.id.txtAboutOwnerAddress);
        txtAboutUsEmail= (TextView)findViewById(R.id.txtAboutOwnerEmail);
        txtAboutUsTel1= (TextView)findViewById(R.id.txtAboutOwnerTel1);
    	txtAboutUsWebSite= (TextView)findViewById(R.id.txtAboutOwnerWebSite);

        btnPageTitle.setTypeface(FontMitra);
        txtAboutUsTitle.setTypeface(FontMitra);
        txtAboutUsAddress.setTypeface(FontMitra);
        txtAboutUsEmail.setTypeface(FontMitra);
        txtAboutUsTel1.setTypeface(FontMitra);
        txtAboutUsWebSite.setTypeface(FontMitra);
        
        txtAboutUsWebSite.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsTel1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        
        txtAboutUsWebSite.setOnClickListener(BtnOnClick);
        txtAboutUsEmail.setOnClickListener(BtnOnClick);
        txtAboutUsTel1.setOnClickListener(BtnOnClick);
    }
    
    private OnClickListener BtnOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0.getId()==txtAboutUsWebSite.getId())
			{
				String url = "http://www.tsip.ir";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			else if(arg0.getId()==txtAboutUsEmail.getId())
			{
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","info@tsip.ir", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
			}
			else if(arg0.getId()==txtAboutUsTel1.getId())
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:09155073669"));
				startActivity(callIntent);
			}
		}
	};
    
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
		 Builder alertbox = new AlertDialog.Builder(AboutOwner.this);
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
	    	LoadActivity(AppMenu.class, "PGuid", PersonGuid);
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
	        	LoadActivity(AppMenu.class,"PGuid",PersonGuid);
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
