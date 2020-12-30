package it.gadg.contagiapp;

import java.sql.Time;
import java.util.Date;

public class Evento {


    public String nome;
    public String x;
    public String y;
    public String data;
    public String oraInizio;
    public int rischio;

    public Evento(String nome,String x,String y,String data,String oraInizio,int rischio){

        this.nome = nome;
        this.x = x;
        this.y=y;
        this.data = data;
        this.rischio=rischio;
        this.oraInizio= oraInizio;



    }

}
