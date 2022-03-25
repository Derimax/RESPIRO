package com.example.RESPIRO.autenticazione;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


         if(app.currentUser() ==null) {
            startActivity(new Intent(FirstActivity.this, LoginActivity.class));
        } else if(app.currentUser().isLoggedIn()) {
            startActivity(new Intent(FirstActivity.this, HomeActivity.class));
        }
    }
}