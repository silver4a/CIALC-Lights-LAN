package com.cialc;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.cialc.Bluetooth.BluetoothService;
import com.cialc.localConnections.VolleyConnection;
import com.cialc.recycler.Adapter;
import com.cialc.recycler.Dispositivo;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Adapter.ListClickItem {
    //Controles globales.
    ImageView bt_bluetooth,bt_radar;

    //Objeto de bluetooth.
    BluetoothService bt;

    //Progress
    static ProgressDialog progressDialog;

    //Array de dispositivos.
    ArrayList<Dispositivo> listDevices;
    RecyclerView recyclerView;
    Adapter adapterDevices;

    //Radar runnable
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------------------------------------
        //Configuración de las vistas
        bt_bluetooth = (ImageView) findViewById(R.id.bt_bluetooth);
        bt_bluetooth.setOnClickListener(this);
        bt_radar = (ImageView) findViewById(R.id.bt_radar);
        bt_radar.setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler);
        progressDialog = new ProgressDialog(this);
        //----------------------------------------

         bt = BluetoothService.getInstance(this,(Activity) this);
         bt.checkPermissions();

        createRecycler();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_bluetooth:
                bluetoohConfig();
                break;
            case R.id.bt_radar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("¿Desea buscar los dispositivos que ya fueron configurados en la red?, esto puede tomar 1 minuto.");
                builder.setPositiveButton("Continuar", (dialog, which) -> {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Buscando los dispositivos en la red.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    handler.postDelayed(runnable, 1000);
                }).setNegativeButton("Cancelar", (dialog, which) -> {
                    ;;
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }

    Runnable runnable = () -> {
        Log.d("RADAR","GO");
        ArrayList<String> urls = new ArrayList<String>();
        for(int k=200;k<=255;k++){
            urls.add("http://" + getIPAddress(String.valueOf(k)) + "/data?ping=1") ;
        }

        for(String url : urls){
            Log.d("RADAR",url);
            progressDialog.setMessage(url);
            VolleyConnection.getInstance(this).setRequest(url, 1000,new VolleyConnection.IVolleyResponse() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("AUTENTICADO")){
                        String hostText = response.split(",")[1].replace("\n","");
                        String ip = url.replace("http://","").replace("/data?ping=1","");
                        boolean add = true;
                        for(Dispositivo dispositivo : listDevices){
                            if(dispositivo.getIpAddress().equals(ip)){
                                add = false;
                            }
                        }
                        if(add) {
                            listDevices.add(new Dispositivo(hostText,
                                    ip,
                                    getBitmap(R.drawable.wifi_ico)));
                            adapterDevices.notifyDataSetChanged();
                            savePreferences();
                        }
                    }
                }

                @Override
                public void onError(String errorMessage) {

                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        progressDialog.dismiss();
    };


    @Override
    protected void onDestroy() {
        //unregisterReceiver(bt.broadcastReceiver);
        super.onDestroy();
    }


    private void bluetoohConfig() {
        bt.configBluetooth(new BluetoothService.OnConnect() {
            @Override
            public void OnSuccess() {
                //dialogConfigWiFi();
                dialogScanWiFi();
                bt.onConnect = null;
            }

            @Override
            public void OnFail() {
                //Error de conexion.
            }
        });
    }

    private void dialogConfigWiFi(String ssidTxt){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View formView = getLayoutInflater().inflate(R.layout.formulario_dialog,null);
        final EditText ssid = (EditText) formView.findViewById(R.id.ssid);
        ssid.setText(ssidTxt);
        ssid.setEnabled(false);
        final EditText password = (EditText) formView.findViewById(R.id.password);
        final EditText hostname = (EditText) formView.findViewById(R.id.hostname);
        final EditText ipAddress = (EditText) formView.findViewById(R.id.ipAddress);

        String numberFinal = Readpreferences("IPNUMBER","199");
        numberFinal = String.valueOf(Integer.parseInt(numberFinal) + 1);

        ipAddress.setText(getIPAddress(numberFinal));

        bt.Get_items_Handler();
        String finalNumber1 = numberFinal;
        builder.setView(formView)
                .setPositiveButton("Conectar", (dialog, which) -> {
                    String ssidText = ssid.getText().toString();
                    String passText = password.getText().toString();
                    String hostText = hostname.getText().toString();

                    String url = "http://"+getIPAddress(finalNumber1)+ "/data?auth=1";
                    Log.i("INFO",url);
                    progressDialog.setMessage("Configurando. . .");
                    new Handler().postDelayed(() -> {
                        VolleyConnection.getInstance(getApplicationContext()).setRequest(url, new VolleyConnection.IVolleyResponse() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("INFO RESPONSE",response);
                                Log.i("Response",response);
                                if(response.contains("AUTENTICADO")){
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Se agregó dispositivo: "+hostText + " correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                //Log.i("INFO - ERROR MESSAJE",errorMessage);
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Es necesario que reinicie el host.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    },15000);

                    addDevice(ssidText,passText,hostText,finalNumber1);
                    Writepreferences("IPNUMBER", finalNumber1);
                })
                .setCancelable(false);
        AlertDialog formulario = builder.create();
        formulario.show();
    }

    private void dialogScanWiFi(){
        progressDialog.setMessage("Buscando las redes disponibles. . .");
        progressDialog.show();
        bt.Get_items_Handler();

        //Se envia la solicitud al ESP32
        bt.writeB("SCAN");
        bt.onResponse = new BluetoothService.OnResponse() {
            @Override
            public void OnSuccess(String response) {
                String redes[] = response.split(",");
                ArrayList<String> arrayRedes = new ArrayList<>();
                String command = redes[0];
                for(String red : redes){
                    if(!(red.contains("RESULT_SCAN")))
                        if(red.length() > 1)
                            arrayRedes.add(red.replace("*",""));
                }

                arrayRedes = BluetoothService.removeDuplicates(arrayRedes);

                String redesFinal[] = new String[arrayRedes.size()];
                for(int k=0;k<arrayRedes.size();k++){
                    Log.i("REDES"+k,arrayRedes.get(k));
                    redesFinal[k] = arrayRedes.get(k);
                }

                if(command.contains("RESULT_SCAN")) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Redes WiFi encontradas")
                            .setItems(redesFinal, (dialog, which) -> {
                                dialogConfigWiFi(redesFinal[which]);
                            })
                            .setPositiveButton("Consultar nuevamente", (dialog, which) -> {
                                //Toast.makeText(MainActivity.this, "consultando again. . ", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                bt.onResponse = null;
                                dialogScanWiFi();
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.dismiss();
                                    bt.onResponse = null;
                                    bt.bluetoothFinish();
                                    bt.finish_conect();
                                }
                            })
                            .setCancelable(false);
                    androidx.appcompat.app.AlertDialog array = builder.create();
                    array.show();
                }
            }

            @Override
            public void OnFail() {

            }
        };

    }

    private void addDevice(String ssidText,String passText,String hostText,String numberText){
        //Se envia al ESP32.
        String datos = "CONNECT$" + ssidText + "," + passText + "," + hostText + ","+numberText + "&";
        //Toast.makeText(MainActivity.this, datos, Toast.LENGTH_SHORT).show();
        bt.writeB(datos);
        listDevices.add(new Dispositivo(hostText,
                                        getIPAddress(numberText),
                                        getBitmap(R.drawable.wifi_ico)));
        adapterDevices.notifyDataSetChanged();
        savePreferences();
    }

    //Creacion del recycler.
    private void createRecycler(){
        listDevices = new ArrayList<>();

        //Configuracion del linear layout del recycler.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerView.LayoutManager layoutManager = linearLayoutManager;
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),getResources().getConfiguration().orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);

        inflateDevices();

        //Swipe
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                listDevices.remove(position);
                adapterDevices.notifyDataSetChanged();
                if(position != 0)
                    savePreferences();

            }
        };

        adapterDevices = new Adapter(this,listDevices,this);
        recyclerView.setAdapter(adapterDevices);

        //Swipe config.
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void inflateDevices(){
        //listDevices.add(new Dispositivo("LUZ1",getBitmap(R.drawable.wifi_ico)));
        //listDevices.add(new Dispositivo("LUZ2",getBitmap(R.drawable.wifi_ico)));
        //listDevices.add(new Dispositivo("LUZ3",getBitmap(R.drawable.wifi_ico)));
        listDevices.add(new Dispositivo("Agregar dispositivo","",getBitmap(R.drawable.add_ico)));
        loadPreferences();
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public String getIPAddress(String finalNumber) {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String[] ipSplit = ip.split("\\.");
        return ipSplit[0] + '.' + ipSplit[1] + '.' + ipSplit[2] + '.' +finalNumber;

    }
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //                                  Manejo de preferencias
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    void savePreferences(){
        int numberDevices = adapterDevices.getItemCount();
        for(int k=1;k<numberDevices;k++){
            Writepreferences("HOST"+k,listDevices.get(k).getHostname() + "," + listDevices.get(k).getIpAddress());
            Writepreferences("DEVICE_NUMBER",String.valueOf(k));
        }
        Writepreferences("DEVICE_NUMBER",String.valueOf(numberDevices-1));
    }

    void loadPreferences(){
        int numberDevices = Integer.parseInt(Readpreferences("DEVICE_NUMBER","0"));
        for(int k=1;k<numberDevices+1;k++){
            String host = Readpreferences("HOST"+k,"ERROR");
            String split[] = host.split(",");
            if(split.length >= 2)
                listDevices.add(new Dispositivo(split[0],split[1],getBitmap(R.drawable.wifi_ico)));
        }
    }

    //Lectura de preferencias
    String Readpreferences(String address, String error) {
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String read = preferences.getString(address, error);
        return read;
    }

    //Escritura de preferencias
    void Writepreferences(String address, String preferencee) {
        SharedPreferences preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(address, preferencee);
        editor.commit();
    }

    void Deletepreference(String address){
        SharedPreferences settings = getSharedPreferences("config", Context.MODE_PRIVATE);
        settings.edit().remove(address).commit();
    }

    @Override
    public void onListItemClick(int id, int clickItem, Dispositivo device) {

        if(clickItem == 0){
            //dialogConfigWiFi();
            bluetoohConfig();
        }else{
            //Autenticacion.
            String url = "http://"+device.getIpAddress() + "/data?auth=1";
            progressDialog.setMessage("Autenticando. . .");
            progressDialog.show();
            VolleyConnection.getInstance(this).setRequest(url, new VolleyConnection.IVolleyResponse() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("AUTENTICADO")){
                        response = response.replace("\r\n","");

                        String informacion[] = response.split(",");

                        //Toast.makeText(MainActivity.this, "Autenticado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                        intent.putExtra("host",device.getHostname() + " - "+device.getIpAddress());
                        intent.putExtra("url",url.substring(0,url.lastIndexOf("/")));
                        intent.putExtra("progressIntensidad",informacion[1]);
                        intent.putExtra("progressTemperatura",informacion[2]);
                        intent.putExtra("progresoHorarioLight",informacion[3]);
                        intent.putExtra("progresohorariotemp",informacion[4]);
                        intent.putExtra("progresotrans",informacion[5]);
                        intent.putExtra("switchH",informacion[6]);
                        intent.putExtra("switchT",informacion[7]);

                        Log.i("Progreso",url.substring(0,url.lastIndexOf("/")));
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(MainActivity.this, "No se pudo autenticar con el dispositivo.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
}
