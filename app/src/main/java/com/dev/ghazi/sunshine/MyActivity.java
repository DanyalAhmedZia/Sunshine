package com.dev.ghazi.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dev.ghazi.sunshine.data.WeatherContract;
import com.dev.ghazi.sunshine.sync.SunshineSyncAdapter;

import java.util.Date;


public class MyActivity extends AppCompatActivity implements Callback {

    private boolean mTwoPan ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        if(findViewById(R.id.weather_detail_container) != null){
            mTwoPan = true;

            if(savedInstanceState == null){
                onItemSelected(WeatherContract.getDbDateString(new Date()));
            }


        }
        else{
            mTwoPan = false;
            getSupportActionBar().setElevation(0f);
        }
        WeatherFragment weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        weatherFragment.setUseTodayLayout(!mTwoPan);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
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

    @Override
    public void onItemSelected(String date) {

        if(!mTwoPan){
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);

            startActivity(intent);

        }
        else{
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.weather_detail_container);
            if (detailFragment == null || detailFragment.getShownDate() != date){

                detailFragment = DetailFragment.newInstance(date);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container,detailFragment)
                        .commit();
            }

        }

    }
}
