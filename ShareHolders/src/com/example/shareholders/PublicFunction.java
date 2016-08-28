package com.example.shareholders;

import java.io.ByteArrayOutputStream;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.PropertyInfo;

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

	public static PropertyInfo GetProperty(String pName,Object pValue,Object pType)
	{
		PropertyInfo P = new PropertyInfo();
		P.setName(pName);
		P.setValue(pValue);
		P.setType(pType);
		return P;
	}
	
}
