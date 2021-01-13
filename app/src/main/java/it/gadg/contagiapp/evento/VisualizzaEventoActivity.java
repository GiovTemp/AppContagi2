package it.gadg.contagiapp.evento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import it.gadg.contagiapp.R;

public class VisualizzaEventoActivity extends AppCompatActivity {
    TextView NomeEventoVisualizza;
    String titoloEvento;
    String luogoEvento;
    String dataEvento;
    String oraEvento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_evento);
        Intent intent = getIntent();
        titoloEvento =intent.getStringExtra("NomeEvento");
        luogoEvento = intent.getStringExtra("LuogoEvento");
        dataEvento = intent.getStringExtra("DataEvento");
        oraEvento = intent.getStringExtra("OraEvento");

        NomeEventoVisualizza = findViewById(R.id.NomeEventoVisualizza);
        NomeEventoVisualizza.setText(titoloEvento);
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