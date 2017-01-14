package com.dev.ghazi.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.MenuItem;


public class DetailActivity extends ActionBarActivity {

    private ShareActionProvider mShareActionProvider;
    private static String mDate;
    public static final String EXTRA_CURSOR = "com.dev.ghazi.sunshine.DetailActivity";
    public static final String DATE_KEY = "forecast_date";
    public static final String LOCATION_KEY = "location";
    public static final String DATE_DEFAULT = "no_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            DetailFragment detailFragment = DetailFragment.newInstance(getIntent().getStringExtra(DATE_KEY));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, detailFragment)
                    .commit();
        }


    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

}