package com.cialc.recycler;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.SeekBar;

import com.cialc.R;
public class Horario {
    private Activity activity;
    private AlertDialog dialog;
    public Horario(Activity myActivity){
        activity=myActivity;
    }
    public void starthorario(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.horario, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }
    void dismissDialog(){
        dialog.dismiss();
    }
}

