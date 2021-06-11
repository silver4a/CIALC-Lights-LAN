package com.cialc.recycler;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.cialc.R;


public class Trasition {
    private Activity activity;
    private AlertDialog dialog;
public Trasition(Activity myActivity){
    activity=myActivity;
}
public void starttransition(){
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    LayoutInflater inflater = activity.getLayoutInflater();
    builder.setView(inflater.inflate(R.layout.transition, null));
    builder.setCancelable(true);
    dialog = builder.create();
    dialog.show();
}
void dismissDialog(){
    dialog.dismiss();
}
}
