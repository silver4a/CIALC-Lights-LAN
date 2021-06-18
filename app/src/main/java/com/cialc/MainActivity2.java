package com.cialc;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.cialc.localConnections.VolleyConnection;
import android.content.SharedPreferences;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    Button buttonHora1, buttonHora2;
    TextView textViewH1,textViewH2, textViewH3, tiempo, Temptrans, Txvhorariointencidad, Txvhorariotemp;
    Switch switchHorario, switchTrans;
    ImageView btnback;
    TextView txtHost;
    SeekBar seekBarIntensidad, seekBarTemperatura;
    String url = "";
    private Object v;
    private int hora, minutos;
    private int hora2, minutos2;
    private Intent intent;
    private SharedPreferences preferences;
    private static final String PROGRESS = "SEEKBARH";
    private static final String PROGRES = "SEEKBART";
    private static final String PROGRE = "SEEKBARTRANS";
    //_____________________________________________________
    private static final String TEXT1 ="text1";
    private static final String TEXT2 ="text2";
    private static final String TEXT3 ="text3";
    private static final String SWITCH1 = "switch1";
    private static final String SWITCH2 = "switch2";
    //______________________________________________________
    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm;ss", Locale.getDefault());
        return dateFormatter.format(calendar.getTime());
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        textViewH3 = (TextView) findViewById(R.id.textViewH3);
        buttonHora1.setOnClickListener(this);
        buttonHora2.setOnClickListener(this);
        switchHorario =(Switch)findViewById(R.id.switchHorario);
        switchTrans = (Switch)findViewById(R.id.switchTrans);
        //-------------------------------------------------------------------------------------
        tiempo = (TextView)findViewById(R.id.tiempo);
        String tiempoo = getTime();
        tiempo.setText(getTime());
        String time = "/data?tiempo=" + String.valueOf(tiempoo) + "&";
        String urlFinal = url + time;
        VolleyConnection.getInstance(getApplicationContext()).setRequest(urlFinal, new VolleyConnection.IVolleyResponse() {
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onError(String errorMessage) {

            }
        });
        //--------------------------------------------------------------------------------------------
        preferences=getSharedPreferences(" ",MODE_PRIVATE);
        final SharedPreferences.Editor editor =preferences.edit();
        switchHorario.setChecked(preferences.getBoolean(SWITCH1, false));
        switchTrans.setChecked(preferences.getBoolean(SWITCH2, false));
        textViewH1.setText(preferences.getString(TEXT1, " "));
        textViewH2.setText(preferences.getString(TEXT2, " "));
        textViewH3.setText(preferences.getString(TEXT3, " "));
        switchHorario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SWITCH1, switchHorario.isChecked());
                editor.apply();
                boolean switch1 = switchHorario.isChecked();
                String comando ="/data?switchH=" + String.valueOf(switch1)+"&";
                String urlFinal =  url+comando;
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
        switchTrans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(SWITCH2, switchTrans.isChecked());
                editor.apply();
                boolean switch2 = switchTrans.isChecked();
                String comando ="/data?switchT=" + String.valueOf(switch2)+"&";
                String urlFinal =  url+comando;
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
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        preferences=getSharedPreferences(" ",MODE_PRIVATE);
        final SharedPreferences.Editor editor =preferences.edit();
        if (v == buttonHora1 && switchHorario.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
            View formView = getLayoutInflater().inflate(R.layout.horario,null);
            builder.setCancelable(false);
            final SeekBar seekBarHorarioLight = (SeekBar)formView.findViewById(R.id.seekBarHorarioLight);
            final SeekBar seekBarHorarioTemp = (SeekBar)formView.findViewById(R.id.seekBarHorarioTemp);
            seekBarHorarioLight.setProgress(preferences.getInt(PROGRESS,0));
            seekBarHorarioTemp.setProgress(preferences.getInt(PROGRES,0));
            final TextView Txvhorariointencidad = (TextView)formView.findViewById(R.id.Txvhorariointencidad);
            final TextView Txvhorariotemp = (TextView)formView.findViewById(R.id.Txvhorariotemp);
            builder.setNegativeButton("close",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                }
            });

            seekBarHorarioLight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    editor.putInt(PROGRESS, seekBarHorarioLight.getProgress());
                    editor.apply();
                    int progreso = seekBarHorarioLight.getProgress();
                    String comando = "/data?horariolight=" + String.valueOf(progreso) + "&";
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
            seekBarHorarioTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    editor.putInt(PROGRES, seekBarHorarioTemp.getProgress());
                    editor.apply();
                    int progreso = seekBarHorarioTemp.getProgress();
                    String comando = "/data?horariotemp=" + String.valueOf(progreso) + "&";
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
            builder.setView(formView);
            AlertDialog dialog = builder.create();
            dialog.show();
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String hm = (hourOfDay+":" +minute);
                    textViewH1.setText(hm);
                    String ini = "/data?TiempoInicio=" + String.valueOf(hourOfDay+":"+minute) + "&";
                    String urlFinal = url + ini;
                    editor.putString(TEXT1, textViewH1.getText().toString());
                    editor.apply();
                    VolleyConnection.getInstance(getApplicationContext()).setRequest(urlFinal, new VolleyConnection.IVolleyResponse() {
                        @Override
                        public void onResponse(String response) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                }
            }, hora, minutos, false);
            timePickerDialog.show();
        }
        if(v == buttonHora2 && switchHorario.isChecked()){
            final Calendar c = Calendar.getInstance();
            hora2 = c.get(Calendar.HOUR_OF_DAY);
            minutos2 = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay2, int minute2) {
                    textViewH2.setText(hourOfDay2+ ":" +minute2);
                    String fin = "/data?TiempoFin=" + String.valueOf(hourOfDay2+":"+minute2) + "&";
                    String urlFinal = url + fin;
                    editor.putString(TEXT2, textViewH2.getText().toString());
                    editor.apply();
                    VolleyConnection.getInstance(getApplicationContext()).setRequest(urlFinal, new VolleyConnection.IVolleyResponse() {
                        @Override
                        public void onResponse(String response) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                }
            }, hora2, minutos2, false);
            timePickerDialog.show();
        }
        if (v == buttonHora1 && switchTrans.isChecked()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
            View formView = getLayoutInflater().inflate(R.layout.transition,null);
            builder.setCancelable(false);
            final SeekBar seekBartrans = (SeekBar)formView.findViewById(R.id.seekBartrans);
            seekBartrans.setProgress(preferences.getInt(PROGRE, 0));
            //textViewH3.setText(preferences.getString(TEXT, ""));
            final TextView Temptrans = (TextView)formView.findViewById(R.id.Temptrans);
            builder.setNegativeButton("close",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            seekBartrans.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    editor.putInt(PROGRE, seekBartrans.getProgress());
                    editor.apply();
                    int progreso = seekBartrans.getProgress();
                    String comando = "/data?trans=" + String.valueOf(progreso) + "&";
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
            builder.setView(formView);
            AlertDialog dialog = builder.create();
            dialog.show();
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay3, int minute3) {
                    textViewH3.setText(hourOfDay3+ ":" +minute3);
                    String trans = "/data?TiempoTrans=" + String.valueOf(hourOfDay3+":"+minute3) + "&";
                    String urlFinal = url + trans;
                    editor.putString(TEXT3, textViewH3.getText().toString());
                    editor.apply();
                    VolleyConnection.getInstance(getApplicationContext()).setRequest(urlFinal, new VolleyConnection.IVolleyResponse() {
                        @Override
                        public void onResponse(String response) {

                        }

                        @Override
                        public void onError(String errorMessage) {

                        }
                    });
                }
            }, hora, minutos, false);
            timePickerDialog.show();
        }

    }
}