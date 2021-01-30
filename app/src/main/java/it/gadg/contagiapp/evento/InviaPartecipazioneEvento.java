package it.gadg.contagiapp.evento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.PartecipazioneEvento;

public class InviaPartecipazioneEvento extends AppCompatActivity {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    EditText idGruppo;
    FirebaseFirestore db;
    FirebaseUser u;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth= FirebaseAuth.getInstance();
        u = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invia_partecipazione_evento);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


    }

    public void inviaRichiesta(View view) {


        idGruppo = findViewById(R.id.idEventoRichiesta);
        final String id = idGruppo.getText().toString();
        db.collection("Utenti").document(u.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        long rischio = (long) document.get("rischio");
                        if (rischio < 50) {
                            db.collection("PartecipazioneEvento").whereEqualTo("UID",u.getUid()).whereEqualTo("idEvento",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot document = task.getResult();
                                    if(document.size()>0){
                                        idGruppo.setText("");
                                        Toast.makeText(getApplicationContext(), "Richiesta già inoltrata", Toast.LENGTH_LONG).show();
                                    }else{
                                        try {
                                            db.collection("Eventi").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            PartecipazioneEvento pE = new PartecipazioneEvento(id, u.getUid());
                                                            pE.status = 0;
                                                            pE.role = "0";
                                                            db.collection("PartecipazioneEvento").add(pE).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                    if (task.isSuccessful()) {
                                                                        idGruppo.setText("");
                                                                        Toast.makeText(getApplicationContext(), "Richiesta inoltrata correttamente", Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "Errore,riprova più tardi", Toast.LENGTH_LONG).show();

                                                                    }
                                                                }

                                                            });

                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Evento non trovato", Toast.LENGTH_LONG).show();
                                                        }


                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Evento non trovato", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Id Evento non valido", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }
                            });

                        }else{
                            Toast.makeText(getApplicationContext(), "Il tuo rischio è troppo alto ,non puoi inviare richieste", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
        });

    }

}