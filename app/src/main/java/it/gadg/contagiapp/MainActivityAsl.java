package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.gadg.contagiapp.splash.Splash;

public class MainActivityAsl extends AppCompatActivity {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    TextView emailInput;
    String email;
    private int RISCHIO_POSITIVO = 100;
    Toolbar toolbar;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_asl);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorLogoBlue));
        Window window = this.getWindow();


        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorLogoBlue));
        emailInput = findViewById(R.id.emailAsl);

        toolbar=findViewById(R.id.toolbarAsl);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onBackPressed() {
        //Pulsante back disabilitato
   }

    public void InsPos(View view) {
        email = emailInput.getText().toString();

        db = FirebaseFirestore.getInstance();

        db.collection("Utenti")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){

                            Toast.makeText(MainActivityAsl.this, getResources().getString(R.string.utenteNonTrovato),
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                final String id = document.getId();


                                //Se sei positivo cambia la tua etichetta da "test" in "positivo"
                                db.collection("Utenti").document(id).update("etichetta", "positivo").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        recreate();
                                        if (task.isSuccessful()) {
                                            //Se sei positivo cambia il tuo "rischio" di contagio a "100" %
                                            db.collection("Utenti").document(id).update("rischio", RISCHIO_POSITIVO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    recreate();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.risPosAsl), Toast.LENGTH_LONG).show();
                                                        recreate();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });

                            }
                        } else {

                            Toast.makeText(MainActivityAsl.this, getResources().getString(R.string.errCompOp),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void logout(View view) {
        mAuth.signOut();
        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.logoutSucc),
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), Splash.class);
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.logoutFail),
                    Toast.LENGTH_SHORT).show();
        }
    }
}