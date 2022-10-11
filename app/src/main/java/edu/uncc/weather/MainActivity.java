/**
 * Homework 6
 * Group9_Homework6
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements CitiesFragment.CitiesFragmentListener, CurrentWeatherFragment.CurrentWeatherListener {

    final String TAG = "test";
    final public static String APIKey = "7fe9da484c7de02dea89cf2ac74c962b";

    String urlImage;
    CurrentWeatherFragment currentWeatherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new CitiesFragment())
                .commit();

    }

    @Override
    public void gotoCurrentWeather(DataService.City city) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, CurrentWeatherFragment.newInstance(city), "Current Weather")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void checkForecast(DataService.City city) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, WeatherForecastFragment.newInstance(city), "Weather Forecast")
                .addToBackStack(null)
                .commit();
    }



}