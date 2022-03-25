package com.example.RESPIRO.eventi;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.RESPIRO.DB.EventiDB;
import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;
import com.example.RESPIRO.impostazioni.ImpostazioniActivity;
import com.example.RESPIRO.info.InfoActivity;
import com.example.RESPIRO.profilo.ProfiloActivity;

import com.example.RESPIRO.rilevamento.CheckRilevamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import io.realm.mongodb.App;


public class EventiActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button addEvento;
    private LinearLayout boxEventiInProgramma;
    private LinearLayout boxEventiVicini;
    AlertDialog dialog;

    public static App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi);

        SharedPreferences s = getSharedPreferences("customData",MODE_PRIVATE);
        String residenza = s.getString("residenza", "");
        boolean isAdmin = s.getBoolean("admin", false);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        boxEventiInProgramma = findViewById(R.id.box_eventi_in_programma);
        boxEventiVicini = findViewById(R.id.box_eventi_vicini);
        addEvento = findViewById(R.id.addEvento);

        if(!isAdmin) {
            addEvento.setClickable(false);
            addEvento.setVisibility(View.INVISIBLE);
        }

        EventiDB.MostraEventiVicini(getApplicationContext(),boxEventiVicini, residenza);
        EventiDB.MostraEventi(getApplicationContext(),boxEventiInProgramma);
        buildDialog();

        addEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.eventiMenu);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.datiMenu:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profiloMenu:
                        startActivity(new Intent(getApplicationContext(), ProfiloActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.eventiMenu:
                        return true;

                    case R.id.infoMenu:
                        startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }




    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_evento, null);

        final EditText nomeEventoEdit = (EditText) view.findViewById(R.id.nomeEventoEdit);
        final EditText oraEdit = (EditText) view.findViewById(R.id.oraEdit);
        final EditText dataEdit = (EditText) view.findViewById(R.id.dataEdit);
        final EditText luogoEdit = (EditText) view.findViewById(R.id.luogoEdit);

        builder.setView(view);
        builder.setTitle("Inserisci tutti i campi richiesti.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    aggiungiEvento(nomeEventoEdit.getText().toString(), oraEdit.getText().toString(),
                            dataEdit.getText().toString(), luogoEdit.getText().toString());
            }
        }).setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();

    }

    private void aggiungiEvento(String nomeEvento, String ora, String data, String luogo) {
        EventiDB.InserisciEventoDB(nomeEvento,ora,data,luogo);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onFabClick(View view) {
        Intent intent = new Intent(EventiActivity.this, CheckRilevamento.class);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    public void vaiImpostazioni(View view) {
        startActivity(new Intent(this, ImpostazioniActivity.class));
    }
}