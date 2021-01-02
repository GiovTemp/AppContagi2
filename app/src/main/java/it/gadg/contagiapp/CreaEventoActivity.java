package it.gadg.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class CreaEventoActivity extends AppCompatActivity {

    EditText nomeEvento;
    EditText luogoEvento;
    EditText dataEvento;
    EditText oraEvento;
    String apiKey = "AIzaSyB0GDHQfu3ADx8FAwae0XnAVyc4-l87VgY";
    String nomeLuogo = "";
    String idLuogo="";

    FirebaseUser user;
    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_evento);
        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //inizializzo sdk google
        Places.initialize(getApplicationContext(),apiKey);
        PlacesClient placesClient = Places.createClient(this);

        //inizializzo fragment autocomplete places

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-33.880490,151.184363),
                new LatLng(-33.858754,151.229596)));
        autocompleteFragment.setCountries("IT");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i("Luogo","Place :" + place.getName() + "," + place.getId());
                nomeLuogo=place.getName();
                idLuogo =place.getId();

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("Errore", String.valueOf(status));
            }
        });

    }

    public void creaEvento(View view) {

        nomeEvento = findViewById(R.id.nomeEvento);

        dataEvento = findViewById(R.id.dataEvento);
        oraEvento = findViewById(R.id.oraEvento);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //TODO implementazione autofill google maps
        //TODO get coordinate da google mas

        final Evento e = new Evento(nomeEvento.getText().toString(),nomeLuogo,idLuogo,dataEvento.getText().toString(), oraEvento.getText().toString(),1);
        db.collection("Eventi").add(e).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        DocumentReference docRef = task.getResult();
                                        String key = docRef.getId();

                                        PartecipazioneEvento pE = new PartecipazioneEvento(key,user.getUid());
                                        db.collection("PartecipazioneEvento").add(pE).addOnCompleteListener(new OnCompleteListener<DocumentReference>(){

                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Evento creato correttamente", Toast.LENGTH_LONG).show();

                                                    finish();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Errore creazione evento", Toast.LENGTH_LONG).show();

                                                }
                            }

                        });

                }
            }
        });





    }


}