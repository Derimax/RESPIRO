package com.example.RESPIRO.profilo;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.RESPIRO.DB.UserDB;
import com.example.RESPIRO.R;

import static android.content.Context.MODE_PRIVATE;
import static com.example.RESPIRO.DB.MonitoraggiDB.elencoMonitoraggiUser;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profilo_fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profilo_fragment1 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView nomeCognomeView;
    private TextView residenzaView;

    private TextView kmPercorsi;
    private TextView numMonitoraggi;
    private TextView oreDedicate;

    private TextView distanzaPercorsaText;
    private TextView oreDedicateText;

    private ProgressBar progressoLivello;
    private TextView livelloTextview;

    private ImageView fotoprofilo;

    AlertDialog dialog;


    public profilo_fragment1() { }

    public static profilo_fragment1 newInstance(String param1, String param2) {
        profilo_fragment1 fragment = new profilo_fragment1();
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

        View v = inflater.inflate(R.layout.fragment1_profilo, container, false);

        nomeCognomeView = v.findViewById(R.id.nome_cognome);
        residenzaView = v.findViewById(R.id.posizione);

        kmPercorsi = v.findViewById(R.id.km_percorsi);
        numMonitoraggi = v.findViewById(R.id.monitoraggi);
        oreDedicate = v.findViewById(R.id.ore_dedicate);

        distanzaPercorsaText = v.findViewById(R.id.distanzaPercorsaText);
        oreDedicateText = v.findViewById(R.id.oreDedicateText);

        progressoLivello = v.findViewById(R.id.progresso_livello);
        livelloTextview = v.findViewById(R.id.livello);

        fotoprofilo = v.findViewById(R.id.fotoprofilo);



        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("customData", MODE_PRIVATE);
        String nome = sharedPreferences.getString("nome","");
        String cognome = sharedPreferences.getString("cognome","");
        String residenza = sharedPreferences.getString("residenza","");
        long avatarDefault = sharedPreferences.getLong("avatar", 1);

        SharedPreferences.Editor edt = sharedPreferences.edit();
        setFotoprofilo(edt);
        setAvatar((int) avatarDefault,edt);

        fotoprofilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dialog.show();}
        });


        elencoMonitoraggiUser(kmPercorsi,distanzaPercorsaText,oreDedicate,oreDedicateText,numMonitoraggi, progressoLivello, livelloTextview);

        nomeCognomeView.setText(nome+" "+cognome);
        residenzaView.setText(residenza);



        return v;

    }

    private void setAvatar(int avatar, SharedPreferences.Editor edt) {


        switch (avatar) {
            case 1: fotoprofilo.setImageResource(R.drawable.avatar_notion_01);
                    edt.putLong("avatar", 1);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(1);
                    break;
            case 2: fotoprofilo.setImageResource(R.drawable.avatar_notion_02);
                    edt.putLong("avatar", 2);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(2);
                    break;
            case 3: fotoprofilo.setImageResource(R.drawable.avatar_notion_03);
                    edt.putLong("avatar", 3);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(3);
                    break;
            case 4: fotoprofilo.setImageResource(R.drawable.avatar_notion_04);
                    edt.putLong("avatar", 4);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(4);
                    break;
            case 5: fotoprofilo.setImageResource(R.drawable.avatar_notion_05);
                    edt.putLong("avatar", 5);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(5);
                    break;
            case 6: fotoprofilo.setImageResource(R.drawable.avatar_notion_06);
                    edt.putLong("avatar", 6);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(6);
                    break;
            case 7: fotoprofilo.setImageResource(R.drawable.avatar_notion_07);
                    edt.putLong("avatar", 7);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(7);
                    break;
            case 8: fotoprofilo.setImageResource(R.drawable.avatar_notion_08);
                    edt.putLong("avatar", 8);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(8);
                    break;
            case 9: fotoprofilo.setImageResource(R.drawable.avatar_notion_09);
                    edt.putLong("avatar", 9);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(9);
                    break;
            case 10: fotoprofilo.setImageResource(R.drawable.avatar_notion_10);
                    edt.putLong("avatar", 10);
                    edt.commit();
                    UserDB.aggiornaAvatarDB(10);
                    break;
            default: break;
        }
    }

    private void setFotoprofilo(SharedPreferences.Editor edt)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_avatar, null);

        final ImageView avatar1 = (ImageView) view.findViewById(R.id.avatar1);
        final ImageView avatar2 = (ImageView) view.findViewById(R.id.avatar2);
        final ImageView avatar3 = (ImageView) view.findViewById(R.id.avatar3);
        final ImageView avatar4 = (ImageView) view.findViewById(R.id.avatar4);
        final ImageView avatar5 = (ImageView) view.findViewById(R.id.avatar5);
        final ImageView avatar6 = (ImageView) view.findViewById(R.id.avatar6);
        final ImageView avatar7 = (ImageView) view.findViewById(R.id.avatar7);
        final ImageView avatar8 = (ImageView) view.findViewById(R.id.avatar8);
        final ImageView avatar9 = (ImageView) view.findViewById(R.id.avatar9);
        final ImageView avatar10 = (ImageView) view.findViewById(R.id.avatar10);
        builder.setView(view);

        avatar1.setOnClickListener(v -> {
            setAvatar(1, edt);
            Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
            dialog.cancel();
        });

        avatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(2, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(3, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(4, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(5, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(6, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(7, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(8, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(9, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        avatar10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvatar(10, edt);
                Toast.makeText(getContext(), "Avatar aggiornato!", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        builder.setMessage("Seleziona un avatar.");
        builder.setCancelable(true);
        dialog = builder.create();
    }

}