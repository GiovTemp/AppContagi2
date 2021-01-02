package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreaEventoActivity extends AppCompatActivity {

    EditText nomeEvento;
    EditText luogoEvento;
    EditText dataEvento;
    EditText oraEvento;

    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_evento);
        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public void creaEvento(View view) {
        nomeEvento = findViewById(R.id.nomeEvento);
        luogoEvento = findViewById(R.id.luogoEvento);
        dataEvento = findViewById(R.id.dataEvento);
        oraEvento = findViewById(R.id.oraEvento);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //TODO implementazione autofill google maps
        //TODO get coordinate da google mas

        final Evento e = new Evento(nomeEvento.getText().toString(),"1","1",dataEvento.getText().toString(), oraEvento.getText().toString(),1);
        db.collection("Eventi").add(e).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        DocumentReference docRef = task.getResult();
                                        String key = docRef.getId();

                                        PartecipazioneEvento pE = new PartecipazioneEvento(key,user.getUid());
                                        db.collection("PartecipazioneEvento").add(pE).addOnCompleteListener(new OnCompleteListener<DocumentReference>(){

                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Evento creato correttamente", Toast.LENGTH_LONG).show();

                                                    finish();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Errore creazione evento", Toast.LENGTH_LONG).show();

                                                }
                            }

                        });

                }
            }
        });





    }
}