package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.gadg.contagiapp.modelli.User;
import it.gadg.contagiapp.splash.Splash;

public class Impostazioni extends AppCompatActivity {
    User utenteLoggato;
    FirebaseFirestore db;


    private AlertDialog.Builder PopupInfo;
    private AlertDialog.Builder PopupCancella;
    private AlertDialog dialog;
    private CardView siCancella, noCancella;
    SwitchCompat switchInglese;
    SharedPreferences preferences;

    boolean stateswitch1;


    int flag = 0;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        preferences = getSharedPreferences("PREFS",0);
        stateswitch1 = preferences.getBoolean("switch1", false);
        switchInglese = (SwitchCompat)  findViewById(R.id.switchInglese);
        switchInglese.setChecked(stateswitch1);

        switchInglese.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stateswitch1 = !stateswitch1;
                switchInglese.setChecked(stateswitch1);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("switch1",stateswitch1);
                editor.apply();
            }

        });

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


                    }
                });

    }

    public void cambiaLingua(View view) {
    }

    public void info(View view) {
    }

    public void cancellaProfilo(View view) {

        PopupCancella = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupcancella, null);




        siCancella = contactPopupView.findViewById(R.id.siCanc);
        noCancella= contactPopupView.findViewById(R.id.noCanc);

        final EditText p =contactPopupView.findViewById(R.id.passwordCanc);

        PopupCancella.setView(contactPopupView);
        dialog = PopupCancella.create();
        dialog.show();



        siCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminaProfilo(p.getText().toString());
                dialog.dismiss();
            }
        });

        noCancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }



    public void eliminaProfilo(String password) {

        if ((!password.equals(""))) {

            mAuth.signInWithEmailAndPassword(utenteLoggato.email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        db.collection("GruppoUtenti").whereEqualTo("UID", utenteLoggato.uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                    if (document.get("ruolo").equals("0")) {
                                        db.collection("GruppoUtenti").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });
                                    } else {
                                        stop();
                                    }


                                }
                            }
                        });


                        if (flag == 0) {
                            db.collection("PartecipazioneEvento").whereEqualTo("UID", utenteLoggato.uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        if (document.get("ruolo").equals("0")) {
                                            db.collection("PartecipazioneEvento").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                        } else {
                                            stop();
                                        }


                                    }
                                }
                            });

                        }


                        if (flag == 0) {
                            db.collection("Utenti").document(utenteLoggato.uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mAuth.signOut();
                                                Toast.makeText(getApplicationContext(), "Utente , eliminato correttamente", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(getApplicationContext(), Splash.class);
                                                startActivity(i);

                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Errore , riprova più tardi", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Password Errata", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Inserisci la password", Toast.LENGTH_LONG).show();
        }
    }

    private void stop() {
        flag = 1;
        Toast.makeText(getApplicationContext(), "Non puoi elimare il tuo profilo finchè ci saranno gruppi o eventi di cui sei admin", Toast.LENGTH_LONG).show();
    }

    public void VisualizzazioneInfo(View view) {


        PopupInfo = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupinfo, null);




        PopupInfo.setView(contactPopupView);
        dialog = PopupInfo.create();
        dialog.show();



    }
}