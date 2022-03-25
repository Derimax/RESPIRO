package com.example.RESPIRO.DB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.RESPIRO.HomeDati.HomeActivity;
import com.example.RESPIRO.R;
import com.example.RESPIRO.autenticazione.FirstActivity;
import com.example.RESPIRO.autenticazione.LoginActivity;
import com.example.RESPIRO.profilo.UserApp;
import com.example.RESPIRO.rilevamento.Monitoraggio;
import com.mongodb.MongoException;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.mongo.result.UpdateResult;
import io.realm.mongodb.sync.SyncConfiguration;
import kotlin.jvm.Synchronized;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;
import static java.lang.Thread.sleep;

public class UserDB {

    public static void inserisciUser(String nome, String cognome, String residenza, long avatar) {

        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("CustomUserData");

        Document evento = new Document("userIDfield", user.getId())
                .append("nome", nome)
                .append("cognome", cognome)
                .append("residenza", residenza)
                .append("MonitoraggiPartition", "MonitoraggiPartition")
                .append("avatar", avatar);


        mongoCollection.insertOne(evento).getAsync(result -> {
            if (result.isSuccess()) {
                Log.v("AVVISO", "Inserted custom user data document. _id of inserted document: "
                        + result.get().getInsertedId());
            } else {
                Log.v("AVVISO", "Errore! :" + result.getError().toString());
            }
        });
    }

    public static void getCustomData(String email, SharedPreferences.Editor edt, Context context){

        final String[] nome = new String[1];
        final String[] cognome = new String[1];
        final String[] residenza = new String[1];
        final long[] avatar = new long[1];

        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("CustomUserData");

        Log.i("AVVISO", "filtro...");
        Document queryFilter = new Document("userIDfield", user.getId());

        mongoCollection.findOne(queryFilter).getAsync(task -> {
            Log.i("AVVISO", "sono qui");
            if (task.isSuccess()) {
                Log.i("AVVISO", "successo!");
                Document result = task.get();
                nome[0] = result.getString("nome");
                cognome[0] = result.getString("cognome");
                residenza[0] = result.getString("residenza");
                avatar[0] = result.getLong("avatar");

                Log.v("AVVISO", "successfully found a document: " + result);
                edt.putString("nome", nome[0]);
                edt.putString("cognome", cognome[0]);
                edt.putString("residenza", residenza[0]);
                edt.putString("email", email);
                edt.putLong("avatar", avatar[0]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    edt.apply();
                } else { edt.commit();}
                Log.v("AVVISO", "nuovi dati salvati nelle sharedPreferences");

                Intent i = new Intent(context, FirstActivity.class);
                context.startActivity(i);
            } else {
                Log.e("AVVISO", "failed to find document with: ", task.getError());
            }
        });
        Log.i("AVVISO", "ora sono qui");
    }

