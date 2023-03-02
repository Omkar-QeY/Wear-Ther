package com.example.afinal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageButton go;
    EditText editText;
    Button search;
    ImageView imageView, backIV;
    TextView country, time, deg, cityname, forecast, pressure, humidity, wind, recommend;
    TextToSpeech tts;
    ImageButton mic, searchmic;

    private static final int RECOGNIZER_RESULT = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editText.setText(matches.get(0).toString());
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();

        go = findViewById(R.id.button);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        editText = findViewById(R.id.city);
        search = findViewById(R.id.search);
        imageView = findViewById(R.id.image);
        time = findViewById(R.id.time);
        deg = findViewById(R.id.deg);

        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        country = findViewById(R.id.country);
        cityname = findViewById(R.id.cityname);
        forecast = findViewById(R.id.forecast);
        recommend = findViewById(R.id.recommend);

        backIV = findViewById(R.id.idIVBack);

        mic = findViewById(R.id.mic);

        searchmic = findViewById(R.id.searchmic);

        searchmic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);
            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS){
                            tts.setLanguage(Locale.US);
                            tts.setSpeechRate(1.0f);
                            tts.speak(recommend.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindWeather();
            }
        });

    }

    public void FindWeather()
    {
        final String city = editText.getText().toString();
        String key = "0d158a89ad784f69a3f75149230203";

        String url = "http://api.weatherapi.com/v1/forecast.json?key=\"+key+\"&q=\"+city+\"&days=1&aqi=yes&alerts=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Double n = null;

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject object = jsonObject.getJSONObject("current");
                    double temp = object.getDouble("temp_c");
                    deg.setText(temp+"°C");

                    JSONObject object1 = jsonObject.getJSONObject("location");
                    String count = object1.getString("country");
                    country.setText(count);

                    String city = object1.getString("name");

                    int isDay = jsonObject.getJSONObject("current").getInt("is_day");

                    if (isDay==1) {
                        //morning
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/459/504/HD-wallpaper-forest-lion-morning-sunrise-illustration-lion-animals-artist-artwork-digital-art-illustration-behance.jpg").into(backIV);
                    }else{
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/142/463/HD-wallpaper-minimal-morning-landscape-8k-minimalism-minimalist-artist-artwork-digital-art-morning-sunrise-mountains.jpg").into(backIV);
                    }

                    JSONObject obj = jsonObject.getJSONObject("current").getJSONObject("condition");
                    String icon = obj.getString("icon");
                    Picasso.get().load("http:".concat(icon)).into(imageView);

                    JSONObject object0 = jsonObject.getJSONObject("location");
                    String date_find = object0.getString("localtime");
                    time.setText(date_find);

                    JSONObject object4 = jsonObject.getJSONObject("current");
                    int humidity_find = object4.getInt("humidity");
                    humidity.setText(humidity_find+"  %");

                    JSONObject object7 = jsonObject.getJSONObject("current");
                    String pressure_find = object7.getString("pressure_mb");
                    pressure.setText(pressure_find+"  hPa");

                    JSONObject object9 = jsonObject.getJSONObject("current");
                    String wind_find = object9.getString("wind_kph");
                    wind.setText(wind_find+"  km/h");

                    JSONObject fobject = jsonObject.getJSONObject("current").getJSONObject("condition");
                    String description = fobject.getString("text");
                    forecast.setText(description);

                    JSONObject fobject2 = jsonObject.getJSONObject("current").getJSONObject("condition");
                    String id1 = fobject2.getString("code");
                    int newid = Integer.parseInt(id1);

                    n=jsonObject.getJSONObject("current").getDouble("temp_c");

                    //thunderstorm
                    if(newid>=1273 && newid<=1282) {
                        recommend.setText("Be prepared to protect yourself with a hat and sunglasses also take Rain gear");
                    }
                    //drizzle
                    else if(newid>=1150 && newid<=1171) {
                        recommend.setText("Wear rain jacket and quick-drying fabrics like nylon.");
                    }

                    //rain
                    else if(newid>=1172 && newid<=1201) {
                        recommend.setText("Equip Rain gear and quick-drying fabrics like nylon. Avoid cotton clothes.");
                    }
                    //snow
                    else if(newid>=1202 && newid<=1237) {
                        recommend.setText("A winter coat, gloves, warm hat and scarf. It is cold, so keep warm enough.");

                    }
                    //atmosphere
                    else if(newid>=701 && newid<=781) {
                        recommend.setText("Wear a light t-shirt or tank top under a sweater or cardigan.");
                    }
                    //clear sky
                    else if(newid==1000) {
                        recommend.setText("Wear breathable light-colored fabrics like cotton, linen, and jersey to stay cool and attract the least heat.");
                    }
                    //scattered clouds
                    else if(newid>=1001 && newid<=1006) {
                        recommend.setText("A jacket or light coat will be comfortable.");
                    }
                    //overcast clouds
                    else if(newid>=1007 && newid<=1029) {
                        recommend.setText("A long-sleeved shirt, and a light cardigan or light sweater is necessary.");
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Enter correct city",Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
    public void checkConnection(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null != activeNetwork){

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){

                Toast.makeText(this, "Wifi Enabled", Toast.LENGTH_SHORT).show();

            } if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){

                Toast.makeText(this, "Mobile Data Enabled", Toast.LENGTH_SHORT).show();

            }
        }
        else {

            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }
    }

}
