package com.example.RESPIRO.profilo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;
import com.example.RESPIRO.eventi.EventiActivity;
import com.example.RESPIRO.impostazioni.ImpostazioniActivity;
import com.example.RESPIRO.info.InfoActivity;
import com.example.RESPIRO.rilevamento.CheckRilevamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfiloActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private ImageView datiMenu;
    private ImageView calendarioMenu;
    private ImageView classificaMenu;
    private ImageView datiMenuicona;
    private ImageView calendarioMenuicona;
    private ImageView classificaMenuicona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilo);


        calendarioMenu = findViewById(R.id.calendarioMenu);
        classificaMenu = findViewById(R.id.classificaMenu);
        datiMenu =findViewById(R.id.datiMenu);
        calendarioMenuicona = findViewById(R.id.calendarioMenuicona);
        classificaMenuicona = findViewById(R.id.classificaMenuicona);
        datiMenuicona =findViewById(R.id.datiMenuicona);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profiloMenu);



        //Fragment dei dati principali mostrato per default
        classificaMenu.setVisibility(View.INVISIBLE);
        calendarioMenu.setVisibility(View.INVISIBLE);
        profilo_fragment1 fragment = new profilo_fragment1();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);

        fragmentTransaction.commit();



        //per passare da una schermata all'altra usando il menu posto in alto nella pagina del profilo

        datiMenuicona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datiMenu.setVisibility(View.VISIBLE);
                classificaMenu.setVisibility(View.INVISIBLE);
                calendarioMenu.setVisibility(View.INVISIBLE);
                profilo_fragment1 fragment = new profilo_fragment1();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, fragment);
                fragmentTransaction.commit();

            }
        });

        calendarioMenuicona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datiMenu.setVisibility(View.INVISIBLE);
                classificaMenu.setVisibility(View.INVISIBLE);
                calendarioMenu.setVisibility(View.VISIBLE);
                profilo_fragment2 fragment = new profilo_fragment2();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, fragment);
                fragmentTransaction.commit();
            }
        });

        classificaMenuicona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datiMenu.setVisibility(View.INVISIBLE);
                classificaMenu.setVisibility(View.VISIBLE);
                calendarioMenu.setVisibility(View.INVISIBLE);
                profilo_fragment3 fragment = new profilo_fragment3();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, fragment);
                fragmentTransaction.commit();
            }
        });


        /*LineDataSet lineDataSet = new LineDataSet(dataValues(), "Data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();*/


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

                        return true;

                    case R.id.eventiMenu:
                        startActivity(new Intent(getApplicationContext(), EventiActivity.class));
                        overridePendingTransition(0,0);
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onFabClick(View view) {
        Intent intent = new Intent(ProfiloActivity.this, CheckRilevamento.class);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    public void vaiImpostazioni(View view) {
        startActivity(new Intent(this, ImpostazioniActivity.class));
    }


    /*private ArrayList<Entry> dataValues() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0,20));
        dataVals.add(new Entry(1,30));
        dataVals.add(new Entry(2,10));
        dataVals.add(new Entry(3,24));
        return dataVals;*/
    }



