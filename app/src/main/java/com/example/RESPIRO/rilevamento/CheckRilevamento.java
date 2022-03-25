package com.example.RESPIRO.rilevamento;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.RESPIRO.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckRilevamento extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    private Button iniziaRilevamento;
    private TextView posRilevata;
    private TextView attenzione;
    private ImageView posizioneOk;

    private String luogo = "";
    private double lat = 0;
    private double lon = 0;
    private boolean presente = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_rilevamento);

        iniziaRilevamento = findViewById(R.id.iniziaRilevamento);
        posRilevata = findViewById(R.id.posizioneRilevata);
        attenzione = findViewById(R.id.attenzione);
        posizioneOk = findViewById(R.id.posizioneOk);

        ArrayList<String> comuniValidi = new ArrayList<String>() {{
            add("Nola");
            add("Camposano");
            add("Carbonara di Nola");
            add("Casamarciano");
            add("Cicciano");
            add("Cimitile");
            add("Comiziano");
            add("Liveri");
            add("Mariglianella");
            add("Marigliano");
            add("Palma Campania");
            add("Roccarainola");
            add("San Paolo Bel Sito");
            add("San Vitaliano");
            add("Saviano");
            add("Scisciano");
            add("Tufino");
            add("Visciano");
        }};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                },10);
            };
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("AVVISO", "posizione cambiata.");

                    Geocoder geocoder = new Geocoder(CheckRilevamento.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addressList.size() > 0)
                            luogo = addressList.get(0).getLocality();
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            posRilevata.setText(luogo);
                            Log.i("AVVISO", luogo);
                            for(String comune: comuniValidi) {
                                if(comune.equals(luogo)) presente=true;
                                Log.i("AVVISO", "valore presente: "+presente);
                                if(presente){
                                    posizioneOk.setVisibility(View.VISIBLE);
                                    attenzione.setVisibility(View.GONE);
                                    iniziaRilevamento.setEnabled(true);
                                }
                                else {
                                    posizioneOk.setVisibility(View.INVISIBLE);
                                    attenzione.setVisibility(View.VISIBLE);
                                    iniziaRilevamento.setEnabled(false);
                                }
                            }
                            presente=false;


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        locationManager.requestLocationUpdates("gps",500,0,locationListener);
    }

    public void back(View v){
        locationManager.removeUpdates(locationListener);
        finish();
    }

    public void iniziaRilevamento(View v) {
        Intent i = new Intent(CheckRilevamento.this,RilevamentoActivity.class);
        if(luogo.length()==0) Log.i("AVVISO", "luogo mancante.");
        i.putExtra("luogo", luogo);
        i.putExtra("latitudine", lat);
        i.putExtra("longitudine", lon);
        locationManager.removeUpdates(locationListener);
        startActivity(i);
    }


}
