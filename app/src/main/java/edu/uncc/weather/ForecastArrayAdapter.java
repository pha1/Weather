package edu.uncc.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ForecastArrayAdapter extends ArrayAdapter<Forecast> {
    public ForecastArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Forecast> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_row_item, parent, false);
        }

        Forecast forecast = getItem(position);

        // Create/Initiate TextView
        TextView textViewDateTime = convertView.findViewById(R.id.textViewDateTime);
        TextView textViewTemp = convertView.findViewById(R.id.textViewTemp);
        TextView textViewTempMax = convertView.findViewById(R.id.textViewTempMax);
        TextView textViewTempMin = convertView.findViewById(R.id.textViewTempMin);
        TextView textViewHumidity = convertView.findViewById(R.id.textViewHumidity);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDesc);

        // Set Text
        textViewDateTime.setText(forecast.dateAndTime);
        textViewTemp.setText(forecast.temp + "F");
        textViewTempMax.setText(forecast.temp_max + "F");
        textViewTempMin.setText(forecast.temp_min + "F");
        textViewHumidity.setText(forecast.humidity + "%");
        textViewDescription.setText(forecast.description);

        // Create a String to hold the URL
        String urlImage = "https://openweathermap.org/img/wn/" + forecast.getIcon() + "@2x.png";

        // Picasso to load the Image from URL to the ImageView
        Picasso.get().load(urlImage).into((ImageView) convertView.findViewById(R.id.imageViewWeatherIcon));

        return convertView;
    }
}
