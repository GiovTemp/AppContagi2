package it.gadg.contagiapp;

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
