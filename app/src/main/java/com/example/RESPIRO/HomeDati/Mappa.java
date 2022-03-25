package com.example.RESPIRO.HomeDati;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.RESPIRO.DB.MonitoraggiDB;
import com.example.RESPIRO.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.RESPIRO.HomeDati.HomeActivity.paesi;
import static com.example.RESPIRO.HomeDati.HomeActivity.posMappa;

public class Mappa extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private TextView pos;
    private ImageView miaPos;

    private MapView mappa = null;
    private RotationGestureOverlay mRotationGestureOverlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_mappa);

        pos = findViewById(R.id.scegliPaese);
        miaPos = findViewById(R.id.miaPos);
        mappa = (MapView) findViewById(R.id.mappa);
        mappa.setTileSource(TileSourceFactory.MAPNIK);

        requestPermissionsIfNecessary(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});

        Intent i = getIntent();
        HashMap<String, Coordinate> coordinate = (HashMap<String, Coordinate>) i.getSerializableExtra("coordinate");
        String posizione = i.getStringExtra("posizione");

        //SET POSIZIONE INIZIALE
        IMapController mapController = mappa.getController();
        mapController.setZoom(16.5);
        GeoPoint startPoint = new GeoPoint(coordinate.get(posizione).getLatitudine(), coordinate.get(posizione).getLongitudine());
        mapController.setCenter(startPoint);

        //per ruotare con due dita
        mRotationGestureOverlay = new RotationGestureOverlay(mappa);
        mRotationGestureOverlay.setEnabled(true);
        mappa.setMultiTouchControls(true);
        mappa.getOverlays().add(this.mRotationGestureOverlay);

        setDefaultPos(posizione, coordinate, mapController);

        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scegliPaeseMappa(coordinate, mapController);
            }
        });

        miaPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("customData", MODE_PRIVATE);
                String residenza = sharedPreferences.getString("residenza","");
                setDefaultPos(residenza, coordinate, mapController);
            }
        });
        MonitoraggiDB.popolaMappa(mappa, this);

    }

    private void setDefaultPos(String posizione, HashMap<String, Coordinate> coordinate, IMapController mapController) {
        pos.setText(posizione);
        Coordinate c = coordinate.get(posizione);
        posMappa(c.getLatitudine(), c.getLongitudine(), mapController);
    }


    public void scegliPaeseMappa(HashMap<String, Coordinate> coordinate,IMapController mapController ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scegli un paese e visualizza i suoi dati.");

        builder.setItems(paesi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Nola
                        pos.setText("Nola");
                        posMappa(coordinate.get("Nola").getLatitudine(), coordinate.get("Nola").getLongitudine(), mapController);
                        break;
                    case 1: // Camposano
                        pos.setText("Camposano");
                        posMappa(coordinate.get("Camposano").getLatitudine(), coordinate.get("Camposano").getLongitudine(), mapController);
                        break;
                    case 2: // Carbonara di Nola
                        pos.setText("Carbonara di Nola");
                        posMappa(coordinate.get("Carbonara di Nola").getLatitudine(), coordinate.get("Carbonara di Nola").getLongitudine(), mapController);
                        break;
                    case 3: // Casamarciano
                        pos.setText("Casamarciano");
                        posMappa(coordinate.get("Casamarciano").getLatitudine(), coordinate.get("Casamarciano").getLongitudine(), mapController);
                        break;
                    case 4: // Cicciano
                        pos.setText("Cicciano");
                        posMappa(coordinate.get("Cicciano").getLatitudine(), coordinate.get("Cicciano").getLongitudine(), mapController);
                        break;
                    case 5: // Cimitile
                        pos.setText("Cimitile");
                        posMappa(coordinate.get("Cimitile").getLatitudine(), coordinate.get("Cimitile").getLongitudine(), mapController);
                        break;
                    case 6: // Comiziano
                        pos.setText("Comiziano");
                        posMappa(coordinate.get("Comiziano").getLatitudine(), coordinate.get("Comiziano").getLongitudine(), mapController);
                        break;
                    case 7: // Liveri
                        pos.setText("Liveri");
                        posMappa(coordinate.get("Liveri").getLatitudine(), coordinate.get("Liveri").getLongitudine(), mapController);
                        break;
                    case 8: // Mariglianella
                        pos.setText("Mariglianella");
                        posMappa(coordinate.get("Mariglianella").getLatitudine(), coordinate.get("Mariglianella").getLongitudine(), mapController);
                        break;
                    case 9: // Marigliano
                        pos.setText("Marigliano");
                        posMappa(coordinate.get("Marigliano").getLatitudine(), coordinate.get("Marigliano").getLongitudine(), mapController);
                        break;
                    case 10: // Palma Campania
                        pos.setText("Palma Campania");
                        posMappa(coordinate.get("Palma Campania").getLatitudine(), coordinate.get("Palma Campania").getLongitudine(), mapController);
                        break;
                    case 11: // Roccarainola
                        pos.setText("Roccarainola");
                        posMappa(coordinate.get("Roccarainola").getLatitudine(), coordinate.get("Roccarainola").getLongitudine(), mapController);
                        break;
                    case 12: // San Paolo Bel Sito
                        pos.setText("San Paolo Bel Sito");
                        posMappa(coordinate.get("San Paolo Bel Sito").getLatitudine(), coordinate.get("San Paolo Bel Sito").getLongitudine(), mapController);
                        break;
                    case 13: // San Vitaliano
                        pos.setText("San Vitaliano");
                        posMappa(coordinate.get("San Vitaliano").getLatitudine(), coordinate.get("San Vitaliano").getLongitudine(), mapController);
                        break;
                    case 14: // Saviano
                        pos.setText("Saviano");
                        posMappa(coordinate.get("Saviano").getLatitudine(), coordinate.get("Saviano").getLongitudine(), mapController);
                        break;
                    case 15: // Scisciano
                        pos.setText("Scisciano");
                        posMappa(coordinate.get("Scisciano").getLatitudine(), coordinate.get("Scisciano").getLongitudine(), mapController);
                        break;
                    case 16: // Tufino
                        pos.setText("Tufino");
                        posMappa(coordinate.get("Tufino").getLatitudine(), coordinate.get("Tufino").getLongitudine(), mapController);
                        break;
                    case 17: // Visciano
                        pos.setText("Visciano");
                        posMappa(coordinate.get("Visciano").getLatitudine(), coordinate.get("Visciano").getLongitudine(), mapController);
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


    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mappa.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mappa.onPause();  //needed for compass, my location overlays, v6.0.0 and up
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


}