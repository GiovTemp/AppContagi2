package it.gadg.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreaGruppoActivity extends AppCompatActivity {

    EditText nGruppo;
    FirebaseFirestore db;

    public void CGruppoButton(View view) {

        nGruppo = findViewById(R.id.nGruppo);
        String NomeGruppo = nGruppo.getText().toString();
        Gruppo g = new Gruppo(NomeGruppo);
        db.collection("Gruppi")
                .add(g);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);
    }


}



