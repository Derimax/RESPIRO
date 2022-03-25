package com.example.RESPIRO.DB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.RESPIRO.R;

import org.bson.Document;

import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import io.realm.mongodb.mongo.iterable.MongoCursor;


import static com.example.RESPIRO.autenticazione.RespiroSubclass.app;



public class EventiDB {




    public static void InserisciEventoDB(String nomeEvento, String ora, String data, String luogo) {

        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("eventi");

        Document evento = new Document("nomeEvento",nomeEvento)
                .append("ora",ora)
                .append("data",data)
                .append("luogo",luogo);

        mongoCollection.insertOne(evento).getAsync(result -> {
            if(result.isSuccess())
            {
                Log.v("Data","Data Inserted Successfully");
            }
            else
            {
                Log.v("Data","Error:"+result.getError().toString());
            }
        });
    }

    public static void MostraEventiVicini(Context context, LinearLayout boxEventiVicini, String residenza) {

        final String[] nomeEvento = {""};
        final String[] ora = {""};
        final String[] data = {""};
        final String[] luogo = {""};

        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("eventi");

        Document queryFilter  = new Document("luogo", residenza);

        RealmResultTask<MongoCursor<Document>> trovaEventi = mongoCollection.find(queryFilter).iterator();
        trovaEventi.getAsync(task -> {
            if(task.isSuccess())
            {
                MongoCursor<Document> results = task.get();
                if(!results.hasNext()) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View nuovoEvento = (RelativeLayout) inflater.inflate(R.layout.evento_default, null);
                    boxEventiVicini.addView(nuovoEvento);
                    Log.i("Result", "Non trovo nulla.");
                }
                while(results.hasNext())  {
                    Document currentDoc = results.next();
                    if(currentDoc.getString("nomeEvento")!=null &&
                            currentDoc.getString("ora")!=null &&
                            currentDoc.getString("data")!=null &&
                            currentDoc.getString("luogo")!=null) {

                        nomeEvento[0] = currentDoc.getString("nomeEvento");
                        ora[0] = currentDoc.getString("ora");
                        data[0] = currentDoc.getString("data");
                        luogo[0] = currentDoc.getString("luogo");

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View nuovoEvento = (RelativeLayout) inflater.inflate(R.layout.evento, null);

                        TextView nomeEventoView = nuovoEvento.findViewById(R.id.nome_evento);
                        TextView dataView = nuovoEvento.findViewById(R.id.data_evento);
                        TextView oraView = nuovoEvento.findViewById(R.id.ora_evento);
                        TextView luogoView = nuovoEvento.findViewById(R.id.luogo_evento);

                        nomeEventoView.setText(nomeEvento[0]);
                        oraView.setText(ora[0]);
                        dataView.setText(data[0]);
                        luogoView.setText(luogo[0]);
                        boxEventiVicini.addView(nuovoEvento);

                    }
                }
            }
            else
            {
                Log.v("Error","Task Error:"+task.getError().toString());
            }
        });



    }


    public static void MostraEventi(Context context, LinearLayout boxEventiInProgramma) {

        final String[] nomeEvento = {""};
        final String[] ora = {""};
        final String[] data = {""};
        final String[] luogo = {""};

        User user = app.currentUser();
        MongoClient mongoClient = user.getMongoClient("mongodb-atlas");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("RespiroDB");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("eventi");

        RealmResultTask<MongoCursor<Document>> trovaEventi = mongoCollection.find().iterator();
        trovaEventi.getAsync(task -> {
            if(task.isSuccess())
            {
                MongoCursor<Document> results = task.get();
                if(!results.hasNext()) Log.i("Result", "Non trovo nulla.");
                while(results.hasNext())  {
                    Document currentDoc = results.next();
                    if(currentDoc.getString("nomeEvento")!=null &&
                            currentDoc.getString("ora")!=null &&
                            currentDoc.getString("data")!=null &&
                            currentDoc.getString("luogo")!=null) {

                        nomeEvento[0] = currentDoc.getString("nomeEvento");
                        ora[0] = currentDoc.getString("ora");
                        data[0] = currentDoc.getString("data");
                        luogo[0] = currentDoc.getString("luogo");

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View nuovoEvento = (RelativeLayout) inflater.inflate(R.layout.evento, null);

                        TextView nomeEventoView = nuovoEvento.findViewById(R.id.nome_evento);
                        TextView dataView = nuovoEvento.findViewById(R.id.data_evento);
                        TextView oraView = nuovoEvento.findViewById(R.id.ora_evento);
                        TextView luogoView = nuovoEvento.findViewById(R.id.luogo_evento);

                        nomeEventoView.setText(nomeEvento[0]);
                        oraView.setText(ora[0]);
                        dataView.setText(data[0]);
                        luogoView.setText(luogo[0]);
                        boxEventiInProgramma.addView(nuovoEvento);

                    }
                }
            }
            else
            {
                Log.v("Error","Task Error:"+task.getError().toString());
            }
        });



    }




}



