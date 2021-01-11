package it.gadg.contagiapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import it.gadg.contagiapp.evento.CreaEventoActivity;
import it.gadg.contagiapp.evento.InviaPartecipazioneEvento;
import it.gadg.contagiapp.evento.ListaEventiActivity;
import it.gadg.contagiapp.gruppo.CreaGruppoActivity;
import it.gadg.contagiapp.gruppo.InvitiGruppi;
import it.gadg.contagiapp.gruppo.ListaGruppiActivity;
import it.gadg.contagiapp.splash.Splash;


public class MainActivity extends Activity {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    FirebaseFirestore db;
    Button positivoB;
    Button negativoB;
    Button inAttesaB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser u = mAuth.getCurrentUser();
        String id = u.getUid();
        positivoB = findViewById(R.id.positivoB);
        negativoB = findViewById(R.id.negativoB);
        inAttesaB = findViewById(R.id.inAttesaB);
        db = FirebaseFirestore.getInstance();


        db.collection("Utenti")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        String etichetta = (String) document.get("etichetta");
                        if (etichetta.equals("super")||etichetta.equals("sicuro")||etichetta.equals("incerto")){
                            inAttesaB.setVisibility(View.VISIBLE);
                        }
                              else if (etichetta.equals("test")){

                                  positivoB.setVisibility(View.VISIBLE);
                                  negativoB.setVisibility(View.VISIBLE);
                        }

                                else if (etichetta.equals("positivo")){

                                    inAttesaB.setVisibility(View.VISIBLE);
                                    }

                    }
                });
    }

    public void logout(View view) {
        mAuth.signOut();
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            Toast.makeText(getApplicationContext(), "Logout riuscito.",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Logout fallito.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void creaEvento(View view) {
        Intent i = new Intent(getApplicationContext(), CreaEventoActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }

    public void creaGruppo(View view) {
        Intent i = new Intent(getApplicationContext(), CreaGruppoActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void ListaEventi(View view) {
        Intent i = new Intent(getApplicationContext(), ListaEventiActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void ListaGruppi(View view) {
        Intent i = new Intent(getApplicationContext(), ListaGruppiActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void ListaInviti(View view) {
        Intent i = new Intent(getApplicationContext(), InvitiGruppi.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void cercaEvento(View view) {
        Intent i = new Intent(getApplicationContext(), InviaPartecipazioneEvento.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void registraContatto(View view) {
        //TODO scambiare informazioni tramite bluetooth ogetto(UID,rischio,data)
        Intent i = new Intent(getApplicationContext(), BtActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
