package it.gadg.contagiapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import it.gadg.contagiapp.evento.CreaEventoActivity;
import it.gadg.contagiapp.evento.InviaPartecipazioneEvento;
import it.gadg.contagiapp.evento.ListaEventiActivity;
import it.gadg.contagiapp.gruppo.CreaGruppoActivity;
import it.gadg.contagiapp.gruppo.InvitiGruppi;
import it.gadg.contagiapp.gruppo.ListaGruppiActivity;
import it.gadg.contagiapp.modelli.User;
import it.gadg.contagiapp.splash.Splash;
import it.gadg.contagiapp.utente.ModificaUtenteActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView richiestaPopup;
    private TextView LabelRischio;
    private Button siPopup, noPopup;

    //Variabili menù hamburger
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;

    public String id;

    FirebaseFirestore db;

    Button positivoB;
    Button negativoB;
    Button inAttesaB;

    private int RISCHIO_POSITIVO = 100;
    private int RISCHIO_TEST = 50;
    private int RISCHIO_NEGATIVO = 0;

    User utenteLoggato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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



        positivoB = findViewById(R.id.positivoB);
        negativoB = findViewById(R.id.negativoB);
        inAttesaB = findViewById(R.id.inAttesaB);
        LabelRischio = findViewById(R.id.LabelRischio);

        //Estraggo l'utente
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u = mAuth.getCurrentUser();
        id = u.getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("Utenti")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        utenteLoggato = new User(document.get("nome").toString(),document.get("cognome").toString(),document.get("email").toString());
                        utenteLoggato.etichetta = (String) document.get("etichetta");
                        utenteLoggato.rischio = (Long) document.get("rischio");
                        utenteLoggato.uid = document.getId();

                        String temp ="Rischio : "+ utenteLoggato.rischio;

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
                });






    }

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

            case R.id.nav_logout:
                this.logout();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        //Pulsante back disabilitato
    }




    public void homeEventi(View view) {
        Intent i = new Intent(getApplicationContext(), HomeEventiActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void homeGruppi(View view) {
        Intent i = new Intent(getApplicationContext(), HomeGruppiActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public void aggRischio(View view) {
        AggiornaRischioThread t = new AggiornaRischioThread();
        t.run();

    }



    public void createNewContactDialog(final int i) {


        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupmain, null);
        richiestaPopup = contactPopupView.findViewById(R.id.richiestaPopup);

        if (i == 1) {
            String temp = "Confermi di essere positivo?";
            richiestaPopup.setText(temp);
        } else if (i == 2) {
            String temp = "Confermi di essere in Attesa?";
            richiestaPopup.setText(temp);
        } else if (i == 3) {
            String temp = "Confermi di essere in Negativo?";
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
                            recreate();
                            if (task.isSuccessful()) {
                                //Se sei positivo cambia il tuo "rischio" di contagio a "100" %
                                db.collection("Utenti").document(id).update("rischio", RISCHIO_POSITIVO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        recreate();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Risulti positivo", Toast.LENGTH_LONG).show();
                                            recreate();
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
                } else if (i == 2) {

                    //Se sei in attesa di un test l'etichetta cambierà in "test"
                    db.collection("Utenti").document(id).update("etichetta", "test").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            recreate();
                            if (task.isSuccessful()) {
                                //Se sei in attesa di un test cambia il tuo "rischio" di contagio a "50" %
                                db.collection("Utenti").document(id).update("rischio", RISCHIO_TEST).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        recreate();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Risulti in Attesa", Toast.LENGTH_LONG).show();
                                            recreate();
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

                } else if (i == 3) {

                    //Se sei negativo cambia la tua etichetta da "test" in "negativo"
                    db.collection("Utenti").document(id).update("etichetta", "super").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            recreate();
                            if (task.isSuccessful()) {
                                //Se sei negativo cambia il tuo "rischio" di contagio a "0" %
                                db.collection("Utenti").document(id).update("rischio", RISCHIO_NEGATIVO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        recreate();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Risulti essere negativo", Toast.LENGTH_LONG).show();
                                            recreate();
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

    public void popupPositivo(View view) {

        this.createNewContactDialog(1);
    }

    public void popupInAttesa(View view) {

        this.createNewContactDialog(2);
    }

    public void popupNegativo(View view) {

        this.createNewContactDialog(3);
    }


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

                                    Cursor c = myDb.rawQuery("SELECT * FROM contattiRegistrati WHERE data >= date('now','-10 day')", null);

                                    int uidIndex = c.getColumnIndex("uid");

                                    c.moveToFirst();
                                    String[] codici = new String [c.getCount()];

                                    do {
                                        codici[n] = c.getString(uidIndex);
                                        System.out.println("-"+c.getString(uidIndex));
                                        n++;
                                    } while (c.moveToNext());
                                    int j;
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        j=0;
                                        do{
                                            String temp= document.getId();

                                            if(temp.equals(codici[j])){

                                                rischio =rischio + (long)document.get("rischio");

                                            }
                                            j++;
                                        }while(j<n);

                                    }
                                    rischio = rischio/n;
                                    toastSuccess(getApplicationContext());
                                }catch (Exception e){
                                    toast(getApplicationContext());
                                }


                            } else {
                                toast(getApplicationContext());
                            }
                        }
                    });



            if(rischio>utenteLoggato.rischio){

                utenteLoggato.rischio = rischio;
                db.collection("Utenti").document(id).update("rischio", rischio).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String temp ="Rischio : "+ rischio;
                        LabelRischio.setText(temp);

                    }
                });
            }


        }


        public void toast(final Context context) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Nessun Contatto trovato", Toast.LENGTH_LONG).show();
                }
            });
        }

        public void toastSuccess(final Context context) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Rischio aggiornato correttamente", Toast.LENGTH_LONG).show();
                }
            });
        }


    }







    //FUNZIONI MENU


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
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void homeeventi() {
        Intent i = new Intent(getApplicationContext(), HomeEventiActivity.class);
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
