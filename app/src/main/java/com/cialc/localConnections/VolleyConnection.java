package com.cialc.localConnections;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleyConnection {
    private static VolleyConnection instance;
    private static Context context;
    private StringRequest stringRequest;

    private VolleyConnection(Context context) {
        this.context = context;
    }

    public static synchronized VolleyConnection getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyConnection(context);
        }
        return instance;
    }

    public void setRequest(String url, final IVolleyResponse request){
        final RequestQueue queue = Volley.newRequestQueue(context);
        final String TAG = "Request";
        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        request.onResponse(response);
                        queue.cancelAll(TAG); //Cancelo todos los procesos.
                        queue.stop(); //detengo el queue.
                        stringRequest.cancel(); //Detengo el stringR.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                request.onError(error.getMessage());
                queue.cancelAll(TAG); //Cancelo todos los procesos.
                queue.stop(); //detengo el queue.
                stringRequest.cancel(); //Detengo el stringR.
            }
        });
        // Set the tag on the request.
        stringRequest.setTag(TAG);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public interface IVolleyResponse{
        void onResponse(String response);
        void onError(String errorMessage);
    }
}

