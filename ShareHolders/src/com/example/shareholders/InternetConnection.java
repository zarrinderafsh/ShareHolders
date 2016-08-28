package com.example.shareholders;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnection {
	
	private Context _Cucontext;
    
    public InternetConnection(Context context){
        this._Cucontext = context;
    }
	public Boolean isConnectingToInternet()
	{
		 ConnectivityManager connectivity = (ConnectivityManager) _Cucontext.getSystemService(Context.CONNECTIVITY_SERVICE);
         if (connectivity != null)
         {
             NetworkInfo[] info = connectivity.getAllNetworkInfo();
             if (info != null)
                 for (int i = 0; i < info.length; i++)
                     if (info[i].getState() == NetworkInfo.State.CONNECTED)
                     {
                         return true;
                     }

         }
         return false;
	}
}
