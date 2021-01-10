package it.gadg.contagiapp.modelli;

public class UtenteRichiesta {
    public String nome;
    public long rischio;
    public String UID;
    public String cognome;
    public String idRichiesta;

    public UtenteRichiesta(String UID) {
        this.UID = UID;
    }
}
