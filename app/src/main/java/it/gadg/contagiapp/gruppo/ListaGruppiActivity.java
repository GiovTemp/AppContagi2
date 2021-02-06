package it.gadg.contagiapp.gruppo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gruppi);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        // the action bar
        ActionBar actionBar = getSupportActionBar();
        // mostra il pulsante per tornare indietro
        actionBar.setDisplayHomeAsUpEnabled(true);


        mAuth= FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        db.collection("GruppoUtenti").whereEqualTo("UID", user.getUid()).whereEqualTo("status", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        flag=queryDocumentSnapshots.size();
                        if(flag==0){
                            setContentView(R.layout.no_gruppi);
                        }else{
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
                            ruoli[j] = getString(R.string.admin);;
                        }else{
                            ruoli[j] = getString(R.string.member);;
                        }

                    }


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
                                intent.putExtra("idGruppo",gr.get(position).id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }


                        }
                    });

                }
            }
        });


        }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }




    class Adapter extends ArrayAdapter<String>{
        Context context;
        String NomiGruppi[];
        String RuoliGruppi[];
        String IdGruppi[];

        Adapter (Context c,String NomiGruppi[] , String RuoliGruppi[], String IdGruppi[]){
            super(c,R.layout.rigagruppo,R.id.NomeMembro,NomiGruppi);
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
            TextView nomeGruppoRiga = rigagruppo.findViewById(R.id.NomeMembro);
            TextView ruoloGruppoRiga = rigagruppo.findViewById(R.id.RischioMembro);
            TextView idGruppoRiga = rigagruppo.findViewById(R.id.idGruppoInvito);

            nomeGruppoRiga.setText(NomiGruppi[position]);
            ruoloGruppoRiga.setText(RuoliGruppi[position]);
            idGruppoRiga.setText(IdGruppi[position]);


            return rigagruppo;
        }
    }
}