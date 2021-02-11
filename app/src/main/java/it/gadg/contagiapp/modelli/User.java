package it.gadg.contagiapp.modelli;

import java.io.Serializable;

public class User implements Serializable {
    public String nome;
    public String cognome;
    public String email;
    public Long rischio;
    public String etichetta;
    public String uid;
    public boolean ruolo;


    public User(String nome,String cognome,String email){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.rischio = 0L;
        this.etichetta="super";
        this.ruolo = false;


    }
}
