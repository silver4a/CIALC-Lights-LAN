package com.cialc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cialc.localConnections.VolleyConnection;

public class MainActivity2 extends AppCompatActivity {

    ImageView btnback;
    TextView txtHost;
    SeekBar seekBarIntensidad,seekBarTemperatura;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        btnback = (ImageView) findViewById(R.id.btn_back);
        btnback.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        txtHost = (TextView) findViewById(R.id.txtHost);
        txtHost.setText(intent.getStringExtra("host"));
        url = intent.getStringExtra("url");

        seekBarIntensidad = (SeekBar) findViewById(R.id.seekBarIntensidad);
        seekBarTemperatura = (SeekBar) findViewById(R.id.seekBarTemperatura);

        //Listeners.
        seekBarIntensidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.i("Progeso Changed: ",String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.i("Progeso stop: ", String.valueOf(seekBar.getProgress()));
                int progreso = seekBarIntensidad.getProgress();
                //Los calculos.



                String comando = "/data?intensidad="+String.valueOf(progreso) + "&";
                String urlFinal = url + comando;
                VolleyConnection.getInstance(getApplicationContext()).setRequest(urlFinal, new VolleyConnection.IVolleyResponse() {
                    @Override
                    public void onResponse(String response) {
                        //Respuesta del ESP32
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        });

        seekBarTemperatura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Log.i("Progeso stop: ", String.valueOf(seekBar.getProgress()));
                int progreso = seekBarTemperatura.getProgress();

                String comando = "/data?temperatura="+String.valueOf(progreso) + "&";
                String urlFinal = url + comando;
                VolleyConnection.getInstance(getApplicationContext()).setRequest(urlFinal, new VolleyConnection.IVolleyResponse() {
                    @Override
                    public void onResponse(String response) {

                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        });

        int pIntensidad = Integer.parseInt(intent.getStringExtra("progressIntensidad"));
        int pTemperatura = Integer.parseInt(intent.getStringExtra("progressTemperatura"));

        seekBarIntensidad.setProgress(pIntensidad);
        seekBarTemperatura.setProgress(pTemperatura);

    }
}