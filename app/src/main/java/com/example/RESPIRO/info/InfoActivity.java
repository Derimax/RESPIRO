package com.example.RESPIRO.info;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;
import com.example.RESPIRO.eventi.EventiActivity;
import com.example.RESPIRO.impostazioni.ImpostazioniActivity;
import com.example.RESPIRO.profilo.ProfiloActivity;
import com.example.RESPIRO.rilevamento.CheckRilevamento;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class InfoActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private EditText emailEdit, oggettoEdit, messaggioEdit;
    private Button inviaMessaggio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.infoMenu);

        emailEdit = findViewById(R.id.email);
        oggettoEdit = findViewById(R.id.email);
        messaggioEdit = findViewById(R.id.messaggio);
        inviaMessaggio = findViewById(R.id.inviaMessaggio);

        inviaMessaggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oggetto = oggettoEdit.getText().toString().trim();
                String messaggio = messaggioEdit.getText().toString().trim();
                String email = "rofifa6613@sueshaw.com";
                if(oggetto.length()==0)
                {
                    Toast.makeText(InfoActivity.this, "Hai dimenticato l'oggetto", Toast.LENGTH_SHORT).show();
                }
                else if(messaggio.length()==0)
                {
                    Toast.makeText(InfoActivity.this, "Hai dimenticato il messaggio", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String mail = "mailto:" + email +
                            "?&subject=" + Uri.encode(oggetto) +
                            "&body=" + Uri.encode(messaggio);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse(mail));
                    try {
                        startActivity(Intent.createChooser(intent,"Send Email.."));
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(InfoActivity.this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.datiMenu:

                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profiloMenu:
                        startActivity(new Intent(getApplicationContext(),ProfiloActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.eventiMenu:
                        startActivity(new Intent(getApplicationContext(),EventiActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.infoMenu:

                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onFabClick(View view) {
        Intent intent = new Intent(InfoActivity.this, CheckRilevamento.class);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    public void vaiImpostazioni(View view) {
        startActivity(new Intent(this, ImpostazioniActivity.class));
    }
}