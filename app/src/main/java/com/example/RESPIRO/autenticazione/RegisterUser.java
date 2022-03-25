package com.example.RESPIRO.autenticazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.RESPIRO.R;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;


public class RegisterUser extends AppCompatActivity {

    private EditText nameView;
    private EditText cognomeView;
    private EditText residenzaView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private Button registerBtn;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameView = (EditText) findViewById(R.id.name);
        cognomeView = findViewById(R.id.cognome);
        residenzaView = findViewById(R.id.residenza);
        emailView = (EditText) findViewById(R.id.newemail);
        passwordView = (EditText) findViewById(R.id.newpassword);
        confirmPasswordView = (EditText) findViewById(R.id.conpassword);
        registerBtn = (Button) findViewById(R.id.registerbtn);
        loginLink = (TextView) findViewById(R.id.loginlink);

        emailView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRegister();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUser.this, LoginActivity.class));
            }
        });
    }

    private void getRegister() {
        final String name = nameView.getText().toString();
        final String cognome = cognomeView.getText().toString();
        String residenza = residenzaView.getText().toString();
        final String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        String conpassword = confirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Campo richiesto!", Toast.LENGTH_SHORT).show();
            //emailView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        }
        if(TextUtils.isEmpty(cognome)) {
            Toast.makeText(this, "Campo richiesto!", Toast.LENGTH_SHORT).show();
            //emailView.setError(getString(R.string.error_field_required));
            focusView = cognomeView;
            cancel = true;
        }
        if(TextUtils.isEmpty(residenza)) {
            Toast.makeText(this, "Campo richiesto!", Toast.LENGTH_SHORT).show();
            //emailView.setError(getString(R.string.error_field_required));
            focusView = residenzaView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Campo richiesto!", Toast.LENGTH_SHORT).show();
            //emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!MiscFunc.isEmailValid(email)) {
            Toast.makeText(this, "e-mail non valida!", Toast.LENGTH_SHORT).show();
            //emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !MiscFunc.isPasswordValid(password)) {
            Toast.makeText(this, "Password non valida!", Toast.LENGTH_SHORT).show();
            //passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (!password.equals(conpassword)) {
            Toast.makeText(this, "Le password non coincidono!", Toast.LENGTH_SHORT).show();
            //confirmPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = confirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            app.getEmailPassword().registerUserAsync(email, password, it  -> {
                if (it.isSuccess()) {
                    SharedPreferences s = getSharedPreferences("customData",MODE_PRIVATE);
                    SharedPreferences.Editor edt = s.edit();
                    edt.putBoolean("primoAccesso",true);
                    edt.commit();
                    Log.i("AVVISO", "Successfully registered user.");
                    Intent i = new Intent(RegisterUser.this, LoginActivity.class);
                    i.putExtra("nome",name);
                    i.putExtra("cognome",cognome);
                    i.putExtra("residenza", residenza);
                    startActivity(i);
                } else {
                    Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
                }
            });
        }
    }
}