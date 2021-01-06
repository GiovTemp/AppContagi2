package it.gadg.contagiapp.modelli;


public class Evento {


    public String nome;
    public String nomeLuogo;
    public String idLuogo;
    public String data;
    public String oraInizio;
    public int rischio;

    public Evento(String nome,String nomeLuogo,String idLuogo,String data,String oraInizio,int rischio){

        this.nome = nome;
        this.nomeLuogo = nomeLuogo;
        this.idLuogo=idLuogo;
        this.data = data;
        this.rischio=rischio;
        this.oraInizio= oraInizio;

    }

}
