package com.dev.ghazi.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.dev.ghazi.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean mUseTodayLayout ;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType  = getItemViewType(cursor.getPosition());
        int layoutID = -1;
        if(viewType == VIEW_TYPE_TODAY){

            layoutID = R.layout.list_item_forecast_today;
        }

        else{
            layoutID = R.layout.list_item_forecast;
        }

        View view =  LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        // Use placeholder image for now
        if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY){
            viewHolder.iconView.setImageResource(
                    Utility.getArtResourceForWeatherCondition(weatherId));
        }
        else{

            viewHolder.iconView.setImageResource(
                    Utility.getIconResourceForWeatherCondition(weatherId));
        }

        // Read date from cursor
        String dateString = cursor.getString(WeatherFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it

        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(WeatherFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it

        viewHolder. descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(WeatherFragment.COL_WEATHER_MAX_TEMP);
        // TODO: Find TextView and set formatted high temperature on it

        viewHolder.highTempView.setText(Utility.formatTemperature(mContext,high,isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(WeatherFragment.COL_WEATHER_MIN_TEMP);
        // TODO: Find TextView and set formatted low temperature on it


        viewHolder.lowTempView.setText(Utility.formatTemperature(mContext,low,isMetric));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}