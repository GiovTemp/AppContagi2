package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.gadg.contagiapp.autenticazione.Login;
import it.gadg.contagiapp.modelli.User;

public class Splash extends AppCompatActivity {

    private static int SPALSH_TIME_OUT = 5000;

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    FirebaseFirestore db;
    User utenteLoggato;
    private String id;
    TextView gadg;
    ImageView logo;

    //Animazioni
    Animation bottomAnimation, middleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash2);


        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation_splash);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation_splash);


        logo = findViewById(R.id.logo);
        gadg = findViewById(R.id.gadg);

        logo.setAnimation(middleAnimation);
        gadg.setAnimation(bottomAnimation);

        //controllo se l'utente è già loggato
        if(null ==  FirebaseAuth.getInstance().getCurrentUser()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            }, 2000);
        }else{
            //Estraggo l'utente
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser u = mAuth.getCurrentUser();
            id = u.getUid();
            db = FirebaseFirestore.getInstance();
            db.collection("Utenti")
                    .document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            utenteLoggato = new User(document.get("nome").toString(),document.get("cognome").toString(),document.get("email").toString());
                            utenteLoggato.etichetta = (String) document.get("etichetta");
                            utenteLoggato.rischio = (Long) document.get("rischio");
                            utenteLoggato.uid = document.getId();
                            utenteLoggato.ruolo= document.getBoolean("ruolo");

                            //reinderizzo in base al tipo d'utente
                            if (utenteLoggato.ruolo){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getApplicationContext(), MainActivityAsl.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.anim,R.anim.no_animation);
                                    }
                                }, 2000);

                            }else{
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                        i.putExtra("utenteLoggato",utenteLoggato);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.anim,R.anim.no_animation);
                                    }
                                }, 2000);


                            }

                        }
                    });
        }





    }
}