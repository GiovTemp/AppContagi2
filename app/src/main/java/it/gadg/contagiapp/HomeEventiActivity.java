package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import it.gadg.contagiapp.evento.CreaEventoActivity;
import it.gadg.contagiapp.evento.InviaPartecipazioneEvento;
import it.gadg.contagiapp.evento.ListaEventiActivity;
import it.gadg.contagiapp.modelli.User;

import it.gadg.contagiapp.utente.ModificaUtenteActivity;


public class HomeEventiActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;
    TextView nomeMenu;
    TextView emailMenu;

    User utenteLoggato;

    String nomeUtente;
    String emailUtente;

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_home_eventi);
        } else {
            setContentView(R.layout.activity_home_evento_landscape);
        }





        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));



        mAuth = FirebaseAuth.getInstance();

        /*---------------------MENU------------------------*/
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        textView=findViewById(R.id.positvoText);
        toolbar=findViewById(R.id.toolbarAsl);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        utenteLoggato = (User) i.getSerializableExtra("utenteLoggato");
        View headerLayout = navigationView.getHeaderView(0);
        nomeMenu=headerLayout.findViewById(R.id.nomeMenu);
        nomeMenu.setText(utenteLoggato.nome + " " +utenteLoggato.cognome);
        emailMenu=headerLayout.findViewById(R.id.emailMenu);
        emailMenu.setText(utenteLoggato.email);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        /*---------------------MENU------------------------*/


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
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
            case R.id.nav_impostazioni:
                this.apriImpostazioni();
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
        i.putExtra("utenteLoggato",utenteLoggato);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void homegruppi() {
        Intent i = new Intent(getApplicationContext(), HomeGruppiActivity.class);
        i.putExtra("utenteLoggato",utenteLoggato);
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
    private void apriImpostazioni() {
        Intent i = new Intent(getApplicationContext(), Impostazioni.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void logout() {
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

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_home_evento_landscape);//carico il layout landscape
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_home_eventi);
        }
    }
}