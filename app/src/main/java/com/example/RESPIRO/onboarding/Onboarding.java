package com.example.RESPIRO.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;
import com.example.RESPIRO.rilevamento.Monitoraggio;

import java.util.ArrayList;

public class Onboarding extends AppCompatActivity {


    private int paginaCorrente = 1;
    private ImageView slidebar, immagine;
    private TextView titolo, testo;
    private LinearLayout indietroBtn;
    private LinearLayout avantiBtn;
    private Button vaiHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);


        slidebar = (ImageView) findViewById(R.id.slidebar);
        immagine = (ImageView) findViewById(R.id.immagine);
        titolo = (TextView) findViewById(R.id.titolo);
        testo = (TextView) findViewById(R.id.testo);
        indietroBtn = findViewById(R.id.indietroBtn);
        vaiHomeButton = findViewById(R.id.vaiHomeBtn);
        avantiBtn = findViewById(R.id.avantiBtn);
    }

    public void vaiAvanti(View view) {
        switch (paginaCorrente) {
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidebar.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.ic_slidebar_2_su_3));
                    immagine.setImageResource(R.drawable.ic_onboarding2);
                }
                indietroBtn.setVisibility(View.VISIBLE);
                avantiBtn.setVisibility(View.VISIBLE);
                titolo.setText("Cos'è RESPIRO.");
                testo.setText("è un progetto che mira al miglioramento \n " +
                        "della qualità dell'aria che respiriamo, in particolare nell'Agro Nolano.");
                paginaCorrente++;
                break;

            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidebar.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.ic_slidebar_3_su_3));
                    immagine.setImageResource(R.drawable.ic_onboarding3);
                }

                indietroBtn.setVisibility(View.VISIBLE);
                vaiHomeButton.setVisibility(View.VISIBLE);
                avantiBtn.setVisibility(View.INVISIBLE);
                titolo.setText("Misura tu stesso.");
                testo.setText("Effettua dei monitoraggi\ne aiutaci a raccogliere dati\nper rendere più pulita la tua città.");
                paginaCorrente++;
                break;

            case 3: break;
        }
    }

    public void vaiIndietro(View view) {
        switch (paginaCorrente) {
            case 1:
                indietroBtn.setClickable(false);
                vaiHomeButton.setVisibility(View.INVISIBLE);
                avantiBtn.setVisibility(View.VISIBLE);
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidebar.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.ic_slidebar_1_su_3));
                    immagine.setImageResource(R.drawable.ic_benvenuto_onboarding);
                }
                indietroBtn.setVisibility(View.INVISIBLE);
                vaiHomeButton.setVisibility(View.INVISIBLE);
                avantiBtn.setVisibility(View.VISIBLE);
                titolo.setText("Benvenuto!");
                testo.setText("Siamo felici di averti qui.\n Concedici qualche istante \n per presentarci.");
                paginaCorrente--;
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    slidebar.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.ic_slidebar_2_su_3));
                    immagine.setImageResource(R.drawable.ic_onboarding2);
                }
                indietroBtn.setVisibility(View.VISIBLE);
                vaiHomeButton.setVisibility(View.INVISIBLE);
                avantiBtn.setVisibility(View.VISIBLE);
                titolo.setText("Cos'è RESPIRO");
                testo.setText("è un progetto che mira al miglioramento \n " +
                        "della qualità dell'aria che respiriamo, in particolare nell'Agro Nolano.");
                paginaCorrente--;
                break;
        }
    }

    public void vaiHome(View view) {

        ArrayList<Monitoraggio> monitoraggiUser = (ArrayList<Monitoraggio>) getIntent().getSerializableExtra("monitoraggiUser");
        Intent i = new Intent();
        i.setClass(getApplicationContext(), HomeActivity.class);
        i.putExtra("monitoraggiUser", monitoraggiUser);
        startActivity(i);
    }
    }
