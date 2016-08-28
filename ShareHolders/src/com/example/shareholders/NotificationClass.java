package com.example.shareholders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationClass {
	public static void Notificationm(Context context,String Title,String Detils,String packge){
        NotificationManager nm=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify=new Notification(R.drawable.elahiyetopleftlogojpg,"سهامدار گرامی شما پیام جدیدی دارید",System.currentTimeMillis());
        notify.flags |= Notification.FLAG_AUTO_CANCEL; //Do not clear the notification
        //notify.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        notify.flags |= Notification.FLAG_SHOW_LIGHTS; //Do not clear the notification
        notify.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notify.defaults |= Notification.DEFAULT_SOUND; // Sound
        CharSequence title=Title;
        CharSequence detils=Detils;
//         Intent nazar = new Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("http://cafebazaar.ir/app/"+packge+"/?l=fa"));
        
        Intent notificationIntent = new Intent(context, Login.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        //Intent intent=new Intent(context,send.class );
        PendingIntent pend=PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notify.setLatestEventInfo(context, title, detils, pend);
        nm.notify(0,notify);
    }
}
