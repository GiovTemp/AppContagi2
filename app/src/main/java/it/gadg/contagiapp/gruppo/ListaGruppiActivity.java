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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.Gruppo;

public class ListaGruppiActivity extends AppCompatActivity {

    FirebaseFirestore db;


    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    final List<Gruppo> listaGruppi = new ArrayList<>();
    final List<String> listaRuoli = new ArrayList<>();
    ListView listView;
    String[] idGruppi;
    String[] ruoli;
    String[] nomi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //TODO ruoli non corrispondenti controllare cicli (problema visibilità "NOMI"


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gruppi);

        mAuth= FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();



        db.collection("GruppoUtenti").whereEqualTo("UID", user.getUid()).whereEqualTo("status", 1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    final List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add((String) document.get("idGruppo"));
                        listaRuoli.add((String) document.get("ruolo"));

                    }

                    int i=0;

                    nomi  = new String[list.size()];
                    do{
                        db.collection("Gruppi").document(list.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        Gruppo g = new Gruppo(document.getString("Nome"));
                                        listaGruppi.add(g);

                                   }
                                } else {
                                    Log.d("Errore", "get failed with ", task.getException());
                                }
                            }


                        });

                        i++;
                    }while (i<list.size());

                    idGruppi = new String[list.size()];
                    for (int j = 0; j < list.size(); j++) {
                        idGruppi[j] = list.get(j);
                    }


                    ruoli = new String[listaRuoli.size()];
                    for (int j = 0; j < listaRuoli.size(); j++) {
                        if(listaRuoli.get(j).equals("1")){
                            ruoli[j] = "admin";
                        }else{
                            ruoli[j] = "utente normale";
                        }

                    }

                /*
                    listView = findViewById(R.id.listaGruppi);
                    Adapter adapter = new Adapter(getApplicationContext(),nomi,ruoli,idGruppi);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(listaRuoli.get(position).equals("1")){
                                Intent intent = new Intent(getApplicationContext(), GestisciGruppoActivity.class);
                                intent.putExtra("NomeGruppo",listaGruppi.get(position).Nome);
                                intent.putExtra("idGruppo",list.get(position));
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }else{
                                Intent intent = new Intent(getApplicationContext(), VisualizzaGruppoActivity.class);
                                intent.putExtra("NomeGruppo",listaGruppi.get(position).Nome);
                                intent.putExtra("idGruppo",list.get(position));
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }


                        }
                    });
                */

                } else {

                    Log.d("QueryGruppi", "Error getting documents: ", task.getException());

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
            super(c,R.layout.rigagruppo,R.id.NomeGruppoRiga,NomiGruppi);
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
            TextView nomeGruppoRiga = rigagruppo.findViewById(R.id.NomeGruppoRiga);
            TextView ruoloGruppoRiga = rigagruppo.findViewById(R.id.RuoloGruppoRiga);
            TextView idGruppoRiga = rigagruppo.findViewById(R.id.idGruppoRiga);

            nomeGruppoRiga.setText(NomiGruppi[position]);
            ruoloGruppoRiga.setText(RuoliGruppi[position]);
            idGruppoRiga.setText(IdGruppi[position]);


            return rigagruppo;
        }
    }
}