package com.cialc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cialc.localConnections.VolleyConnection;

import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    Button buttonHora1, buttonHora2;
    TextView textViewH1,textViewH2;
    Switch switchHorario, switchTrans;
    ImageView btnback;
    TextView txtHost;
    SeekBar seekBarIntensidad, seekBarTemperatura;
    String url = "";
    private Object v;
    private int hora, minutos;
    private int hora2, minutos2;



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
                String comando = "/data?intensidad=" + String.valueOf(progreso) + "&";
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
                String comando = "/data?temperatura=" + String.valueOf(progreso) + "&";
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
        //----------------------------------------------------------------------------------------
        int pIntensidad = Integer.parseInt(intent.getStringExtra("progressIntensidad"));
        int pTemperatura = Integer.parseInt(intent.getStringExtra("progressTemperatura"));
        seekBarIntensidad.setProgress(pIntensidad);
        seekBarTemperatura.setProgress(pTemperatura);
        //----------------------------------------------------------------------------------------
        buttonHora1 = (Button) findViewById(R.id.buttonHora1);
        buttonHora2 = (Button) findViewById(R.id.buttonHora2);
        textViewH1 = (TextView) findViewById(R.id.textViewH1);
        textViewH2 = (TextView) findViewById(R.id.textViewH2);
        buttonHora1.setOnClickListener(this);
        buttonHora2.setOnClickListener(this);
        switchHorario =(Switch)findViewById(R.id.switchHorario);
        switchTrans = (Switch)findViewById(R.id.switchTrans);
        switchHorario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        switchTrans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == buttonHora1) {
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    textViewH1.setText(hourOfDay+ ":" +minute);
                }
            }, hora, minutos, false);
            timePickerDialog.show();
        }
        if(v == buttonHora2){
            final Calendar c = Calendar.getInstance();
            hora2 = c.get(Calendar.HOUR_OF_DAY);
            minutos2 = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay2, int minute2) {
                    textViewH2.setText(hourOfDay2+ ":" +minute2);
                }
            }, hora2, minutos2, false);
            timePickerDialog.show();
        }
    }

}