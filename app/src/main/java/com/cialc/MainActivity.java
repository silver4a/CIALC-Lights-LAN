package com.cialc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cialc.recycler.Adapter;
import com.cialc.recycler.Dispositivo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Adapter.ListClickItem {
    //Controles globales.
    ImageView bt_bluetooth;

    //Objeto de bluetooth.
    BluetoothService bt;

    //Array de dispositivos.
    ArrayList<Dispositivo> listDevices;
    RecyclerView recyclerView;
    Adapter adapterDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------------------------------------
        //Configuración de las vistas
        bt_bluetooth = (ImageView) findViewById(R.id.bt_bluetooth);
        bt_bluetooth.setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler);
        //----------------------------------------

         bt = BluetoothService.getInstance(this,(Activity) this);

        createRecycler();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_bluetooth:
                //Se configura el BT.
                bluetoohConfig();
                break;
        }
    }

    private void bluetoohConfig() {
        bt.configBluetooth(new BluetoothService.OnConnect() {
            @Override
            public void OnSuccess() {
                dialogConfigWiFi();
            }

            @Override
            public void OnFail() {
                //Error de conexion.
            }
        });
    }

    private void dialogConfigWiFi(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View formView = getLayoutInflater().inflate(R.layout.formulario_dialog,null);
        final EditText ssid = (EditText) formView.findViewById(R.id.ssid);
        final EditText password = (EditText) formView.findViewById(R.id.password);
        final EditText hostname = (EditText) formView.findViewById(R.id.hostname);
        bt.Get_items_Handler();
        builder.setView(formView)
                .setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ssidText = ssid.getText().toString();
                        String passText = password.getText().toString();
                        String hostText = hostname.getText().toString();

                        //Se envia al ESP32.
                        String datos = "$" + ssidText + "," + passText + "," + hostText + "&";
                        //Toast.makeText(MainActivity.this, datos, Toast.LENGTH_SHORT).show();
                        bt.writeB(datos);
                        listDevices.add(new Dispositivo(hostText,getBitmap(R.drawable.wifi_ico)));
                        adapterDevices.notifyDataSetChanged();
                        savePreferences();
                    }
                })
                .setCancelable(false);
        AlertDialog formulario = builder.create();
        formulario.show();
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
        listDevices.add(new Dispositivo("Agregar dispositivo",getBitmap(R.drawable.add_ico)));
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

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //                                  Manejo de preferencias
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    void savePreferences(){
        int numberDevices = adapterDevices.getItemCount();
        for(int k=1;k<numberDevices;k++){
            Writepreferences("HOST"+k,listDevices.get(k).getHostname());
            Writepreferences("DEVICE_NUMBER",String.valueOf(k));
        }
        Writepreferences("DEVICE_NUMBER",String.valueOf(numberDevices-1));
    }

    void loadPreferences(){
        int numberDevices = Integer.parseInt(Readpreferences("DEVICE_NUMBER","0"));
        for(int k=1;k<numberDevices+1;k++){
            String host = Readpreferences("HOST"+k,"ERROR");
            listDevices.add(new Dispositivo(host,getBitmap(R.drawable.wifi_ico)));
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
            Toast.makeText(this, "device: "+device.getHostname(), Toast.LENGTH_SHORT).show();
        }
    }
}
