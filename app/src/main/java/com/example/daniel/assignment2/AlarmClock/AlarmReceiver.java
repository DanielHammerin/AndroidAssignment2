package com.example.daniel.assignment2.AlarmClock;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

/**
 * Created by Daniel on 2016-09-16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static boolean alarmactive = true;
    @Override
    public void onReceive(final Context context, Intent intent) {
        AlarmClockActivity instance = AlarmClockActivity.getInstance();
        alarmactive = true;

        Uri alarmURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmURI == null) {
            alarmURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmURI);
        //play(ringtone);
        ringtone.play();
        instance.alarmToast();

        ComponentName compName = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(compName)));
        setResultCode(Activity.RESULT_OK);
    }
    public void play(Ringtone ringtone) {
        if (!alarmactive) {
            ringtone.stop();
        }
        while (alarmactive) {
            ringtone.play();
        }
    }
}
