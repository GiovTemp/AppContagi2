package it.gadg.contagiapp.gruppo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.GruppoInvito;


public class InvitiGruppi extends AppCompatActivity {

    ListView listView;
    FirebaseFirestore db;
    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    int flag;
    String[] idGruppi;
    String[] ruoli;
    String[] nomi;
    ArrayList<GruppoInvito> gr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviti_gruppi);
        mAuth= FirebaseAuth.getInstance();
        user= mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("GruppoUtenti").whereEqualTo("UID", user.getUid()).whereEqualTo("status", 0).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                flag = queryDocumentSnapshots.size();
                if (flag>0){
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String id = document.getString("idGruppo");
                        final GruppoInvito x = new GruppoInvito(id);


                        db.collection("Gruppi").document((String) document.get("idGruppo")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        x.nome = document.getString("Nome");
                                        salvaGruppo(x);

                                    }
                                } else {
                                    Log.d("Errore", "get failed with ", task.getException());
                                }
                            }

                        });
                    }
            }else{
                    Toast.makeText(getApplicationContext(), "Non hai inviti", Toast.LENGTH_LONG).show();
                }
            }

            private void salvaGruppo(GruppoInvito x) {
                gr.add(x);

                if (gr.size() == flag && flag!=0) {
                    idGruppi = new String[gr.size()];
                    for (int j = 0; j < gr.size(); j++) {
                        idGruppi[j] = gr.get(j).id;
                    }

                    nomi = new String[gr.size()];
                    for (int j = 0; j < gr.size(); j++) {
                        nomi[j] = gr.get(j).nome;
                    }

                    listView = findViewById(R.id.listaInviti);
                    InvitiGruppi.Adapter adapter = new InvitiGruppi.Adapter(getApplicationContext(), nomi, idGruppi);
                    listView.setAdapter(adapter);
                }

            }
        });


    }


    class Adapter extends ArrayAdapter<String> {
        Context context;
        String NomiGruppi[];
        String IdGruppi[];

        Adapter (Context c,String NomiGruppi[] , String IdGruppi[]){
            super(c,R.layout.rigagruppo,R.id.NomeMembro,NomiGruppi);
            this.context = c;
            this.NomiGruppi=NomiGruppi;
            this.IdGruppi=IdGruppi;


        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rigainvito = layoutInflater.inflate(R.layout.rigainvito,parent,false);
            TextView NomeGruppoInvito = rigainvito.findViewById(R.id.NomeMembro);
            TextView idGruppoInvito = rigainvito.findViewById(R.id.idGruppoInvito);

            NomeGruppoInvito.setText(NomiGruppi[position]);
            idGruppoInvito.setText(IdGruppi[position]);

            final Button accetta = rigainvito.findViewById(R.id.accettaInvito);
            accetta.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                              accettaInvito(IdGruppi[position]);

                                           }

                                           private void accettaInvito(String s) {

                                               db.collection("GruppoUtenti").whereEqualTo("UID", user.getUid()).whereEqualTo("idGruppo", s).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                                   @Override
                                                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                       for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                                            db.collection("GruppoUtenti").document(document.getId()).update("status", 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    recreate();
                                                                    if(task.isSuccessful()){
                                                                       Toast.makeText(getApplicationContext(), "Ora fai parte del gruppo :" + NomiGruppi[position], Toast.LENGTH_LONG).show();
                                                                    }else{
                                                                        Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                                                                    }

                                                                }
                                                            });

                                                       }
                                                   }
                                               });

                                           }
                                       }
            );

            final Button rifiuta = rigainvito.findViewById(R.id.rifiutaInvito);
            rifiuta.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                             rifiutaInvito(idGruppi[position]);
                                           }

                                           private void rifiutaInvito(String s) {

                                               db.collection("GruppoUtenti").whereEqualTo("UID", user.getUid()).whereEqualTo("idGruppo", s).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                                   @Override
                                                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                       for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                                           db.collection("GruppoUtenti").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   recreate();
                                                                   if(task.isSuccessful()){
                                                                       Toast.makeText(getApplicationContext(), "Invito rifiutato" +
                                                                               " :" + NomiGruppi[position], Toast.LENGTH_LONG).show();
                                                                   }else{
                                                                       Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                                                                   }

                                                               }
                                                           });

                                                       }
                                                   }
                                               });

                                           }
                                       }
            );

            return rigainvito;
        }
    }


}