package it.gadg.contagiapp.autenticazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    EditText rEmail;
    EditText rPassword;
    EditText rNome;
    EditText rCognome;

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    public void RegisterButton(View view) {

        System.out.println("ciao");
        rEmail = findViewById(R.id.rEmail);
        rPassword= findViewById(R.id.rPassword);
        rNome = findViewById(R.id.rNome);
        rCognome = findViewById(R.id.rCognome);


        String email = rEmail.getText().toString();
        String password = rPassword.getText().toString();
        String nome = rNome.getText().toString();
        String cognome = rCognome.getText().toString();


        // Validazioni Dati
        if(!nomeValido(nome) )
            Toast.makeText(getApplicationContext(),"Nome non Valido", Toast.LENGTH_SHORT).show();
        else if(!cognomeValido(cognome)){
            Toast.makeText(getApplicationContext(),"Cognome non Valido", Toast.LENGTH_SHORT).show();
        }else if(!emailValida(email)){
            Toast.makeText(getApplicationContext(),"Email non Valida", Toast.LENGTH_SHORT).show();
        }
        else if(!passwordValida(password)){
            Toast.makeText(getApplicationContext(),"Password non Valida ( Minimo 7 caratteri) ", Toast.LENGTH_SHORT).show();
        }else {
            this.createFirebaseUser(email,password,nome,cognome);
        }


    }

    private void createFirebaseUser(final String email, String password, final String nome, final String cognome){


        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("Registrazione", "createUserWithEmail:success");

                            setInfo(nome,cognome,email);
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("Registrazione", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                        }


                    }
                });



    }



    private void setInfo(String nome,String cognome,String email){
       User u = new User(nome,cognome,email);
       FirebaseUser user = mAuth.getCurrentUser();

       FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Utenti").document(user.getUid()).set(u);



    }



    //TODO aggiungere vincoli di sicurezza per i vari input

    //controllo sul nome
    private boolean nomeValido(String nome){


            // Regex per controllare se il nome è valdio.
            String regex = "^[A-Za-z]\\w{3,29}$";

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
    private boolean cognomeValido(String cognome){


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


    private boolean emailValida(String email){
        {
            // Regex per controllare se l'email è valida.
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
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

    private boolean passwordValida(String password){
        return password.length()>7;
    }

}