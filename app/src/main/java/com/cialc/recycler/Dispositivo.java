package com.cialc.recycler;

import android.graphics.Bitmap;

public class Dispositivo {

    private String hostname;
    private Bitmap image;

    public Dispositivo(String hostname, Bitmap image) {
        this.hostname = hostname;
        this.image = image;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
