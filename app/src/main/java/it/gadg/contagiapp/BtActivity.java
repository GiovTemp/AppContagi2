package it.gadg.contagiapp;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class BtActivity extends AppCompatActivity implements SensorEventListener {


    private boolean isAccelerometerAvailable;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private GestioneConnessione gestioneConnessione;

       private Button salvaContatto;
    private Button inviaDati;

    private TextView StatoConn;
    private TextView StatoDati;


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
                            setState(getResources().getString(R.string.notConnected));
                            break;
                        case GestioneConnessione.STATE_LISTEN:
                            setState(getResources().getString(R.string.notConnected));
                            break;
                        case GestioneConnessione.STATE_CONNECTING:
                            setState(getResources().getString(R.string.connInCorso));
                            break;
                        case GestioneConnessione.STATE_CONNECTED:
                            setState(getResources().getString(R.string.connCon)+" " + connectedDevice);
                            StatoConn.setText(R.string.ConnSucc);
                            StatoConn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            break;
                    }
                    break;

                case MESSAGE_READ:
                    byte[] buffer = (byte[]) message.obj;
                    String inputBuffer = new String(buffer, 0, message.arg1);

                    //leggo la stringa
                    if(inputBuffer.length()>0){
                        infoContatto = inputBuffer;
                        StatoDati.setText(R.string.DatiRecSucc);
                        StatoDati.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        Toast.makeText(context, getResources().getString(R.string.DatiRecSucc), Toast.LENGTH_SHORT).show();

                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    if( message.getData().getString(TOAST).equals("Fail")){
                        Toast.makeText(context, getResources().getString(R.string.ConnFail), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,getResources().getString(R.string.ConnClose), Toast.LENGTH_SHORT).show();
                    }

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

        StatoDati = findViewById(R.id.StatoDati);
        StatoConn = findViewById(R.id.StatoConn);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser u = mAuth.getCurrentUser();
        message = u.getUid();
        context = this;

        gestioneConnessione = new GestioneConnessione(context, handler);
        init();
        initBluetooth();

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerAvailable=true;
        } else {

            isAccelerometerAvailable=false;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // assigno le direzioni
            float x=event.values[0];
            float y=event.values[1];
            float z=event.values[2];
            if (x>10){
                //chiudo la connessione
                gestioneConnessione.stop();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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


                if(StatoDati.getText().toString().equals(getResources().getString(R.string.DatiRecFail))){
                    gestioneConnessione.stop();
                }else{
                    try{
                        SQLiteDatabase myDb = openOrCreateDatabase("Contatti",MODE_PRIVATE,null);
                        myDb.execSQL("CREATE TABLE IF NOT EXISTS contattiRegistrati(uid TEXT,data DATE)");
                        myDb.execSQL("INSERT INTO contattiRegistrati(uid,data) VALUES( " + "'" + infoContatto + "'," + "datetime())");
                        gestioneConnessione.stop();
                        //Mostrare un avviso a schermo della registrazione effettuata correttamente
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.contRegSucc) , Toast.LENGTH_LONG).show();

                    }catch (Exception e){
                        Toast.makeText(context, getResources().getString(R.string.contRegErr), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

    }

    private void initBluetooth() {
        //verifco che il dispostivo possieda il bt
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, getResources().getString(R.string.btNotFound), Toast.LENGTH_SHORT).show();
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
                        .setMessage(getResources().getString(R.string.geoPerm))
                        .setPositiveButton(getResources().getString(R.string.concedi), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.nega), new DialogInterface.OnClickListener() {
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
        if (isAccelerometerAvailable){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccelerometerAvailable){
            sensorManager.unregisterListener(this);
        }
    }
}