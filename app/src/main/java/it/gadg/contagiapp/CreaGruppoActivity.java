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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static it.gadg.contagiapp.R.id.nGruppo;

public class CreaGruppoActivity extends AppCompatActivity {

    EditText nGruppo;
    FirebaseFirestore db;


    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);


    }

    public void CGruppoButton(View view) {

        nGruppo = findViewById(R.id.nGruppo);
        String NomeGruppo = nGruppo.getText().toString();
        Gruppo g = new Gruppo(NomeGruppo);
        db.collection("Gruppi")
                .add(g).
        addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference docRef = task.getResult();
                    String key = docRef.getId();

                    GruppoUtenti gU = new GruppoUtenti(key,user.getUid());
                    db.collection("GruppoUtenti").add(gU).addOnCompleteListener(new OnCompleteListener<DocumentReference>(){

                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Gruppo utente creato correttamente", Toast.LENGTH_LONG).show();

                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Errore creazione gruppo", Toast.LENGTH_LONG).show();

                            }
                        }

                    });;


        user.getUid();

    }

   }
        });}}




