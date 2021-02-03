package it.gadg.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;
import it.gadg.contagiapp.autenticazione.RegisterActivity;
import it.gadg.contagiapp.modelli.User;
import it.gadg.contagiapp.splash.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModificaUtenteActivity extends AppCompatActivity {

    EditText nuovoNome;
    EditText nuovoCognome;

    EditText passwordMod;
    User utenteLoggato;
    FirebaseFirestore db;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_utente);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        db.collection("Utenti")
                .document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        utenteLoggato = new User(document.get("nome").toString(), document.get("cognome").toString(), document.get("email").toString());
                        utenteLoggato.etichetta = (String) document.get("etichetta");
                        utenteLoggato.rischio = (Long) document.get("rischio");
                        utenteLoggato.uid = document.getId();

                        nuovoNome = findViewById(R.id.nuovoNome);
                        nuovoCognome = findViewById(R.id.nuovoCognome);

                        passwordMod = findViewById(R.id.passwordMod);


                        nuovoNome.setText(utenteLoggato.nome);
                        nuovoCognome.setText(utenteLoggato.cognome);

                    }
                });




    }


    public void ModificaUtenteButton(View view) {


        String nome = nuovoNome.getText().toString();
        String cognome = nuovoCognome.getText().toString();


        // Validazioni Dati
        if (!nomeValido(nome))
            Toast.makeText(getApplicationContext(), "Nome non Valido, inserire solo caratteri", Toast.LENGTH_SHORT).show();
        else if (!cognomeValido(cognome)) {
            Toast.makeText(getApplicationContext(), "Cognome non Valido, inserire solo caratteri", Toast.LENGTH_SHORT).show();
        } else if (!passwordMod.getText().toString().equals("")) {
            this.editFirebaseUser(nome, cognome);
        } else {
            Toast.makeText(getApplicationContext(), "Inserisci password", Toast.LENGTH_LONG).show();
        }


    }

    private void editFirebaseUser(final String nome, final String cognome) {

        mAuth.signInWithEmailAndPassword(utenteLoggato.email, passwordMod.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    db.collection("Utenti").document(utenteLoggato.uid).update("nome", nome).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            recreate();
                            if (task.isSuccessful()) {
                                db.collection("Utenti").document(utenteLoggato.uid).update("cognome", cognome).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        recreate();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Dati aggiornati correttamente", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Password errata", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean nomeValido(String nome) {


        // Regex per controllare se il nome è valdio.
        String regex = "^[A-Za-z]\\w{2,29}$";

        // Compila il ReGex
        Pattern p = Pattern.compile(regex);

        // se il nome è vuoto
        // return false
        if (nome == null) {
            return false;
        }

        // Pattern class contiene il metodo matcher()
        //per trovare la corrispondenza tra un dato e il Nome
        Matcher m = p.matcher(nome);

        // Return se il nome corrisponde con la stringa Regex
        return m.matches();
    }

    // Controllo sul cognome
    private boolean cognomeValido(String cognome) {


        // Regex per controllare se il cognome è valido.
        String regex = "^[A-Za-z]\\w{3,29}$";

        // Compila il ReGex
        Pattern p = Pattern.compile(regex);

        // Se il il cognome è vuoto
        // return false
        if (cognome == null) {
            return false;
        }

        // Pattern class contiene il metodo matcher()
        //per trovare la corrispondenza tra un dato e il Cognome
        Matcher m = p.matcher(cognome);

        // Return se il nome corrisponde con la stringa Regex
        return m.matches();
    }


}

