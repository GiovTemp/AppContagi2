package it.gadg.contagiapp;

public class GruppoUtenti {

    public String idGruppo;
    public String UID;
    public int ruolo;
    public int status;
    public GruppoUtenti(String idGruppo,String UID){

        this.idGruppo = idGruppo;
        this.UID = UID;
        this.ruolo = 1;
        this.status = 1;

    }

}


