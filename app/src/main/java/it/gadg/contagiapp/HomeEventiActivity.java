package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.gadg.contagiapp.evento.CreaEventoActivity;
import it.gadg.contagiapp.evento.InviaPartecipazioneEvento;
import it.gadg.contagiapp.evento.ListaEventiActivity;
import it.gadg.contagiapp.splash.Splash;
import it.gadg.contagiapp.utente.ModificaUtenteActivity;


public class HomeEventiActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_eventi);

        mAuth = FirebaseAuth.getInstance();



        /*---------------------MENU------------------------*/
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        textView=findViewById(R.id.textView);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                this.home();
                break;
            case R.id.nav_gruppi:
                this.homegruppi();
                break;

            case R.id.nav_eventi:
                break;
            case R.id.nav_logout:
                this.logout();
                break;
            case R.id.nav_profilo:
                this.modificaUtente();
                break;
            case R.id.nav_contatto:
                this.registraContatto();
                break;
        }

        return true;
    }




    public void creaEvento(View view) {
        Intent i = new Intent(getApplicationContext(), CreaEventoActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void ListaEventi(View view) {
        Intent i = new Intent(getApplicationContext(), ListaEventiActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void cercaEvento(View view) {
        Intent i = new Intent(getApplicationContext(), InviaPartecipazioneEvento.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }




    //FUNZIONI MENU

    private void home() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void homegruppi() {
        Intent i = new Intent(getApplicationContext(), HomeGruppiActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void registraContatto() {

        Intent i = new Intent(getApplicationContext(), BtActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void modificaUtente() {
        Intent i = new Intent(getApplicationContext(), ModificaUtenteActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }


    public void logout() {
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

}