    public static void setClassifica(Context context, LinearLayout classificaLayout, LinearLayout posClassificaUtente) {


        User utente = app.currentUser();
        MongoClient mongoClient = utente.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("CustomUserData");

        //cerco tutti gli user nel database
        RealmResultTask<MongoCursor<Document>> elencoUser = mongoCollection.find().iterator();
        elencoUser.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                TreeMap<Integer, UserApp> classifica = new TreeMap<>();
                if (!results.hasNext()) Log.i("Result", "Non trovo nulla.");
                while (results.hasNext()) {
                    long sommaPunti = 0;
                    Document currentDoc = results.next();
                    String userId = currentDoc.getString("userIDfield");
                    String nome = currentDoc.getString("nome");
                    String cognome = currentDoc.getString("cognome");
                    String residenza = currentDoc.getString("residenza");
                    UserApp user = new UserApp();
                    user.setNome(nome);
                    user.setCognome(cognome);
                    user.setResidenza(residenza);
                    user.setId(userId);

                    SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                            .allowQueriesOnUiThread(true)
                            .allowWritesOnUiThread(true)
                            .build();

                    //cerco tutti i monitoraggi effettuati dall'user che ho trovato e sommo i punti accumulati da ogni singolo
                    //monitoraggio in modo da ottenere i punti totali dell'user
                    Realm realm = Realm.getInstance(config);
                    RealmResults<Monitoraggio> elencoMonitoraggi = realm.where(Monitoraggio.class)
                            .equalTo("eseguitoDa", userId).findAll();

                    for (Monitoraggio m : elencoMonitoraggi) {
                        sommaPunti += m.getPunti();
                    }
                    int somma = (int) sommaPunti;
                    classifica.put(somma, user);
                }
                // Calling the method valueSort
                Map sortedMap = valueSort(classifica);

                Set<Map.Entry<Integer, UserApp>> set = sortedMap.entrySet();
                Iterator<Map.Entry<Integer, UserApp>> i = set.iterator();
                int posizione = 1;
                while (i.hasNext()) {
                    Map.Entry<Integer, UserApp> mp = (Map.Entry<Integer, UserApp>) i.next();
                    Log.i("AVVISO", mp.getKey() + ":   "+mp.getValue().getNome());
                    //inflate
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View postoInClassifica = (LinearLayout) inflater.inflate(R.layout.classifica_item, null);

                    TextView posizioneView = postoInClassifica.findViewById(R.id.posizioneUtente);
                    TextView nomeView = postoInClassifica.findViewById(R.id.nomeUtente);
                    TextView residenzaView = postoInClassifica.findViewById(R.id.paeseUtente);
                    TextView puntiView = postoInClassifica.findViewById(R.id.puntiUtente);
                    String cognome = mp.getValue().getCognome();
                    char cognomeAbbreviato = cognome.charAt(0);
                    posizioneView.setText(Integer.toString(posizione));
                    nomeView.setText(mp.getValue().getNome()+" "+cognomeAbbreviato+".");
                    puntiView.setText(Integer.toString(mp.getKey()));
                    residenzaView.setText(mp.getValue().getResidenza());
                    classificaLayout.addView(postoInClassifica);

                    Log.i("AVVISO", "id user: "+mp.getValue().getId()+", id user attivo: "+app.currentUser().getId());
                    String utenteid = mp.getValue().getId();
                    String utenteAttivoId = app.currentUser().getId();
                    if(utenteid.equals(utenteAttivoId)) {
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        View posUtente = (LinearLayout) layoutInflater.inflate(R.layout.pos_classifica_utente, null);

                        TextView posizioneUtenteView = posUtente.findViewById(R.id.posizioneUtenteAttivo);
                        TextView nomeUtenteView = posUtente.findViewById(R.id.nomeUtenteAttivo);
                        TextView residenzaUtenteView = posUtente.findViewById(R.id.paeseUtenteAttivo);
                        TextView puntiUtenteView = posUtente.findViewById(R.id.puntiUtenteAttivo);
                        String cognomeUtente = mp.getValue().getCognome();
                        char cognomeAbbreviatoUtente = cognomeUtente.charAt(0);
                        posizioneUtenteView.setText(Integer.toString(posizione));
                        nomeUtenteView.setText(mp.getValue().getNome()+" "+cognomeAbbreviatoUtente+".");
                        puntiUtenteView.setText(Integer.toString(mp.getKey()));
                        residenzaUtenteView.setText(mp.getValue().getResidenza());
                        posClassificaUtente.addView(posUtente);
                    }
                    posizione++;
                }


            }
        });


    }

    public static <K, V extends Comparable<V>> Map<K, V> valueSort(final Map<K, V> map) {
        // Static Method with return type Map and
        // extending comparator class which compares values
        // associated with two keys
        Comparator<K> valueComparator = new Comparator<K>() {

            // return comparison results of values of
            // two keys
            public int compare(K k1, K k2) {
                int comp = map.get(k1).compareTo(
                        map.get(k2));
                if (comp == 0)
                    return 1;
                else
                    return comp;
            }

        };
        // SortedMap created using the comparator
        Map<K, V> sorted = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            sorted = new TreeMap<K, V>(valueComparator).descendingMap();
        }

        sorted.putAll(map);

        return sorted;
    }


    public static void aggiornaAvatarDB(long i) {

        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("CustomUserData");

        Log.i("AVVISO", "aggiorno codice avatar nel db...");

        Document query = new Document().append("userIDfield", user.getId());
        Bson update = new Document().append("avatar", i);
        Bson set = new Document().append("$set", update);
        try {
            RealmResultTask<UpdateResult> result = mongoCollection.updateOne(query, set);
            result.getAsync(task -> {
                if(task.isSuccess()){
                    Log.i("AVVISO", "Avatar aggiornato.");
                }else {
                    Log.i("AVVISO", "Errore. Avatar non aggiornato."+task.getError().toString());
                }
            });
        } catch (MongoException me) {
            Log.i("AVVISO", "Errore!: "+me);
        }
    }


}