package it.gadg.contagiapp.gruppo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;

public class VisualizzaGruppoActivity extends AppCompatActivity {

    TextView NomeGruppoVisualizza;
    FirebaseFirestore db;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    String idGruppo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_gruppo);
        Intent intent = getIntent();
        String temp =intent.getStringExtra("NomeGruppo");
        idGruppo =intent.getStringExtra("idGruppo");

        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        NomeGruppoVisualizza = findViewById(R.id.NomeGruppoVisualizza);
        NomeGruppoVisualizza.setText(temp);


    }

    public void abbandonaGruppo(View view) {
        db.collection("GruppoUtenti").whereEqualTo("idGruppo", idGruppo).whereEqualTo("UID",mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    db.collection("GruppoUtenti").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Errore : riprova più tardi", Toast.LENGTH_LONG).show();
                            }else{
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Gruppo abbandonato", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void listaMembri(View view) {
        Intent intent = new Intent(getApplicationContext(), MembriGruppo.class);
        intent.putExtra("idGruppo",idGruppo);
        intent.putExtra("ruolo",0);
        startActivity(intent);
    }
}