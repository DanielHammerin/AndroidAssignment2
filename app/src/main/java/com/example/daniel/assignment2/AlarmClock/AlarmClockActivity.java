package com.example.daniel.assignment2.AlarmClock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.daniel.assignment2.R;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmClockActivity extends AppCompatActivity {

    TextView currentTime;
    Thread clockThread = null;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker timePicker;
    private static AlarmClockActivity instance;

    @Override
    public void onStart() {
        super.onStart();
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_clock_activity);
        timePicker = (TimePicker) findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long date = System.currentTimeMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            String dateString = sdf.format(date);

                            currentTime = (TextView) findViewById(R.id.currenttime);
                            currentTime.setText(dateString);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        clockThread = new Thread(runnable);
        clockThread.start();

    }
    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            Log.d("AlarmClockActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            Intent newIntent = new Intent(AlarmClockActivity.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(AlarmClockActivity.this, 0, newIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(AlarmClockActivity.this, "Alarm Set!", Toast.LENGTH_SHORT).show();
        }
        else {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(AlarmClockActivity.this, "Alarm Canceled!", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    public void alarmOff(View view) {
        AlarmReceiver.alarmactive = false;
    }
    */
    public static AlarmClockActivity getInstance() {
        return instance;
    }
    public void alarmToast() {
        Toast.makeText(AlarmClockActivity.this, "Alarm!", Toast.LENGTH_SHORT).show();
    }
}
