package it.gadg.contagiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import it.gadg.contagiapp.autenticazione.Login;
import it.gadg.contagiapp.modelli.User;

import it.gadg.contagiapp.utente.ModificaUtenteActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView richiestaPopup;
    private TextView LabelRischio;
    private CardView siPopup, noPopup;

    String id;

    //Variabili menù hamburger
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;
    TextView nomeMenu;
    TextView emailMenu;



    FirebaseFirestore db;

    CardView positivoB;
    CardView negativoB;
    CardView inAttesaB;

    ImageView imgTest;

    private int RISCHIO_POSITIVO = 100;
    private int RISCHIO_TEST = 50;
    private int RISCHIO_NEGATIVO = 0;

    private int RISCHIO_ROSSO=49;
    private int RISCHIO_GIALLO=24;

    User utenteLoggato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        id= mAuth.getCurrentUser().getUid();//ricaviamo UID dell'utente
        db = FirebaseFirestore.getInstance();

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));



        /*---------------------MENU------------------------*/
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        textView=findViewById(R.id.positvoText);
        toolbar=findViewById(R.id.toolbar);

        View headerLayout = navigationView.getHeaderView(0);
        nomeMenu=headerLayout.findViewById(R.id.nomeMenu);
        emailMenu=headerLayout.findViewById(R.id.emailMenu);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        /*---------------------MENU------------------------*/

        /*---------------------RIFERIMENTI AI POPUP------------------------*/
        positivoB = findViewById(R.id.positivoB);
        negativoB = findViewById(R.id.invitabutton);
        inAttesaB = findViewById(R.id.inAttesaB);
        LabelRischio = findViewById(R.id.LabelRischio);
        imgTest = findViewById(R.id.imgTest);
        /*---------------------MENU------------------------*/

        Intent i = getIntent();
        utenteLoggato = (User) i.getSerializableExtra("utenteLoggato");//ricaviamo i dati dell'utente loggato


        nomeMenu.setText(utenteLoggato.nome + " " +utenteLoggato.cognome);//impostiamo i valori del menù
        emailMenu.setText(utenteLoggato.email);

        controllaAggiornamento();//verificahiamo se c'è stato l'aggiornamento giornaliero del rischio

        String temp =LabelRischio.getText().toString()+ utenteLoggato.rischio + "%";//impostiamo il rischio di contagio attuale

        if(utenteLoggato.rischio>RISCHIO_ROSSO){
            LabelRischio.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorLigthRed));
        }else if ((utenteLoggato.rischio>RISCHIO_GIALLO)){
            LabelRischio.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorYellow));
        }

        LabelRischio.setText(temp);

        if (utenteLoggato.etichetta.equals("super") || utenteLoggato.etichetta.equals("sicuro") || utenteLoggato.etichetta.equals("incerto")) {

            inAttesaB.setVisibility(View.VISIBLE);

        } else if (utenteLoggato.etichetta.equals("test")) {

            positivoB.setVisibility(View.VISIBLE);
            negativoB.setVisibility(View.VISIBLE);

        } else if (utenteLoggato.etichetta.equals("positivo")) {

            inAttesaB.setVisibility(View.VISIBLE);
        }






    }

    //funzione per controllare se c'è stato l'aggiornamento giornaliero del rischio
    private void controllaAggiornamento() {

        try {

            //apro il DBLite
            SQLiteDatabase myDb = openOrCreateDatabase("DateAggiornamentoRischio", MODE_PRIVATE, null);
            myDb.execSQL("CREATE TABLE IF NOT EXISTS dateAgg(data DATE)");//creo la tabella

            Cursor c = myDb.rawQuery("SELECT * FROM dateAgg WHERE data = date('now')", null);//controllo se vi sono righe contenti la data di oggi

            if(c.getCount()==0){
                myDb.execSQL("INSERT INTO dateAgg(data) VALUES(date('now'))");//aggiungo la data di oggi al DB
                AggiornaRischioThread t = new AggiornaRischioThread();//lancio il thread per l'aggiornamento del rischio
                t.run();
            }
            c.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    /*---------------------MENU------------------------*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                break;

            case R.id.nav_gruppi:
                this.homegruppi();
                break;

            case R.id.nav_eventi:
                this.homeeventi();
                break;

            case R.id.nav_contatto:
                this.registraContatto();
                break;

            case R.id.nav_profilo:
                this.modificaUtente();
                break;

            case R.id.nav_impostazioni:
                this.apriImpostazioni();
                break;

            case R.id.nav_logout:
                this.logout();
                break;
        }

        return true;
    }

    /*---------------------MENU------------------------*/

    @Override
    public void onBackPressed() {
        //Pulsante back disabilitato
    }


    /*---------------------REDIRECT PULSANTIU HOME------------------------*/
    public void registraContatto(View view) {
        Intent i = new Intent(getApplicationContext(), BtActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }



    public void homeEventi(View view) {
        Intent i = new Intent(getApplicationContext(), HomeEventiActivity.class);
        i.putExtra("utenteLoggato",utenteLoggato);
        startActivity(i);
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }


    public void homeGruppi(View view) {
        Intent i = new Intent(getApplicationContext(), HomeGruppiActivity.class);
        i.putExtra("utenteLoggato",utenteLoggato);
        startActivity(i);
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }
    /*---------------------REDIRECT PULSANTIU HOME------------------------*/


    //Funzione per aggiornare manualmente il rischio
    public void aggRischio(View view) {
        AggiornaRischioThread t = new AggiornaRischioThread();//Lancio il thread per aggionare il rischio manualmente
        t.run();

    }


    /*---------------------POPUP------------------------*/

    public void popupPositivo(View view) {

        this.createNewContactDialog(1);
    }

    public void popupInAttesa(View view) {

        this.createNewContactDialog(2);
    }

    public void popupNegativo(View view) {

        this.createNewContactDialog(3);
    }


    //crea il popup
    public void createNewContactDialog(final int i) {


        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupmain, null);
        richiestaPopup = contactPopupView.findViewById(R.id.richiestaPopup);

        if (i == 1) {

            String temp = getString(R.string.confermipos);
            richiestaPopup.setText(temp);

        } else if (i == 2) {

            String temp = getString(R.string.confermi_di_essere_in_attesa_del_test);
            richiestaPopup.setText(temp);
        } else if (i == 3) {

            String temp = getString(R.string.confermineg);
            richiestaPopup.setText(temp);

        }

        siPopup = contactPopupView.findViewById(R.id.siPopup);
        noPopup = contactPopupView.findViewById(R.id.noPopup);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        siPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 1) {

                    //Se sei positivo cambia la tua etichetta da "test" in "positivo"
                    db.collection("Utenti").document(id).update("etichetta", "positivo").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //Se sei positivo cambia il tuo "rischio" di contagio a "100" %

                                db.collection("Utenti").document(id).update("rischio", RISCHIO_POSITIVO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.risPos), Toast.LENGTH_LONG).show();
                                            finish();
                                            Intent i = new Intent(getApplicationContext(), Splash.class);
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } else if (i == 2) {

                    //Se sei in attesa di un test l'etichetta cambierà in "test"
                    db.collection("Utenti").document(id).update("etichetta", "test").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //Se sei in attesa di un test cambia il tuo "rischio" di contagio a "50" %
                                db.collection("Utenti").document(id).update("rischio", RISCHIO_TEST).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.risWait), Toast.LENGTH_LONG).show();
                                            finish();
                                            Intent i = new Intent(getApplicationContext(), Splash.class);
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                } else if (i == 3) {

                    //Se sei negativo cambia la tua etichetta da "test" in "negativo"
                    db.collection("Utenti").document(id).update("etichetta", "super").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //Se sei negativo cambia il tuo "rischio" di contagio a "0" %
                                db.collection("Utenti").document(id).update("rischio", RISCHIO_NEGATIVO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.risNeg), Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(getApplicationContext(), Splash.class);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }

            }
        });

        noPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


    }

    /*---------------------POPUP------------------------*/



    public class AggiornaRischioThread implements Runnable {

        public long rischio =0;
        public int n=0;

        @Override
        public void run() {

            db.collection("Utenti")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {

                                try{
                                    SQLiteDatabase myDb = openOrCreateDatabase("Contatti", MODE_PRIVATE, null);
                                    myDb.execSQL("CREATE TABLE IF NOT EXISTS contattiRegistrati(uid TEXT,data DATE)");

                                    //estraggo tutti i dati relativi agli incotri avvenuti negli ultimi 10 giorni
                                    Cursor c = myDb.rawQuery("SELECT * FROM contattiRegistrati WHERE data >= date('now','-10 day')", null);

                                    int uidIndex = c.getColumnIndex("uid");

                                    c.moveToFirst();
                                    String[] codici = new String [c.getCount()];

                                    do {
                                        codici[n] = c.getString(uidIndex);//salvo tutti gli uid in un array
                                        n++;
                                    } while (c.moveToNext());
                                    int j;
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        j=0;
                                        do{
                                            String temp= document.getId();

                                            //se l'uid dell'utente è nell'array dei codici aggiorno il rischio totale
                                            if(temp.equals(codici[j])){

                                                rischio =rischio + (long)document.get("rischio");

                                            }
                                            j++;
                                        }while(j<n);

                                    }
                                    rischio = rischio/n;//mi calcolo il rischio mecio
                                    //se il rischio ottenuto è superio all'attuale modifico il riscio dell'utente
                                    if(rischio>utenteLoggato.rischio){

                                        utenteLoggato.rischio = rischio;
                                        db.collection("Utenti").document(id).update("rischio", rischio).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                String temp = rischio + "%";
                                                LabelRischio.setText(temp);

                                            }
                                        });
                                    }
                                    toastSuccess(getApplicationContext());
                                }catch (Exception e){
                                    toast(getApplicationContext());
                                }


                            } else {
                                toast(getApplicationContext());
                            }
                        }
                    });





        }


        public void toast(final Context context) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(context, getResources().getString(R.string.noContact), Toast.LENGTH_LONG).show();
                }
            });
        }

        public void toastSuccess(final Context context) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(context, getResources().getString(R.string.riskAgg), Toast.LENGTH_LONG).show();
                }
            });
        }


    }







    /*---------------------MENU------------------------*/


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

    private void homegruppi() {
        Intent i = new Intent(getApplicationContext(), HomeGruppiActivity.class);
        i.putExtra("utenteLoggato",utenteLoggato);
        startActivity(i);
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
    }

    private void homeeventi() {
        Intent i = new Intent(getApplicationContext(), HomeEventiActivity.class);
        i.putExtra("utenteLoggato",utenteLoggato);
        startActivity(i);
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
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

    /*---------------------MENU------------------------*/

    /*---------------------GESTIONE CAMBIO CONFIGURAZIONE------------------------*/

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }

    /*---------------------GESTIONE CAMBIO CONFIGURAZIONE------------------------*/
}
