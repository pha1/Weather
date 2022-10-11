/**
 * Homework 6
 * Group9_Homework6
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.weather;

import android.content.Context;
import android.media.Image;
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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.weather.databinding.FragmentCurrentWeatherBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrentWeatherFragment extends Fragment {
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private DataService.City mCity;
    FragmentCurrentWeatherBinding binding;

    private final OkHttpClient client = new OkHttpClient();
    final String TAG = "test";

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
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
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");

        getCurrentWeather();

        binding.textViewCityName.setText(mCity.getCity() + ", " + mCity.getCountry());

        binding.buttonCheckForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWeatherListener.checkForecast(mCity);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CurrentWeatherListener) {
            currentWeatherListener = (CurrentWeatherListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CurrentWeatherListener");
        }
    }

    CurrentWeatherListener currentWeatherListener;

    public interface CurrentWeatherListener {
        void checkForecast(DataService.City mCity);
    }

    public void getCurrentWeather() {

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder()
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

                Log.d(TAG, "onResponse: Start Response");
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray weatherJson = jsonObject.getJSONArray("weather");

                        Weather weather = new Weather();

                        for (int i = 0; i < weatherJson.length(); i++) {
                            JSONObject weatherJsonObject = weatherJson.getJSONObject(i);

                            weather.setId(weatherJsonObject.getInt("id"));
                            weather.setMain(weatherJsonObject.getString("main"));
                            weather.setDescription(weatherJsonObject.getString("description"));
                            weather.setIcon(weatherJsonObject.getString("icon"));
                        }

                        JSONObject jsonObject1 = jsonObject.getJSONObject("main");
                        weather.setTemp(jsonObject1.getDouble("temp"));
                        weather.setTemp_min(jsonObject1.getDouble("temp_min"));
                        weather.setTemp_max(jsonObject1.getDouble("temp_max"));
                        weather.setHumidity(jsonObject1.getInt("humidity"));

                        JSONObject jsonObject2 = jsonObject.getJSONObject("wind");
                        weather.setSpeed(jsonObject2.getDouble("speed"));
                        weather.setDeg(jsonObject2.getDouble("deg"));

                        JSONObject jsonObject3 = jsonObject.getJSONObject("clouds");
                        weather.setCloudiness(jsonObject3.getInt("all"));

                        String urlImage = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@2x.png";

                        displayWeather(weather);
                        Handler mainHandler = new Handler(Looper.getMainLooper());

                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load(urlImage).into(binding.imageViewWeatherIcon);
                            }
                        };
                        mainHandler.post(myRunnable);

                    } catch (JSONException e) {

                    }
                } else {

                }
            }
        });
    }


    public void displayWeather(Weather weather) {
        binding.textViewTemp.setText(weather.getTemp() + " F");
        binding.textViewTempMax.setText(weather.getTemp_max() + " F");
        binding.textViewTempMin.setText(weather.getTemp_min() + " F");
        binding.textViewDesc.setText(weather.getDescription());
        binding.textViewHumidity.setText(weather.getHumidity() + "%");
        binding.textViewWindSpeed.setText(weather.getSpeed() + " miles/hr");
        binding.textViewWindDegree.setText(weather.getDeg() + " degrees");
        binding.textViewCloudiness.setText(weather.getCloudiness() + "%");}
}