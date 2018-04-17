package org.project.harsh.fblind;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Harsh Gupta on 3/25/2018.
 */

public class language extends Activity {
    TextView lang;
    TextView select;
    TextView gobacklang;
    ArrayList <String> lan=new ArrayList<String>();
    ArrayList <String> code=new ArrayList<String>();
    int init=0;
    Locale myLocale;
    String ll="en";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        lang=(TextView)findViewById(R.id.language);
        select=(TextView)findViewById(R.id.select);
        gobacklang=(TextView)findViewById(R.id.gobacklanguage);
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=3;
        lan.add("English");
        code.add("pt");
        lan.add("Hindi");
        code.add("hi");
        lan.add("Bengali");
        code.add("bn-rIN");
        Log.e("sfdgvsdgvdxvgdxg",ll);
    }
    @Override public void onResume()
    {
        super.onResume();
        GlobalVars.lastActivity = language.class;
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=3;
        GlobalVars.selectTextView(lang,false);
        GlobalVars.selectTextView(select,false);
        GlobalVars.selectTextView(gobacklang,false);
        GlobalVars.talk(getResources().getString(R.string.welcomelang));
    }

    public void select()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: //language
                GlobalVars.selectTextView(lang,true);
                GlobalVars.selectTextView(select,false);

                GlobalVars.talk(getResources().getString(R.string.langselect));
                break;

            case 2: //select
                GlobalVars.selectTextView(lang,false);
                GlobalVars.selectTextView(select,true);
                GlobalVars.selectTextView(gobacklang,false);
                //GlobalVars.talk(getResources().getString(R.string.layoutAlarmsCreateDay2) + GlobalVars.getDayName(dayValue));
                GlobalVars.talk(getResources().getString(R.string.langok));
                break;

            case 3: //goback languagege
                GlobalVars.selectTextView(lang,false);
                GlobalVars.selectTextView(select,false);
                GlobalVars.selectTextView(gobacklang,true);
                //GlobalVars.talk(getResources().getString(R.string.layoutAlarmsCreateTimeHour2) + GlobalVars.alarmTimeHoursValues.get(hourValue) + getResources().getString(R.string.layoutAlarmsCreateHours));
                GlobalVars.talk(getResources().getString(R.string.backToPreviousMenu));
                break;
        }
    }

    public void execute()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: //lang
                if(init+1>lan.size()-1){
                    init=0;
                }
                else
                    init=init+1;
                lang.setText(lan.get(init));
                GlobalVars.talk(lan.get(init));
                break;

            case 2: //select
                Main onj=new Main();
                //Log.e("dasdas",code.get(init));

                GlobalVars.g(code.get(init));
                //Log.e("dfsvfxvxvdxvxcvcx",code.get(init));
                updateViews(code.get(init));


                break;

            case 3: //GO BACK
                this.finish();
                break;
        }
    }

    private void previousItem()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: //lang
                if (init-1<0)
                {
                    init = lan.size()-1;
                }
                else
                {
                    init = init -1;
                }
                lang.setText(lan.get(init));
                //GlobalVars.talk(GlobalVars.getDayName(dayValue));
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

            case GlobalVars.ACTION_SELECT_PREVIOUS:
                previousItem();
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

            case GlobalVars.ACTION_SELECT_PREVIOUS:
                previousItem();
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
        Intent refresh = new Intent(this, language.class);
        startActivity(refresh);
        finish();
    }

}
