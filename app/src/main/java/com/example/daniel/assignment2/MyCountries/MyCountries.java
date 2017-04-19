package com.example.daniel.assignment2.MyCountries;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SyncAdapterType;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.assignment2.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MyCountries extends AppCompatActivity implements CalendarProviderClient, LoaderManager.LoaderCallbacks<Cursor>{
    public static ArrayList<String> entryArray = new ArrayList<>();
    public static ListView countryList;

    public static SimpleCursorAdapter cursorAdapter;
    public SharedPreferences prefs;

    private int entryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_countries_activity_main);
        getMyCountriesCalendarId();

        countryList = (ListView) findViewById(R.id.country_list);
        cursorAdapter = new sickCursorAdapter(this, R.layout.my_countries_row, null, EVENTS_LIST_PROJECTION, new int[]{R.id.whenVisit},0);
        //adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, entryArray);
        countryList.setAdapter(cursorAdapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        updatePrefrences();

        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Bundle bundle = new Bundle();
        String sort = "";
        SharedPreferences.Editor editor = prefs.edit();

        switch (item.getItemId()) {
            case R.id.add_country:
                Intent intent = new Intent(this, AddCountryActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.country_ascending:
                sort = (CalendarContract.Events.TITLE + " ASC");
                bundle.putString("Sort", sort);
                editor.putInt("sortPref", 1);
                restartLoader(bundle);
                editor.apply();
                return true;
            case R.id.country_descending:
                sort = CalendarContract.Events.TITLE + " DESC";
                bundle.putString("Sort", sort);
                editor.putInt("sortPref", 2);
                restartLoader(bundle);
                editor.apply();
                return true;
            case R.id.year_ascending:
                sort = CalendarContract.Events.DTSTART + " ASC";
                bundle.putString("Sort", sort);
                editor.putInt("sortPref", 3);
                restartLoader(bundle);
                editor.apply();
                return true;
            case R.id.year_descending:
                sort = CalendarContract.Events.DTSTART + " DESC";
                bundle.putString("Sort", sort);
                editor.putInt("sortPref", 4);
                restartLoader(bundle);
                editor.apply();
                return true;
            case R.id.prefs:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, 3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK){
                String c =data.getStringExtra("country");
                String d =data.getStringExtra("date");
                //adapter.add(c + ", " + d);
                addNewEvent(Integer.valueOf(d), c);
                restartLoader(null);
                Toast.makeText(MyCountries.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(MyCountries.this, "Shit went wrong yo!", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                String c = data.getStringExtra("country");
                String d = data.getStringExtra("date");

                updateEvent(entryID, Integer.valueOf(d), c);
                restartLoader(null);
                Toast.makeText(MyCountries.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(MyCountries.this, "Shit went wrong yo!", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 3) {
            updatePrefrences();
        }
    }

    public void updatePrefrences() {
        boolean divider = prefs.getBoolean("showDivider", false);
        boolean backgroundColor = prefs.getBoolean("switchBackgroundColor", false);
        String dividerHeight = prefs.getString("dividerHeight", "1");
        int sortType = prefs.getInt("sortPref", 1);

        if (divider) {
            countryList.setDivider(ResourcesCompat.getDrawable(getResources(), R.drawable.divider_line, null));
            countryList.setDividerHeight(Integer.valueOf(dividerHeight));
        }
        else {
            countryList.setDividerHeight(0);
            countryList.setDivider(null);
        }

        if (backgroundColor) {
            countryList.setBackgroundColor(getResources().getColor(R.color.white));
        }
        else {
            countryList.setBackgroundColor(getResources().getColor(R.color.pink));
        }
        Bundle bundle = new Bundle();
        String sort = "";
        switch (sortType) {
            case 1:
                sort = (CalendarContract.Events.TITLE + " ASC");
                bundle.putString("Sort", sort);
                restartLoader(bundle);
                break;
            case 2:
                sort = CalendarContract.Events.TITLE + " DESC";
                bundle.putString("Sort", sort);
                restartLoader(bundle);
                break;
            case 3:
                sort = CalendarContract.Events.DTSTART + " ASC";
                bundle.putString("Sort", sort);
                restartLoader(bundle);
                break;
            case 4:
                sort = CalendarContract.Events.DTSTART + " DESC";
                bundle.putString("Sort", sort);
                restartLoader(bundle);
                break;
        }
    }

    @Override
    public long getMyCountriesCalendarId() {
        long id;
        Cursor cursor = getContentResolver().query(CALENDARS_LIST_URI,
                CALENDARS_LIST_PROJECTION,
                CALENDARS_LIST_SELECTION,
                CALENDARS_LIST_SELECTION_ARGS,
                null);

        if(!cursor.moveToFirst()) {
            Uri uri = syncAdapterData(CALENDARS_LIST_URI, ACCOUNT_TITLE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
            values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            values.put(CalendarContract.Calendars.NAME, CALENDAR_TITLE);
            values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_TITLE);
            values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            values.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_TITLE);
            values.put(CalendarContract.Calendars.VISIBLE, 1);
            values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

            Uri finishedURI = getContentResolver().insert(uri, values);
            id = ContentUris.parseId(finishedURI);
        }else {
            id = cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
        }
        return id;
    }

    private static Uri syncAdapterData(Uri uri, String acc, String type) {
        return uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, acc)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, type)
                .build();
        //return uri;
    }

    public void restartLoader(Bundle args) {
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, args, this);
    }

    @Override
    public void addNewEvent(int year, String country) {
        ContentValues entryValues = new ContentValues();
        entryValues.put(CalendarContract.Events.CALENDAR_ID, getMyCountriesCalendarId());
        entryValues.put(CalendarContract.Events.DTSTART, CalendarUtils.getEventStart(year));
        entryValues.put(CalendarContract.Events.DTEND, CalendarUtils.getEventEnd(year));
        entryValues.put(CalendarContract.Events.TITLE, (Character.toUpperCase(country.charAt(0)) + country.substring(1)));
        entryValues.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarUtils.getTimeZoneId());
        getContentResolver().insert(CalendarContract.Events.CONTENT_URI, entryValues);
        restartLoader(null);
    }

    @Override
    public void updateEvent(int eventId, int year, String country) {
        ContentResolver contentresolver = getContentResolver();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(CalendarContract.Events.DTSTART, CalendarUtils.getEventStart(year));
        contentvalues.put(CalendarContract.Events.DTEND, CalendarUtils.getEventEnd(year));
        contentvalues.put(CalendarContract.Events.CALENDAR_ID, getMyCountriesCalendarId());
        contentvalues.put(CalendarContract.Events.TITLE, (Character.toUpperCase(country.charAt(0)) + country.substring(1)));
        contentvalues.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarUtils.getTimeZoneId());
        Uri uri = ContentUris.withAppendedId(EVENTS_LIST_URI, entryID);
        contentresolver.update(uri, contentvalues, null, null);
        restartLoader(null);
    }

    @Override
    public void deleteEvent(int eventId) {
        ContentResolver contentresolver = getContentResolver();
        Uri uri = ContentUris.withAppendedId(EVENTS_LIST_URI, entryID);
        contentresolver.delete(uri, null, null);
        restartLoader(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            return new CursorLoader(this, EVENTS_LIST_URI, EVENTS_LIST_PROJECTION,
                    CalendarContract.Events.CALENDAR_ID + "=" + getMyCountriesCalendarId(),
                    null, args.getString("Sort"));
        }
        return new CursorLoader(this, EVENTS_LIST_URI, EVENTS_LIST_PROJECTION,
                CalendarContract.Events.CALENDAR_ID + "=" + getMyCountriesCalendarId(),
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadManager(int id, Bundle args, LoaderManager.LoaderCallbacks callbacks) {
        getLoaderManager().restartLoader(id, args, callbacks);
    }

    private class sickCursorAdapter extends SimpleCursorAdapter {
        private int layout;
        private Cursor cursor;
        private Context context;
        private final LayoutInflater inflater;

        public sickCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.layout = layout;
            this.cursor = c;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView (Context context, Cursor cursor, ViewGroup parent) {
            return inflater.inflate(layout, null);
        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            TextView country = (TextView) view.findViewById(R.id.country);
            TextView year = (TextView) view.findViewById(R.id.date);

            final int calendarEventID = cursor.getInt(CalendarProviderClient.PROJ_EVENTS_LIST_ID_INDEX);
            final int calendarYear = CalendarUtils.getEventYear(cursor.getLong(CalendarProviderClient.PROJ_EVENTS_LIST_DTSTART_INDEX));
            final String calendarCountry = cursor.getString(CalendarProviderClient.PROJ_EVENTS_LIST_TITLE_INDEX);

            country.setText(calendarCountry);
            year.setText(String.valueOf(calendarYear));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setMessage("Choose an Option");
                    alertBuilder.setPositiveButton("Update Event", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, AddCountryActivity.class);
                            entryID = calendarEventID;
                            startActivityForResult(intent, 2);
                        }
                    });

                    alertBuilder.setNegativeButton("Delete Event", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            entryID = calendarEventID;
                            deleteEvent(entryID);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
            });
        }
    }
}
