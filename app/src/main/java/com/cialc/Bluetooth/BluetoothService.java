package com.cialc.Bluetooth;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    boolean isScan = false;
    public static byte S_Bluetooth=0;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> devicesAvailable;
    ArrayAdapter<String> arrayAdapter;
    public OnResponse onResponse;
    public OnConnect onConnect;

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
        this.devicesAvailable = new ArrayList<BluetoothDevice>();
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        activity.registerReceiver(broadcastReceiver,filter);
    }


    //***************************************************************************************
    //                      CONFIGURACI??N Y CONEXI??N BLUETOOTH
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
                                    Toast.makeText(context, "Error de conexi??n", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }).setPositiveButton("Buscar dispositivo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            discoverDevices(onConnect);
                        }
                    })
                            .setTitle("Available devices")
                            .setCancelable(true);
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
    //Inicia conexi??n Bluetooth
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
                    progressDialog.setMessage("Conexi??n exitosa");
                    finish_conect();
                    break;
                case 2:
                    progressDialog.setMessage("Conexi??n fallida");
                    S_Bluetooth=0;
                    finish_conect();
                    break;
            }
        }
    };
    //Finaliza el dialogo de conexi??n.
    public void finish_conect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
            }
        },1000);
    }
    //Env??o de datos.
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
    //Finaliza la conexi??n Bluetooth
    public void bluetoothFinish() {
        try {
            //writeB("Finish");
            mOutputStream.close();
            mSocket.close();
            state = false;
            S_Bluetooth = 0;
           // conect.setText("       Conectar       ");
            Toast.makeText(context, "Conexi??n finalizada", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Configuraci??n completa.", Toast.LENGTH_SHORT).show();
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

    //AlertDialog para mostrar los dispositivos.
    private void dialogDevices(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setAdapter(arrayAdapter, (dialog, which) -> {
            mDevice = devicesAvailable.get(which);
            Toast.makeText(context, "Conectando a: "+mDevice.getName(), Toast.LENGTH_SHORT).show();
            try {
                progress_dialog();
                bluetoothConect(this.onConnect);
            } catch (Exception e) {
                Toast.makeText(context, "Error de conexi??n", Toast.LENGTH_SHORT).show();
            }
        })
                .setTitle("Available devices")
                .setCancelable(true);
        AlertDialog Devices_Dialog = builder.create();
        Devices_Dialog.show();
    }


    public void discoverDevices(OnConnect onConnect){
        this.onConnect = onConnect;
        devicesAvailable.clear();
        arrayAdapter.clear();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Buscando dispositivos. . . ");
        progressDialog.show();

        if(bluetoothAdapter.isDiscovering()){
            Log.i("discoverDevices","Ya se est?? buscando dispositivos");
        }else if(bluetoothAdapter.startDiscovery()){
            Log.i("discoverDevices","Buscando dispositivos");
        }else{
            Log.i("discoverDevices","Error al buscar dispositivos.");
        }
    }

    public void cancelDiscover(){
        if(bluetoothAdapter.cancelDiscovery()){
            Log.i("discoverDevices","Deteniendo busqueda de dispositivos");
        }else{
            Log.i("discoverDevices","Error al detener busqueda");
        }
        progressDialog.dismiss();
    }

    public final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i("discoverDevices", "Inici?? la busqueda.");
                    isScan = true;
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if(isScan) {
                        devicesAvailable = removeDuplicates(devicesAvailable);
                        Log.i("discoverDevices", "Finaliz?? la busqueda. " + devicesAvailable.size() + " dispositivos encontrados.");
                        progressDialog.dismiss();

                        for (BluetoothDevice device : devicesAvailable) {
                            Log.i("discoverDevices", "Device: " + device.getName() + " - " + device.getAddress());
                            arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                        arrayAdapter.notifyDataSetChanged();
                        isScan = false;
                        if(devicesAvailable.size() > 0)
                            dialogDevices();
                        else{
                            Toast.makeText(context, "No se encontraron dispositivos para vincular.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }else{
                        activity.unregisterReceiver(broadcastReceiver);
                        onConnect.OnSuccess();
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Log.i("discoverDevices","Dispositivo encontrado: "+device.getName());
                    devicesAvailable.add(device);
                    break;

            }
        }
    };

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("CIALC", "permission not granted yet!");
            Log.d("CIALC","Whitout this permission Blutooth devices cannot be searched!");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }
    //******************************************************************************
    //******************************************************************************

    //Interfaces de comunicaci??n.
    public interface OnConnect{
        void OnSuccess();
        void OnFail();
    }

    public interface OnResponse{
        void OnSuccess(String response);
        void OnFail();
    }
}
