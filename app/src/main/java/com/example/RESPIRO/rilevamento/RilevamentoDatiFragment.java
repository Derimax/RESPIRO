package com.example.RESPIRO.rilevamento;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.RESPIRO.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RilevamentoDatiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RilevamentoDatiFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private InviaDati inviaDati;
    private TextView kmLive;
    private TextView pmLive;
    private TextView pm10Live;
    Chronometer tempoLive;
    ArrayList<Double> valoriPM25 = new ArrayList<>();
    ArrayList<Double> valoriPM10 = new ArrayList<>();
    long durata = 0;

    public RilevamentoDatiFragment() {}

    // Interface - fragment to activity
    public interface InviaDati {
        void durataDalFragment(long durata);
        void pm25DalFragment(ArrayList<Double> valoriPM25);
        void pm10DalFragment(ArrayList<Double> valoriPM10);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        inviaDati = (InviaDati) context;
    }


    public static RilevamentoDatiFragment newInstance(String param1, String param2) {
        RilevamentoDatiFragment fragment = new RilevamentoDatiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rilevamento_dati, container, false);
        kmLive = v.findViewById(R.id.kmLive);
        tempoLive = (Chronometer) v.findViewById(R.id.tempoLive);
        pmLive = v.findViewById(R.id.pmLive);
        pm10Live = v.findViewById(R.id.pm10Live);

        return v;

    }

    public void riceviDatiLive(String dati) {
        //il formato della stringa è valorepm2.5-valorepm10

        String[] parts = dati.split("-", 2);
        String pmLiveString = parts[0];
        String pm10LiveString= parts[1];

        valoriPM10.add(Double.parseDouble(pm10LiveString));
        Log.i("info", "ho aggiunto un valore pm10");
        valoriPM25.add(Double.parseDouble(pmLiveString));
        Log.i("info", "ho aggiunto un valore pm2.5");

        inviaDati.pm10DalFragment(valoriPM10);
        inviaDati.pm25DalFragment(valoriPM25);

        pmLive.setText(pmLiveString);
        pm10Live.setText(pm10LiveString);

    }

    public void kmPercorsi (int distance) {
        kmLive.setText(Integer.toString(distance));
    }

    public void startTime() {
        tempoLive.setBase(SystemClock.elapsedRealtime());
        tempoLive.start();
    }

    public void stopTime() {
        tempoLive.stop();
    }

    public void getTime() {
        durata = SystemClock.elapsedRealtime() - tempoLive.getBase();
        inviaDati.durataDalFragment(durata);
    }

}