package com.example.RESPIRO.impostazioni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.RESPIRO.R;
import com.example.RESPIRO.autenticazione.LoginActivity;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;

public class ImpostazioniActivity extends AppCompatActivity {

    private RelativeLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        logout =findViewById(R.id.logout);

    }

    public void logout(View view) {

        app.currentUser().logOutAsync( result -> {
            if (result.isSuccess()) {
                Log.v("AVVISO", "logout effettuato");
                Toast.makeText(this, "logout effettuato.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Log.e("AUTH", result.getError().toString());
            }
        });

    }

    public void back(View v) {
        finish();
    }
}