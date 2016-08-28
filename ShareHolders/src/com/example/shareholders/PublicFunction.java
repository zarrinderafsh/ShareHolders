package com.example.shareholders;

import java.io.ByteArrayOutputStream;

import org.kobjects.base64.Base64;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class PublicFunction {

	public String ConvertImageViewToBase64String(BitmapDrawable drawable)
	{
		byte[] image;
		String StrImageProfile="";
		try
		{
			Bitmap bitmap = drawable.getBitmap();
		    ByteArrayOutputStream stream=new ByteArrayOutputStream();
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
		    image = stream.toByteArray();
		    StrImageProfile = Base64.encode(image);
		}
		catch(Exception ex){}
	    return StrImageProfile;
	}
	
}
