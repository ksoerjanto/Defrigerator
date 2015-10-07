package com.example.android.defridgerator;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ksoerjanto on 6/28/15.
 */
public class Message {

    public static void message(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
