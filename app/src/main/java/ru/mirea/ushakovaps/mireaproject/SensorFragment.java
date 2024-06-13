package ru.mirea.ushakovaps.mireaproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.ushakovaps.mireaproject.databinding.FragmentSensorBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment implements SensorEventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SensorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SensorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SensorFragment newInstance(String param1, String param2) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private FragmentSensorBinding binding;
    private TextView pressure;
    private TextView height;
    private TextView weather;
    private WebView webView;
    private SensorManager sensorManager;
    private Sensor BarometerSensor;


    @Override
    public  void onAttach(Context context)
    {
        super.onAttach(context);
        binding = FragmentSensorBinding.inflate(getLayoutInflater());

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        BarometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        pressure = view.findViewById(R.id.textViewPressure);
        height = view.findViewById(R.id.textViewHeight);
        weather = view.findViewById(R.id.textViewWeather);
        webView = view.findViewById(R.id.webView);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        new DownloadPageTask().execute("https://ipinfo.io/json");
    }
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, BarometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressureValue = event.values[0];
            float pressureInMmHg = pressureValue * 0.75006375541921f;
            pressure.setText("Нынешнее атмосферное давление: " + pressureInMmHg + "мм рт.ст.");
            float heightValue = pressureInMmHg * 12;
            height.setText("Следовательно, высота над уровнем моря: " + heightValue + "м");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private class DownloadPageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            weather.setText("Загрузка погоды...");
            Log.d(MainActivity.class.getSimpleName(), "Загрузка погоды...");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d(MainActivity.class.getSimpleName(), result);
            try {
                JSONObject responseJson = new JSONObject(result);
                Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
                String loc = responseJson.getString("loc");
                String latitude = loc.split(",")[0];
                String longitude = loc.split(",")[1];
                String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
                Log.d(MainActivity.class.getSimpleName(), url);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(url);
                weather.setText("Погода загружена:");
                Log.d(MainActivity.class.getSimpleName(), "Погода загружена");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        private String downloadIpInfo(String address) throws IOException {
            InputStream inputStream = null;
            String data = "";
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(100000);
                connection.setConnectTimeout(100000);
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read = 0;
                    while ((read = inputStream.read()) != -1) {
                        bos.write(read); }
                    bos.close();
                    data = bos.toString();
                } else {
                    data = connection.getResponseMessage()+". Error Code: " + responseCode;
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return data;
        }
    }
}