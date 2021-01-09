package it.gadg.contagiapp.modelli;

public class PartecipazioneEvento {

    public String idEvento;
    public String UID;
    public int role;
    public int status;

    public PartecipazioneEvento(String idEvento,String UID){
        this.idEvento = idEvento;
        this.UID = UID;
        this.role=1;
        this.status=1;
    }

}
