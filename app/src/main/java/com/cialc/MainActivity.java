package com.cialc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Controles globales.
    ImageView bt_bluetooth;
    LinearLayout[] items = new LinearLayout[18];
    TextView[] txts = new TextView[18];
    Map<Integer,String> hostnames = new HashMap<Integer,String>();
    int totalHostnames;

    //Objeto de bluetooth.
    BluetoothService bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----------------------------------------
        //Configuración de las vistas
        bt_bluetooth = (ImageView) findViewById(R.id.bt_bluetooth);
        bt_bluetooth.setOnClickListener(this);
        //----------------------------------------

         bt = BluetoothService.getInstance(this,(Activity) this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_bluetooth:
                //Se configura el BT.
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
                break;
        }
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
                        //saveHostname(hostText);
                    }
                })
                .setCancelable(false);
        AlertDialog formulario = builder.create();
        formulario.show();
    }

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    //                                  Manejo de preferencias
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
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

}
