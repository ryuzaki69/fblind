package org.project.harsh.fblind;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import java.util.Locale;

public class tutorial extends Activity implements TextToSpeech.OnInitListener {
    private TextView tutorial;
    private TextView mainApp;
    private boolean okToFinish = false;
    Locale myLocale;
    public static final String MainPP_SP = "MainPP_data";
    public static final int R_PERM = 2822;
    private static final int REQUEST= 112;
    private TextToSpeech tts1;
    Context mContext = this;
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);
        GlobalVars.lastActivity = Contacts.class;
        tutorial = (TextView) findViewById(R.id.tutorial);
        mainApp = (TextView) findViewById(R.id.mainApp);
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=1;
        GlobalVars.context = this;

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG", "@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.READ_LOGS,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.CHANGE_WIFI_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.WRITE_SETTINGS,

            };

            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                //    callNextActivity();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }
        tts1 = new TextToSpeech(this, this);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    //  callNextActivity();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override public void onResume()
    {
        super.onResume();
        try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
        GlobalVars.lastActivity = Contacts.class;
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=2;
        GlobalVars.selectTextView(tutorial,false);
        GlobalVars.selectTextView(mainApp,false);
        tts1.setLanguage(new Locale ("hi","IN"));
        speakOut(getResources().getString(R.string.welcometutor));
    }

    public void select()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: //tutorials
                GlobalVars.selectTextView(tutorial,true);
                GlobalVars.selectTextView(mainApp,false);
                speakOut(getResources().getString(R.string.advice));
                break;

            case 2: //main app
                GlobalVars.selectTextView(tutorial, false);
                GlobalVars.selectTextView(mainApp,true);
                speakOut(getResources().getString(R.string.mainapp));
                break;
        }}

    public void execute()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: //LIST CONTACTS
                Log.e("jjhjh","svsdvdvs");
                speakOut(getResources().getString(R.string.tut));
                break;

            case 2: //GO BACK TO THE MAIN MENU
                GlobalVars.startActivity(Main.class);
                break;
        }
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        int result = GlobalVars.detectMovement(event);
        switch (result)
        {
            case GlobalVars.ACTION_SELECT:
                select();
                break;

            case GlobalVars.ACTION_EXECUTE:
                execute();
                break;
        }
        return super.onTouchEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        int result = GlobalVars.detectKeyUp(keyCode);
        switch (result)
        {
            case GlobalVars.ACTION_SELECT:
                select();
                break;

            case GlobalVars.ACTION_EXECUTE:
                execute();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return GlobalVars.detectKeyDown(keyCode);
    }
    public void updateViews(String languageCode) {

        Locale myLocale = new Locale(languageCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, tutorial.class);
        startActivity(refresh);
        finish();
    }
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts1.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                //speakOut("maaa ki chhhhuuuuuuuuuuu");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }
    private void speakOut(String str) {
        Log.e("are speakout",str);
        tts1.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }
}