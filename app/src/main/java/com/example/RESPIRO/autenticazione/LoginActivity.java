package com.example.RESPIRO.autenticazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.content.Intent;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.RESPIRO.DB.UserDB;
import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;
import com.example.RESPIRO.onboarding.Onboarding;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;

public class LoginActivity extends AppCompatActivity {


    private EditText emailView;
    private EditText passwordView;
    private Button signInBtn;
    private TextView registerLink;
    private TextView resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        signInBtn = findViewById(R.id.signinbtn);
        registerLink = findViewById(R.id.registerlink);
        resetPassword = findViewById(R.id.resetpassword);
        emailView.setError(null);
        passwordView.setError(null);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    checkLogin();
                    return true;
                }
                return false;
            }
        });

        signInBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterUser.class));
            }
        });

    }

    private void checkLogin() {
        final String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

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

        if (cancel) {
            focusView.requestFocus();
        }
        else { login(email, password);}
    }

    private void login(String email, String password) {

        SharedPreferences s = getApplicationContext().getSharedPreferences("customData",MODE_PRIVATE);
        SharedPreferences.Editor edt = s.edit();

        if(email.equals("admin@gmail.com")) {
            edt.putBoolean("admin",true);
        } else {
            edt.putBoolean("admin", false);
        }

            //UserPasswordCredential credential = new UserPasswordCredential(params[0], params[1]);
            Credentials emailPasswordCredentials = Credentials.emailPassword(email, password);
            AtomicReference<User> user = new AtomicReference<>();

            app.loginAsync(emailPasswordCredentials, it -> {
                if (it.isSuccess()) {
                    Log.v("AVVISO", "Successfully authenticated using an email and password: "+email+" "+password);
                    user.set(app.currentUser());
                    boolean primoAccesso = s.getBoolean("primoAccesso",false);

                    if(primoAccesso) {
                        Log.i("AVVISO", "è il primo accesso di questo utente");
                        Intent i = getIntent();
                        String nome = i.getStringExtra("nome");
                        String cognome = i.getStringExtra("cognome");
                        String residenza = i.getStringExtra("residenza");

                        edt.putString("nome", nome);
                        edt.putString("cognome", cognome);
                        edt.putString("residenza", residenza);
                        edt.putString("email", email);
                        edt.putBoolean("primoAccesso", false);
                        edt.putLong("avatar", 1);
                        edt.commit();

                        UserDB.inserisciUser(nome,cognome,residenza, 1);

                        Intent intent = new Intent(LoginActivity.this, Onboarding.class);
                        startActivity(intent);

                    } else {

                        Log.i("AVVISO", "NON è il primo accesso di questo utente");
                        String emailSalvata = s.getString("email","nessuna email salvata");
                        Log.i("AVVISO", "email salvata: "+emailSalvata);
                        Log.i("AVVISO", "i dati: "+s.getString("residenza", "niente."));

                        if(!email.equals(emailSalvata)) {
                            Log.i("AVVISO", "utente accede con credenziali diverse sul dispositivo. Aggiorno sharedPreferences e database.");
                            UserDB.getCustomData(email, edt, LoginActivity.this);
                            Log.i("AVVISO", "Aggiornate sharedPreferences e database.");
                        }
                    }

                } else {
                    Toast.makeText(this, "email o password errate, Riprova", Toast.LENGTH_SHORT).show();
                }
            });

    }


    private void recoverPassword() {
        String email = emailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Per resettare la password, inserisci la mail nel campo.", Toast.LENGTH_SHORT).show();
            //emailView.setError(getString(R.string.error_field_required));
        } else {
            app.getEmailPassword().sendResetPasswordEmailAsync(email, it -> {
                if (it.isSuccess()) {
                    Log.i("AVVISO", "Successfully sent the user a reset password link to " + email);
                } else {
                    Log.e("AVVISO", "Failed to send the user a reset password link to " + email + ": " + it.getError().getErrorMessage());
                }
            });
        }
    }


}
