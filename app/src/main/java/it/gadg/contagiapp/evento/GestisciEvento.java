package it.gadg.contagiapp.evento;

import androidx.appcompat.app.AppCompatActivity;
import it.gadg.contagiapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class GestisciEvento extends AppCompatActivity {


    TextView NomeEventoGestione;
    TextView RuoloEventoGestione;
    String idEvento;
    String titoloEvento;
    String luogoEvento;
    String dataEvento;
    String oraEvento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestisci_evento);

        Intent intent = getIntent();
        titoloEvento =intent.getStringExtra("NomeEvento");
        luogoEvento = intent.getStringExtra("LuogoEvento");
        dataEvento = intent.getStringExtra("DataEvento");
        oraEvento = intent.getStringExtra("OraEvento");

        NomeEventoGestione = findViewById(R.id.NomeEventoGestione);
        NomeEventoGestione.setText(titoloEvento);

        RuoloEventoGestione = findViewById(R.id.RuoloEventoGestione);
        RuoloEventoGestione.setText("sei admin");

        idEvento = intent.getStringExtra("idEvento");

    }

    public void richiesteEvento(View view) {
        Intent intent = new Intent(getApplicationContext(), RichiesteEvento.class);
        intent.putExtra("idEvento",idEvento);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void aggEventoCalendario(View view) {


        String[] data = dataEvento.split("/");
        Calendar beginTime = Calendar.getInstance();
        String[] ora = oraEvento.split(":");

        beginTime.set(Integer.parseInt(data[2]), Integer.parseInt(data[1])-1, Integer.parseInt(data[0]), Integer.parseInt(ora[0]),  Integer.parseInt(ora[1]));
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, titoloEvento)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, luogoEvento);
        startActivity(intent);

    }
}