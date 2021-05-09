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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
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
        items[0] = (LinearLayout) findViewById(R.id.c1f1);
        items[1] = (LinearLayout) findViewById(R.id.c2f1);
        items[2] = (LinearLayout) findViewById(R.id.c3f1);
        items[3] = (LinearLayout) findViewById(R.id.c1f2);
        items[4] = (LinearLayout) findViewById(R.id.c2f2);
        items[5] = (LinearLayout) findViewById(R.id.c3f2);
        items[6] = (LinearLayout) findViewById(R.id.c1f3);
        items[7] = (LinearLayout) findViewById(R.id.c2f3);
        items[8] = (LinearLayout) findViewById(R.id.c3f3);
        items[9] = (LinearLayout) findViewById(R.id.c1f4);
        items[10] = (LinearLayout) findViewById(R.id.c2f4);
        items[11] = (LinearLayout) findViewById(R.id.c3f4);
        items[12] = (LinearLayout) findViewById(R.id.c1f5);
        items[13] = (LinearLayout) findViewById(R.id.c2f5);
        items[14] = (LinearLayout) findViewById(R.id.c3f5);
        items[15] = (LinearLayout) findViewById(R.id.c1f6);
        items[16] = (LinearLayout) findViewById(R.id.c2f6);
        items[17] = (LinearLayout) findViewById(R.id.c3f6);

        txts[0] = (TextView) findViewById(R.id.txtc1f1);
        txts[1] = (TextView) findViewById(R.id.txtc2f1);
        txts[2] = (TextView) findViewById(R.id.txtc3f1);
        txts[3] = (TextView) findViewById(R.id.txtc1f2);
        txts[4] = (TextView) findViewById(R.id.txtc2f2);
        txts[5] = (TextView) findViewById(R.id.txtc3f2);
        txts[6] = (TextView) findViewById(R.id.txtc1f3);
        txts[7] = (TextView) findViewById(R.id.txtc2f3);
        txts[8] = (TextView) findViewById(R.id.txtc3f3);
        txts[9] = (TextView) findViewById(R.id.txtc1f4);
        txts[10] = (TextView) findViewById(R.id.txtc2f4);
        txts[11] = (TextView) findViewById(R.id.txtc3f4);
        txts[12] = (TextView) findViewById(R.id.txtc1f5);
        txts[13] = (TextView) findViewById(R.id.txtc2f5);
        txts[14] = (TextView) findViewById(R.id.txtc3f5);
        txts[15] = (TextView) findViewById(R.id.txtc1f6);
        txts[16] = (TextView) findViewById(R.id.txtc2f6);
        txts[17] = (TextView) findViewById(R.id.txtc3f6);

        //OnClick items.
        items[0].setOnClickListener(this);
        items[1].setOnClickListener(this);
        items[2].setOnClickListener(this);
        items[3].setOnClickListener(this);
        items[4].setOnClickListener(this);
        items[5].setOnClickListener(this);
        items[6].setOnClickListener(this);
        items[7].setOnClickListener(this);
        items[8].setOnClickListener(this);
        items[9].setOnClickListener(this);
        items[10].setOnClickListener(this);
        items[11].setOnClickListener(this);
        items[12].setOnClickListener(this);
        items[13].setOnClickListener(this);
        items[14].setOnClickListener(this);
        items[15].setOnClickListener(this);
        items[16].setOnClickListener(this);
        items[17].setOnClickListener(this);

        items[0].setOnLongClickListener(this);
        items[1].setOnLongClickListener(this);
        items[2].setOnLongClickListener(this);
        items[3].setOnLongClickListener(this);
        items[4].setOnLongClickListener(this);
        items[5].setOnLongClickListener(this);
        items[6].setOnLongClickListener(this);
        items[7].setOnLongClickListener(this);
        items[8].setOnLongClickListener(this);
        items[9].setOnLongClickListener(this);
        items[10].setOnLongClickListener(this);
        items[11].setOnLongClickListener(this);
        items[12].setOnLongClickListener(this);
        items[13].setOnLongClickListener(this);
        items[14].setOnLongClickListener(this);
        items[15].setOnLongClickListener(this);
        items[16].setOnLongClickListener(this);
        items[17].setOnLongClickListener(this);

        //----------------------------------------

         bt = BluetoothService.getInstance(this,(Activity) this);

         //Lectura de preferencias.
        totalHostnames = Integer.parseInt(Readpreferences("HOSTNAME_NUMBER","0"));
        loadHostname();
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


            case R.id.c1f1:
                Toast.makeText(this, "Se presiono c1f1", Toast.LENGTH_SHORT).show(); break;
            case R.id.c2f1: break;
            case R.id.c3f1: break;

            case R.id.c1f2: break;
            case R.id.c2f2: break;
            case R.id.c3f2: break;

            case R.id.c1f3: break;
            case R.id.c2f3: break;
            case R.id.c3f3: break;

            case R.id.c1f4: break;
            case R.id.c2f4: break;
            case R.id.c3f4: break;

            case R.id.c1f5: break;
            case R.id.c2f5: break;
            case R.id.c3f5: break;

            case R.id.c1f6: break;
            case R.id.c2f6: break;
            case R.id.c3f6: break;

        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.c1f1: deleteItem(0); break;
            case R.id.c2f1: deleteItem(1);  break;
            case R.id.c3f1: deleteItem(2);  break;

            case R.id.c1f2: deleteItem(3);  break;
            case R.id.c2f2: deleteItem(4);  break;
            case R.id.c3f2: deleteItem(5);  break;

            case R.id.c1f3: deleteItem(6);  break;
            case R.id.c2f3: deleteItem(7);  break;
            case R.id.c3f3: deleteItem(8);  break;

            case R.id.c1f4: deleteItem(9);  break;
            case R.id.c2f4: deleteItem(10);   break;
            case R.id.c3f4: deleteItem(11);   break;

            case R.id.c1f5: deleteItem(12);   break;
            case R.id.c2f5: deleteItem(13);   break;
            case R.id.c3f5: deleteItem(14);   break;

            case R.id.c1f6: deleteItem(15);   break;
            case R.id.c2f6: deleteItem(16);   break;
            case R.id.c3f6: deleteItem(17);   break;
        }
        return true;
    }

    private void deleteItem(int item){
        Deletepreference("HOST" + String.valueOf(item));
        items[0].setVisibility(View.GONE);
        totalHostnames-=1;
        Writepreferences("HOSTNAME_NUMBER",String.valueOf(totalHostnames));
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
                        saveHostname(hostText);
                    }
                })
                .setCancelable(false);
        AlertDialog formulario = builder.create();
        formulario.show();
    }

    private void saveHostname(String nameHost){
        if(totalHostnames >= 17){
            Toast.makeText(this, "Se superó el limite de dispositivos.", Toast.LENGTH_SHORT).show();
            totalHostnames=17;
        }
        else {
            Writepreferences("HOST" + String.valueOf(totalHostnames), nameHost);
            totalHostnames += 1;
            Writepreferences("HOSTNAME_NUMBER",String.valueOf(totalHostnames));
            loadHostname();
        }
    }

    private void loadHostname(){
        for (int k = 0; k < totalHostnames; k++) {
            String nameHost = Readpreferences("HOST" + k, "ERROR");
            if (!nameHost.equals("ERROR"))
                hostnames.put(k, nameHost);
        }

        //Se cargan en visibilidad.
        for(int k = 0;k<hostnames.size();k++){
            String host = (String) hostnames.get(k);
            txts[k].setText(host);
            items[k].setVisibility(View.VISIBLE);
        }
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
