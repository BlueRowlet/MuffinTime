package com.figer.game.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.figer.game.GameStage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "BluetoothService";

    private static final String appName = "MYAPP";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    public boolean isConnected() {
        return connected;
    }

    public boolean connected = false;

    public boolean isBtOn() {
        return btOn;
    }

    public boolean btOn = false;

    public String getIncomingMessage() {
        return incomingMessage;
    }

    String incomingMessage = "";

    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

//    ProgressDialog mProgressDialog;
    public BluetoothService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }
    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try{
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try{
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            if(socket != null){
                connected(socket,mmDevice);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }

    }
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            connected(mmSocket,mmDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }
    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }
    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

//        //initprogress dialog
//        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth"
//                ,"Please Wait...",true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    public synchronized void stop(){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mInsecureAcceptThread != null){
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //dismiss the progressdialog when connection is established
//            try{
//                mProgressDialog.dismiss();
//            }catch (NullPointerException e){
//                e.printStackTrace();
//            }


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            connected = true;
        }

        public void run(){
            byte[] buffer = new byte[2048];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    incomingMessage = new String(buffer, 0, bytes);
                    System.out.println("read: " + incomingMessage);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    e.printStackTrace();
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            System.out.println("write: " + text);
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
                e.printStackTrace();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
    }

    public void enableBluetooth(){
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(enableIntent);
            btOn = true;
        }
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            btOn = false;
        }
    }

    public void enableDiscoverability(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
        mContext.startActivity(discoverableIntent);
    }
}

/*
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothService {
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;

    public int mState;

    //SWITCHERS
    private boolean btOn = false;

    public boolean isConnectedOn() {
        return connectedOn;
    }

    private boolean connectedOn;

    String msgIn = null;

    Context mContext;
    private UUID deviceUUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //CLASSES
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    //CONSTRUCTOR
    public BluetoothService(Context context){
        mContext = context;
        mState = BluetoothConstants.STATE_NONE;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectedOn = false;
        start();
    }

    //GETTERS

    public synchronized int getState(){
        return mState;
    }

    public boolean getBtOn(){
        return btOn;
    }

    public boolean getConnected(){return connectedOn;}

    public String getMsgIn() {
        return msgIn;
    }

    //SETTERS
    public synchronized void setState(int state){
        mState = state;
    }

    //FUNCTIONS
    public synchronized void start(){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mAcceptThread == null){
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

//            mAcceptThread = new AcceptThread();
//            mAcceptThread.start();

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        System.out.println("STATE_CONNECTING");
        setState(BluetoothConstants.STATE_CONNECTING);
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(BluetoothConstants.STATE_CONNECTED);
    }

    public synchronized void stop(){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(BluetoothConstants.STATE_NONE);
    }

    public void connectionFailed(){
        mState = BluetoothConstants.STATE_NONE;
    }

    public void connectionLost(){
        mState = BluetoothConstants.STATE_NONE;
    }

    public void write(byte[] out){
        ConnectedThread r;
        synchronized(this){
            if(mState != BluetoothConstants.STATE_CONNECTED){ return; }
            r = mConnectedThread;
        }

        r.write(out);
    }

    //BLUETOOTH FUNCTIONS
    public void enableBluetooth(){
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(enableIntent);
            btOn = true;
        }
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            btOn = false;
        }
    }

    public void enableDiscoverability(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
        mContext.startActivity(discoverableIntent);
    }

    //CLASS DEFINITIONS
    public class AcceptThread extends Thread{
        private final BluetoothServerSocket mServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("game", deviceUUID);
            } catch (IOException e){
                e.printStackTrace();
            }
            mServerSocket = tmp;
            System.out.println(tmp);
        }

        public void run(){
//            BluetoothSocket socket = null;
//            while(mState != BluetoothConstants.STATE_CONNECTED){
//                try{
//                    socket = mServerSocket.accept();
//                } catch (IOException e){
//                    break;
//                }
//
//                if(socket != null){
//                    synchronized (BluetoothService.this){
//                        System.out.println(mState);
//                        switch(mState){
//                            case BluetoothConstants.STATE_LISTEN:
//                                break;
//                            case BluetoothConstants.STATE_CONNECTING:
//                                System.out.println("START");
//                                connected(socket, socket.getRemoteDevice());
//                                break;
//                            case BluetoothConstants.STATE_NONE:
//                            case BluetoothConstants.STATE_CONNECTED:
//                                System.out.println("STATE_CONNECTED");
//                                connectedOn = true;
//                                try{
//                                    socket.close();
//                                } catch(IOException e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                        }
//                    }
//                    try{
//                        mServerSocket.close();
//                    } catch(IOException e){
//                        e.printStackTrace();
//                    }
//                    break;
//                }

                BluetoothSocket socket = null;

                try{
                    socket = mServerSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                }

                //talk about this is in the 3rd
                if(socket != null){
                    connected(socket,mDevice);
                }
            }

        public void cancel(){
            try{
                mServerSocket.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket tmp = null;
            mDevice = device;
        }

        public void run(){
            BluetoothSocket tmp = null;
            try{
                tmp = mDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch(IOException e){
                e.printStackTrace();
            }
            mSocket = tmp;

            mBluetoothAdapter.cancelDiscovery();

            try{
                mSocket.connect();
            } catch(IOException connectException){
                try{
                    mSocket.close();
                } catch(IOException closeException){
                    closeException.printStackTrace();
                }
                return;
            }
//
//            synchronized(BluetoothService.this){
//                mConnectThread = null;
//            }

            connected(mSocket, mDevice);
        }

        public void cancel(){
            try{
                mSocket.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch(IOException e){
                e.printStackTrace();
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = mInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    msgIn = incomingMessage;
                    System.out.println("read " + incomingMessage);
                } catch(IOException e){
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer){
            String text = new String(buffer, Charset.defaultCharset());
            System.out.println("write: " + text);
            try{
                mOutStream.write(buffer);
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        public void cancel(){
            try{
                mSocket.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
*/