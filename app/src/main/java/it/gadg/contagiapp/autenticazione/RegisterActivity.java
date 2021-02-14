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
import com.google.firebase.firestore.FirebaseFirestore;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.User;

import java.util.regex.*;

public class RegisterActivity extends AppCompatActivity {

    //variabili per i campi
    EditText rEmail;
    EditText rPassword;
    EditText rNome;
    EditText rCognome;

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //modifico colore barra delle azioni
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void RegisterButton(View view) {

        //collego i campi alle variabili
        rEmail = findViewById(R.id.rEmail);
        rPassword = findViewById(R.id.rPassword);
        rNome = findViewById(R.id.rNome);
        rCognome = findViewById(R.id.rCognome);


        //estraggo le stringhe
        String email = rEmail.getText().toString();
        String password = rPassword.getText().toString();
        String nome = rNome.getText().toString();
        String cognome = rCognome.getText().toString();


        // Validazioni Dati

        if (!nomeValido(nome))
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nomeErr), Toast.LENGTH_SHORT).show();
        else if (!cognomeValido(cognome)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.cognomeErr), Toast.LENGTH_SHORT).show();
        } else if (!emailValida(email)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.emailErr), Toast.LENGTH_SHORT).show();
        } else if (!passwordValida(password)) {
            Toast.makeText(getApplicationContext(), R.string.passErr, Toast.LENGTH_LONG).show();
        } else {
            this.createFirebaseUser(email, password, nome, cognome);
        }


    }

    //funzione per creare l'utente
    private void createFirebaseUser(final String email, String password, final String nome, final String cognome) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Registrazione", "createUserWithEmail:success");

                            setInfo(nome, cognome, email);//crea utente nel firestore

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Registrazione", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.authFail), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


    }


    //funzion eper creare l'utnete nel firestore
    private void setInfo(String nome, String cognome, String email) {
        User u = new User(nome, cognome, email);//creo un nuovo oggetto Utente
        FirebaseUser user = mAuth.getCurrentUser();//ricavo l'utente appena iscritto

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Utenti").document(user.getUid()).set(u);//salvo l'utente utilizzando l'uid come chiave della coppia


    }


    //TODO aggiungere vincoli di sicurezza per i vari input

    //controllo sul nome
    private boolean nomeValido(String nome) {


        // Regex per controllare se il nome è valdio.
        String regex = "^[A-Za-z]{3,29}$";

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
        String regex = "^[A-Za-z]{3,29}$";

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


    private boolean emailValida(String email) {
        {
            // Regex per controllare se il cognome è valido.
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            // Compila il ReGex
            Pattern pat = Pattern.compile(emailRegex);

            // Se l'email è vuota
            // return false
            if (email == null)
                return false;

            // Return se l'email corrisponde con la stringa Regex
            return pat.matcher(email).matches();
        }
    }

    private boolean passwordValida(String password) {
        {

            // Regex per controllare se la password è valida.
            String regex = "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[@#$%^&+=])"
                    + "(?=\\S+$).{8,20}$";

            // Compila il ReGex
            Pattern p = Pattern.compile(regex);

            // Se la password è vuota
            // return false
            if (password == null)
                return false;


            // Pattern class contiene il metodo matcher()
            //per trovare la corrispondenza tra un dato e la password
            Matcher m = p.matcher(password);

            // Return se la password corrisponde con la stringa Regex
            return m.matches();

        }
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}