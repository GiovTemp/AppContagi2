package it.gadg.contagiapp.gruppo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import it.gadg.contagiapp.R;

public class VisualizzaGruppoActivity extends AppCompatActivity {

    TextView NomeGruppoVisualizza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_gruppo);
        Intent intent = getIntent();
        String temp =intent.getStringExtra("NomeGruppo");
        NomeGruppoVisualizza = findViewById(R.id.NomeGruppoVisualizza);
        NomeGruppoVisualizza.setText(temp);


    }
}