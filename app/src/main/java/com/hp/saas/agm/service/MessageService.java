package com.hp.saas.agm.service;

import android.content.Context;
import android.widget.Toast;

public class MessageService {

    private Context context;
    public void init(Context applicationContext) {
        this.context = applicationContext;
    }

    public void show(String message) {
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }





}
