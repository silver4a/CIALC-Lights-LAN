package com.cialc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Controles globales.
    ImageView bt_bluetooth;

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
                //dialogConfigWiFi();
                break;

        }
    }

    private void dialogConfigWiFi(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View formView = getLayoutInflater().inflate(R.layout.formulario_dialog,null);
        final EditText ssid = (EditText) formView.findViewById(R.id.ssid);
        final EditText password = (EditText) formView.findViewById(R.id.password);
        final EditText hostname = (EditText) formView.findViewById(R.id.hostname);
        builder.setView(formView)
                .setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        AlertDialog formulario = builder.create();
        formulario.show();
    }
}