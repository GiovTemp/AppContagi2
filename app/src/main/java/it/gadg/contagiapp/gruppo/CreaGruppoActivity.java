package it.gadg.contagiapp.gruppo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.Gruppo;
import it.gadg.contagiapp.modelli.GruppoUtenti;

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

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        // the action bar
        ActionBar actionBar = getSupportActionBar();
        // mostra il pulsante per tornare indietro
        actionBar.setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.gruppoCreaSucc), Toast.LENGTH_LONG).show();

                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.gruppoCreaFail), Toast.LENGTH_LONG).show();

                            }
                       }

                    });


    }

   }
        });
    }
}




