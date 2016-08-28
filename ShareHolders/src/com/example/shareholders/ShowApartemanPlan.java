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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowApartemanPlan extends Activity {

	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle;
	
	DatabaseHelper dbh;
	SQLiteDatabase db;
	
	Gallery OneImageView;
	
	String PersonGuid,ApId;
	
	ImageView imageView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showoneimage);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        	ApId = getIntent().getStringExtra("ApId").toString();
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
        
        //OneImageView = (Gallery)findViewById(R.id.imageViewOnePicShop);
        imageView1 = (ImageView)findViewById(R.id.imageView1);
        
        btnPageTitle = (TextView)findViewById(R.id.picgallerypagetitle);
        
        btnPageTitle.setTypeface(FontMitra);
        
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
//        
//   		FillData();
   		
   		try
   		{
        LoadPic();
   		}
   		catch (Exception e) {
			// TODO: handle exception
   			Toast.makeText(getApplicationContext(), "پلن برای این آپارتمان ثبت نشده است", Toast.LENGTH_LONG).show();
		}
    }
    

    
    int [] PicIdArray;
	public void LoadPic()
	{
		db = dbh.getReadableDatabase();
   		Cursor cursors = db.rawQuery("select (select pic from picgallery where picCode=aparteman.pic) pic from aparteman where Code = "+ApId, null);

   		if(cursors.getCount() > 0)
   		{
   			cursors.moveToNext();
   			byte[] decodedByte = Base64.decode(cursors.getString(cursors.getColumnIndex("pic")), Base64.DEFAULT);
			Bitmap Bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
			if(Bmp!=null)
			{
				imageView1.setImageBitmap(Bmp);
			}
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
		 Builder alertbox = new AlertDialog.Builder(ShowApartemanPlan.this);
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
	    	LoadActivity(Aparteman.class, "PGuid", PersonGuid);
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}

	
}
