package com.example.RESPIRO.autenticazione;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.example.RESPIRO.HomeDati.HomeActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.ClientResetRequiredError;
import io.realm.mongodb.sync.DiscardUnsyncedChangesStrategy;
import io.realm.mongodb.sync.SyncSession;


public class RespiroSubclass extends Application {

    public static App app;
    public static Realm backgroundThreadRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        String appID = "application-0-buyoh";
        String realmName = "RespiroRealm";
        RealmConfiguration config = new RealmConfiguration.Builder().name(realmName).deleteRealmIfMigrationNeeded().build();
        backgroundThreadRealm = Realm.getInstance(config);

        app = new App(new AppConfiguration.Builder(appID).defaultSyncClientResetStrategy(new DiscardUnsyncedChangesStrategy() {
            @Override
            public void onBeforeReset(Realm realm) {
                Log.w("EXAMPLE", "Beginning client reset for " + realm.getPath());
            }
            @Override
            public void onAfterReset(Realm before, Realm after) {
                Log.w("EXAMPLE", "Finished client reset for " + before.getPath());
            }
            @Override
            public void onError(SyncSession session, ClientResetRequiredError error) {
                Log.e("EXAMPLE", "Couldn't handle the client reset automatically." +
                        " Falling back to manual recovery: " + error.getErrorMessage());
                //handleManualReset(session.getUser().getApp(), session, error);
            }
        }).build());
        gestisciLogin(app);
        }

    public void gestisciLogin(App app) {
        if(app.currentUser() != null) {
            Log.i("info","Utente già loggato");
            Intent i = new Intent(this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {

            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

    }

}


