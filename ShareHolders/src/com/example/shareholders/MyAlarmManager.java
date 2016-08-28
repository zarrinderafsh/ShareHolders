package com.example.shareholders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

public class MyAlarmManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "My Alarm Manager Is Runing", Toast.LENGTH_SHORT).show();
		
//		String PersonGuid;
//		try
//        {
//        	//PersonGuid = intent.getStringExtra("PGuid").toString();
//			PersonGuid = intent.getExtras().getString("PGuid");
//        }
//        catch (Exception e) {
//        	PersonGuid = "0000-0000-0000-0000";
//		}
		
		SyncMessageUnRead SMU = new SyncMessageUnRead(context);
		SMU.AsyncExecute();
		
		SyncNewsUnRead SNU = new SyncNewsUnRead(context);
		SNU.AsyncExecute();
		
		SyncLastSharePriceUnRead SLSU = new SyncLastSharePriceUnRead(context);
		SLSU.AsyncExecute();
		
		//scheduleAlarms(context);
		
	}
	
	static void scheduleAlarms(Context ctxt) {
		try
		{
			AlarmManager mgr=(AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
			Intent i=new Intent(ctxt, MyAlarmManager.class);
			//i.putExtra("PGuid", PersonGuid);
			PendingIntent pi=PendingIntent.getBroadcast(ctxt, 0, i, 0);
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 61000, pi);
		}catch (Exception e) {
			// TODO: handle exception
		}
	  }

}
