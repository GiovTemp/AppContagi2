package it.gadg.contagiapp.gruppo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.GruppoUtenti;

public class GestisciGruppoActivity extends AppCompatActivity {

    FirebaseFirestore db;

    TextView NomeGruppoGestione;
    TextView RuoloGruppoGestione;
    EditText EmailInvito;
    String idGruppo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestisci_gruppo);
        Intent intent = getIntent();
        String temp =intent.getStringExtra("NomeGruppo");
        NomeGruppoGestione = findViewById(R.id.NomeGruppoGestione);
        NomeGruppoGestione.setText(temp);

        RuoloGruppoGestione = findViewById(R.id.RuoloGruppoGestione);
        RuoloGruppoGestione.setText("sei admin");

        idGruppo = intent.getStringExtra("idGruppo");

        db = FirebaseFirestore.getInstance();


    }

    public void invitaUtente(View view) {


        EmailInvito = findViewById(R.id.emailInvito);
        String email = EmailInvito.getText().toString();

        db.collection("Utenti")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){

                            Toast.makeText(GestisciGruppoActivity.this, "Utente non trovato",
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String id = document.getId();

                                db.collection("GruppoUtenti").whereEqualTo("UID",id).whereEqualTo("idGruppo",idGruppo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot document = task.getResult();
                                        if (document.size() == 0) {
                                            GruppoUtenti gU = new GruppoUtenti(idGruppo, id);
                                            gU.ruolo = "0";
                                            gU.status = 0;
                                            db.collection("GruppoUtenti").add(gU).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(GestisciGruppoActivity.this, "Invito inviato correttamente",
                                                                Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(GestisciGruppoActivity.this, "Non siamo riusciti a completare l'operazione",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(GestisciGruppoActivity.this, "Invito già inviato",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        } else {

                            Toast.makeText(GestisciGruppoActivity.this, "Non siamo riusciti a completare l'operazione .",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });




    }
}