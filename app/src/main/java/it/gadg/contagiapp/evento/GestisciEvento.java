package it.gadg.contagiapp.evento;

import androidx.appcompat.app.AppCompatActivity;
import it.gadg.contagiapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

public class GestisciEvento extends AppCompatActivity {


    TextView NomeEventoGestione;
    TextView RuoloEventoGestione;
    String idEvento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestisci_evento);

        Intent intent = getIntent();
        String temp =intent.getStringExtra("NomeEvento");
        NomeEventoGestione = findViewById(R.id.NomeEventoGestione);
        NomeEventoGestione.setText(temp);

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
}