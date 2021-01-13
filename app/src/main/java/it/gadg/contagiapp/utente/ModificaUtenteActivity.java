package it.gadg.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;
import it.gadg.contagiapp.autenticazione.RegisterActivity;
import it.gadg.contagiapp.modelli.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModificaUtenteActivity extends AppCompatActivity {

    EditText nuovoNome;
    EditText nuovoCognome;
    EditText nuovaEmail;
    EditText nuovaPassword;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_utente);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }


    public void ModificaUtenteButton(View view) {

        nuovoNome = findViewById(R.id.nuovoNome);
        nuovoCognome= findViewById(R.id.nuovoCognome);
        nuovaEmail = findViewById(R.id.nuovaEmail);
        nuovaPassword = findViewById(R.id.nuovaPassword);


        String email = nuovaEmail.getText().toString();
        String password = nuovaPassword.getText().toString();
        String nome = nuovoNome.getText().toString();
        String cognome = nuovoCognome.getText().toString();

        // Validazioni Dati
        if(!nomeValido(nome) )
            Toast.makeText(getApplicationContext(),"Nome non Valido, inserire solo caratteri", Toast.LENGTH_SHORT).show();
        else if(!cognomeValido(cognome)){
            Toast.makeText(getApplicationContext(),"Cognome non Valido, inserire solo caratteri", Toast.LENGTH_SHORT).show();
        }else if(!emailValida(email)){
            Toast.makeText(getApplicationContext(),"Email non Valida", Toast.LENGTH_SHORT).show();
        }
        else if(!passwordValida(password)){
            Toast.makeText(getApplicationContext(),"Password non Valida: minimo 7 caratteri, inserire almeno @#$%^&+= e una maiuscola ", Toast.LENGTH_LONG).show();
        }else {
            this.editFirebaseUser(email,password,nome,cognome);

    }
}

    private void editFirebaseUser(String email, String password, String nome, String cognome) {

    }

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
            // Regex per controllare se il cognome è valido.
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
    }}

