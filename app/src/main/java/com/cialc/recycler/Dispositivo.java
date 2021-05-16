package com.cialc.recycler;

import android.graphics.Bitmap;

public class Dispositivo {

    private String hostname;
    private String ipAddress;
    private Bitmap image;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Dispositivo(String hostname, String ipAddress, Bitmap image) {
        this.hostname = hostname;
        this.image = image;
        this.ipAddress = ipAddress;
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
