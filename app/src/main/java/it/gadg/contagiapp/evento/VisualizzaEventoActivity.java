package it.gadg.contagiapp.evento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import it.gadg.contagiapp.R;

public class VisualizzaEventoActivity extends AppCompatActivity {
    TextView NomeEventoVisualizza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_evento);
        Intent intent = getIntent();
        String temp =intent.getStringExtra("NomeEvento");
        NomeEventoVisualizza = findViewById(R.id.NomeEventoVisualizza);
        NomeEventoVisualizza.setText(temp);
    }
}