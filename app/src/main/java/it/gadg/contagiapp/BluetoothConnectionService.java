package it.gadg.contagiapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {
    private static final String TAG ="BtConnServ";
    private static final String appName ="ContagiAPP";

    private static final UUID MY_UUID=UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mbluetoothAdapter;
    Context mContext;

    private AcceptThread mAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context){
        mContext = context;
        mbluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread{

        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
           BluetoothServerSocket tmp=null;

           try{
               tmp = mbluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName,MY_UUID);
               Log.d(TAG,"AcceptThread : UUID" + MY_UUID);
           }catch (IOException e){
               Log.e(TAG,"AcceptThread : Errore" + e.getMessage());
           }

           mmServerSocket=tmp;


        }

        public void run(){
            Log.d(TAG,"run : AcceptThread Running" + MY_UUID);

            BluetoothSocket socket = null;

            try{
                Log.d(TAG,"run : RFCOM server socket start..." );
                socket=mmServerSocket.accept();
                Log.d(TAG,"run : RFCOM server socket accepted connection..." );
            }catch (IOException e){
                Log.e(TAG,"AcceptThread : Errore" + e.getMessage());
            }

            if(socket!=null){
                connected(socket,mmDevice);
            }
            Log.i(TAG,"END mAccepThread");
        }

        public void cancel(){
            Log.d(TAG,"cancel : Canceling AcceptThread" );
            try{
                mmServerSocket.close();
            }catch (IOException e){
                Log.e(TAG,"cancel:Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }

    }

    private class ConnectThread extends Thread{

        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device,UUID uuid){
            Log.i(TAG,"ConnectThread : started" );
            mmDevice = device;
            deviceUUID=uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG,"Run mConnectThread");

            try{
                Log.d(TAG,"ConnectThread : Trying to create InsecureRfcommSocket using UUID");
                tmp=mmDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            }catch (IOException e){
                Log.e(TAG,"ConnectThread: Could not create InsecureRfcommSocket . " + e.getMessage());
            }

           mmSocket = tmp;

            mbluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();

                Log.d(TAG,"run:ConnectThread connected");
            } catch (IOException e) {

                try {
                    mmSocket.close();
                    Log.d(TAG,"run: closed socket");
                } catch (IOException e1) {
                    Log.e(TAG,"mConnectThread: run: Unable to close connection in socket . " + e1.getMessage());
                }
                Log.d(TAG,"run: ConnectThread: Could not connect to UUID " + MY_UUID);
            }
            connected(mmSocket,mmDevice);
        }

        public void cancel(){
            try{
                Log.d(TAG,"cancel: Closing Client Socket");
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG,"cancel : close() of mmSocket in Connectthread failed." + e.getMessage());
            }
        }

    }


    public synchronized void start(){
        Log.d(TAG,"start");

        if(mConnectThread!=null){
            mConnectThread.cancel();
            mConnectThread=null;
        }
        if(mAcceptThread==null){
            mAcceptThread= new AcceptThread();
            mAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG,"startClient : Started.");

        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth","Please Wait...",true);

        mConnectThread = new ConnectThread(device,uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread{

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG,"ConnectThread: Starting.");

            mmSocket=socket;
            InputStream tmpIn = null;
            OutputStream tmpOut=null;

            mProgressDialog.dismiss();

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try{
                    bytes=mmInStream.read(buffer);
                    String incomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG,"InputStream"+incomingMessage);
                }catch (IOException e){
                    Log.e(TAG,"read : Error reading to inputstream:"+e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG,"write : writing to outputstream:"+text);

            try{
                mmOutStream.write(bytes);
            }catch (IOException e){
                Log.e(TAG,"write : Error writing to outputstream:"+e.getMessage());
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            }catch (IOException e){

            }
        }


    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {

        Log.d(TAG,"connected: Starting . ");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();

    }

    public void write(byte[] out){
        ConnectThread r;
        Log.d(TAG,"write: Write Called");
        mConnectedThread.write(out);
    }

}
