package com.android.wifimulticast;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/4/12.
 */

public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.color.colorPrimaryDark);
        Intent dialogIntent = new Intent(getBaseContext(), WifiService.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(dialogIntent);
    }
}
