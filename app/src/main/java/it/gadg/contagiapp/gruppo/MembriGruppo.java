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
import java.util.Arrays;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.MembroGruppo;

public class MembriGruppo extends AppCompatActivity {
    FirebaseFirestore db;


    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    String idGruppo;
    String[] idUtenti;
    String[] cognomi;
    String[] rischi;
    String[] nomi;
    int flag;
    int permessi;
    ArrayList<MembroGruppo> m = new ArrayList<>();

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membri_gruppo);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        idGruppo = intent.getStringExtra("idGruppo");
        permessi =intent.getIntExtra("ruolo",0);

        db.collection("GruppoUtenti").whereEqualTo("idGruppo", idGruppo).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                flag = queryDocumentSnapshots.size();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    String id = document.getString("UID");
                    if (!id.equals(mAuth.getUid())) {


                        final MembroGruppo x = new MembroGruppo();
                        x.uid = id;

                        db.collection("Utenti").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        x.nome = document.getString("nome");
                                        x.cognome = document.getString("cognome");
                                        x.nome = document.getString("nome");
                                        x.rischio = (long) document.get("rischio");
                                        salvaUtente(x);

                                    }
                                } else {
                                    Log.d("Errore", "get failed with ", task.getException());
                                }
                            }

                        });
                    }
                }

            }

            private void salvaUtente(MembroGruppo x) {
                m.add(x);

                System.out.println("ciao");

                if (m.size() == flag-1) {
                    idUtenti = new String[m.size()];
                    for (int j = 0; j < m.size(); j++) {
                        idUtenti[j] = m.get(j).uid;
                    }

                    nomi = new String[m.size()];
                    for (int j = 0; j < m.size(); j++) {
                        nomi[j] = m.get(j).nome;
                    }

                    cognomi = new String[m.size()];
                    for (int j = 0; j < m.size(); j++) {
                        cognomi[j] = m.get(j).nome;
                    }

                    rischi = new String[m.size()];
                    for (int j = 0; j < m.size(); j++) {
                        rischi[j] = String.valueOf(m.get(j).rischio);
                    }

                    System.out.println("ciao"+Arrays.toString(nomi));

                    listView = findViewById(R.id.listaMembri);
                    MembriGruppo.Adapter adapter = new MembriGruppo.Adapter(getApplicationContext(), nomi, cognomi, rischi, idUtenti);
                    listView.setAdapter(adapter);


                }
            }
        });
    }

    class Adapter extends ArrayAdapter<String> {
        Context context;
        String NomiUtenti[];
        String CognomiUtenti[];
        String Rischi[];
        String IdUtenti[];

        Adapter(Context c, String NomiUtenti[], String CognomiUtenti[], String Rischi[], String IdUtenti[]) {
            super(c, R.layout.rigamembro, R.id.NomeMembro, NomiUtenti);
            this.context = c;
            this.NomiUtenti = NomiUtenti;
            this.CognomiUtenti = CognomiUtenti;
            this.Rischi = Rischi;
            this.IdUtenti = IdUtenti;


        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rigamembro = layoutInflater.inflate(R.layout.rigamembro, parent, false);
            TextView nomeMembro = rigamembro.findViewById(R.id.NomeMembro);
            TextView rischioMembro = rigamembro.findViewById(R.id.RischioMembro);
            TextView idMembro = rigamembro.findViewById(R.id.idMembro);


            String temp = NomiUtenti[position] + " " + CognomiUtenti[position];
            idMembro.setText(IdUtenti[position]);
            nomeMembro.setText(temp);
            rischioMembro.setText(Rischi[position]);
            final Button btn = rigamembro.findViewById(R.id.espelliMembro);
            if (permessi==1){
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        espelliMembro(idUtenti[position]);
                    }

                    private void espelliMembro(String id) {
                        db.collection("GruppoUtenti").whereEqualTo("idGruppo", idGruppo).whereEqualTo("UID", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    db.collection("GruppoUtenti").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Errore : riprova più tardi", Toast.LENGTH_LONG).show();
                                            } else {
                                                recreate();
                                                Toast.makeText(getApplicationContext(), "Utente espulso", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }


            return rigamembro;
        }
    }


}