package com.example.daniel.assignment2.MyCountries;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.daniel.assignment2.R;

public class AddCountryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.saveButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText country = (EditText) findViewById(R.id.visitedCountry);
                final EditText date = (EditText) findViewById(R.id.whenVisit);

                if (country.getText().toString().equals(null)) {
                    Toast.makeText(AddCountryActivity.this, "So, you didn't visit a country?", Toast.LENGTH_SHORT).show();
                }
                else if (date.getText().toString().equals(null)) {
                    Toast.makeText(AddCountryActivity.this, "No date?", Toast.LENGTH_SHORT).show();
                }
                else if (!country.getText().toString().equals("") && !date.getText().toString().equals("")) {
                    addEntry(country.getText().toString(), date.getText().toString());
                }
                else {
                    Snackbar.make(view, "Something went wrong!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });
    }
    public void addEntry(String country, String date) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("country",country);
        returnIntent.putExtra("date",date);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
