package it.gadg.contagiapp.evento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void inviaRichiesta(View view) {


        idGruppo = findViewById(R.id.nGruppo);//ricavo l'id del gruppo dal campo nascosto della riga
        final String id = idGruppo.getText().toString();
        //ricavo le informazioni sull'utente loggato
        db.collection("Utenti").document(u.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        long rischio = (long) document.get("rischio");
                        //controllo se il rischio consente l'operazione
                        if (rischio < 50) {
                            //Controllo se esiste già una partecipazione all'evento esistente
                            db.collection("PartecipazioneEvento").whereEqualTo("UID",u.getUid()).whereEqualTo("idEvento",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot document = task.getResult();
                                    if(document.size()>0){
                                        idGruppo.setText("");
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.reqSendErr), Toast.LENGTH_LONG).show();
                                    }else{
                                        try {
                                            //ricavo le informazioni sull'evento e mi assicuro che qesut'ultimo esista
                                            db.collection("Eventi").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            PartecipazioneEvento pE = new PartecipazioneEvento(id, u.getUid());
                                                            pE.status = 0;//preimposto lo stato 0 cioè in attesa
                                                            pE.role = "0";//preimposto il ruolo "membro"
                                                            //inserisco la partecipazione
                                                            db.collection("PartecipazioneEvento").add(pE).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                    if (task.isSuccessful()) {
                                                                        idGruppo.setText("");
                                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.reqSucc), Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();

                                                                    }
                                                                }

                                                            });

                                                        } else {
                                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.eventNotFound), Toast.LENGTH_LONG).show();
                                                        }


                                                    } else {
                                                        Toast.makeText(getApplicationContext(),  getResources().getString(R.string.eventNotFound), Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(),  getResources().getString(R.string.eventIdErr), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }
                            });

                        }else{
                            Toast.makeText(getApplicationContext(),  getResources().getString(R.string.highRiskReq), Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
        });

    }

}