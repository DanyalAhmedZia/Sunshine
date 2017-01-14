package com.dev.ghazi.sunshine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dev.ghazi.sunshine.data.WeatherContract;
import com.dev.ghazi.sunshine.data.WeatherContract.LocationEntry;
import com.dev.ghazi.sunshine.data.WeatherContract.WeatherEntry;
import com.dev.ghazi.sunshine.sync.SunshineSyncAdapter;

import java.util.Date;

//import android.app.LoaderManager.LoaderCallbacks;

/**
 * Created by Ghazi on 11/3/2014.
 */
public class WeatherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static ForecastAdapter mForecastAdapter;

    private static final int FORECAST_LOADER = 0;
    private String mLocation;
    public static final String LIST_POSITION = "position";
    private ListView mListView;
    private boolean mUseTodayLayout;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    private int mPostion ;

    public WeatherFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPostion != ListView.INVALID_POSITION){

            outState.putInt(LIST_POSITION,mPostion);
        }
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout= useTodayLayout;
        if(mForecastAdapter!= null){
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        if (savedInstanceState != null ){

            mPostion = savedInstanceState.getInt(LIST_POSITION);
        }

        mListView  = (ListView) rootView.findViewById(R.id.listview_forecast);



        // The SimpleCursorAdapter will take data from the database through the
        // Loader and use it to populate the ListView it's attached to.
//        mForecastAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.list_item_forecast,
//                null,
//                // the column names to use to fill the textviews
//                new String[]{WeatherContract.WeatherEntry.COLUMN_DATETEXT,
//                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
//                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
//                },
//                // the textviews to fill with the data pulled from the columns above
//                new int[]{R.id.list_item_date_textview,
//                        R.id.list_item_forecast_textview,
//                        R.id.list_item_high_textview,
//                        R.id.list_item_low_textview
//                },
//                0
//        );

        mForecastAdapter = new ForecastAdapter(getActivity(),null,0);
        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

//        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                boolean isMetric = Utility.isMetric(getActivity());
//                switch (columnIndex) {
//                    case COL_WEATHER_MAX_TEMP:
//                    case COL_WEATHER_MIN_TEMP: {
//                        // we have to do some formatting and possibly a conversion
//                        ((TextView) view).setText(Utility.formatTemperature(
//                                cursor.getDouble(columnIndex), isMetric));
//                        return true;
//                    }
//                    case COL_WEATHER_DATE: {
//                        String dateString = cursor.getString(columnIndex);
//                        TextView dateView = (TextView) view;
//                        dateView.setText(Utility.formatDate(dateString));
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
        mListView.setEmptyView(rootView.findViewById(R.id.emptyView));
        mListView.setAdapter(mForecastAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String forecastStr = (String)adapterView.getItemAtPosition(i);
//                Intent intent = new Intent(getActivity(),DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT,forecastStr);
//                startActivity(intent);
                mPostion = i;
                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(i)) {
                    String dateString = cursor.getString(COL_WEATHER_DATE);
                    ((Callback) getActivity()).onItemSelected(dateString);


                }
            }
        });



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(R.id.action_refresh == id) {
            fetchWeather();
            return true;
        }
        if(R.id.action_map == id){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefLocation = sharedPreferences.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

            showMap(Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",prefLocation).build());
        }
        return super.onOptionsItemSelected(item);
    }
    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void fetchWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
//        Intent intent = new Intent(getActivity(), SunshineService.AlarmReciever.class);
//        intent.putExtra(SunshineService.WEATHER_LOCATION,mLocation);
//        mPendingIntent = PendingIntent.getBroadcast(getActivity(),0,intent,PendingIntent.FLAG_ONE_SHOT);
//        mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        mAlarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 5000,mPendingIntent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(mPostion != ListView.INVALID_POSITION){
            mListView.setSelection(mPostion);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);

    }
}
