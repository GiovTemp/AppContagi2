package it.gadg.contagiapp;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BtActivity extends AppCompatActivity {



    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private GestioneConnessione gestioneConnessione;

       private Button salvaContatto;
    private Button inviaDati;


    private final int LOCATION_PERMISSION_REQUEST = 101;
    private final int SELECT_DEVICE = 102;

    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;


    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";
    private String connectedDevice;
    private String message;
    private String infoContatto;
    private FirebaseAuth mAuth;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case GestioneConnessione.STATE_NONE:
                            setState("Non Connesso");
                            break;
                        case GestioneConnessione.STATE_LISTEN:
                            setState("Non Connesso");
                            break;
                        case GestioneConnessione.STATE_CONNECTING:
                            setState("Connessione in corso...");
                            break;
                        case GestioneConnessione.STATE_CONNECTED:
                            setState("Connesso con : " + connectedDevice);
                            break;
                    }
                    break;

                case MESSAGE_READ:
                    byte[] buffer = (byte[]) message.obj;
                    String inputBuffer = new String(buffer, 0, message.arg1);

                    //leggo la stringa
                    if(inputBuffer.length()>0){
                        infoContatto = inputBuffer;
                        Toast.makeText(context, "Dati ricevuti correttamente", Toast.LENGTH_SHORT).show();
                        //TODO settare visibilità pulsante conferma
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context, message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void setState(CharSequence subTitle) {
        getSupportActionBar().setSubtitle(subTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u = mAuth.getCurrentUser();
        message = u.getUid();
        context = this;

        gestioneConnessione = new GestioneConnessione(context, handler);
        init();
        initBluetooth();

    }

    private void init() {

        salvaContatto = findViewById(R.id.salvaContatto);
        inviaDati = findViewById(R.id.inviaDati);



        inviaDati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               gestioneConnessione.write(message.getBytes());

            }
        });

        salvaContatto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO cambiare message con infoContatto
                //Salvo il contatto del db locale

                //context.deleteDatabase("Conttati");
                //myDb.execSQL("DROP TABLE contattiRegistrati");

                try{
                    SQLiteDatabase myDb = openOrCreateDatabase("Contatti",MODE_PRIVATE,null);
                    myDb.execSQL("CREATE TABLE IF NOT EXISTS contattiRegistrati(uid TEXT,data DATE)");
                    myDb.execSQL("INSERT INTO contattiRegistrati(uid,data) VALUES( " + " ' " + message + "'," + "datetime())");
                    gestioneConnessione.stop();
                    //Mostrare un avviso a schermo della registrazione effettuata correttamente
                    Toast.makeText(getApplicationContext(), "Contatto Registrato con successo", Toast.LENGTH_LONG).show();

                }catch (Exception e){
                    Toast.makeText(context, "Errore non siamo riusciti a salvare il contatto", Toast.LENGTH_LONG).show();
                }





            }
        });




    }

    private void initBluetooth() {
        //verifco che il dispostivo possieda il bt
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth non trovato", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inserisco opzioni menu
        getMenuInflater().inflate(R.menu.menu_bt_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //lancio le funzioni in base al pulsante del menu selezionato
        switch (item.getItemId()) {
            case R.id.menu_search_devices:
                //controllo che ci siano i permessi
                checkPermissions();
                return true;
            case R.id.menu_enable_bluetooth:
                enableBluetooth();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        //Controllo i permessi per la geolocalizzazione da usare per individuare i dispositivi vicini
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BtActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(context, DeviceListActivity.class);
            startActivityForResult(intent, SELECT_DEVICE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            String address = data.getStringExtra("deviceAddress");
            gestioneConnessione.connect(bluetoothAdapter.getRemoteDevice(address));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(intent, SELECT_DEVICE);
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Il permesso di geolicalizzazione è necessario per il funzionamento dell'app .")
                        .setPositiveButton("Concedi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton("Nega", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BtActivity.this.finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void enableBluetooth() {
        //se il bt non è attivato lo attivo
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        //rendo il dispositvo visibile e lo metto in stato di ricerca
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //distruggo la connessione se l'utente dovesse cliccare sul tasto per tornare indietro
        if (gestioneConnessione != null) {
            gestioneConnessione.stop();
        }
    }
}