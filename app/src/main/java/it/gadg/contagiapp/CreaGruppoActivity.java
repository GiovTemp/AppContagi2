package it.gadg.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreaGruppoActivity extends AppCompatActivity {

    EditText nGruppo;
    FirebaseFirestore db;

    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    public void CGruppoButton(View view) {

        nGruppo = findViewById(R.id.nGruppo);
        String NomeGruppo = nGruppo.getText().toString();
        Gruppo g = new Gruppo(NomeGruppo);
        db.collection("Gruppi")
                .add(g);

        user.getUid();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);


    }


}



