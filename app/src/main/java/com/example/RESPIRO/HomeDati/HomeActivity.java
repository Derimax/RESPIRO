package com.example.RESPIRO.HomeDati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.RESPIRO.DB.MonitoraggiDB;
import com.example.RESPIRO.R;
import com.example.RESPIRO.eventi.EventiActivity;
import com.example.RESPIRO.impostazioni.ImpostazioniActivity;
import com.example.RESPIRO.info.InfoActivity;
import com.example.RESPIRO.profilo.ProfiloActivity;
import com.example.RESPIRO.rilevamento.CheckRilevamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private Button vaiMappa;

    private TextView pos;
    private TextView mediapm10;
    private TextView mediapm25;
    private TextView consiglio;

    private CustomGauge progressPm10;
    private CustomGauge progressPm25;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;

    String posizione ="";


    HashMap<String, Coordinate> coordinate = new HashMap<String, Coordinate>();
    public static String[] paesi = {"Nola", "Camposano", "Carbonara di Nola",
            "Casamarciano", "Cicciano", "Cimitile", "Comiziano", "Liveri",
            "Mariglianella", "Marigliano", "Palma Campania", "Roccarainola", "San Paolo Bel Sito",
            "San Vitaliano", "Saviano", "Scisciano", "Tufino", "Visciano"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_home);

        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { return true;}
        });

        pos = findViewById(R.id.pos);
        mediapm10 = findViewById(R.id.mediapm10);
        mediapm25 = findViewById(R.id.mediapm25);
        consiglio = findViewById(R.id.consiglio);
        progressPm10 = findViewById(R.id.progressPm10);
        progressPm25 = findViewById(R.id.progressPm25);
        vaiMappa = findViewById(R.id.vaiMappa);

        //SET POSIZIONE INIZIALE
        IMapController mapController = map.getController();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.datiMenu);

        coordinate.put("Nola", new Coordinate(40.92693656976596, 14.529772088427803));
        coordinate.put("Camposano", new Coordinate(40.95357938286446, 14.529303053493418));
        coordinate.put("Carbonara di Nola", new Coordinate(40.875497631208056, 14.577296220865387));
        coordinate.put("Casamarciano", new Coordinate(40.932311311676045, 14.550371644779549));
        coordinate.put("Cicciano", new Coordinate(40.96311173345301, 14.53869806168918));
        coordinate.put("Cimitile", new Coordinate(40.94137528480474, 14.527240018066445));
        coordinate.put("Comiziano", new Coordinate(40.951673925897985, 14.550945155628495));
        coordinate.put("Liveri", new Coordinate(40.904059281854536, 14.565888902090117));
        coordinate.put("Mariglianella", new Coordinate(40.92888482771809, 14.437415382869325));
        coordinate.put("Marigliano", new Coordinate(40.92319495307001, 14.454778146982818));
        coordinate.put("Palma Campania", new Coordinate(40.86778481881571, 14.551614798595761));
        coordinate.put("Roccarainola", new Coordinate(40.97200099673764, 14.559952418547583));
        coordinate.put("San Paolo Bel Sito", new Coordinate(40.91618551112939, 14.545528099039846));
        coordinate.put("San Vitaliano", new Coordinate(40.925369022605786, 14.48031176385631));
        coordinate.put("Saviano", new Coordinate(40.90852851234383, 14.510758697850873));
        coordinate.put("Scisciano", new Coordinate(40.91577442139605, 14.481272630219454));
        coordinate.put("Tufino", new Coordinate(40.955491663196675, 14.565388338087962));
        coordinate.put("Visciano", new Coordinate(40.92470849224604, 14.583511563019853));

        Log.i("AVVISO",""+coordinate.toString());
        setDefaultPosition(mapController);

        MonitoraggiDB.popolaMappaHome(map,this, posizione);

        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scegliPaese(mapController);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.datiMenu:

                        return true;

                    case R.id.profiloMenu:
                        Intent intent = new Intent(getApplicationContext(), ProfiloActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.eventiMenu:
                        startActivity(new Intent(getApplicationContext(), EventiActivity.class));
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.infoMenu:
                        startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onFabClick(View view) {
        Intent intent = new Intent(HomeActivity.this, CheckRilevamento.class);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    public void vaiImpostazioni(View view) {
        startActivity(new Intent(this, ImpostazioniActivity.class));
    }

    public void scegliPaese(IMapController mapController) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scegli un paese e visualizza i suoi dati.");

        builder.setItems(paesi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Nola
                        posizione = "Nola";
                        pos.setText("Nola");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Nola",mediapm10,mediapm25,consiglio,progressPm10, progressPm25);
                        posMappa(coordinate.get("Nola").getLatitudine(), coordinate.get("Nola").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 1: // Camposano
                        posizione = "Camposano";
                        pos.setText("Camposano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Camposano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Camposano").getLatitudine(), coordinate.get("Camposano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 2: // Carbonara di Nola
                        posizione = "Carbonara di Nola";
                        pos.setText("Carbonara di Nola");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Carbonara di Nola",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Carbonara di Nola").getLatitudine(), coordinate.get("Carbonara di Nola").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 3: // Casamarciano
                        posizione = "Casamarciano";
                        pos.setText("Casamarciano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Casamarciano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Casamarciano").getLatitudine(), coordinate.get("Casamarciano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 4: // Cicciano
                        posizione = "Cicciano";
                        pos.setText("Cicciano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Cicciano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Cicciano").getLatitudine(), coordinate.get("Cicciano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 5: // Cimitile
                        posizione = "Cimitile";
                        pos.setText("Cimitile");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Cimitile",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Cimitile").getLatitudine(), coordinate.get("Cimitile").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 6: // Comiziano
                        posizione = "Comiziano";
                        pos.setText("Comiziano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Comiziano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Comiziano").getLatitudine(), coordinate.get("Comiziano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 7: // Liveri
                        posizione = "Liveri";
                        pos.setText("Liveri");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Liveri",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Liveri").getLatitudine(), coordinate.get("Liveri").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 8: // Mariglianella
                        posizione = "Mariglianella";
                        pos.setText("Mariglianella");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Mariglianella",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Mariglianella").getLatitudine(), coordinate.get("Mariglianella").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 9: // Marigliano
                        posizione = "Marigliano";
                        pos.setText("Marigliano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Marigliano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Marigliano").getLatitudine(), coordinate.get("Marigliano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 10: // Palma Campania
                        posizione = "Palma Campania";
                        pos.setText("Palma Campania");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Palma Campania",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Palma Campania").getLatitudine(), coordinate.get("Palma Campania").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 11: // Roccarainola
                        posizione = "Roccarainola";
                        pos.setText("Roccarainola");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Roccarainola",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Roccarainola").getLatitudine(), coordinate.get("Roccarainola").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 12: // San Paolo Bel Sito
                        posizione = "San Paolo Bel Sito";
                        pos.setText("San Paolo Bel Sito");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("San Paolo Bel Sito",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("San Paolo Bel Sito").getLatitudine(), coordinate.get("San Paolo Bel Sito").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 13: // San Vitaliano
                        posizione = "San Vitaliano";
                        pos.setText("San Vitaliano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("San Vitaliano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("San Vitaliano").getLatitudine(), coordinate.get("San Vitaliano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 14: // Saviano
                        posizione = "Saviano";
                        pos.setText("Saviano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Saviano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Saviano").getLatitudine(), coordinate.get("Saviano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 15: // Scisciano
                        posizione = "Scisciano";
                        pos.setText("Scisciano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Scisciano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Scisciano").getLatitudine(), coordinate.get("Scisciano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 16: // Tufino
                        posizione = "Tufino";
                        pos.setText("Tufino");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Tufino",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Tufino").getLatitudine(), coordinate.get("Tufino").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    case 17: // Visciano
                        posizione = "Visciano";
                        pos.setText("Visciano");
                        MonitoraggiDB.trovaMonitoraggiPerLuogo("Visciano",mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
                        posMappa(coordinate.get("Visciano").getLatitudine(), coordinate.get("Visciano").getLongitudine(), mapController);
                        MonitoraggiDB.popolaMappaHome(map,getApplicationContext(), posizione);
                        break;
                    default:
                        dialog.cancel();
                        break;
                }
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setDefaultPosition(IMapController mapController) {
        SharedPreferences sharedPreferences = getSharedPreferences("customData", MODE_PRIVATE);
        String residenza = sharedPreferences.getString("residenza","");
        pos.setText(residenza);
        posizione = residenza;
        Log.i("AVVISO", "residenza: "+posizione);
        Coordinate c = new Coordinate(coordinate.get(residenza).getLatitudine(),coordinate.get(residenza).getLongitudine());
        Log.i("AVVISO",""+c.getLatitudine()+c.getLongitudine());
        posMappa(c.getLatitudine(), c.getLongitudine(), mapController);
        MonitoraggiDB.trovaMonitoraggiPerLuogo(residenza,mediapm10,mediapm25,consiglio, progressPm10, progressPm25);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void requestPermissionsIfNecessary(@NonNull String[] permissions) {
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

    public static void posMappa(double latitudine, double longitudine, @NonNull IMapController mapController) {
        mapController.setZoom(15.5);
        GeoPoint startPoint = new GeoPoint(latitudine, longitudine);
        mapController.animateTo(startPoint);
        mapController.setCenter(startPoint);
    }

    public void vaiMappa(View view) {
        Intent i = new Intent(HomeActivity.this, Mappa.class);
        i.putExtra("coordinate",coordinate);
        i.putExtra("posizione", posizione);
        startActivity(i);
    }
}
