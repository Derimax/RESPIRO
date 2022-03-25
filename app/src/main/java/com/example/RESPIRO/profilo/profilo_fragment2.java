package com.example.RESPIRO.profilo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.RESPIRO.DB.MonitoraggiDB;
import com.example.RESPIRO.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class profilo_fragment2 extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button eliminaTutto;
    private Button trovaTuttoRealm;
    private Button eliminaUno;
    private Button aggiornaRealm;


    private TextView mese;
    private LinearLayout elencoMonitoraggiPerData;

    AlertDialog dialog;

    public profilo_fragment2() {
        // Required empty public constructor
    }

    public static profilo_fragment2 newInstance(String param1, String param2) {
        profilo_fragment2 fragment = new profilo_fragment2();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2_profilo, container, false);

        mese = v.findViewById(R.id.mese);
        elencoMonitoraggiPerData = v.findViewById(R.id.elencoMonitoraggiPerData);
        eliminaTutto = v.findViewById(R.id.eliminaTutto);
        trovaTuttoRealm = v.findViewById(R.id.trovaTutti);
        eliminaUno = v.findViewById(R.id.eliminaUno);
        aggiornaRealm = v.findViewById(R.id.aggiornaRealm);

        SharedPreferences s = this.getActivity().getSharedPreferences("customData", Context.MODE_PRIVATE);
        boolean isAdmin = s.getBoolean("admin", false);
        buildDialog();

        if(isAdmin) {
            eliminaTutto.setClickable(true);
            eliminaTutto.setVisibility(View.VISIBLE);
            eliminaTutto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminaTutto();
                }
            });

            trovaTuttoRealm.setClickable(true);
            trovaTuttoRealm.setVisibility(View.VISIBLE);
            trovaTuttoRealm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trovaTutto();
                }
            });

            eliminaUno.setClickable(true);
            eliminaUno.setVisibility(View.VISIBLE);
            eliminaUno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });

            aggiornaRealm.setClickable(true);
            aggiornaRealm.setVisibility(View.VISIBLE);
            aggiornaRealm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aggiornaRealm();
                }
            });
        }

        Date dataOggi = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        String strDate = dateFormat.format(dataOggi);
        mese.setText(strDate);

        CompactCalendarView compactCalendarView = (CompactCalendarView) v.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(TimeZone.getDefault(),Locale.ITALIAN);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        MonitoraggiDB.popolamentoCalendarioMonitoraggi(compactCalendarView);

        //di default vengono mostrati gli eventuali monitoraggi effettuati in data odierna
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String oggi = df.format(dataOggi);
        Log.i("AVVISO","data di oggi: "+oggi);
        try {
            MonitoraggiDB.mostraMonitoraggiPerData(getContext(), oggi, elencoMonitoraggiPerData);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                elencoMonitoraggiPerData.removeAllViewsInLayout();
                Log.i("AVVISO","data cliccata: "+dateClicked);
                DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                String dataMonitoraggio = dateFormat.format(dateClicked);
                Log.i("AVVISO","data passata: "+dataMonitoraggio);
                try {
                    MonitoraggiDB.mostraMonitoraggiPerData(getActivity().getApplicationContext(), dataMonitoraggio,elencoMonitoraggiPerData);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                DateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
                String m = dateFormat.format(firstDayOfNewMonth);
                mese.setText(m);
            }
        });
        return v;
    }

    private void aggiornaRealm() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Tutti i monitoraggi del realm saranno eliminati e aggiornati con quelli presenti nel db. Continuare?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonitoraggiDB.aggiornaMonitoraggiRealm();
                        Toast.makeText(getContext(), "Aggiornati tutti i monitoraggi del realm.", Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                "annulla",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void eliminaTutto() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Sei sicuro di voler eliminare tutti i monitoraggi? l'operazione è irreversibile.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonitoraggiDB.eliminaTuttiMonitoraggi();
                        Toast.makeText(getContext(), "Eliminati tutti i monitoraggi.", Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                "annulla",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void trovaTutto() {
        MonitoraggiDB.trovaTuttiMonitoraggiRealm();
    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_monitoraggio, null);

        final EditText idEdit = (EditText) view.findViewById(R.id.idMonitoraggio);

        builder.setView(view);
        builder.setTitle("Inserisci l'id del monitoraggio da eliminare.").setPositiveButton("elimina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = idEdit.getText().toString();
                Log.i("AVVISO", "ObjectId: "+id);
                dialog.cancel();
                MonitoraggiDB.eliminaUnMonitoraggioRealm(id);
            }
        }).setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();

    }






    public static void removeView(View view) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
    }

}