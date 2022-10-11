/**
 * Homework 6
 * Group9_Homework6
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.weather;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import edu.uncc.weather.databinding.FragmentWeatherForecastBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();

    FragmentWeatherForecastBinding binding;
    final String TAG = "test";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_CITY = "city";

    private DataService.City mCity;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param city Parameter 1.
     * @return A new instance of fragment WeatherForcastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherForecastFragment newInstance(DataService.City city) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    ForecastArrayAdapter adapter;
    ListView listView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Weather Forecast");

        getForecast();

        binding.textViewCityName.setText(mCity.getCity() + ", " + mCity.getCountry());

        listView = binding.listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: Position: " + i);
            }
        });
    }

    public void loadList(ArrayList<Forecast> fiveDayForecast) {
        listView = binding.listView;
        adapter = new ForecastArrayAdapter(getActivity(), R.layout.forecast_row_item, fiveDayForecast);
        listView.setAdapter(adapter);
    }

    public void getForecast() {
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder()
                .addQueryParameter("lat", String.valueOf(mCity.getLat()))
                .addQueryParameter("lon", String.valueOf(mCity.getLon()))
                .addQueryParameter("appid",MainActivity.APIKey)
                .addQueryParameter("units", "imperial")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                Log.d(TAG, "onResponse: Start Response for Forcast");
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        // Retrieve the List
                        JSONArray forecastJson = jsonObject.getJSONArray("list");
                        Log.d(TAG, "onResponse: " + forecastJson.length());
                        // The list is an array of objects that contains objects/arrays inside of it
                        ArrayList<Forecast> fiveDayForecast = new ArrayList<>();

                        // Iterate the list
                        for (int i = 0; i < forecastJson.length(); i++) {
                            //Log.d(TAG, "onResponse: Created Forecast object");
                            Forecast forecast = new Forecast();

                            // Get the object
                            JSONObject forecastJsonObject = forecastJson.getJSONObject(i);
                            //Log.d(TAG, "onResponse: Outer Json Object");

                            // Get the temp information
                            JSONObject forecastInnerObject = forecastJsonObject.getJSONObject("main");
                            //Log.d(TAG, "onResponse: Main Object");
                            forecast.setTemp(forecastInnerObject.getInt("temp"));
                            forecast.setTemp_min(forecastInnerObject.getDouble("temp_min"));
                            forecast.setTemp_max(forecastInnerObject.getDouble("temp_max"));
                            forecast.setHumidity(forecastInnerObject.getInt("humidity"));

                            // Get weather information
                            JSONArray innerArray = forecastJsonObject.getJSONArray("weather");
                            //Log.d(TAG, "onResponse: Weather Array");
                            forecastInnerObject = innerArray.getJSONObject(0);
                            forecast.setDescription(forecastInnerObject.getString("description"));
                            //Log.d(TAG, "onResponse: Set Description");
                            forecast.setIcon(forecastInnerObject.getString("icon"));
                            //Log.d(TAG, "onResponse: Set Icon");

                            // Get date and time
                            forecast.setDateAndTime(forecastJsonObject.getString("dt_txt"));
                            Log.d(TAG, "onResponse: " + forecast.dateAndTime);

                            fiveDayForecast.add(forecast);
                        }

                        weatherForecastListener.updateForecast(fiveDayForecast);

                        Log.d(TAG, "onResponse: # of Objects " + fiveDayForecast.size());

                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: ArrayList size = " + fiveDayForecast.size());
                                loadList(fiveDayForecast);
                            }
                        };
                        mainHandler.post(myRunnable);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WeatherForecastListener) {
            weatherForecastListener = (WeatherForecastListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement WeatherForecastListener");
        }
    }

    WeatherForecastListener weatherForecastListener;

    public interface WeatherForecastListener {
        void updateForecast(ArrayList<Forecast> fiveDayForecast);
    }
}