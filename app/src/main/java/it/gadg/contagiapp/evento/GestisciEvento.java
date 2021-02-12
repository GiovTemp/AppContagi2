package it.gadg.contagiapp.evento;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import it.gadg.contagiapp.MainActivity;
import it.gadg.contagiapp.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class GestisciEvento extends AppCompatActivity {

    FirebaseFirestore db;

    //variabili per il popup di eliminazione dell'evento
    private AlertDialog.Builder PopupEliminazioneEvento;
    private AlertDialog dialog;
    private CardView siEliminazioneE, noEliminazioneE;

    TextView NomeEventoGestione;
    TextView InfoGestione;
    String idEvento;
    String titoloEvento;
    String luogoEvento;
    String dataEvento;
    String oraEvento;

    int flag=0; // variabile per il controllo del buon esito dell'eliminazione dell'evento


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestisci_evento);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        db = FirebaseFirestore.getInstance();

        //estrapoliamo i dati dall'intente
        Intent intent = getIntent();
        titoloEvento =intent.getStringExtra("NomeEvento");
        luogoEvento = intent.getStringExtra("LuogoEvento");
        dataEvento = intent.getStringExtra("DataEvento");
        oraEvento = intent.getStringExtra("OraEvento");

        setTitle(titoloEvento);//settiamo il titolo dell'activity
        NomeEventoGestione = findViewById(R.id.NomeEventoGestione);
        NomeEventoGestione.setText(titoloEvento);

        //settiamo le info dell'evento
        InfoGestione = findViewById(R.id.infoEvento);
        String temp=" il " + dataEvento +" dalle " + oraEvento + "\n\npresso : " +luogoEvento;
        InfoGestione.setText(temp);

        idEvento = intent.getStringExtra("idEvento");

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void richiesteEvento(View view) {
        Intent intent = new Intent(getApplicationContext(), RichiesteEvento.class);
        intent.putExtra("idEvento",idEvento);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void aggEventoCalendario(View view) {


        String[] data = dataEvento.split("/");//splitto la stringa dell'event
        Calendar beginTime = Calendar.getInstance();
        String[] ora = oraEvento.split(":");//splitto la stringa dell'ora
        //setto la data l'orario d'inizio per il calendario
        beginTime.set(Integer.parseInt(data[2]), Integer.parseInt(data[1])-1, Integer.parseInt(data[0]), Integer.parseInt(ora[0]),  Integer.parseInt(ora[1]));
        //lancio intent implicito per il calendario
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())//passo la data e l'ora
                .putExtra(CalendarContract.Events.TITLE, titoloEvento)//passo il titolo
                .putExtra(CalendarContract.Events.EVENT_LOCATION, luogoEvento);//passo il luogo
        startActivity(intent);

    }

    public void eliminaEvento() {


        db.collection("PartecipazioneEvento").whereEqualTo("idEvento", idEvento).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    //elima tutte le partecipazione
                        db.collection("PartecipazioneEvento").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    //segnalo l'errore durante le eliminazioni
                                    flag=1;
                                }
                            }
                        });
                }
            }
        });

        //se tutte le eliminazioni sono andate a buon fine cancello l'evento
        if(flag==0){
            db.collection("Eventi").document(idEvento).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.delEvento), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
        }



    }



    public void condividiEvento(View view) {

        //setto il messaggio
        String messaggio = getResources().getString(R.string.partecipa) +" " + titoloEvento +"\n\n"+getResources().getString(R.string.codiceShare) +" "+ idEvento;

        //creo e lancio l'intent implicito
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, messaggio);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    public void ConfermaEliminazioneEvento(View view) {


        PopupEliminazioneEvento = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupeliminaevento, null);




        siEliminazioneE = contactPopupView.findViewById(R.id.siEliminazioneE);
        noEliminazioneE= contactPopupView.findViewById(R.id.noEliminazioneE);

        PopupEliminazioneEvento.setView(contactPopupView);
        dialog = PopupEliminazioneEvento.create();
        dialog.show();

        siEliminazioneE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminaEvento();
                dialog.dismiss();
            }
        });

        noEliminazioneE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


    }
}