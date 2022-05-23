package com.example.RESPIRO.rilevamento;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.List;

public class RilevamentoActivity extends AppCompatActivity implements RilevamentoDatiFragment.InviaDati{
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    int distanza = 0;

    private MapView map = null;
    private Button inizia;
    private Button termina;
    private RotationGestureOverlay mRotationGestureOverlay;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private long durataMonitoraggio;
    private ArrayList<Double> valoriPM25registrati;
    private ArrayList<Double> valoriPM10registrati;


    private final String MQTTHOST = "tcp://broker.hivemq.com:1883";
    private final String USERNAME = "Username";
    private final String PASSWORD = "Password1";
    String topicStr = "test/topic";
    MqttAndroidClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP UserApp Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_rilevamento);

        inizia = findViewById(R.id.inizia);
        termina = findViewById(R.id.termina);
        map = findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        RilevamentoDatiFragment rilevamentoDatiFragment = new RilevamentoDatiFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameRilevamento, rilevamentoDatiFragment);
        fragmentTransaction.commit();

        //SET POSIZIONE INIZIALE
        IMapController mapController = map.getController();
        mapController.setZoom(15.5);
        GeoPoint startPoint = new GeoPoint(40.923, 14.5338);
        mapController.setCenter(startPoint);

        //per ruotare con due dita
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(this.mRotationGestureOverlay);


        //per ottenere la posizione tramite GPS
        List<GeoPoint> geoPoints = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {

                Polyline line = new Polyline();
                line.getOutlinePaint().setColor(Color.BLUE);
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.animateTo(point);
                mapController.setZoom(18.5);
                geoPoints.add(point);
                line.setPoints(geoPoints);
                distanza = (int) line.getDistance();
                kmPercorsi(distanza);
                map.getOverlayManager().add(line);
                map.invalidate();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                },10);
            }
            return;
        } else {
            inizia();
        }

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }

    public void annullaRilevamento (View v) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Sei sicuro di voler tornare indietro? Il monitoraggio non verrà salvato.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(RilevamentoActivity.this, HomeActivity.class));
                    }
                });

        builder1.setNegativeButton(
                "continua",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Sei sicuro di voler tornare indietro? Il monitoraggio non verrà salvato.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(RilevamentoActivity.this, HomeActivity.class));
                    }
                });

        builder1.setNegativeButton(
                "continua",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void kmPercorsi(int distance) {
        FragmentManager manager = getSupportFragmentManager();
        RilevamentoDatiFragment myFragment = (RilevamentoDatiFragment) manager.findFragmentById(R.id.frameRilevamento);
        myFragment.kmPercorsi(distance);
    }

    private void inizia() {
        inizia.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                inizia.setVisibility(View.INVISIBLE);
                inizia.setClickable(false);
                termina.setVisibility(View.VISIBLE);
                termina.setClickable(true);
                stabilisciConnessioneMQTT();
                FragmentManager manager = getSupportFragmentManager();
                RilevamentoDatiFragment myFragment = (RilevamentoDatiFragment) manager.findFragmentById(R.id.frameRilevamento);
                myFragment.startTime();
                locationManager.requestLocationUpdates("gps",
                        500,
                        0,
                        locationListener);
            }
        });
        termina();
    }

    private void termina() {

        termina.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
                if(valoriPM10registrati==null || valoriPM25registrati==null ) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RilevamentoActivity.this);
                    builder1.setMessage("Sembra che il tuo sensore non stia inviando dati correttamente. Controlla che sia tutto apposto e inizia un nuovo rilevamento.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(RilevamentoActivity.this, HomeActivity.class));
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    double mediaPM25 = calcolaMediaPM25(valoriPM25registrati);
                    double mediaPM10 = calcolaMediaPM10(valoriPM10registrati);

                    Intent intent = getIntent();
                    String luogo = intent.getStringExtra("luogo");
                    double lat = intent.getDoubleExtra("latitudine", 0);
                    double lon = intent.getDoubleExtra("longitudine", 0);

                    FragmentManager manager = getSupportFragmentManager();
                    RilevamentoDatiFragment myFragment = (RilevamentoDatiFragment) manager.findFragmentById(R.id.frameRilevamento);
                    myFragment.stopTime();
                    myFragment.getTime();
                    Intent i = new Intent(RilevamentoActivity.this, RecapRilevamento.class);
                    i.putExtra("distanza",distanza);
                    i.putExtra("durata", durataMonitoraggio);
                    i.putExtra("valoreMedioPM25", mediaPM25);
                    i.putExtra("valoreMedioPM10", mediaPM10);
                    i.putExtra("luogo", luogo);
                    i.putExtra("latitudine", lat);
                    i.putExtra("longitudine", lon);
                    disconnettitiMQTT();
                    startActivity(i);
                }
            }
        });

    }

    private Double calcolaMediaPM10(ArrayList<Double> valoriPM10registrati) {
        if(valoriPM10registrati==null) {
            Log.i("AVVISO", "Non ci sono valori di pm 2.5 rilevati.");
        } else {
            double sommaPM10 = 0;
            int numElementi = 0;
            for(double valorePM10 : valoriPM10registrati) {
                sommaPM10+=valorePM10;
                numElementi++;
            }
            return sommaPM10/numElementi;
        }
        return null;
    }

    private Double calcolaMediaPM25(ArrayList<Double> valoriPM25registrati) {
        if(valoriPM25registrati==null) {
            Log.i("AVVISO", "Non ci sono valori di pm 2.5 rilevati.");
        } else {
            double sommaPM25 = 0;
            int numElementi = 0;
            for(double valorePM25 : valoriPM25registrati) {
                sommaPM25+=valorePM25;
                numElementi++;
            }
            return sommaPM25/numElementi;
        }
    return null;
    }

    private void stabilisciConnessioneMQTT(){

        //creo e stabilisco una connessione MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(RilevamentoActivity.this, MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();

        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i("Connessione", "Connessione riuscita!");
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i("Connessione", "Connessione riuscita!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.i("AVVISO", "Connessione persa"+ cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //qui va il textview dove verranno mostrati i dati ricevuti
                FragmentManager manager = getSupportFragmentManager();
                RilevamentoDatiFragment myFragment = (RilevamentoDatiFragment) manager.findFragmentById(R.id.frameRilevamento);
                String messaggio = new String(message.getPayload());
                myFragment.riceviDatiLive(messaggio);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    private void disconnettitiMQTT() {
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("Disconnessione", "Disconnesso!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("Disconnessione", "disconnessione fallita.");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void setSubscription() {
        try {
            client.subscribe(topicStr, 0);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
        switch (requestCode) {
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    inizia();
                return;
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    @Override
    public void durataDalFragment(long durata) {
        durataMonitoraggio = durata;
    }

    @Override
    public void pm25DalFragment(ArrayList<Double> valoriPM25) {
        valoriPM25registrati = valoriPM25;
    }

    @Override
    public void pm10DalFragment(ArrayList<Double> valoriPM10) {
        valoriPM10registrati = valoriPM10;
    }
}