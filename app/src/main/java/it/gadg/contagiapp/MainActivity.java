package it.gadg.contagiapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import it.gadg.contagiapp.evento.CreaEventoActivity;
import it.gadg.contagiapp.evento.ListaEventiActivity;
import it.gadg.contagiapp.gruppo.CreaGruppoActivity;
import it.gadg.contagiapp.gruppo.ListaGruppiActivity;
import it.gadg.contagiapp.splash.Splash;


public class MainActivity extends Activity {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();

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
}
