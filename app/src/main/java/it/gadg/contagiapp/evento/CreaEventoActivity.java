package it.gadg.contagiapp.evento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.Evento;
import it.gadg.contagiapp.modelli.PartecipazioneEvento;

public class CreaEventoActivity extends AppCompatActivity {

    EditText nomeEvento;
    EditText dataEvento;
    EditText oraEvento;
    String apiKey = "AIzaSyB0GDHQfu3ADx8FAwae0XnAVyc4-l87VgY";//chiave per utilizzare google maps
    String nomeLuogo = "";
    String idLuogo="";

    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_evento);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //inizializzo sdk google
        Places.initialize(getApplicationContext(),apiKey);
        PlacesClient placesClient = Places.createClient(this);

        //inizializzo fragment autocomplete places
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        assert autocompleteFragment != null;
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);//filtriamo solo i luoghi
        autocompleteFragment.setHint(getResources().getString(R.string.luogoEvento));//impostiamo manualmente l'hint
        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setHintTextColor(getResources().getColor(R.color.hint));//impostiamo manualmente il colore dell'hint
        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);//nacsonidamo l'icona della lente d'ingrandiemnto
        autocompleteFragment.setCountries("IT");//restrigniamo la riecercan solo a luoghi italiani
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME));

        ((EditText)autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input)).setTextSize(15.0f);

        autocompleteFragment.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.editTextBG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("Luogo","Place :" + place.getName() + "," + place.getId());
                nomeLuogo=place.getName();//ci salviamo il nome del luogo
                idLuogo =place.getId();// ci salviamo l'id del luogo

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("Errore", String.valueOf(status));
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void creaEvento(View view) {

        //creo i collegamenti
        nomeEvento = findViewById(R.id.nomeEvento);
        dataEvento = findViewById(R.id.dataEvento);
        oraEvento = findViewById(R.id.oraEvento);

        //ricavo i valori degli input
        String data = dataEvento.getText().toString();
        String ora = oraEvento.getText().toString();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();//istanzio il db


        if(!dataValida(data) ){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.dataErr), Toast.LENGTH_SHORT).show();}
        else if(!macchinaDelTempo(data)){
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.macchinaDelTempo), Toast.LENGTH_SHORT).show();}
        else if(!oraValido(ora)){
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.oraErr), Toast.LENGTH_SHORT).show();
             }else {
            //estraggo dal db l'utente loggato
            db.collection("Utenti").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                           long rischio = (long) document.get("rischio");
                           //mi assicuro che l'utente possa creare l'evento
                           if(rischio<50){
                               //creo l'oggetto vento
                               final Evento e = new Evento(nomeEvento.getText().toString(),nomeLuogo,idLuogo,dataEvento.getText().toString(), oraEvento.getText().toString(), (int) rischio);
                               db.collection("Eventi").add(e).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                   @Override
                                   public void onComplete(@NonNull Task<DocumentReference> task) {
                                       if (task.isSuccessful()) {
                                           DocumentReference docRef = task.getResult();
                                           String key = docRef.getId();
                                            //creo l'oggetto partecipazione evento
                                           PartecipazioneEvento pE = new PartecipazioneEvento(key,user.getUid());
                                           db.collection("PartecipazioneEvento").add(pE).addOnCompleteListener(new OnCompleteListener<DocumentReference>(){

                                               @Override
                                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                                   if (task.isSuccessful()) {
                                                       Toast.makeText(getApplicationContext(), getResources().getString(R.string.eventSucc), Toast.LENGTH_LONG).show();
                                                       finish();
                                                   }else{
                                                       Toast.makeText(getApplicationContext(), getResources().getString(R.string.eventFail), Toast.LENGTH_LONG).show();

                                                   }
                                               }

                                           });

                                       }
                                   }
                               });
                           }else{
                               Toast.makeText(getApplicationContext(), getResources().getString(R.string.highRisk), Toast.LENGTH_LONG).show();
                           }

                        }else{
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.utenteNonTrovato), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        }
    }

    private boolean macchinaDelTempo(String data) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            //converto la string ain data
            date = format.parse(data);
            Date today = new Date();
            //verifico se la data è nel passato
            if(date.before(today)){
             return false;
            }else{
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.YEAR, 10);
                Date newDate = c.getTime();
                //controllo se la data è in un futuro troppo lontano
               if(date.after(newDate)){
                   return false;
               }else{
                   return true;
               }
            }
        } catch (ParseException e) {

            e.printStackTrace();
        }
        return true;
    }

    private boolean oraValido(String ora) {


        // Regex convalida l'ora nel formato 24h.
        String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

        // Compila ReGex
        Pattern p = Pattern.compile(regex);

        // Se ora è vuoto
        // return false
        if (ora == null) {
            return false;
        }

        // Pattern class contiene il metodo matcher()
        //per trovare la corrispondenza tra un dato e l'ora
        Matcher m = p.matcher(ora);

        // Return se l'ora corrisponde con la stringa Regex
        return m.matches();
    }

    private boolean dataValida(String data) {
        // TODO Conversione stringa in data

        String regex = "^(3[01]|[12][0-9]|0[1-9])/" +
                "(1[0-2]|0[1-9])"
                +"/[0-9]{4}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence)data);
        return matcher.matches();
    }}


