package com.example.RESPIRO.profilo;

public class Classifica {
    private int pos;
    private String nome;
    private String paese;
    private int punti;

    public Classifica(int pos, String nome, String paese, int punti) {
        this.pos = pos;
        this.nome = nome;
        this.paese = paese;
        this.punti = punti;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPaese() {
        return paese;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public int getPunti() {
        return punti;
    }

    public void setPunti(int punti) {
        this.punti = punti;
    }
}
