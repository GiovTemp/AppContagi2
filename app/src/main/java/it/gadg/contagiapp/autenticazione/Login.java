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
import it.gadg.contagiapp.modelli.User;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
       getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(null !=  FirebaseAuth.getInstance().getCurrentUser()){
            updateUI(currentUser);
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void updateUI(FirebaseUser currentUser) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("Utenti")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if(!(document.getBoolean("ruolo"))){
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(getApplicationContext(), MainActivityAsl.class);
                            startActivity(i);
                        }

                    }
                });

    }

    public void login(View view) {
        EditText lEmail = findViewById(R.id.lEmail);
        EditText lPassword = findViewById(R.id.lPassword);
        String email = lEmail.getText().toString();
        String password = lPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "signInWithEmail:success");
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.authSucc) ,
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.authFail),
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });


    }


    public void reqReg(View view) {
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}