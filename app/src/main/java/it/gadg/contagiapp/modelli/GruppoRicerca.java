package it.gadg.contagiapp.modelli;

public class GruppoRicerca {
    public String nome;
    public String id;
    public String ruolo;

    public GruppoRicerca(String id,String ruolo){

        this.id=id;
        this.ruolo=ruolo;
    }

    public void setNome(String nome){
        this.nome=nome;
    }
}
