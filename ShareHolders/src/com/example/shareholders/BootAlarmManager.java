package com.example.shareholders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BootAlarmManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctxt, Intent arg1) {
		// TODO Auto-generated method stub
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
