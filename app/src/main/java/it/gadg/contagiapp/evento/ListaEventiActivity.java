package it.gadg.contagiapp.evento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.gadg.contagiapp.R;

public class ListaEventiActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth= FirebaseAuth.getInstance();
        FirebaseUser u = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        assert u != null;
        db.collection("ParticipazioneEvento")
                .whereEqualTo("UID", u.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("QueryEventi", document.getId() + " => " + document.getData());
                                Toast.makeText(getApplicationContext(), "Query riuscita", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("QueryEventi", "Error getting documents: ", task.getException());
                            Toast.makeText(getApplicationContext(), "Query Fallita", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventi);
    }
}