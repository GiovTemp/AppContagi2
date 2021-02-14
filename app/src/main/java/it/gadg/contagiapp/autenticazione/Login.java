package it.gadg.contagiapp.autenticazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.MainActivityAsl;
import it.gadg.contagiapp.R;
import it.gadg.contagiapp.Splash;
import it.gadg.contagiapp.modelli.User;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth; // dichiaro la variabile per gestire l'autenticazione con firebase


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inizializziamo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);//carichiamo il layout

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));// modifichiamo il colore della barra delle zioni del dispositvo
    }


    @Override
    public void onBackPressed() {
        //Pulsante back disabilitato
    }


    @Override
    public void onStart() {
        super.onStart();
        // Controlliamo se l'utente è già loggato
        FirebaseUser currentUser = mAuth.getCurrentUser();// ricavo l'utente loggato
        //se current user è diverso da null gestico l'autologin
        if (null != FirebaseAuth.getInstance().getCurrentUser()) {
            updateUI(currentUser);
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateUI(FirebaseUser currentUser) {

        //estraggo dal Firestore i dati dell'utente per capire che tipo di utente stiamo trattando
        FirebaseFirestore db;//dichiaro la variabile per il firestore
        db = FirebaseFirestore.getInstance();//la inizializzo
        db.collection("Utenti")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        //il ruolo può assumere due valori ( true = ASL, false = utente normale)
                        if (!(document.getBoolean("ruolo"))) {
                            Intent i = new Intent(getApplicationContext(), Splash.class);//reindirizzo verso l'activity main dell'utente normale
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getApplicationContext(), Splash.class);//reinderizzo verso la MainAcitivy per le ASL
                            startActivity(i);
                        }

                    }
                });

    }

    //funzione che verrà chiamata al momento del click sul puslante login
    public void login(View view) {
        EditText lEmail = findViewById(R.id.lEmail);//prendiamo il riferimento al campo di input dell'email
        EditText lPassword = findViewById(R.id.lPassword);//prendiamo il riferimento al campo di input della password
        String email = lEmail.getText().toString();//ricavo il testo dal capmo e lo converto in stringa
        String password = lPassword.getText().toString();

        //chiamo la funzione del Firebase per gestire l'autenticazione
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.authSucc),
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);//chiamo la funzione per reinderizzare verso la activity corretta
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.authFail),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });


    }


    //funzione che verrà chiamata nel momento in cui l'utente clicca sul pulsante per registrarsi
    public void reqReg(View view) {
        finish();
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}