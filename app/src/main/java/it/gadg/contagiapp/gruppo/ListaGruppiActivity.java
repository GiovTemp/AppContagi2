package it.gadg.contagiapp.gruppo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.GruppoRicerca;

public class ListaGruppiActivity extends AppCompatActivity {

    FirebaseFirestore db;


    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    ListView listView;
    String[] idGruppi;
    String[] ruoli;
    String[] nomi;
    ArrayList<GruppoRicerca> gr = new ArrayList<>();
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //TODO ruoli non corrispondenti controllare cicli (problema visibilità "NOMI"


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gruppi);

        mAuth= FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        db.collection("GruppoUtenti").whereEqualTo("UID", user.getUid()).whereEqualTo("status", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                flag=queryDocumentSnapshots.size();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    String id = document.getString("idGruppo");
                    String ruolo = document.getString("ruolo");
                    final GruppoRicerca x = new GruppoRicerca(id, ruolo);

                    db.collection("Gruppi").document((String) document.get("idGruppo")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    x.setNome(document.getString("Nome"));
                                    salvaGruppo(x);

                                }
                            } else {
                                Log.d("Errore", "get failed with ", task.getException());
                            }
                        }

                    });
                }
            }

            private void salvaGruppo(GruppoRicerca x) {
                gr.add(x);

                if(gr.size()==flag){
                    idGruppi = new String[gr.size()];
                    for (int j = 0; j < gr.size(); j++) {
                        idGruppi[j] = gr.get(j).id;
                    }

                    nomi = new String[gr.size()];
                    for (int j = 0; j < gr.size(); j++) {
                        nomi[j] = gr.get(j).nome;
                    }

                    ruoli = new String[gr.size()];
                    for (int j = 0; j < gr.size(); j++) {
                        if(gr.get(j).ruolo.equals("1")){
                            ruoli[j] = "admin";
                        }else{
                            ruoli[j] = "utente normale";
                        }

                    }

                    System.out.println(Arrays.toString(nomi));
                    System.out.println(Arrays.toString(ruoli));
                    System.out.println(Arrays.toString(idGruppi));

                    listView = findViewById(R.id.listaGruppi);
                    Adapter adapter = new Adapter(getApplicationContext(),nomi,ruoli,idGruppi);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(gr.get(position).ruolo.equals("1")){
                                Intent intent = new Intent(getApplicationContext(), GestisciGruppoActivity.class);
                                intent.putExtra("NomeGruppo",gr.get(position).nome);
                                intent.putExtra("idGruppo",gr.get(position).id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }else{
                                Intent intent = new Intent(getApplicationContext(), VisualizzaGruppoActivity.class);
                                intent.putExtra("NomeGruppo",gr.get(position).nome);
                                intent.putExtra("idGruppo",gr.get(position).nome);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }


                        }
                    });

                }
            }
        });


        }




    class Adapter extends ArrayAdapter<String>{
        Context context;
        String NomiGruppi[];
        String RuoliGruppi[];
        String IdGruppi[];

        Adapter (Context c,String NomiGruppi[] , String RuoliGruppi[], String IdGruppi[]){
            super(c,R.layout.rigagruppo,R.id.NomeGruppoInvito,NomiGruppi);
            this.context = c;
            this.NomiGruppi=NomiGruppi;
            this.RuoliGruppi=RuoliGruppi;
            this.IdGruppi=IdGruppi;


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rigagruppo = layoutInflater.inflate(R.layout.rigagruppo,parent,false);
            TextView nomeGruppoRiga = rigagruppo.findViewById(R.id.NomeGruppoInvito);
            TextView ruoloGruppoRiga = rigagruppo.findViewById(R.id.RuoloGruppoRiga);
            TextView idGruppoRiga = rigagruppo.findViewById(R.id.idGruppoInvito);

            nomeGruppoRiga.setText(NomiGruppi[position]);
            ruoloGruppoRiga.setText(RuoliGruppi[position]);
            idGruppoRiga.setText(IdGruppi[position]);


            return rigagruppo;
        }
    }
}