package com.example.RESPIRO.profilo;

public class UserApp implements Comparable {

private String nome, cognome, residenza, id;

    public UserApp(String nome, String cognome, String residenza, String id) {
        this.nome = nome;
        this.cognome = cognome;
        this.residenza = residenza;
        this.id = id;
    }
    public UserApp() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getResidenza() {
        return residenza;
    }

    public void setResidenza(String residenza) {
        this.residenza = residenza;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
