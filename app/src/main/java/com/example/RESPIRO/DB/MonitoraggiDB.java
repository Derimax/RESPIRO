package com.example.RESPIRO.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.RESPIRO.R;
import com.example.RESPIRO.rilevamento.Monitoraggio;
import com.example.RESPIRO.rilevamento.RecapRilevamento;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;
import io.realm.mongodb.sync.SyncConfiguration;
import pl.pawelkleczkowski.customgauge.CustomGauge;

import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;
import static com.example.RESPIRO.rilevamento.RecapRilevamento.round;

public class MonitoraggiDB {

    public static void inserisciMonitoraggioRealm(Monitoraggio monitoraggio) {
        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE","Successfully opened a realm with reads and writes allowed on the UI thread.");
            }
        });
        Realm backgroundThreadRealm = Realm.getInstance(config);

        backgroundThreadRealm.executeTransaction (transactionRealm -> {
            transactionRealm.insert(monitoraggio);
    });
    }

    public static void inserisciMonitoraggio(Monitoraggio monitoraggio) {

        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE","Successfully opened a realm with reads and writes allowed on the UI thread.");
            }
        });
        Realm backgroundThreadRealm = Realm.getInstance(config);

        backgroundThreadRealm.executeTransaction (transactionRealm -> {
            transactionRealm.insert(monitoraggio);
            Log.v("AVVISO", "Monitoraggio inserito. Transaction avvenuta.");
            User user = app.currentUser();
            MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("monitoraggi");

            Document evento = new Document("eseguitoDa",user.getId())
                    .append("MonitoraggiPartition",monitoraggio.getMonitoraggiPartition())
                    .append("durataMillis",monitoraggio.getDurata())
                    .append("distanza",monitoraggio.getDistanza())
                    .append("mediaPM25",monitoraggio.getMediaPM25())
                    .append("mediaPM10",monitoraggio.getMediaPM10())
                    .append("dataMonitoraggio", monitoraggio.getDataMonitoraggio())
                    .append("puntiAccumulati",monitoraggio.getPunti())
                    .append("luogo",monitoraggio.getLuogo())
                    .append("latitudine",monitoraggio.getLatitudine())
                    .append("longitudine",monitoraggio.getLongitudine());

            mongoCollection.insertOne(evento).getAsync(result -> {
                if(result.isSuccess()) {
                    Log.v("AVVISO","Data Inserted Successfully");
                }
                else {
                    Log.v("AVVISO","Error:"+result.getError().toString());
                }
            });
        });
        backgroundThreadRealm.close();


    }

    public static void elencoMonitoraggiUser(TextView kmPercorsi, TextView distanzaPercorsaText,
                                             TextView oreDedicate, TextView oreDedicateText, TextView numMonitoraggi,
                                             ProgressBar progressoLivello, TextView livelloTextview) {



        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm.getInstanceAsync(config, new Realm.Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE", "Successfully opened a realm with reads and writes allowed on the UI thread.");
                realm.executeTransaction(transactionRealm -> {
                    RealmResults<Monitoraggio> elencoMonitoraggi = transactionRealm.where(Monitoraggio.class)
                            .equalTo("eseguitoDa", app.currentUser().getId()).findAll();
                    int totMetriPercorsi = 0;
                    double totMillisDedicati = 0;
                    int numMonitoraggiEffettuati = 0;
                    long totPunti = 0;
                    int livello = 1;

                    for (Monitoraggio m: elencoMonitoraggi) {
                        totMetriPercorsi += m.getDistanza();
                        totMillisDedicati += m.getDurata();
                        totPunti += m.getPunti();
                        numMonitoraggiEffettuati++;
                    }

                    if(totPunti<100) {
                        progressoLivello.setProgress((int) totPunti);
                        livelloTextview.setText(Integer.toString(livello));
                    } else {
                        while(totPunti<100) {
                            totPunti /= 100;
                            livello++;
                        }
                        progressoLivello.setProgress((int) totPunti);
                        livelloTextview.setText(Integer.toString(livello));
                    }

                    double totKmPercorsi = totMetriPercorsi/1000;
                    if(totKmPercorsi<1){
                        kmPercorsi.setText(Integer.toString(totMetriPercorsi));
                        distanzaPercorsaText.setText("m percorsi");
                    }
                    else { kmPercorsi.setText(Double.toString(round(totKmPercorsi, 1)));}

                    double hour = (totMillisDedicati / (1000 * 60 * 60)) % 24;
                    if(hour<1) {
                        double minute = totMillisDedicati / 60000;
                        if(minute<1) {
                            int secondi = (int) (totMillisDedicati/1000);
                            oreDedicate.setText(Integer.toString(secondi));
                            oreDedicateText.setText("secondi\ndedicati");
                        } else {
                        oreDedicate.setText(Double.toString(round(minute, 1)));
                        oreDedicateText.setText("minuti\ndedicati");
                        }
                    }
                    else {
                        oreDedicate.setText(Double.toString(round(hour, 1)));
                        oreDedicateText.setText("ore\ndedicate");
                    }


                    numMonitoraggi.setText(Integer.toString(numMonitoraggiEffettuati));
                    Log.i("AVVISO", "num monitoraggi effettuati: "+numMonitoraggiEffettuati);
                    Log.i("AVVISO", "totMillisDedicati: "+totMillisDedicati);
                    Log.i("AVVISO", "totMetriPercorsi: "+totMetriPercorsi);
                });
                realm.close();
            }
        });


    }

    public static void mostraMonitoraggiPerData(Context context, String dataMonitoraggio,
                                                LinearLayout elencoMonitoraggiPerData) throws ParseException {

        String eseguitoDa = app.currentUser().getId();

        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();

        Realm realm = Realm.getInstance(config);
        RealmResults<Monitoraggio> elencoMonitoraggi = realm.where(Monitoraggio.class)
                .equalTo("eseguitoDa",eseguitoDa).and()
                .equalTo("dataMonitoraggio", dataMonitoraggio).findAll();

        if(elencoMonitoraggi.size()==0) {
            Log.i("AVVISO", "Non ci sono monitoraggi effettuati in questa data.");
            LayoutInflater inflater = LayoutInflater.from(context);
            View recapMonitoraggio = (RelativeLayout) inflater.inflate(R.layout.nessun_monitoraggio, null);
            elencoMonitoraggiPerData.addView(recapMonitoraggio);
        }
        else {
            for (Monitoraggio m : elencoMonitoraggi) {
                Log.i("AVVISO", m.toString());

                long durataMillis = m.getDurata();
                long distanza = m.getDistanza();
                long puntiAccumulati = m.getPunti();
                String data = m.getDataMonitoraggio();
                Log.i("AVVISO", "Ho salvato i valori che mi interessano");

                LayoutInflater inflater = LayoutInflater.from(context);
                View recapMonitoraggio = (RelativeLayout) inflater.inflate(R.layout.recap_monitoraggio, null);
                TextView durataView = recapMonitoraggio.findViewById(R.id.tempoView);
                TextView distanzaView = recapMonitoraggio.findViewById(R.id.distanzaPercorsaText);
                TextView puntiView = recapMonitoraggio.findViewById(R.id.punti);
                TextView dataView = recapMonitoraggio.findViewById(R.id.dataView);
                TextView unitDiMisura = recapMonitoraggio.findViewById(R.id.unitDiMisura);

                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                Date d = formatter.parse(data);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                String strDate = dateFormat.format(d);
                dataView.setText(strDate);

                durataView.setText(RecapRilevamento.formattaTempo(durataMillis));
                puntiView.setText(Long.toString(puntiAccumulati));

                if (distanza < 1000) {
                    distanzaView.setText(Long.toString(distanza));
                    unitDiMisura.setText("m");

                }
                elencoMonitoraggiPerData.addView(recapMonitoraggio);
            }
        }
        realm.close();
    }

    public static void popolamentoCalendarioMonitoraggi(CompactCalendarView compactCalendarView) {

        String eseguitoDa = app.currentUser().getId();


        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();

        Realm realm = Realm.getInstance(config);
        RealmResults<Monitoraggio> elencoMonitoraggi = realm.where(Monitoraggio.class)
                .equalTo("eseguitoDa",eseguitoDa).findAll();

        for(Monitoraggio m : elencoMonitoraggi) {
            String data = m.getDataMonitoraggio();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            Date date = null;
            try {
                date = sdf.parse(data);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();

            Event ev1 = new Event(Color.rgb(140, 215, 247), millis, "");
            compactCalendarView.addEvent(ev1);
        }
    }

    public static void eliminaUnMonitoraggioRealm(String id) {

        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();

        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE","Successfully opened a realm with reads and writes allowed on the UI thread.");
            }
        });

        Realm realm = Realm.getInstance(config);
        realm.executeTransaction(r -> {
            ObjectId objId = new ObjectId(id);
            Monitoraggio m = realm.where(Monitoraggio.class).equalTo("_id", objId).findFirst();
            m.deleteFromRealm();
            Log.i("AVVISO", "Ho eliminato il monitoraggio con id: "+objId+" dal realm.");

            User user = app.currentUser();
            MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("monitoraggi");

            Document query = new Document("_id",objId);
            mongoCollection.deleteOne(query).getAsync(task -> {
                if(task.isSuccess()){
                    Log.i("AVVISO", "eliminato il monitoraggio con id: "+objId);
                }
                else {
                    Log.i("AVVISO", "errore nell'eliminazione del monitoraggio con id: "+objId);
                }
            });
        });
        realm.close();
    }

    public static void eliminaTuttiMonitoraggi() {
        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.v("EXAMPLE","Successfully opened a realm with reads and writes allowed on the UI thread.");
            }
        });
        Realm backgroundThreadRealm = Realm.getInstance(config);
        backgroundThreadRealm.executeTransaction(r -> {
            r.deleteAll();
            Log.i("AVVISO", "Ho eliminato tutti i monitoraggi dal realm.");
            User user = app.currentUser();
            MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("monitoraggi");


            mongoCollection.deleteMany(new Document()).getAsync(task -> {
                if(task.isSuccess()){
                    Log.i("AVVISO", "eliminato tutti i monitoraggi dalla collezione");
                }
                else {
                    Log.i("AVVISO", "errore nell'eliminazione dei monitoraggi.");
                }
            });
        });
        backgroundThreadRealm.close();
    }

    public static void trovaTuttiMonitoraggiRealm() {
        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
        Realm realm = Realm.getInstance(config);
        RealmResults<Monitoraggio> elenco = realm.where(Monitoraggio.class).findAll();
        if (elenco.isEmpty()) {
            Log.i("AVVISO", "non ci sono monitoraggi nel realm.");
        }else {
            for (Monitoraggio m : elenco) {
                Log.i("AVVISO", "MONITORAGGIO: "+m.toString());
            }
        }

    }

    public static void aggiornaMonitoraggiRealm() {

        eliminaTuttiMonitoraggi();
        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("monitoraggi");

       RealmResultTask<MongoCursor<Document>> tutto = mongoCollection.find(new Document()).iterator();
       tutto.getAsync(task -> {
           if(task.isSuccess()) {
               MongoCursor<Document> results = task.get();
               if(!results.hasNext()) {
                   Log.i("AVVISO", "Non ci sono monitoraggi nel database.");
               }
               while(results.hasNext())  {
                   Document currentDoc = results.next();
                   Monitoraggio monitoraggio = new Monitoraggio();
                   monitoraggio.setDataMonitoraggio(currentDoc.getString("dataMonitoraggio"));
                   monitoraggio.setEseguitoDa(currentDoc.getString("eseguitoDa"));
                   monitoraggio.setDistanza(currentDoc.getLong("distanza"));
                   monitoraggio.setDurata(currentDoc.getLong("durata"));
                   monitoraggio.setPunti(currentDoc.getLong("punti"));
                   monitoraggio.setMediaPM10(currentDoc.getDouble("mediaPM10"));
                   monitoraggio.setMediaPM25(currentDoc.getDouble("mediaPM25"));
                   monitoraggio.set_id(currentDoc.getObjectId("_id"));
                   monitoraggio.setMonitoraggiPartition("MonitoraggiPartition");
                   monitoraggio.setLuogo(currentDoc.getString("luogo"));
                   monitoraggio.setLatitudine(currentDoc.getDouble("latitudine"));
                   monitoraggio.setLongitudine(currentDoc.getDouble("longitudine"));

                   inserisciMonitoraggioRealm(monitoraggio);
                   Log.i("AVVISO", "monitoraggio aggiornato: "+monitoraggio.toString());
               }
               Log.i("AVVISO", "monitoraggi del realm aggiornati.");
           }
           else {
               Log.v("Error","Task Error:"+task.getError().toString());
           }
       });

    }

    public static void trovaMonitoraggiPerLuogo(String luogo, TextView mediapm10, TextView mediapm25,
                                                TextView consiglio, CustomGauge progressPm10, CustomGauge progressPm25) {

        double mediepm10 = 0;
        double mediepm25 = 0;
        int num = 0;

        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();

        Realm realm = Realm.getInstance(config);
        RealmResults<Monitoraggio> elencoMonitoraggi = realm.where(Monitoraggio.class)
                .equalTo("luogo", luogo).findAll();

        if(elencoMonitoraggi.size()==0) {
            Log.i("AVVISO", "Non ci sono monitoraggi effettuati in questo luogo.");
            progressPm25.setPointStartColor(Color.parseColor("#5f5f5f"));
            progressPm25.setPointEndColor(Color.parseColor("#5f5f5f"));
            progressPm10.setPointStartColor(Color.parseColor("#5f5f5f"));
            progressPm10.setPointEndColor(Color.parseColor("#5f5f5f"));
            progressPm10.setValue(0);
            progressPm25.setValue(0);
            mediapm10.setTextColor(Color.parseColor("#5f5f5f"));
            mediapm25.setTextColor(Color.parseColor("#5f5f5f"));
            mediapm10.setText("0.0");
            mediapm25.setText("0.0");
            consiglio.setText("Sembra che non siano stati\neffettuati monitoraggi qui.");
        }
        else {
            for (Monitoraggio m : elencoMonitoraggi) {

                double pm25 = m.getMediaPM25();
                double pm10 = m.getMediaPM10();
                mediepm10 += pm10;
                mediepm25 += pm25;
                num++;
            }

            double mediaTotpm25 = mediepm25/num;
            double mediaTotpm10 = mediepm10/num;


                double progressopm25 = (270*mediaTotpm25)/30;
                progressPm25.setValue((int) progressopm25);

                double progressopm10 = (270*mediaTotpm10)/60;
                progressPm10.setValue((int) progressopm10);

                //colore pm25 in base al valore
                if(mediaTotpm25<10) {
                    progressPm25.setPointStartColor(Color.parseColor("#4BB543")); //verde
                    progressPm25.setPointEndColor(Color.parseColor("#4BB543"));
                    consiglio.setText("I valori del PM2.5 sono molto bassi\nin questa zona.");
                }
                if(10<mediaTotpm25 && mediaTotpm25<20) {
                    progressPm25.setPointStartColor(Color.parseColor("#deb204")); //giallo
                    progressPm25.setPointEndColor(Color.parseColor("#deb204"));

                    consiglio.setText("I valori del PM2.5 sono entro i limiti, \nma non molto bassi.");
                }
                if(mediaTotpm25>20) {
                    progressPm25.setPointStartColor(Color.parseColor("#c9ac04"));//rosso
                    progressPm25.setPointEndColor(Color.parseColor("#c90404"));
                    consiglio.setText("I valori del PM2.5 superano i limiti\nin questa zona.");
                }

                //colore pm10 in base al valore
                if(mediaTotpm10<20) {
                    progressPm10.setPointStartColor(Color.parseColor("#4BB543")); //verde
                    progressPm10.setPointEndColor(Color.parseColor("#4BB543"));
                    consiglio.setText("I valori del PM10 sono molto bassi\nin questa zona.");
                }
                if(20<mediaTotpm10 && mediaTotpm10<40) {
                    progressPm10.setPointStartColor(Color.parseColor("#deb204")); //giallo
                    progressPm10.setPointEndColor(Color.parseColor("#deb204"));
                    consiglio.setText("I valori del PM10 sono entro i limiti, \nma non molto bassi.");
                }
                if(mediaTotpm10>40) {
                    progressPm25.setPointStartColor(Color.parseColor("#c9ac04"));//rosso
                    progressPm25.setPointEndColor(Color.parseColor("#c90404"));
                    consiglio.setText("I valori del PM10 superano i limiti\nin questa zona.");
                }

                //consiglio pm10 e pm25 in base al valore
                if(mediaTotpm25<10 && mediaTotpm10<20) {
                    consiglio.setText("I valori di particolato\nsono molto bassi in questa zona.");
                }
                if((10<mediaTotpm25 && mediaTotpm10<25) && (20<mediaTotpm25 && mediaTotpm10<40)) {
                    consiglio.setText("I valori del particolato sono entro i limiti,\nma non molto bassi.");
                }
                if(mediaTotpm25>20 && mediaTotpm10>40) {
                    consiglio.setText("I valori del particolato\n superano i limiti in questa zona");
                }

                mediapm10.setText(Double.toString(round(mediaTotpm10,1)));
                mediapm25.setText(Double.toString(round(mediaTotpm25,1)));
        }
        realm.close();
    }

    public static void popolaMappaHome(MapView mappa, Context context, String luogo) {

        mappa.getOverlays().clear();

        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();

        Realm realm = Realm.getInstance(config);
        RealmResults<Monitoraggio> elencoMonitoraggi = realm.where(Monitoraggio.class).equalTo("luogo", luogo).findAll();

        for (Monitoraggio m : elencoMonitoraggi) {
            double lat = m.getLatitudine();
            double lon = m.getLongitudine();
            Marker marker = new Marker(mappa);
            marker.setPosition(new GeoPoint(lat,lon));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setIcon(context.getResources().getDrawable(R.drawable.monitoraggio_azzurro));
            mappa.getOverlays().add(marker);
        }
        realm.close();
    }

    public static void popolaMappa(MapView mappa, Context context) {

        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "MonitoraggiPartition")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();

        Realm realm = Realm.getInstance(config);
        RealmResults<Monitoraggio> elencoMonitoraggi = realm.where(Monitoraggio.class).findAll();

        for (Monitoraggio m : elencoMonitoraggi) {
            double lat = m.getLatitudine();
            double lon = m.getLongitudine();
            double valorePm25 = m.getMediaPM25();
            double valorePm10 = m.getMediaPM10();

            //colore marker verde: casi
            if(valorePm25<10 && valorePm10<20 || valorePm25<10 && (valorePm10>20 && valorePm10<40) ||
                    (valorePm25>10 && valorePm25<20) && valorePm10<20) {
                Marker marker = new Marker(mappa);
                marker.setPosition(new GeoPoint(lat,lon));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setIcon(context.getResources().getDrawable(R.drawable.monitoraggio_verde));
                InfoWindow iw = new InfoWindow(R.layout.bubble, mappa) {
                    @Override
                    public void onOpen(Object item) {
                        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.bubble_layout);
                        TextView data = (TextView) mView.findViewById(R.id.bubble_title);
                        TextView pm25 = (TextView) mView.findViewById(R.id.bubble_description);
                        TextView pm10 = (TextView) mView.findViewById(R.id.bubble_subdescription);
                        ImageView i = mView.findViewById(R.id.bubble_image);
                        i.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                close();
                            }
                        });
                        data.setText(m.getDataMonitoraggio());
                        pm25.setText(Double.toString(round(valorePm25, 1)));
                        pm10.setText(Double.toString(round(valorePm10, 1)));
                    }

                    @Override
                    public void onClose() {

                    }
                };
                marker.setInfoWindow(iw);
                mappa.getOverlays().add(marker);
            }
            else if((10<valorePm25 && valorePm25<20) && (20<valorePm10 && valorePm10<40) ||
                    (10<valorePm25 && valorePm25<20) && valorePm10>40 ||
            valorePm25>20 && (20<valorePm10 && valorePm10<40)) {
                Marker marker = new Marker(mappa);
                marker.setPosition(new GeoPoint(lat,lon));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setIcon(context.getResources().getDrawable(R.drawable.monitoraggio_giallo));
                mappa.getOverlays().add(marker);
            }
            else if(valorePm25>20 && valorePm10>40) {
                Marker marker = new Marker(mappa);
                marker.setPosition(new GeoPoint(lat,lon));
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setIcon(context.getResources().getDrawable(R.drawable.monitoraggio_rosso));
                mappa.getOverlays().add(marker);
            }

        }
    }


}







