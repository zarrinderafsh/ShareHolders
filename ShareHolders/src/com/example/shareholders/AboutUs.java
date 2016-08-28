package com.example.shareholders;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUs extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle,txtAboutUsTitle,txtAboutUsAddress,txtAboutUsPostalCode,txtAboutUsEmail,txtAboutUsTel1,
	txtAboutUsTel2,txtAboutUsTel3,txtAboutUsTel4,txtAboutUsFax,txtAboutUsWebSite,txtAboutUsSMS,txtAboutUsTitleFromDb;
	Button BtnSave,BtnShowGoogleMapPosition;
	ImageView AboutUsimageView1;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	String PersonGuid;
	String BackActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);
        
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
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        btnPageTitle = (TextView)findViewById(R.id.aboutuspagetitle);
        
        txtAboutUsTitle= (TextView)findViewById(R.id.txtAboutUsTitle);
        txtAboutUsAddress= (TextView)findViewById(R.id.txtAboutUsAddress);
        txtAboutUsPostalCode= (TextView)findViewById(R.id.txtAboutUsPostalCode);
        txtAboutUsEmail= (TextView)findViewById(R.id.txtAboutUsEmail);
        txtAboutUsTel1= (TextView)findViewById(R.id.txtAboutUsTel1);
    	txtAboutUsTel2= (TextView)findViewById(R.id.txtAboutUsTel2);
    	txtAboutUsTel3= (TextView)findViewById(R.id.txtAboutUsTel3);
    	txtAboutUsTel4= (TextView)findViewById(R.id.txtAboutUsTel4);
    	txtAboutUsFax= (TextView)findViewById(R.id.txtAboutUsFax);
    	txtAboutUsWebSite= (TextView)findViewById(R.id.txtAboutUsWebSite);
    	txtAboutUsSMS= (TextView)findViewById(R.id.txtAboutUsSMS);
    	txtAboutUsTitleFromDb= (TextView)findViewById(R.id.txtAboutUsTitleFromDb);
    	
    	AboutUsimageView1 = (ImageView)findViewById(R.id.AboutUsimageView1);
        
        BtnSave = (Button)findViewById(R.id.personelinfosave);
        BtnShowGoogleMapPosition = (Button)findViewById(R.id.BtnShowGoogleMapPosition);
        
        BtnShowGoogleMapPosition.setTypeface(FontMitra);
        btnPageTitle.setTypeface(FontMitra);
        txtAboutUsTitle.setTypeface(FontMitra);
        txtAboutUsAddress.setTypeface(FontMitra);
        txtAboutUsPostalCode.setTypeface(FontMitra);
        txtAboutUsEmail.setTypeface(FontMitra);
        txtAboutUsTel1.setTypeface(FontMitra);
        txtAboutUsTel2.setTypeface(FontMitra);
        txtAboutUsTel3.setTypeface(FontMitra);
        txtAboutUsTel4.setTypeface(FontMitra);
        txtAboutUsFax.setTypeface(FontMitra);
        txtAboutUsWebSite.setTypeface(FontMitra);
        txtAboutUsSMS.setTypeface(FontMitra);
        txtAboutUsTitleFromDb.setTypeface(FontMitra);
        
        txtAboutUsSMS.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsWebSite.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsTel1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsTel2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsTel3.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txtAboutUsTel4.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        
        txtAboutUsSMS.setOnClickListener(BtnOnClick);
        txtAboutUsWebSite.setOnClickListener(BtnOnClick);
        txtAboutUsEmail.setOnClickListener(BtnOnClick);
        txtAboutUsTel1.setOnClickListener(BtnOnClick);
        txtAboutUsTel2.setOnClickListener(BtnOnClick);
        txtAboutUsTel3.setOnClickListener(BtnOnClick);
        txtAboutUsTel4.setOnClickListener(BtnOnClick);
        BtnShowGoogleMapPosition.setOnClickListener(BtnOnClick);
        
        dbh=new DatabaseHelper(getApplicationContext());
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
   		
   		FillData();
        
    }
    
    private void FillData()
	{
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select title,(select pic from picgallery where picCode=aboutus.pic) pic from aboutus order by id desc", null);

		if(cursors.getCount() > 0)
		{
			String title,pic;
			cursors.moveToNext();
			title = cursors.getString(cursors.getColumnIndex("title"));
			pic = cursors.getString(cursors.getColumnIndex("pic"));
			txtAboutUsTitleFromDb.setText(title);
			
			try
			{
				byte[] decodedByte = Base64.decode(pic, Base64.DEFAULT);
				Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
				AboutUsimageView1.setImageBitmap(Bmp);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	}
    
    private OnClickListener BtnOnClick = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(arg0.getId()==txtAboutUsSMS.getId())
			{
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);         
				sendIntent.setData(Uri.parse("sms:30009900000531"));
				sendIntent.putExtra("sms_body", ""); 
				startActivity(sendIntent);
			}
			else if(arg0.getId()==txtAboutUsWebSite.getId())
			{
				String url = "http://www.elahiye.com";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			else if(arg0.getId()==txtAboutUsEmail.getId())
			{
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","info@elahiye.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
			}
			else if(arg0.getId()==txtAboutUsTel1.getId())
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:05115313610"));
				startActivity(callIntent);
			}
			else if(arg0.getId()==txtAboutUsTel2.getId())
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:05115313611"));
				startActivity(callIntent);
			}
			else if(arg0.getId()==txtAboutUsTel3.getId())
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:05115313612"));
				startActivity(callIntent);
			}
			else if(arg0.getId()==txtAboutUsTel4.getId())
			{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:05115313613"));
				startActivity(callIntent);
			}
			else if(arg0.getId()==BtnShowGoogleMapPosition.getId())
			{
				if(IC.isConnectingToInternet()==true)
				{
					if(BackActivity.compareTo("Guest")==0)
			    	{
						LoadActivity(GoogleMapPositionGuest.class, "PGuid", PersonGuid);
			    	}
					else
					{
						LoadActivity(GoogleMapPosition.class, "PGuid", PersonGuid);
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), PersianReshape.reshape("شما به اینترنت دسترسی ندارید"), Toast.LENGTH_SHORT).show();
				}
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
		 Builder alertbox = new AlertDialog.Builder(AboutUs.this);
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
