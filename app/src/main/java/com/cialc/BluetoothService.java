package com.cialc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    public static BluetoothService instance;
    Context context;
    Activity activity;
    BluetoothSocket mSocket;
    BluetoothDevice mDevice;
    OutputStream mOutputStream;
    boolean state = false;
    public static byte S_Bluetooth=0;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<String> decivesAvailable;
    ArrayAdapter<String> arrayAdapter;
    public OnResponse onResponse;

    //Singleton.
    public static synchronized BluetoothService getInstance(Context context, Activity activity){

        if(instance == null){
            instance = new BluetoothService(context,activity);
        }
        return instance;
    }

    public BluetoothService(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.decivesAvailable = new ArrayList<String>();
    }

    //***************************************************************************************
    //                      CONFIGURACIÓN Y CONEXIÓN BLUETOOTH
    //***************************************************************************************
    //Funcion que configura, activa y busca los perifericos conectados con el dispositivo.
    public void configBluetooth(OnConnect onConnect){
        if(bluetoothAdapter == null){
            Toast.makeText(context, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }
        else{
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, 1);
            }
            else{
                //Find devices
                final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    String devices = "";
                    String MacDevices="";
                    String Device_all_info="";
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        devices += deviceName + ",";
                        MacDevices+=deviceHardwareAddress + ",";
                        Device_all_info += deviceName + "\n" + deviceHardwareAddress + "/";
                    }
                    final String[] namesDevices = devices.split(",");
                    final String[] MACAddress = MacDevices.split(",");
                    String[] DEVICE_ALL_INFO = Device_all_info.split("/");
                    //Alert showing devices information.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(DEVICE_ALL_INFO, (dialog, which) -> {
                        for(BluetoothDevice device : pairedDevices){
                            if(device.getName().equals(namesDevices[which])){
                                mDevice=device;
                                Toast.makeText(context, "Conectando a: "+device.getName(), Toast.LENGTH_SHORT).show();
                                try {
                                    progress_dialog();
                                    bluetoothConect(onConnect);
                                } catch (Exception e) {
                                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                            .setTitle("Available devices")
                            .setCancelable(false);
                    AlertDialog Devices_Dialog = builder.create();
                    Devices_Dialog.show();
                }
                else{
                    Toast.makeText(context, "No tiene ningun dispositivo emparejado con este", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //****************************************************************************
    //Inicia conexión Bluetooth
    public void bluetoothConect(OnConnect onConnect)  throws IOException {
        final Thread carga = new Thread(){
            public void run(){
                try {
                    //-----------------------------------------------------------------------
                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                    mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);//Crea el socket.
                    mSocket.connect();
                    try {
                        mOutputStream = mSocket.getOutputStream();
                        MyConexionBT = new ConnectedThread(mSocket);
                        MyConexionBT.start();
                    }catch (Exception e){;}
                    try {
                        writeB("Init");
                        //conect.setText("Desconectar");
                    }catch (Exception e) {

                    }
                    S_Bluetooth=1;
                }
                catch (Exception e) {
                    //Error connect.
                    S_Bluetooth=2;
                    onConnect.OnFail();
                }
                finally {
                    try {

                    }
                    catch(Exception e){

                    }
                }
            }
        };

            carga.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(S_Bluetooth == 1)
                        onConnect.OnSuccess();
                    else
                        onConnect.OnFail();
                }
            },3000);

    }
    //ProgressDialog para mientras conecte.
    ProgressDialog progressDialog;
    Handler pb = new Handler();
    public void progress_dialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Inicializando socket");
        progressDialog.setCancelable(false);
        progressDialog.show();
        pb.postDelayed(PB, 2000);
    }
    //Mientras conecta [Segundo plano]
    Runnable PB = new Runnable() {
        @Override
        public void run() {
            switch (S_Bluetooth){
                case 0:
                    progressDialog.setMessage("Conectando a: "+mDevice.getName());
                    pb.postDelayed(this, 700);
                    break;
                case 1:
                    progressDialog.setMessage("Conexión exitosa");
                    finish_conect();
                    break;
                case 2:
                    progressDialog.setMessage("Conexión fallida");
                    S_Bluetooth=0;
                    finish_conect();
                    break;
            }
        }
    };
    //Finaliza el dialogo de conexión.
    public void finish_conect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        },1000);
    }
    //Envío de datos.
    public void writeB(String i) {
        try {
            String msg = String.valueOf(i);
            msg += "\n";
            mOutputStream.write(msg.getBytes());
           // Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
            S_Bluetooth = 1;
        } catch (Exception e) {
            Toast.makeText(context, "Error de conexion", Toast.LENGTH_SHORT).show();
        }
    }
    //Finaliza la conexión Bluetooth
    public void bluetoothFinish() {
        try {
            //writeB("Finish");
            mOutputStream.close();
            mSocket.close();
            state = false;
           // conect.setText("       Conectar       ");
            Toast.makeText(context, "Conexión finalizada", Toast.LENGTH_SHORT).show();
        }catch (Exception e){

        }
    }
    //Recepcion de datos.
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    //Manejo de datos recibidos.
    public void Get_items_Handler(){
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);
                    //add_item_list(R.drawable.rcv, DataStringIN.toString()); //Imprimo en la lista el dato.
                    //sound.start();
                    //vibrator.vibrate(100);

                    if(DataStringIN.toString().contains("OK")){
                        Toast.makeText(context, "Configuración completa.", Toast.LENGTH_SHORT).show();
                        bluetoothFinish();
                    }

                    onResponse.OnSuccess(DataStringIN.toString());

                    //Toast.makeText(context, DataStringIN.toString(), Toast.LENGTH_SHORT).show();
                    DataStringIN.delete(0, DataStringIN.length());
                }
            }
        };
    }

    //clase de segundo plano que lee la informacion obtenida.
    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (Exception e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[256];
            int bytes;
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (Exception e) {
                    // break;
                }
            }
        }
    }
    //******************************************************************************
    //******************************************************************************

    public interface OnConnect{
        void OnSuccess();
        void OnFail();
    }

    public interface OnResponse{
        void OnSuccess(String response);
        void OnFail();
    }
}
