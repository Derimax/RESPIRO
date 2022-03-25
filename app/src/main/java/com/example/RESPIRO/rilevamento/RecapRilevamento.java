package com.example.RESPIRO.rilevamento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.RESPIRO.DB.MonitoraggiDB;
import com.example.RESPIRO.R;
import com.example.RESPIRO.profilo.ProfiloActivity;

import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;

public class RecapRilevamento extends AppCompatActivity {

    private TextView distanzaRecap,durataRecap, valoreMedioPM25, valoreMedioPM10, puntiAccumulati;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap_rilevamento);

        Intent i = getIntent();

        int distanza = i.getIntExtra("distanza", 0);
        long durata = i.getLongExtra("durata", 0);
        double mediaPM25 = i.getDoubleExtra("valoreMedioPM25", 0);
        double mediaPM10 = i.getDoubleExtra("valoreMedioPM10", 0);
        String luogo = i.getStringExtra("luogo");
        double lat = i.getDoubleExtra("latitudine", 0);
        double lon = i.getDoubleExtra("longitudine", 0);


        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String dataMonitoraggio = dateFormat.format(date);
        Log.i("AVVISO", "Data monitoraggio: "+dataMonitoraggio);

        String durataFormattata = formattaTempo(durata);

        int punti = calcolaPunti(distanza, durata);

        distanzaRecap = findViewById(R.id.metriPercorsiRecap);
        durataRecap = findViewById(R.id.durataRecap);
        valoreMedioPM10 = findViewById(R.id.valoreMedioPm10);
        valoreMedioPM25 = findViewById(R.id.valoreMedioPm25);
        puntiAccumulati = findViewById(R.id.puntiAccumulati);

        distanzaRecap.setText(Integer.toString(distanza)+ " m");
        durataRecap.setText(durataFormattata);
        valoreMedioPM10.setText(Double.toString(round(mediaPM10,2))+" (µg/m3)");
        valoreMedioPM25.setText(Double.toString(round(mediaPM25,2))+" (µg/m3)");
        puntiAccumulati.setText(Integer.toString(punti));

        Monitoraggio monitoraggio = new Monitoraggio();
        monitoraggio.setDataMonitoraggio(dataMonitoraggio);
        monitoraggio.setEseguitoDa(app.currentUser().getId());
        monitoraggio.setDistanza(Long.valueOf(distanza));
        monitoraggio.setDurata(durata);
        monitoraggio.setPunti(Long.valueOf(punti));
        monitoraggio.setMediaPM10(mediaPM10);
        monitoraggio.setMediaPM25(mediaPM25);
        monitoraggio.set_id(new ObjectId());
        monitoraggio.setMonitoraggiPartition("MonitoraggiPartition");
        monitoraggio.setLuogo(luogo);
        monitoraggio.setLatitudine(lat);
        monitoraggio.setLongitudine(lon);

        MonitoraggiDB.inserisciMonitoraggio(monitoraggio);
    }


    public static int calcolaPunti(int distanza, long durata) {
        int d = (int) durata;
        int punti = d/60000 + distanza/100 + 10;
        return punti;
    }

    public static String formattaTempo(long durata) {
        long second = (durata / 1000) % 60;
        long minute = (durata / (1000 * 60)) % 60;
        long hour = (durata / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void vaiProfilo(View v) {
        startActivity(new Intent(RecapRilevamento.this, ProfiloActivity.class));
    }

}