package com.example.RESPIRO.rilevamento;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Monitoraggio extends RealmObject {
    @PrimaryKey
    private ObjectId _id;

    @Required
    private String MonitoraggiPartition;

    private String dataMonitoraggio;

    @Required
    private Long distanza;

    @Required
    private Long durata;

    @Required
    private String eseguitoDa;

    @Required
    private Double mediaPM10;

    @Required
    private Double mediaPM25;

    @Required
    private Long punti;

    @Required
    private String luogo;

    @Required
    private Double latitudine;

    @Required
    private Double longitudine;

    // Standard getters & setters

    public ObjectId get_id() { return _id; }
    public void set_id(ObjectId _id) { this._id = _id; }

    public String getMonitoraggiPartition() { return MonitoraggiPartition; }
    public void setMonitoraggiPartition(String MonitoraggiPartition) { this.MonitoraggiPartition = MonitoraggiPartition; }

    public String getDataMonitoraggio() { return dataMonitoraggio; }
    public void setDataMonitoraggio(String dataMonitoraggio) { this.dataMonitoraggio = dataMonitoraggio; }

    public Long getDistanza() { return distanza; }
    public void setDistanza(Long distanza) { this.distanza = distanza; }

    public Long getDurata() { return durata; }
    public void setDurata(Long durata) { this.durata = durata; }

    public String getEseguitoDa() { return eseguitoDa; }
    public void setEseguitoDa(String eseguitoDa) { this.eseguitoDa = eseguitoDa; }

    public Double getMediaPM10() { return mediaPM10; }
    public void setMediaPM10(Double mediaPM10) { this.mediaPM10 = mediaPM10; }

    public Double getMediaPM25() { return mediaPM25; }
    public void setMediaPM25(Double mediaPM25) { this.mediaPM25 = mediaPM25; }

    public Long getPunti() { return punti; }
    public void setPunti(Long punti) { this.punti = punti; }

    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }

    public Double getLatitudine() { return latitudine; }
    public void setLatitudine(Double latitudine) { this.latitudine = latitudine; }

    public Double getLongitudine() { return longitudine; }
    public void setLongitudine(Double longitudine) { this.longitudine = longitudine; }

    @Override
    public String toString() {
        return "Monitoraggio{" +
                "_id=" + _id +
                ", MonitoraggiPartition='" + MonitoraggiPartition + '\'' +
                ", dataMonitoraggio='" + dataMonitoraggio + '\'' +
                ", distanza=" + distanza +
                ", durata=" + durata +
                ", eseguitoDa='" + eseguitoDa + '\'' +
                ", mediaPM10=" + mediaPM10 +
                ", mediaPM25=" + mediaPM25 +
                ", punti=" + punti +
                ", luogo='" + luogo + '\'' +
                ", latitudine=" + latitudine +
                ", longitudine=" + longitudine +
                '}';
    }
}
